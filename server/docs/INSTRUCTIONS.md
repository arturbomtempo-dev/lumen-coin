# Lumen Coin API - Development Instructions

## Architecture

The API follows a strict layered architecture. Each layer has a single responsibility and dependencies only flow downward.

```
Controller → Service (interface) → ServiceImpl → Repository → Entity
```

- **Entity** - JPA-mapped domain object. Lives in `<module>/entity/`.
- **Repository** - Spring Data JPA interface. Lives in `<module>/repository/`.
- **Service** - Interface defining the contract. Lives in `<module>/service/`.
- **ServiceImpl** - Concrete implementation. Lives in `<module>/service/impl/`. Annotated `@Service`.
- **Controller** - REST endpoint. Lives in `<module>/controller/`. Annotated `@RestController`.
- **DTO** - Data transfer objects. Lives in `<module>/dto/request/` and `<module>/dto/response/`.
- **Mapper** - MapStruct interface. Lives in `<module>/mapper/`.

---

## Package Structure

```
br.pucminas.lumen_coin_api
├── auth/
│   ├── controller/
│   ├── dto/
│   │   ├── request/
│   │   └── response/
│   └── service/
│       └── impl/
├── config/          - Spring configuration beans
├── security/        - JWT filter, UserPrincipal, UserDetailsService, JwtService
└── user/
    ├── controller/
    ├── dto/
    │   ├── request/
    │   └── response/
    ├── entity/
    ├── enums/
    ├── mapper/
    ├── repository/
    └── service/
        └── impl/
```

---

## Language and Code Style

- All code, names, and documentation must be in **English**.
- No comments of any kind in production code (no inline, block, or Javadoc comments).
- No `System.out.println` or manual logging - use SLF4J if logging is required.
- No `TODO` markers in committed code.

---

## Naming Conventions

| Artifact     | Pattern                   | Example                  |
| ------------ | ------------------------- | ------------------------ |
| Entity       | `PascalCase`              | `Student`, `Company`     |
| Repository   | `<Entity>Repository`      | `StudentRepository`      |
| Service      | `<Entity>Service`         | `StudentService`         |
| ServiceImpl  | `<Entity>ServiceImpl`     | `StudentServiceImpl`     |
| Controller   | `<Entity>Controller`      | `StudentController`      |
| Request DTO  | `<Action><Entity>Request` | `RegisterStudentRequest` |
| Response DTO | `<Entity>Response`        | `StudentResponse`        |
| Mapper       | `<Entity>Mapper`          | `StudentMapper`          |
| Enum         | `PascalCase`              | `UserRole`, `Avatar`     |
| Enum values  | `UPPER_SNAKE_CASE`        | `MARIO`, `INSTITUTION`   |

---

## Entities

