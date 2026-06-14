# Copilot Instructions

## Stack

- **Java 26** with **Spring Boot 4.1.0**
- **Spring Data JDBC** (not JPA/Hibernate) for persistence
- **Spring Web MVC** for REST endpoints
- **Bean Validation** (`spring-boot-starter-validation`)
- **Testcontainers** for integration tests (Docker required)
- Build tool: **Maven** (use `./mvnw` wrapper)

## Build & Test Commands

```bash
# Build
./mvnw package

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=QueueingMeSoftlyApplicationTests

# Run a single test method
./mvnw test -Dtest=QueueingMeSoftlyApplicationTests#contextLoads

# Run the app
./mvnw spring-boot:run

# Run the app with Testcontainers (dev mode)
./mvnw spring-boot:test-run
```

## Architecture

This is an early-stage Spring Boot application. The base package is `com.queueingmesoftly`.

**Test infrastructure:** `TestcontainersConfiguration` is a `@TestConfiguration` class imported by integration tests and used by `TestQueueingMeSoftlyApplication` to launch the app locally with Testcontainers-managed services. Add container definitions (e.g., `PostgreSQLContainer`) to `TestcontainersConfiguration` — the test application entry point and integration tests will pick them up automatically via `@Import(TestcontainersConfiguration.class)`.

**Persistence:** Uses Spring Data JDBC, not JPA. Model classes are plain POJOs mapped with Spring Data JDBC annotations (`@Table`, `@Column`, `@Id`). No `@Entity`, no lazy loading, no session management.

## Key Conventions

- `TestQueueingMeSoftlyApplication` is the local dev entry point — it starts the real application with Testcontainers providing backing services. Run it instead of the main class when developing locally without a pre-existing database.
- Integration tests annotate with `@Import(TestcontainersConfiguration.class)` and `@SpringBootTest` to wire in the same container configuration.
- There is no database driver declared yet — add a driver dependency (e.g., `postgresql`) alongside a matching Testcontainers module when introducing persistence.
- Spring Boot 4.1 slice test starters are declared (`spring-boot-starter-data-jdbc-test`, `spring-boot-starter-webmvc-test`, `spring-boot-starter-validation-test`). Use their corresponding slice annotations (`@DataJdbcTest`, `@WebMvcTest`) for focused tests rather than `@SpringBootTest` when full context isn't needed.
