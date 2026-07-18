# Modernization Plan — scala-spring (se-scala-movie)

Audit date: 2026-07-18 · Branch: `claude/repo-modernization-audit-6se2sn`

## 1. What this repo is

A small (~530 LOC Scala) movie-ticketing REST API: Spring Boot + Spring
Security (JWT bearer tokens) + Spring Data JPA/REST over MySQL, written in
Scala with `@BeanProperty` Java-bean-style models. Build tool: Gradle.
One ScalaTest suite (`ModelSuite`) that boots the full Spring context.

## 2. Baseline (before modernization)

| Component | Current | Latest stable (Jul 2026) | Status |
|---|---|---|---|
| Gradle wrapper | 3.5 (2017) | 8.14.x | EOL; wrapper dist download fails in this env (403 via proxy) and Gradle 3.5 cannot run on Java 21 anyway |
| Java target | 1.8 | 21 (LTS) | Only JDK 21 is installed; Boot 1.5 cannot run on it |
| Spring Boot | 1.5.3.RELEASE (2017) | 3.5.16 (3.5 line) / 4.x | EOL since Aug 2019; many CVEs in transitive deps (Tomcat 8.5, Jackson 2.8, Hibernate 5.0, Spring Fw 4.3) |
| Scala | 2.11.7 (2015) | 2.13.18 (2.x line) | 2.11 EOL; no JDK 21 support |
| ScalaTest | 3.0.1 (`_2.11`) | 3.2.20 (`_2.13`) | EOL |
| JUnit | 4.12 | 4.13.2 | 4.12 has CVE-2020-15250 (TemporaryFolder info disclosure) |
| MySQL driver | `mysql:mysql-connector-java:5.1.13` (2010) | `com.mysql:mysql-connector-j` 9.x | Multiple CVEs (e.g. CVE-2017-3523/3586/3589, CVE-2018-3258); relocated coordinates |
| JWT filter | `org.visola…:spring-security-token-filter:1.1` | abandoned (~2017) | Built for Boot 1.x / `javax.servlet`; incompatible with Boot 3; the reason Scala was pinned to 2.11 |
| `spring-data-rest-hal-browser` | Boot-managed | replaced by `spring-data-rest-hal-explorer` | Deprecated/discontinued |
| `scalatest-spring_2.11` 0.3.1 | test ctx glue | abandoned | Replace with direct `TestContextManager` usage |
| Repos | `jcenter()`, `repo.spring.io/libs-snapshot` | Maven Central only | JCenter shut down (2021); snapshot repo unnecessary |
| CI | checkout@v4, setup-java@v4 (JDK 8), compile-only | JDK 21 + gradle/actions/setup-gradle@v4 + real tests | Was compile-only because tests needed local MySQL |

### Baseline build/test result

- `./gradlew --version` → **FAILS** (Gradle 3.5 distribution download 403 through
  the proxy; Gradle 3.5 is also incompatible with Java 21).
- `gradle assemble` (system Gradle 8.14.3) → **FAILS**:
  `Failed to apply plugin 'org.springframework.boot' > Configuration with name 'runtime' not found`
  (Boot 1.5 Gradle plugin + `compile/testCompile` configurations removed in Gradle 7+).
- Tests: **cannot run** (build does not evaluate); historically required a local
  MySQL at `localhost:3306` even in CI, so CI only compiled.

Conclusion: nothing builds on a current toolchain; this is a full-stack
migration, not incremental bumps.

## 3. Prioritized checklist

1. **Toolchain** (must, blocking everything)
   - [ ] Regenerate Gradle wrapper at 8.14.3 (system Gradle used locally
         because the proxy 403s wrapper distribution downloads; CI/dev
         machines use `./gradlew` normally).
   - [ ] Modern `build.gradle`: `plugins {}` DSL, Java 21 toolchain,
         `implementation/testImplementation`, Maven Central only.
2. **Framework migration** (must)
   - [ ] Spring Boot 1.5.3 → 3.5.16 (+ `io.spring.dependency-management`):
         `javax.*` → `jakarta.*` (persistence, validation);
         `WebSecurityConfigurerAdapter` → `SecurityFilterChain` bean with
         lambda DSL; `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity`;
         `antMatchers` → `requestMatchers`; password storage must use a
         `PasswordEncoder` (plaintext storage no longer supported).
   - [ ] Scala 2.11.7 → 2.13.18 (the 2.11 pin existed only for the dead JWT
         library).
   - [ ] Replace abandoned `spring-security-token-filter` with a small
         in-repo JWT `TokenService` + `OncePerRequestFilter` on
         jjwt 0.13.0, preserving endpoint behavior (login/register return a
         bearer token; `Authorization: Bearer …` authenticates requests) and
         the `security.token.filter.secret` property name.
   - [ ] Spring Data 3: repositories extend `CrudRepository` (+
         `PagingAndSortingRepository` no longer includes CRUD);
         `hal-browser` → `hal-explorer`; deprecated
         `org.hibernate.validator.constraints.Email` → jakarta `@Email`.
   - [ ] MySQL driver → Boot-managed `com.mysql:mysql-connector-j`;
         `com.mysql.jdbc.Driver` → `com.mysql.cj.jdbc.Driver`.
3. **Tests actually run** (must)
   - [ ] Port `ModelSuite` to ScalaTest 3.2 (`AnyFlatSpec`,
         `scalatestplus junit-4-13` runner) with direct
         `TestContextManager`, backed by in-memory H2 in the test profile so
         `gradle test` needs no MySQL — locally and in CI.
4. **Bug fixes** (should, with regression coverage)
   - [ ] Registration previously saved plaintext passwords; now BCrypt-encoded
         (required by Spring Security 5+; also the right thing).
5. **Best practices** (should)
   - [ ] `.gitignore` additions (Gradle/IDE), README refresh (run
         instructions, JDK/Gradle requirements), keep `.editorconfig`.
6. **CI** (must)
   - [ ] JDK 8 → temurin 21, `gradle/actions/setup-gradle@v4`, run
         `./gradlew build` (compiles + runs the H2-backed tests).
7. **Security notes**
   - [ ] Old stack CVEs resolved by the Boot 3.5 BOM (Tomcat, Jackson,
         Hibernate, Spring Fw/Security), JUnit 4.13.2, MySQL Connector/J 9.x.
   - [ ] The JWT secret and demo admin credentials remain hardcoded defaults
         (pre-existing; now overridable via properties — flagged, not
         rotated, as this is a demo app).

## 4. Deferred / out of scope

- Spring Boot 4.x / Spring Framework 7: released Nov 2025; the 1.5 → 3.5
  jump is already the largest safe step. 3.5 → 4 is a follow-up.
- Scala 3 rewrite: source-compatible-ish but a real migration; not needed.
- Docker/testcontainers-based MySQL integration tests.

*(Section 5 "Done vs Deferred" is appended at the end of the work.)*
