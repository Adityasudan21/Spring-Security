# Spring Security - Core Concepts

A hands-on Spring Boot project demonstrating the foundational concepts of **Spring Security**, including Basic Authentication, CSRF protection, session management, database-backed user authentication using JPA + PostgreSQL, and custom security configuration.

---

## Tech Stack

| Technology        | Version          |
|-------------------|------------------|
| Java              | 21               |
| Spring Boot       | 4.0.6            |
| Spring Security   | (managed by BOM) |
| Spring Data JPA   | (managed by BOM) |
| PostgreSQL        | 42.7.11          |
| Lombok            | 1.18.42          |
| Maven             | Wrapper          |

---

## Project Structure

```
src/
└── main/
    ├── java/com/springboot/Spring_Security/
    │   ├── SpringSecurityApplication.java      # Entry point
    │   ├── config/
    │   │   ├── SecurityConfig.java             # Basic auth concepts (lambda vs imperative DSL)
    │   │   └── DaoSecurityConfig.java          # Active filter chain + DaoAuthenticationProvider
    │   ├── Controller/
    │   │   ├── HomeController.java             # /hello, /SessionInfo endpoints
    │   │   ├── StudentController.java          # CRUD + CSRF token endpoint
    │   │   └── UserController.java             # POST /register — hashes password, saves user
    │   ├── model/
    │   │   ├── Student.java                    # Student model (Lombok)
    │   │   ├── User.java                       # JPA entity mapped to user_security table
    │   │   └── UserPrincipal.java              # Wraps User, implements UserDetails
    │   ├── repository/
    │   │   └── UserRepo.java                   # JPA repository with findByUsername query
    │   └── service/
    │       ├── MyUserDetailsService.java       # Loads user from DB for Spring Security
    │       └── UserService.java                # Saves user via UserRepo
    └── resources/
        └── application.properties              # App config, DB connection, DDL policy
```

---

## Concepts Covered

### 1. Database-backed Authentication
Users are stored in a PostgreSQL table (`user_security`). Spring Security loads them via:

- **`UserRepo`** — JPA repository with a `findByUsername(String)` query method
- **`MyUserDetailsService`** — implements `UserDetailsService`, fetches the `User` entity and wraps it in `UserPrincipal`
- **`UserPrincipal`** — implements `UserDetails`, exposes password, username, and granted authorities to Spring Security
- **`DaoAuthenticationProvider`** — wired with `MyUserDetailsService` inside `SecurityConfig`

### 2. User Registration with BCrypt (`UserController` + `UserService`)
`POST /register` accepts a `User` JSON body, hashes the password with **BCrypt (12 rounds)** before saving it to the database, and returns the saved user. No authentication is required to register.

```json
{ "username": "Aditya", "password": "mypassword" }
```

`BCryptPasswordEncoder` is instantiated with strength `12` directly in the controller for clarity. The same encoder is configured in `DaoAuthenticationProvider` so login comparisons work correctly.

### 3. Custom Security Filter Chain (`DaoSecurityConfig.java`)
The active security configuration:
- Wires `DaoAuthenticationProvider` with `MyUserDetailsService` and `BCryptPasswordEncoder(12)`
- Disables CSRF (stateless REST API)
- Requires authentication on all requests
- Enables HTTP Basic Auth
- Sets session policy to `STATELESS` (credentials sent on every request — ideal for REST/Postman)

`SecurityConfig.java` is kept as a reference showing both **Lambda DSL** and **Imperative/Anonymous Class** approaches side by side (no active `@Bean`).

### 4. CSRF Protection
- `GET` requests are CSRF-exempt by default.
- `POST` / `DELETE` requests require a CSRF token when CSRF is enabled.
- `GET /csrf-token` returns the current token for use in Postman or frontend clients.

### 5. Session Management
`SessionCreationPolicy.STATELESS` makes the app fully stateless — no session is created or reused between requests. This is the correct policy for REST APIs.

### 6. Session Info Endpoint
`GET /SessionInfo` returns the current HTTP session ID, useful for observing session behaviour when switching between STATELESS and default policies.

---

## Prerequisites

- Java 21+
- PostgreSQL running locally on port `5432`
- A database named `DB` created in PostgreSQL

```sql
CREATE DATABASE "DB";
```

---

## Configuration

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/DB
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Override credentials via environment variables:
```bash
DB_URL=jdbc:postgresql://localhost:5432/mydb
DB_USERNAME=myuser
DB_PASSWORD=mypassword
```

The `user_security` table is auto-created on first run by `ddl-auto=update`. Register a user via the API instead of inserting manually — passwords are stored as BCrypt hashes:

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username": "Aditya", "password": "mypassword"}'
```

---

## Running the Application

```bash
./mvnw spring-boot:run
```

Then test at [http://localhost:8080/hello](http://localhost:8080/hello) using Basic Auth credentials from the database.

---

## API Endpoints

| Method   | Endpoint        | Auth Required | Description                                      |
|----------|-----------------|---------------|--------------------------------------------------|
| `POST`   | `/register`     | No            | Register a new user (password BCrypt-hashed)     |
| `GET`    | `/hello`        | Yes           | Returns a greeting string                        |
| `GET`    | `/SessionInfo`  | Yes           | Returns the current session ID                   |
| `GET`    | `/students`     | Yes           | Returns list of all students                     |
| `POST`   | `/student`      | Yes           | Adds a new student                               |
| `DELETE` | `/student`      | Yes           | Removes last student                             |
| `GET`    | `/csrf-token`   | Yes           | Returns the current CSRF token                   |
| `GET`    | `/logout`       | Yes           | Spring Security logout endpoint                  |

---

## Testing with Postman

1. **Register a user** — `POST /register` with raw JSON body (no auth needed):
   ```json
   { "username": "Aditya", "password": "mypassword" }
   ```
2. **Authenticate** — For all other endpoints, select **Basic Auth** and enter the same credentials.
3. For `POST /student`, first hit `GET /csrf-token` and pass the token as header `X-CSRF-TOKEN` (only needed when CSRF is enabled).

---

## Key Learning Points

- `UserDetailsService` is the bridge between your database and Spring Security — it returns a `UserDetails` object that Spring uses to validate credentials.
- `UserPrincipal` wraps your domain `User` entity so it doesn't need to implement `UserDetails` directly, keeping the model clean.
- `DaoAuthenticationProvider` connects `UserDetailsService` and a `PasswordEncoder` to perform the actual credential check.
- `BCryptPasswordEncoder` with strength `12` means each password is hashed with 2^12 = 4096 rounds — slow enough to resist brute force, fast enough for real use.
- Passwords must be encoded at registration and the same encoder must be set on `DaoAuthenticationProvider` — mismatching encoders will cause every login to fail.
- `SessionCreationPolicy.STATELESS` disables server-side session storage — credentials must be sent with every request.
- CSRF protection is enabled by default and should be explicitly disabled for stateless REST APIs.
- Spring Boot BOM manages dependency versions — never hardcode versions for `spring-boot-starter-*` artifacts, as version mismatches break Spring Data JPA bean registration.
