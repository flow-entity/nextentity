# Repository Guidelines

## Project Structure & Module Organization
This repository is a multi-module Maven project targeting Java 25+. The root `pom.xml` aggregates:
- `nextentity-core`: main DSL, JDBC/JPA execution, and most unit/integration tests.
- `nextentity-spring`: Spring integration layer built on top of `nextentity-core`.
- `nextentity-examples`: runnable usage examples and Spring Boot-based integration tests.

Production code lives under `src/main/java`, tests under `src/test/java`, and test/application config under `src/test/resources` or `src/main/resources`. User-facing documentation is in `docs/guides`.

## Build, Test, and Development Commands
- `mvn clean test`: compile all modules and run the full test suite.
- `mvn clean package -DskipTests`: build artifacts without running tests.
- `mvn -pl nextentity-core test`: run only core-module tests.
- `mvn -pl nextentity-spring test`: run only Spring integration tests.
- `mvn -pl nextentity-examples test`: verify documented examples still work.
- `mvn -pl nextentity-core -Dtest="*IntegrationTest" test`: focus on core integration coverage.

Run commands from the repository root unless you are working inside a single module.

## Coding Style & Naming Conventions
Use 4-space indentation and standard Java brace style. Keep packages lowercase (`io.github.nextentity...`), classes/interfaces in PascalCase, methods/fields in camelCase, and constants in UPPER_SNAKE_CASE. Preserve the existing API-first style: short, explicit method names and concise Javadoc only where behavior is not obvious. Follow existing suffixes such as `*Repository`, `*Provider`, and `*Factory`.

## Testing Guidelines
Tests use JUnit 5, AssertJ, Mockito, Spring Boot Test, and Testcontainers. Name fast unit tests `*Test` and database-backed scenarios `*IntegrationTest`. In `nextentity-core`, JaCoCo enforces a minimum 65% instruction coverage at bundle level, so new features should include coverage, not just happy-path checks. Integration tests may start containers; keep them deterministic and reset mutated state in `@AfterEach` when needed.

## Commit & Pull Request Guidelines
Recent history follows a Conventional Commit style such as `docs: ...`, `fix: ...`, `refactor: ...`, and `test: ...`. Keep subject lines imperative and scoped to one change. PRs should include a brief summary, affected modules, commands run locally, and any related issue or guide update. Screenshots are generally unnecessary for this library unless documentation output is the primary change.

## Configuration Tips
Do not commit real database credentials. Keep local overrides in environment-specific config, and treat files in `nextentity-examples/src/main/resources` as safe examples rather than production defaults.
