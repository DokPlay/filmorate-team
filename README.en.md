# Filmorate — team project template

[Русская версия](README.md)

Production-ready skeleton of a movie recommendation service built with Spring Boot 3.2. Includes Flyway migrations, containerization, logging, and concise project documentation.

## Stack
- Java 21, Spring Boot 3.2
- PostgreSQL 16 + Flyway
- Gradle 8 (wrapper `./gradlew`)
- Docker / Docker Compose

## Quick start
1. Install Java 21 and Docker.
2. Build the artifact:
   ```bash
   ./gradlew clean build
   ```
3. Launch application and database via Docker Compose:
   ```bash
   docker-compose up --build
   ```
   The service is available at `http://localhost:8080`, the database at `localhost:5432`.

## Local development without Docker
1. Start PostgreSQL 16 locally and create the `filmorate` database.
2. Export environment variables:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/filmorate
   export SPRING_DATASOURCE_USERNAME=filmorate
   export SPRING_DATASOURCE_PASSWORD=filmorate
   export ROOT_LOG_LEVEL=INFO
   ```
3. Run the application:
   ```bash
   ./gradlew bootRun
   ```
4. Apply migrations manually (optional):
   ```bash
   ./gradlew flywayMigrate
   ```

## Profiles and configuration
- Default profile — `dev` (configured in `src/main/resources/application.yml`).
- Additional configs:
  - `application-dev.yml` — verbose SQL logging and enabled `flyway:clean`.
  - `application-test.yml` — H2-based test profile.
  - `logback-spring.xml` — console and rolling file logging.
- Key environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `LOG_FILE_PATH`, `ROOT_LOG_LEVEL`.

## Docker & containerization
- `Dockerfile` uses a two-stage build: Gradle build → minimal JRE image.
- `docker-compose.yml` brings up `db` (PostgreSQL) and `app` with healthcheck and port mappings.
- Application logs are written to `/var/log/filmorate` inside the container and mounted to the `app-logs` volume.

## Logging
- Console logs for local debugging and cluster visibility.
- Rolling file logs: 10 MB per file, up to 7 days (100 MB total).
- Root logger level is controlled via `ROOT_LOG_LEVEL` (`INFO` by default).

## Testing
- Integration tests use H2 in PostgreSQL compatibility mode (`application-test.yml`).
- Run tests:
  ```bash
  ./gradlew test
  ```

## CI/CD recommendations
- Add linters (Checkstyle, Spotless) and Docker image build to CI.
- For production deployments, consider a Helm chart using the same environment variables.

