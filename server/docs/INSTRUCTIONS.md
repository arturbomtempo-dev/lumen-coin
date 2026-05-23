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
