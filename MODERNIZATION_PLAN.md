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
   - [x] Regenerate Gradle wrapper at 8.14.3 (system Gradle used locally
         because the proxy 403s wrapper distribution downloads; CI/dev
         machines use `./gradlew` normally).
   - [x] Modern `build.gradle`: `plugins {}` DSL, Java 21 toolchain,
         `implementation/testImplementation`, Maven Central only.
2. **Framework migration** (must)
   - [x] Spring Boot 1.5.3 → 3.5.16 (+ `io.spring.dependency-management`):
         `javax.*` → `jakarta.*` (persistence, validation);
         `WebSecurityConfigurerAdapter` → `SecurityFilterChain` bean with
         lambda DSL; `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity`;
         `antMatchers` → `requestMatchers`; password storage must use a
         `PasswordEncoder` (plaintext storage no longer supported).
   - [x] Scala 2.11.7 → 2.13.18 (the 2.11 pin existed only for the dead JWT
         library).
   - [x] Replace abandoned `spring-security-token-filter` with a small
         in-repo JWT `TokenService` + `OncePerRequestFilter` on
         jjwt 0.13.0, preserving endpoint behavior (login/register return a
         bearer token; `Authorization: Bearer …` authenticates requests) and
         the `security.token.filter.secret` property name.
   - [x] Spring Data 3: repositories extend `CrudRepository` (+
         `PagingAndSortingRepository` no longer includes CRUD);
         `hal-browser` → `hal-explorer`; deprecated
         `org.hibernate.validator.constraints.Email` → jakarta `@Email`.
   - [x] MySQL driver → Boot-managed `com.mysql:mysql-connector-j`;
         `com.mysql.jdbc.Driver` → `com.mysql.cj.jdbc.Driver`.
3. **Tests actually run** (must)
   - [x] Port `ModelSuite` to ScalaTest 3.2 (`AnyFlatSpec`,
         `scalatestplus junit-4-13` runner) with direct
         `TestContextManager`, backed by in-memory H2 in the test profile so
         `gradle test` needs no MySQL — locally and in CI.
4. **Bug fixes** (should, with regression coverage)
   - [x] Registration previously saved plaintext passwords; now BCrypt-encoded
         (required by Spring Security 5+; also the right thing).
5. **Best practices** (should)
   - [x] `.gitignore` additions (Gradle/IDE), README refresh (run
         instructions, JDK/Gradle requirements), keep `.editorconfig`.
6. **CI** (must)
   - [x] JDK 8 → temurin 21, `gradle/actions/setup-gradle@v4`, run
         `./gradlew build` (compiles + runs the H2-backed tests).
7. **Security notes**
   - [x] Old stack CVEs resolved by the Boot 3.5 BOM (Tomcat, Jackson,
         Hibernate, Spring Fw/Security), JUnit 4.13.2, MySQL Connector/J 9.x.
   - [x] The JWT secret and demo admin credentials remain hardcoded defaults
         (pre-existing; now overridable via properties — flagged, not
         rotated, as this is a demo app).

## 4. Deferred / out of scope

- Spring Boot 4.x / Spring Framework 7: released Nov 2025; the 1.5 → 3.5
  jump is already the largest safe step. 3.5 → 4 is a follow-up.
- Scala 3 rewrite: source-compatible-ish but a real migration; not needed.
- Docker/testcontainers-based MySQL integration tests.


## 5. Done vs Deferred (final)

### Done

- **Toolchain**: Gradle wrapper 3.5 → 8.14.3 (bin distro); modern
  `plugins {}` build script; Java 21 toolchain; Maven Central only.
- **Framework**: Spring Boot 1.5.3 → **3.5.16**, Spring Security 6, Spring
  Data 3, Hibernate 6; full `javax.*` → `jakarta.*` migration;
  `WebSecurityConfigurerAdapter` → `SecurityFilterChain` + lambda DSL +
  `@EnableMethodSecurity`; `@GeneratedValue` pinned to `IDENTITY` to
  preserve pre-Hibernate-6 column semantics (needed for the JDBC security
  inserts against the JPA-generated `authorities` table).
- **Scala**: 2.11.7 → **2.13.18**; ScalaTest 3.0.1 → 3.2.20; deprecation
  warnings fixed (`-deprecation -feature` now enabled).
