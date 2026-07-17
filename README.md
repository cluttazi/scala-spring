# scala-spring — Movie Tickets REST API

[![CI](https://github.com/cluttazi/scala-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/cluttazi/scala-spring/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Scala](https://img.shields.io/badge/Scala-2.11-red.svg)](https://www.scala-lang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

A movie-ticket booking REST API demonstrating that Scala and the Spring ecosystem work well together: Spring Boot, Spring Security with JWT tokens, Spring Data JPA/REST — all written in idiomatic Scala.

## Features

- **User registration and login** issuing JWT tokens (via `spring-security-token-filter`)
- **Movie listing** at `GET /api/v1/movies`
- **Ticket purchase** endpoints backed by JPA entities (`Movie`, `Ticket`, `User`, `Authority`, `CreditCard`)
- **Spring Data REST** management API exposed under `/api/v1/manage` (with HAL browser)
- **Stateless security** configuration with role-based access

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Scala 2.11 |
| Framework | Spring Boot 1.5 (Web, Actuator, Data JPA, Data REST, Security) |
| Auth | JWT via [spring-security-token-filter](https://github.com/visola/spring-security-token-filter) |
| Persistence | MySQL, Hibernate |
| Testing | ScalaTest + scalatest-spring |
| Build | Gradle (wrapper included) |

## Getting Started

### Prerequisites

- JDK 8
- MySQL running locally

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

The test suite bootstraps the full Spring context, so it needs the MySQL instance configured above.

## Project Structure

```
src/main/scala/com/chrisluttazi/sescalamovie/
├── WebApplication.scala    # Spring Boot entry point
├── config/                 # Web security (JWT filter, stateless sessions)
├── controller/             # Login, registration, movies, tickets
├── messages/               # Request/response object mappers
├── model/                  # JPA entities
└── repository/             # Spring Data repositories
```

## Continuous Integration

Every push and pull request compiles all main and test sources on JDK 8 via [GitHub Actions](.github/workflows/ci.yml).

## License

This project is licensed under the MIT License – see [LICENSE](LICENSE) for details.
