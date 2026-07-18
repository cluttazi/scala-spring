# scala-spring — Movie Tickets REST API

[![CI](https://github.com/cluttazi/scala-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/cluttazi/scala-spring/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Scala](https://img.shields.io/badge/Scala-2.13-red.svg)](https://www.scala-lang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

A movie-ticket booking REST API demonstrating that Scala and the Spring ecosystem work well together: Spring Boot, Spring Security with JWT tokens, Spring Data JPA/REST — all written in idiomatic Scala.

## Features

- **User registration and login** issuing JWT tokens (HS256 via [jjwt](https://github.com/jwtk/jjwt))
- **Movie listing** at `GET /api/v1/movies`
- **Ticket purchase** endpoints backed by JPA entities (`Movie`, `Ticket`, `User`, `Authority`, `CreditCard`)
- **Spring Data REST** management API exposed under `/api/v1/manage` (with HAL explorer)
- **Stateless security** configuration with role-based access

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Scala 2.13 |
| Framework | Spring Boot 3.5 (Web, Actuator, Data JPA, Data REST, Security) |
| Auth | JWT bearer tokens via an in-repo Spring Security 6 filter on [jjwt](https://github.com/jwtk/jjwt) |
| Persistence | MySQL (runtime), Hibernate 6; in-memory H2 for tests |
| Testing | ScalaTest 3.2 (Spring `TestContextManager`) |
| Build | Gradle 8 (wrapper included), Java 21 toolchain |

## Getting Started

### Prerequisites

- JDK 21
- MySQL running locally (only for actually running the app; tests use in-memory H2)

### Database Setup

Create the schema and adjust the credentials in `src/main/resources/application.properties`:

```sql
CREATE DATABASE db_example;
```

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_example
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
```

The schema is recreated on each run (`spring.jpa.hibernate.ddl-auto=create-drop`).

### Run

```bash
./gradlew bootRun
```

To debug, run `./gradlew bootRun --debug-jvm` and attach your IDE to the JVM.

### Test

```bash
./gradlew test
```

The test suite bootstraps the full Spring context against an in-memory H2 database, so no local MySQL is needed to run tests.

## Project Structure

```
src/main/scala/com/chrisluttazi/sescalamovie/
├── WebApplication.scala    # Spring Boot entry point
├── config/                 # Web security (JWT filter, stateless sessions)
├── controller/             # Login, registration, movies, tickets
├── messages/               # Request/response object mappers
├── model/                  # JPA entities
├── repository/             # Spring Data repositories
└── security/               # JWT token service + bearer authentication filter
```

## Continuous Integration

Every push and pull request builds the project and runs the full test suite on JDK 21 via [GitHub Actions](.github/workflows/ci.yml).

## License

This project is licensed under the MIT License – see [LICENSE](LICENSE) for details.
