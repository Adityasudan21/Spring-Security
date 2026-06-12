# Spring Security - Core Concepts

A hands-on Spring Boot project demonstrating the foundational concepts of **Spring Security**, including Basic Authentication, CSRF protection, session management, and custom security configuration.

---

## Tech Stack

| Technology        | Version  |
|-------------------|----------|
| Java              | 21       |
| Spring Boot       | 4.0.6    |
| Spring Security   | (managed)|
| Lombok            | 1.18.42  |
| Maven             | Wrapper  |

---

## Project Structure

```
src/
└── main/
    ├── java/com/springboot/Spring_Security/
    │   ├── SpringSecurityApplication.java   # Entry point
    │   ├── config/
    │   │   └── SecurityConfig.java          # Custom security filter chain
    │   ├── Controller/
    │   │   ├── HomeController.java          # /hello, /SessionInfo endpoints
    │   │   └── StudentController.java       # CRUD + CSRF token endpoint
    │   └── model/
    │       └── Student.java                 # Student model (Lombok)
    └── resources/
        └── application.properties           # App config, default credentials
```

---

## Concepts Covered

### 1. Basic Authentication
Spring Security auto-configures HTTP Basic Auth. Default credentials can be overridden in `application.properties`:
```properties
spring.security.user.name=Aditya
spring.security.user.password=1234
```

### 2. Custom Security Filter Chain (`SecurityConfig.java`)
Two approaches are demonstrated side by side:

- **Lambda DSL** (modern, concise) — shown as commented reference
- **Imperative / Anonymous Class style** (verbose, explicit) — the active `@Bean`

Both chains:
- Disable CSRF
- Require authentication on all requests
- Can be extended with form login, HTTP Basic, and session policies

### 3. CSRF Protection
- `GET` requests are CSRF-exempt by default.
- `POST` / `DELETE` requests require a CSRF token when CSRF is enabled.
- A dedicated endpoint `/csrf-token` returns the current CSRF token for use in Postman or frontend clients.

### 4. Session Management
The commented lambda config shows how to set `SessionCreationPolicy.STATELESS`, making the app fully stateless (useful for REST APIs / Postman testing).

### 5. Session Info Endpoint
`/SessionInfo` returns the current HTTP session ID, useful for observing session behaviour across requests.

---

## API Endpoints

| Method   | Endpoint       | Auth Required | Description                          |
|----------|----------------|---------------|--------------------------------------|
| `GET`    | `/hello`       | Yes           | Returns a greeting string            |
| `GET`    | `/SessionInfo` | Yes           | Returns the current session ID       |
| `GET`    | `/students`    | Yes           | Returns list of all students         |
| `POST`   | `/student`     | Yes           | Adds a new student (requires CSRF)   |
| `DELETE` | `/student`     | Yes           | Removes last student (requires CSRF) |
| `GET`    | `/csrf-token`  | Yes           | Returns the current CSRF token       |
| `GET`    | `/logout`      | Yes           | Spring Security logout endpoint      |

---

## Running the Application

```bash
./mvnw spring-boot:run
```

Then open [http://localhost:8080/hello](http://localhost:8080/hello) — you will be prompted for credentials.

**Default credentials:**
- Username: `Aditya`
- Password: `1234`

---

## Testing with Postman

1. Select **Basic Auth** under the Authorization tab.
2. Enter username `Aditya` and password `1234`.
3. For `POST /student`, first hit `GET /csrf-token` and pass the token as a header `X-CSRF-TOKEN`.

---

## Key Learning Points

- Spring Security provides a default login form and `/logout` route out of the box.
- Without `SessionCreationPolicy.STATELESS`, the browser session persists and re-authentication isn't needed on every request.
- With `STATELESS`, credentials must be sent with every request — ideal for REST APIs.
- CSRF protection is enabled by default and must be explicitly disabled for stateless REST APIs.
