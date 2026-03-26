# AGENTS.md - NextEntity Development Guide

> This file provides guidance for AI coding agents working in the NextEntity repository.

## Project Overview

**NextEntity** is a SQL DSL library for Java (similar to JPA Criteria API but cleaner). It's a multi-module Maven project:
- `nextentity-core` - Core query/CRUD DSL
- `nextentity-spring` - Spring Framework integration

**Repository**: https://github.com/flow-entity/nextentity
**Language**: Java 17+ (uses module system)

---

## Build & Test Commands

### Full Build
```bash
mvn clean install
```

### Single Module Build
```bash
# Core only
mvn clean install -pl nextentity-core

# Spring only
mvn clean install -pl nextentity-spring
```

### Run Tests
```bash
# All tests
mvn test

# Single test class
mvn test -Dtest=CrudOperationsIntegrationTest

# Single test method
mvn test -Dtest=CrudOperationsIntegrationTest#shouldInsertSingleEmployee

# With specific database (via Maven profile or system property)
mvn test -Dtestcontainers.mysql=true -Dtestcontainers.postgresql=true

# Skip tests
mvn clean install -DskipTests
```

### Code Quality
```bash
# Run JaCoCo coverage check (requires 65%+ coverage)
mvn clean verify

# Compile only
mvn compile
mvn test-compile
```

### IDE Support
- **IntelliJ IDEA**: Open `pom.xml` as project, enable annotation processing for Lombok
- **VS Code**: Install Java Extension Pack, configure `java.home` and `maven.executable.path`

---

## Code Style Guidelines

### Indentation & Formatting
- **4 spaces** (no tabs)
- Line length: ~120 chars (soft limit)
- One blank line between method declarations in classes
- No blank line after opening brace `{`

### File Structure
```java
// 1. Package declaration
package io.github.nextentity.core.meta;

// 2. Import statements (grouped, sorted)
//    - java.* imports
//    - javax.* / jakarta.* imports  
//    - Third-party imports (org.*, com.*)
//    - Internal imports (io.github.nextentity.*)
//    - Static imports

// 3. Class declaration with Javadoc

// 4. Class body: fields → constructors → methods
```

### Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Class | PascalCase | `SimpleEntity`, `QueryBuilder` |
| Interface | PascalCase | `EntityType`, `Repository` |
| Method | camelCase | `getProjection()`, `insertAll()` |
| Field (private) | camelCase | `tableName`, `projectionTypeGenerator` |
| Constant | UPPER_SNAKE | N/A (rarely used) |
| Package | lowercase | `io.github.nextentity.core.meta` |
| Test class | PascalCase + Test suffix | `CrudOperationsIntegrationTest` |

### Javadoc Requirements

**Required on:**
- All `public` and `protected` classes
- All `public` and `protected` methods
- All `interface` definitions

**Format:**
```java
/**
 * Brief description of the class/method.
 * <p>
 * Additional details if needed.
 *
 * @param config Description of parameter
 * @return Description of return value
 * @since 1.0.0
 * @author HuangChengwei
 */
```

### Type Annotations

Use JSpecify annotations for null-safety:
```java
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public @NonNull String process(@Nullable String input) { ... }
```

### Entity Classes (JPA)

Entities use **manual getters/setters** (no Lombok `@Data` in core tests):

```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    private String email;
    
    // Explicit getters/setters (required for reflection-based DSL)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... other getters/setters
    
    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
```

### Error Handling

- Use **RuntimeException** hierarchy (no checked exceptions)
- Create specific exception types when meaningful
- Include context in exception messages:
```java
throw new IllegalArgumentException("Entity must not be null");
```

### Logging

Use SLF4J with parameterized logging:
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger log = LoggerFactory.getLogger(MyClass.class);

// GOOD - parameterized logging
log.info("Inserted {} records", count);
log.debug("Query: {} with params: {}", sql, params);