- All entities extend `User` (`@Inheritance(strategy = JOINED)`, table `tb_users`).
- Use Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor` on every entity.
- Each subclass implements the abstract `getRole(): UserRole` method - do not call `setRole()`.
- Use `@Column` constraints to enforce field length and nullability at the DB level.
- `createdAt` and `updatedAt` are set automatically via `@PrePersist` / `@PreUpdate` in `User`.

---

## DTOs

- Request DTOs are Java **records** with Jakarta validation annotations.
- Response DTOs are Java **records** - no validation annotations.
- Validation happens **only at the DTO boundary** (controller layer via `@Valid`).
- Never expose the `password` field in any response DTO.
- Use `Instant` for timestamp fields (`createdAt`, `updatedAt`).

---

## Mappers

- Use **MapStruct** (`componentModel = "spring"`) for entity-to-response DTO mapping.
- Mapper interfaces live in `<module>/mapper/`.
- Never perform mapping logic manually inside services or controllers.

---

## Services

- Services must depend on the **interface**, not the concrete implementation.
- Password encoding: always use `PasswordEncoder.encode()` - never store plaintext passwords.
- Avatar defaults: `Company` → `Avatar.COMPANY`, `Institution` → `Avatar.INSTITUTION`.
- Business logic and data transformation belong in the service layer.

---

## Controllers

- Controllers must not contain business logic.
- Use `@RequestMapping("/<resource>")` at the class level (plural nouns).
- Registration endpoints return `ResponseEntity` with status **201 Created**.
- Use `@Valid` on all `@RequestBody` parameters backed by request DTOs.
- Method-level security uses `@PreAuthorize` annotations - do not duplicate security logic inside service methods.

---

## Authentication

The API uses **JWT stored in an HTTP-only cookie** (`lumen_auth` by default).

### Flow

1. `POST /auth/login` - validates credentials, issues JWT as an HTTP-only cookie, returns `{ "message": "Logged in successfully" }`.
2. Every subsequent request sends the cookie automatically (browser) or manually (non-browser clients via `Cookie` header).
3. `JwtAuthenticationFilter` reads the cookie, validates the token, and sets `SecurityContextHolder`.
4. `POST /auth/logout` - clears the cookie (sets `Max-Age=0`), returns `{ "message": "Logged out successfully" }`.

### JWT Claims

| Claim   | Value             |
| ------- | ----------------- |
| `sub`   | User UUID         |
| `email` | User email        |
| `role`  | UserRole name     |
| `name`  | User display name |

### Cookie Properties

| Property   | Value                          |
| ---------- | ------------------------------ |
| `HttpOnly` | `true` - not accessible via JS |
| `SameSite` | `Strict`                       |
| `Secure`   | `false` in dev, `true` in prod |
| `Path`     | `/`                            |
| `Max-Age`  | `JWT_EXPIRATION_MS / 1000`     |

---

## Authorization

- Public endpoints (no authentication required):
  - `POST /auth/login`
  - `POST /auth/logout`
  - `POST /students`
  - `POST /institutions`
  - `POST /companies`
- All other endpoints require a valid JWT cookie.
- `POST /teachers` requires `ROLE_INSTITUTION` (`@PreAuthorize("hasRole('INSTITUTION')")`).

---

## Security Rules

- **Never** hardcode secrets, passwords, or API keys in source code.
- All secrets are loaded from environment variables via `application.properties` placeholders.
- The `.env` file must never be committed to version control.
- `JWT_SECRET` must be at least 256 bits (32 bytes). Generate with:
  ```bash
  openssl rand -base64 32
  ```
- `JWT_COOKIE_SECURE` must be `true` in any environment served over HTTPS.

---

## Validation

- Use Jakarta Validation annotations on request DTO fields.
- A `@ControllerAdvice` (`GlobalExceptionHandler`) must handle `MethodArgumentNotValidException` and return structured error responses.
- Do not validate inside service methods - trust that the controller layer enforced the contract.

---

## Error Responses

All error responses follow the structure:

```json
{
  "timestamp": "2025-01-01T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": { "field": "reason" }
}
```

Use a `GlobalExceptionHandler` to centralize exception handling.

---

## Environment Variables

All configurable values live in `.env` (never committed). See `.env.example` for the full list with descriptions.

---

## WhatsApp Notifications (WAHA)

The API sends WhatsApp notifications via **WAHA (WhatsApp HTTP API)**, a self-hosted Docker service that exposes a REST API on top of WhatsApp Web. Notifications are dispatched asynchronously using `@Async`, mirroring the email notification pattern.

### When notifications are sent

| Event                     | Recipient | Method                               |
| ------------------------- | --------- | ------------------------------------ |
| Student registration      | Student   | `WhatsAppService.sendWelcome`        |
| Teacher registration      | Teacher   | `WhatsAppService.sendTeacherWelcome` |
| Coins sent by teacher     | Teacher   | `WhatsAppService.sendCoinSent`       |
| Coins received by student | Student   | `WhatsAppService.sendCoinReceived`   |

### Architecture

```
br.pucminas.lumen_coin_api.whatsapp
├── dto/
│   └── WhatsAppSendTextRequest.java   - Payload record sent to WAHA /api/sendText
├── service/
│   ├── WhatsAppService.java           - Interface defining available operations
│   └── impl/
│       └── WhatsAppServiceImpl.java   - Implementation; builds RestClient from WAHA config
```

### Environment variables

| Variable        | Default                 | Purpose                                  |
| --------------- | ----------------------- | ---------------------------------------- |
| `WAHA_BASE_URL` | `http://localhost:3000` | Base URL of the WAHA container           |
| `WAHA_API_KEY`  | _(empty)_               | API key generated by WAHA on first start |
| `WAHA_SESSION`  | `default`               | WhatsApp session name                    |
| `WAHA_ENABLED`  | `false`                 | Enable/disable WhatsApp sending          |

### Phone number format