- **JWT**: abandoned `spring-security-token-filter` replaced by an in-repo
  `TokenService` / `JwtTokenService` / `TokenAuthenticationFilter` on
  jjwt 0.13.0; same property (`security.token.filter.secret`), same 1-hour
  validity, same bearer-token flow.
- **Bug/behavior fixes** (each with regression coverage):
  - registration stored plaintext passwords → now `PasswordEncoder`-encoded
    (mandatory under Spring Security 5+), and the post-save authentication
    uses the raw credential so the login round-trip still works;
  - bootstrap admin creation moved to an idempotent `ApplicationRunner`
    (runs after the Hibernate schema exists);
  - `MovieRepository.findAll`/`MovieResponse` use `java.util.List` instead
    of the fragile concrete `java.util.ArrayList` return type (JSON shape
    unchanged).
- **Tests**: suite ported and expanded from 1 to 6 tests, running against
  in-memory H2 — `./gradlew test` needs no MySQL, locally or in CI. Noted
  semantic change: the invalid-save test now fails inside Hibernate
  (`JpaSystemException`) instead of Spring Security 4's accidental
  `AuthenticationCredentialsNotFoundException`; the test's intent (invalid
  entity rejected) is preserved and documented in the suite.
- **CI**: JDK 8 compile-only → JDK 21 (temurin), `actions/checkout@v4`,
  `actions/setup-java@v4`, `gradle/actions/setup-gradle@v4` (with caching),
  full `./gradlew build` including tests, failure test-report artifact.
- **Security**: EOL Boot 1.5 stack (Tomcat 8.5, Jackson 2.8, Spring Fw 4.3,
  Hibernate 5.0 — dozens of published CVEs) replaced by the maintained
  Boot 3.5 BOM; MySQL Connector/J 5.1.13 (CVE-2017-3523 et al.) → 9.x;
  JUnit 4.12 (CVE-2020-15250) → 4.13.2; plaintext password storage removed.
- **Docs**: README refreshed (stack table, JDK 21, H2-backed tests, badges).

### Deferred

- **Spring Boot 4.x / Framework 7** (GA Nov 2025): 1.5 → 3.5 was the
  largest safe single step; 3.5 → 4 is a small follow-up once 3.5 is proven.
- **Scala 3** rewrite: not needed; 2.13 is fully supported.
- **Hardcoded demo secret/credentials** (`security.token.filter.secret`,
  `admin@admin.com`/`password`): now overridable via properties but the
  defaults remain in-repo, as this is a demo app. Rotate/externalize before
  any real deployment.
- **MySQL-backed integration tests** (testcontainers/Docker): environment
  has no Docker; H2 (MySQL mode) covers the JPA layer.
- **Pre-existing security-semantics quirks** left as-is (behavior
  preserving): e.g. `@PreAuthorize("authenticated")` on
  `UserRepository.findByUsername` means anonymous callers of the
  (web-permitAll) `/api/v1/register` and `/api/v1/tickets` endpoints are
  denied at the method layer and receive HTTP 412; redesigning endpoint
  security is a product decision, not a modernization step.

### Environment note

The sandbox proxy blocks the wrapper distribution download
(services.gradle.org redirects to a blocked GitHub release URL), so local
verification used the system Gradle 8.14.3 — the same version the committed
wrapper pins. `./gradlew` works normally on GitHub runners and dev machines.

## 6. Summary (PR-description style)

**What changed**: full modernization of a 2017-era Scala + Spring Boot demo:
Gradle 3.5 → 8.14.3, Java 8 → 21, Scala 2.11 → 2.13, Spring Boot 1.5 →
3.5.16 (jakarta namespace, Spring Security 6 component-style config, Spring
Data 3), the abandoned JWT filter library replaced by a small in-repo jjwt
implementation, passwords now stored encoded, tests ported to ScalaTest
3.2 and made self-contained on H2 (1 → 6 tests), CI upgraded to JDK 21 with
Gradle caching and now runs the full suite.

**Risks**: password hashes and the Security-6 method-security semantics are
behavior changes forced by the framework jump (both documented and
regression-tested); Hibernate 6 may generate slightly different DDL than
Hibernate 5 for the throwaway `create-drop` schema; the redeclared
repository CRUD methods rely on Spring Data 3 honoring redeclarations
(verified by tests).

**Untouched**: all endpoint paths, request/response JSON shapes, entity/table
names, the `security.token.filter.secret` property, MySQL as the runtime
database, project/package layout, MIT license.
