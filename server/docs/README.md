# Lumen Coin API

REST API for the **Lumen Coin** gamified academic platform. Built with Spring Boot 3.4.5 and Java 21.

## Stack

| Layer      | Technology                             |
| ---------- | -------------------------------------- |
| Runtime    | Java 21                                |
| Framework  | Spring Boot 3.4.5                      |
| Database   | PostgreSQL 17                          |
| ORM        | Spring Data JPA / Hibernate            |
| Auth       | JWT (JJWT 0.12.6) via HTTP-only cookie |
| Mapping    | MapStruct 1.5.5                        |
| Validation | Jakarta Validation                     |
| Build      | Maven (mvnw wrapper)                   |

---

## Getting Started

### 1. Prerequisites

- Java 21
- Maven (or use the `./mvnw` wrapper - no installation needed)
- Docker and Docker Compose

### 2. Configure environment

```bash
cp .env.example .env
```

Edit `.env` and fill in all required values. Generate a secure JWT secret:

```bash
openssl rand -base64 32
```

### 3. Start the database

```bash
docker compose up -d
```

### 4. Run the API

```bash
./mvnw spring-boot:run
```

The server starts on `http://localhost:8080` (or the port set in `SERVER_PORT`).

---

## Authentication

The API uses **JWT stored in an HTTP-only cookie**. This means the token is never exposed to JavaScript, which prevents XSS-based token theft.

### Login

```bash
curl -c cookies.txt -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "yourpassword"}'
```

Response:

```json
{ "message": "Logged in successfully" }
```

The cookie `lumen_auth` is set automatically. Pass `-c cookies.txt` / `-b cookies.txt` in curl to persist and send the cookie across requests.

### Logout

```bash
curl -b cookies.txt -X POST http://localhost:8080/auth/logout
```

Response:

```json
{ "message": "Logged out successfully" }
```

The cookie is cleared server-side (`Max-Age=0`).

### Why HTTP-only cookies instead of Bearer tokens?

| Property           | HTTP-only Cookie                | `Authorization` Header (Bearer) |
| ------------------ | ------------------------------- | ------------------------------- |
| XSS risk           | None (JS cannot read)           | High (stored in `localStorage`) |
| CSRF risk          | Mitigated via `SameSite=Strict` | None                            |
| Browser auto-send  | Yes                             | No - must be managed manually   |
| Mobile/API clients | Manual `Cookie` header          | Standard `Authorization` header |

---

## API Endpoints

All endpoints return `application/json`.

### Auth

| Method | Path           | Auth required | Description       |
| ------ | -------------- | ------------- | ----------------- |
| POST   | `/auth/login`  | No            | Authenticate user |
| POST   | `/auth/logout` | No            | Clear auth cookie |

### Students

| Method | Path             | Auth required | Description        |
| ------ | ---------------- | ------------- | ------------------ |
| POST   | `/students`      | No            | Register a student |
| GET    | `/students`      | Yes           | List all students  |
| GET    | `/students/{id}` | Yes           | Get student by ID  |

### Teachers

| Method | Path             | Auth required            | Description        |
| ------ | ---------------- | ------------------------ | ------------------ |
| POST   | `/teachers`      | Yes (`ROLE_INSTITUTION`) | Register a teacher |
| GET    | `/teachers`      | Yes                      | List all teachers  |
| GET    | `/teachers/{id}` | Yes                      | Get teacher by ID  |

### Institutions

| Method | Path                 | Auth required | Description             |
| ------ | -------------------- | ------------- | ----------------------- |
| POST   | `/institutions`      | No            | Register an institution |
| GET    | `/institutions`      | Yes           | List all institutions   |
| GET    | `/institutions/{id}` | Yes           | Get institution by ID   |

### Companies

| Method | Path              | Auth required | Description        |
| ------ | ----------------- | ------------- | ------------------ |
| POST   | `/companies`      | No            | Register a company |
| GET    | `/companies`      | Yes           | List all companies |
| GET    | `/companies/{id}` | Yes           | Get company by ID  |

---

## User Roles

| Role          | Can register via     | Created by       |
| ------------- | -------------------- | ---------------- |
| `STUDENT`     | `POST /students`     | Self             |
| `TEACHER`     | `POST /teachers`     | Institution only |
| `INSTITUTION` | `POST /institutions` | Self             |
| `COMPANY`     | `POST /companies`    | Self             |

---

## Environment Variables

| Variable            | Description                                    | Default                                       |
| ------------------- | ---------------------------------------------- | --------------------------------------------- |
| `SERVER_PORT`       | HTTP port                                      | `8080`                                        |
| `DB_HOST`           | PostgreSQL host                                | `localhost`                                   |
| `DB_PORT`           | PostgreSQL port                                | `5432`                                        |
| `DB_NAME`           | Database name                                  | `lumen-coin-db`                               |
| `DB_USER`           | Database user                                  | `lumen`                                       |
| `DB_PASSWORD`       | Database password                              | -                                             |
| `JPA_DDL_AUTO`      | Hibernate DDL strategy (`update`, `create`, …) | `update`                                      |
| `JPA_SHOW_SQL`      | Log SQL queries                                | `false`                                       |
| `BCRYPT_STRENGTH`   | BCrypt work factor (min 10, recommended 12+)   | `12`                                          |
| `JWT_SECRET`        | HMAC-SHA256 signing secret (min 256 bits)      | -                                             |
| `JWT_EXPIRATION_MS` | Token TTL in milliseconds                      | `86400000` (24 h)                             |
| `JWT_COOKIE_NAME`   | Name of the auth cookie                        | `lumen_auth`                                  |
| `JWT_COOKIE_SECURE` | Send cookie over HTTPS only                    | `false` (set `true` in production)            |
| `ALLOWED_ORIGINS`   | Comma-separated CORS allowed origins           | `http://localhost:5173,http://localhost:5174` |

---

## Project Structure

```
src/main/java/br/pucminas/lumen_coin_api/
├── auth/            - Login / logout endpoints and JWT cookie issuance
├── config/          - SecurityConfig, CORS, beans
├── security/        - JwtService, JwtAuthenticationFilter, UserPrincipal
└── user/            - Student, Teacher, Institution, Company modules
```

See [docs/INSTRUCTIONS.md](INSTRUCTIONS.md) for coding conventions and architecture guidelines.