Phone numbers are stored in the `phone` column of `tb_users` as **Brazilian national format** (10–11 digits, DDD + number, no country code). Example: `31999999999` for `(31) 99999-9999`.

The service prepends `55` and appends `@c.us` at send time: `5531999999999@c.us`.

### Local setup with WAHA

**Prerequisites:** Docker installed on your machine.

**1. Start the WAHA container**

```bash
docker run -it --rm \
  --platform linux/amd64 \
  -p 3000:3000/tcp \
  -v waha-data:/app/.sessions \
  -e WAHA_API_KEY=<your-api-key> \
  --name waha \
  devlikeapro/waha
```

Wait for the following line in the terminal output:

```
WhatsApp HTTP API is running on: http://[::1]:3000
```

> Apple Silicon (M1/M2/M3) users must include `--platform linux/amd64` for emulation via Rosetta 2. The `-v waha-data:/app/.sessions` volume persists the authenticated session across container restarts.

**2. Authenticate your WhatsApp number**

On first run, WAHA generates credentials. Copy the `WAHA_API_KEY` value printed in the terminal output and save it to your `.env` file.

Then generate the QR code:

```bash
curl "http://localhost:3000/api/default/auth/qr?format=image" \
  -H "X-Api-Key: <your-api-key>" \
  --output qr.png && open qr.png
```

Scan the QR code with your WhatsApp mobile app via **Settings → Linked Devices → Link a Device**.

**3. Confirm the session is active**

```bash
curl "http://localhost:3000/api/sessions/default" \
  -H "X-Api-Key: <your-api-key>"
```

Look for `"status": "WORKING"` in the response. After scanning, the session persists in the Docker volume so subsequent starts reconnect automatically.

**4. Configure the application**

Add the following to your `.env` file:

```env
WAHA_BASE_URL=http://localhost:3000
WAHA_API_KEY=<your-api-key>
WAHA_SESSION=default
WAHA_ENABLED=true
```

**5. Disable for testing**

Set `WAHA_ENABLED=false` in `.env` to suppress all WhatsApp calls without removing the integration. The service will log debug messages and return immediately.

---

## Image Storage (Cloudinary)

The API stores benefit images via **Cloudinary**, a cloud-based media management service. Images are uploaded as `multipart/form-data` and only the resulting secure URL is persisted in the database.

### Architecture

```
br.pucminas.lumen_coin_api.storage
├── exception/
│   └── StorageException.java                     - Thrown on invalid file type or upload failure
├── service/
│   ├── StorageService.java                        - Interface defining the upload contract
│   └── impl/
│       └── CloudinaryStorageServiceImpl.java      - Cloudinary implementation
```

`CloudinaryConfig` (in `config/`) creates the `Cloudinary` bean from environment variables.

### When images are uploaded

| Endpoint                     | Operation | Image field    |
| ---------------------------- | --------- | -------------- |
| `POST /benefits/{companyId}` | Required  | `image` (part) |
| `PUT /benefits/{id}`         | Optional  | `image` (part) |

If `image` is omitted on update, the existing URL is kept unchanged.

### Request format

Both create and update endpoints use `multipart/form-data` with two parts:

| Part    | Content-Type       | Description                       |
| ------- | ------------------ | --------------------------------- |
| `data`  | `application/json` | JSON with the benefit fields      |
| `image` | image/\*           | Image file (JPEG, PNG, WebP, GIF) |

### Allowed file types

`image/jpeg`, `image/png`, `image/webp`, `image/gif`. Any other MIME type returns `422 Unprocessable Entity`.

### Environment variables

| Variable                | Purpose                                  |
| ----------------------- | ---------------------------------------- |
| `CLOUDINARY_CLOUD_NAME` | Your Cloudinary cloud name               |
| `CLOUDINARY_API_KEY`    | API key from the Cloudinary dashboard    |
| `CLOUDINARY_API_SECRET` | API secret from the Cloudinary dashboard |

### Local setup with Cloudinary

**1. Create a free account**

Go to [https://cloudinary.com](https://cloudinary.com) and sign up. No credit card required for the free tier.

**2. Get your credentials**

After logging in, open the [Cloudinary Console Dashboard](https://console.cloudinary.com/). You will see your **Cloud name**, **API Key**, and **API Secret** at the top of the page.

**3. Configure the application**

Add the following to your `.env` file:

```env
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

Replace the placeholder values with the credentials from the dashboard.