// BAD - string concatenation
log.info("Inserted " + count + " records");
```

---

## Test Patterns

### Integration Test Structure

```java
@DisplayName("CRUD Operations Integration Tests")
public class CrudOperationsIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(CrudOperationsIntegrationTest.class);

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single employee")
    void shouldInsertSingleEmployee(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(100L, "Test User", "test@example.com");
        
        // When
        config.getUpdateExecutor().insert(employee, Employee.class);
        
        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(100L)
                .getList();
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("Test User");
    }
}
```

### Test Naming
- Test class: `{Feature}IntegrationTest` or `{Feature}Test`
- Test method: `should{Action}{ExpectedResult}` or `test{Scenario}`

### Test Dependencies
```xml
<!-- AssertJ for fluent assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers for database testing -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId>
    <version>2.0.3</version>
    <scope>test</scope>
</dependency>
```

### Test Utilities
```java
// Entity factories in test fixtures
private Employee createTestEmployee(Long id, String name, String email) {
    Employee employee = new Employee();
    employee.setId(id);
    employee.setName(name);
    employee.setEmail(email);
    employee.setSalary(50000.0);
    employee.setActive(true);
    employee.setStatus(EmployeeStatus.ACTIVE);
    return employee;
}
```

---

## Module Structure

```
nextentity/
├── nextentity-core/          # Core DSL
│   ├── src/main/java/
│   │   └── io/github/nextentity/
│   │       ├── api/          # Public query API
│   │       ├── core/         # Internal implementation
│   │       └── meta/         # Metamodel classes
│   └── src/test/java/
│       └── io/github/nextentity/
│           ├── jdbc/          # JDBC implementation tests
│           ├── jpa/          # JPA implementation tests
│           └── integration/  # Cross-implementation tests
│
└── nextentity-spring/        # Spring integration
    └── src/test/java/
        └── io/github/nextentity/spring/
            └── integration/  # Spring-specific tests
```

---

## Key APIs

### Query DSL Pattern
```java
// Create query from repository
List<User> users = userRepository
    .where(User::getId).eq(1)
    .where(User::getStatus).ne(Status.INACTIVE)
    .orderBy(User::getCreatedAt).desc()
    .getList();

// Fetch with joins
List<Article> articles = articleRepository
    .fetch(Article::getAuthor)
    .where(Article::getTitle).like("%example%")
    .getList();
```

### CRUD Operations
```java
// Insert
repository.insert(entity, EntityClass.class);

// Batch insert
repository.insertAll(entities, EntityClass.class);

// Update
repository.update(entity, EntityClass.class);

// Delete
repository.delete(entity, EntityClass.class);
```

---

## Common Patterns

### Working with Enums
```java
public enum EmployeeStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

// In queries
.where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
```

### Handling Nulls
```java
// Null-safe equality
.where(User::getEmail).eqIfNotNull(searchEmail)

// Null-safe comparison
.where(User::getSalary).gtIfNotNull(minSalary)
```

### Aggregation
```java
// Select with aggregation
Tuple result = userRepository
    .select(
        get(User::getDepartmentId).count(),
        get(User::getSalary).avg()
    )
    .groupBy(User::getDepartmentId)
    .requireSingle();
```

---

## Working with Libraries

When working with Spring, JPA, or other frameworks:

1. **Use Context7 MCP** for up-to-date documentation:
   - Call `resolve-library-id` first to get the library ID
   - Then call `query-docs` with specific questions

2. **Check existing patterns** in `src/test/java` before implementing

---

## Coverage Requirements

- **Minimum coverage**: 65% (JaCoCo rule)
- Run `mvn verify` to check coverage
- Add tests for edge cases and error conditions

---

## Gotchas

1. **Reflection-based DSL**: Entity getters/setters must follow naming convention (`getXxx`, `setXxx`)
2. **Testcontainers**: Integration tests require Docker running
3. **Module system**: Core uses Java modules (`module-info.java`)
4. **No Lombok in core**: Use explicit getters/setters for reflection compatibility
5. **Parameterized tests**: Most integration tests run against MySQL AND PostgreSQL via Testcontainers

---

*Last updated: 2026-03-24*
