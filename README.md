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
    │   │   └── SecurityConfig.java             # Custom security filter chain + AuthenticationProvider
    │   ├── Controller/
    │   │   ├── HomeController.java             # /hello, /SessionInfo endpoints
    │   │   └── StudentController.java          # CRUD + CSRF token endpoint
    │   ├── model/
    │   │   ├── Student.java                    # Student model (Lombok)
    │   │   ├── User.java                       # JPA entity mapped to user_security table
    │   │   └── UserPrincipal.java              # Wraps User, implements UserDetails
    │   ├── repository/
    │   │   └── UserRepo.java                   # JPA repository with findByUsername query
    │   └── service/
    │       └── MyUserDetailsService.java       # Loads user from DB for Spring Security
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

### 2. Custom Security Filter Chain (`SecurityConfig.java`)
Two approaches are shown side by side for learning purposes:

- **Lambda DSL** (modern, concise) — the active `@Bean`
- **Imperative / Anonymous Class style** (verbose, explicit) — kept as commented reference

The active chain:
- Disables CSRF
- Requires authentication on all requests
- Enables HTTP Basic Auth
- Sets session policy to `STATELESS` (credentials sent on every request — ideal for REST/Postman)

### 3. CSRF Protection
- `GET` requests are CSRF-exempt by default.
- `POST` / `DELETE` requests require a CSRF token when CSRF is enabled.
- `GET /csrf-token` returns the current token for use in Postman or frontend clients.

### 4. Session Management
`SessionCreationPolicy.STATELESS` makes the app fully stateless — no session is created or reused between requests. This is the correct policy for REST APIs.

### 5. Session Info Endpoint
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

The `user_security` table is auto-created on first run by `ddl-auto=update`. Manually insert a test user:
```sql
INSERT INTO user_security (id, username, password) VALUES (1, 'Aditya', '1234');
```

---

## Running the Application

```bash
./mvnw spring-boot:run
```

Then test at [http://localhost:8080/hello](http://localhost:8080/hello) using Basic Auth credentials from the database.

---

## API Endpoints

| Method   | Endpoint        | Auth Required | Description                          |
|----------|-----------------|---------------|--------------------------------------|
| `GET`    | `/hello`        | Yes           | Returns a greeting string            |
| `GET`    | `/SessionInfo`  | Yes           | Returns the current session ID       |
| `GET`    | `/students`     | Yes           | Returns list of all students         |
| `POST`   | `/student`      | Yes           | Adds a new student                   |
| `DELETE` | `/student`      | Yes           | Removes last student                 |
| `GET`    | `/csrf-token`   | Yes           | Returns the current CSRF token       |
| `GET`    | `/logout`       | Yes           | Spring Security logout endpoint      |

---

## Testing with Postman

1. Select **Basic Auth** under the Authorization tab.
2. Enter the username and password of a user stored in your `user_security` table.
3. For `POST /student`, first hit `GET /csrf-token` and pass the token as header `X-CSRF-TOKEN` (only needed when CSRF is enabled).

---

## Key Learning Points

- `UserDetailsService` is the bridge between your database and Spring Security — it returns a `UserDetails` object that Spring uses to validate credentials.
- `UserPrincipal` wraps your domain `User` entity so it doesn't need to implement `UserDetails` directly, keeping the model clean.
- `DaoAuthenticationProvider` connects `UserDetailsService` and a `PasswordEncoder` to perform the actual credential check.
- `SessionCreationPolicy.STATELESS` disables server-side session storage — credentials must be sent with every request.
- CSRF protection is enabled by default and should be explicitly disabled for stateless REST APIs.
- Spring Boot BOM manages dependency versions — never hardcode versions for `spring-boot-starter-*` artifacts, as version mismatches break Spring Data JPA bean registration.
