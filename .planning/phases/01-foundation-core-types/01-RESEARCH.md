# Phase 1: Foundation & Core Types - Research

**Researched:** 2026-03-22
**Domain:** Java SQL DSL, Type-Safe Query Building, Expression System
**Confidence:** HIGH

## Summary

The foundation phase establishes the core type-safe query building capabilities of NextEntity. This includes implementing SELECT/FROM/WHERE clauses with method reference-based property access, ORDER BY and GROUP BY operations, and the underlying expression system that enables compile-time type safety. The framework already has substantial infrastructure in place, including expression builders, path operators, and query structure management.

The core architecture leverages Java method references (e.g., `User::getId`) for type-safe property access, which eliminates string-based field references prone to errors. The system uses a fluent API pattern where query components are chained together (select → fetch → where → orderBy → etc.).

**Primary recommendation:** Leverage the existing expression system and build upon the established architecture patterns to implement the remaining core query functionality.

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| CORE-01 | Users can use type-safe SELECT/FROM/WHERE clauses to build queries | Supported by existing expression system, Path<T,U> interface, and WhereImpl |
| CORE-02 | Users can use type-safe ORDER BY clauses | Supported by OrderByStep interface and OrderOperator implementation |
| CORE-03 | Users can use type-safe GROUP BY clauses | Supported by GroupByStep interface and existing groupBy implementations |
| TYPE-01 | Users can use method references (such as User::getId) for type-safe attribute reference | Supported by Path<T,U> interface and its specialized reference types |
| TYPE-02 | Entity metamodel provides table and property metadata for type safety | Supported by Metamodel interface and EntityType implementation |
| TYPE-03 | Expression system provides type-safe eq/ne/gt/lt/in operations | Supported by ExpressionBuilder interface and its specialized operators (NumberOperator, StringOperator, etc.) |
</phase_requirements>

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Java | 17+ | Runtime platform | Project baseline requirement |
| Maven | 3.6.0+ | Build system | Standard Java build tool |
| JUnit | 5.10.3 | Unit testing | Modern Java testing framework |
| Mockito | Latest | Mock objects for testing | Industry standard mocking framework |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| JPA API | Jakarta EE | Entity annotations | Entity definition and mapping |
| Lombok | 1.18.30 | Code generation | Reduce boilerplate in entities |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Method references | String property names | String approach loses compile-time type safety |
| Fluent API | Static query builders | Static approach limits composability and flexibility |

**Installation:**
```bash
mvn clean install
```

## Architecture Patterns

### Recommended Project Structure
```
nextentity-core/
├── src/main/java/io/github/nextentity/api/    # Core interfaces (Path, ExpressionBuilder, etc.)
├── src/main/java/io/github/nextentity/core/   # Implementation classes (WhereImpl, expressions, etc.)
├── src/main/java/io/github/nextentity/core/expression/   # Expression implementations
├── src/main/java/io/github/nextentity/core/meta/        # Metamodel implementations
└── src/test/java/io/github/nextentity/       # Unit and integration tests
```

### Pattern 1: Fluent Query Building Chain
**What:** A fluent API that allows chaining query components using method calls
**When to use:** Building SQL queries with type-safe property access
**Example:**
```java
// Source: nextentity-core/src/test/java/io/github/nextentity/core/WhereImplTest.java
repository.select(Employee.class)
    .where(Employee::getId).eq(1L)
    .orderBy(Employee::getName, Employee::getSalary)
    .getList();
```

### Pattern 2: Method Reference-Based Property Access
**What:** Using Java method references for type-safe property access instead of string literals
**When to use:** Any property reference in queries to maintain compile-time type safety
**Example:**
```java
// Source: nextentity-core/src/test/java/io/github/nextentity/core/WhereImplTest.java
var result = whereImpl.where(Employee::getName);  // Returns StringOperator for building conditions
var numericResult = whereImpl.where(Employee::getSalary);  // Returns NumberOperator
```

### Pattern 3: Expression Builder Hierarchy
**What:** Interface hierarchy for building typed expressions (ExpressionBuilder → NumberOperator → StringOperator)
**When to use:** Implementing type-specific operations (arithmetic for numbers, string operations for text)
**Example:**
```java
// Source: nextentity-core/src/main/java/io/github/nextentity/api/ExpressionBuilder.java
interface NumberOperator<T, U extends Number, B> extends ExpressionBuilder<T, U, B> {
    NumberOperator<T, U, B> add(U value);
    NumberOperator<T, U, B> subtract(U value);
    // ... other arithmetic operations
}
```

### Anti-Patterns to Avoid
- **String-based property access:** Avoid `"name"` strings for property references as they break type safety
- **Manual SQL concatenation:** Don't build SQL strings manually; use the expression system instead
- **Breaking the fluent chain:** Don't store intermediate states unnecessarily; leverage method chaining

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Type-safe property access | Manual reflection or string property names | Path<T,U> and method references | Complex type system with compile-time safety |
| Expression evaluation | Custom expression trees and evaluators | ExpressionBuilder hierarchy | Sophisticated type checking and validation logic |
| Query component chaining | Manual state management | Fluent API interfaces (WhereStep, OrderByStep, etc.) | Complex state transitions and validations |
| Method reference resolution | Custom method reference processing | Java's built-in method reference mechanism | JVM-optimized and well-tested |
| SQL generation | Direct SQL string building | QueryStructure and metamodel system | Database-specific dialects and parameter binding |

**Key insight:** The expression system is deceptively complex due to type safety requirements and the need to handle various data types with appropriate operators while maintaining the fluent API.

## Common Pitfalls

### Pitfall 1: Generic Type Complexity
**What goes wrong:** The generic type parameters become extremely complex (e.g., `PathOperator<T, U, B>`) which makes implementation difficult to understand and debug
**Why it happens:** Type safety requires tracking multiple generic parameters across method chains
**How to avoid:** Study existing implementations and use the same patterns; don't try to simplify the generic signatures
**Warning signs:** Compiler errors related to incompatible generic types, inability to chain methods properly

### Pitfall 2: Null Handling in Expression Building
**What goes wrong:** Not properly handling null values in expressions can lead to unexpected behavior
**Why it happens:** Different databases handle NULL differently in comparisons and operations
**How to avoid:** Use provided null-safe methods like `eqIfNotNull`, `in()` with proper null checks
**Warning signs:** Queries returning unexpected results when values are null

### Pitfall 3: Expression Tree Construction
**What goes wrong:** Incorrectly building expression trees can result in malformed SQL
**Why it happens:** The internal representation of queries is complex and requires careful construction
**How to avoid:** Follow existing patterns in the codebase, use provided builder methods, and test thoroughly
**Warning signs:** Runtime exceptions during query execution, incorrect SQL generated

## Code Examples

Verified patterns from official sources:

### Basic WHERE Clause with Method References
```java
// Source: nextentity-core/src/test/java/io/github/nextentity/core/WhereImplTest.java
// Test demonstrating method reference usage in WHERE clauses
@Test
void where_WithPath_ShouldReturnPathOperator() {
    var operator = whereImpl.where(Employee::getName);
    assertThat(operator).isNotNull(); // Returns StringOperator for building conditions
}

@Test
void where_WithNumberPath_ShouldReturnNumberOperator() {
    var operator = whereImpl.where(Employee::getSalary);
    assertThat(operator).isNotNull(); // Returns NumberOperator for building numeric conditions
}
```

### GROUP BY Clause Implementation
```java
// Source: nextentity-core/src/test/java/io/github/nextentity/core/WhereImplTest.java
// Example of using GROUP BY with method references
@Test
void groupBy_WithSingleExpression_ShouldAddGrouping() {
    var result = whereImpl.groupBy(Employee::getId);
    QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
    assertThat(structure.groupBy().asList()).hasSize(1);
}

@Test
void groupBy_WithPathCollection_ShouldAddAllGroupings() {
    List<Path<Employee, ?>> paths = Arrays.asList(
            Employee::getId,
            Employee::getStatus
    );
    var result = whereImpl.groupBy(paths);
    QueryStructure structure = ((WhereImpl<?, ?>) result).getQueryStructure();
    assertThat(structure.groupBy().asList()).hasSize(2);
}
```

### ORDER BY Clause Implementation
```java
// Source: nextentity-core/src/test/java/io/github/nextentity/core/WhereImplTest.java
// Example of using ORDER BY with method references
@Test
void orderBy_WithPathCollection_ShouldReturnOrderOperator() {
    List<Path<Employee, ? extends Comparable<?>>> paths = Arrays.asList(
            Employee::getName,
            Employee::getSalary
    );
    var operator = whereImpl.orderBy(paths);
    assertThat(operator).isNotNull(); // Returns OrderOperator for specifying sort order
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| String-based property access | Method reference-based access (e.g., User::getId) | From inception | Compile-time type safety |
| Manual SQL building | Fluent API with expression system | From inception | Reduced error potential |
| Runtime type checking | Compile-time type checking | From inception | Better developer experience |

**Deprecated/outdated:**
- Raw string field names: Replaced with method references for type safety
- Manual SQL concatenation: Replaced with expression builder system

## Open Questions

1. **Query Structure Management**
   - What we know: QueryStructure manages the internal representation of query components
   - What's unclear: Detailed interaction patterns between different query structure elements during complex operations
   - Recommendation: Review QueryStructure implementation to understand the full lifecycle

2. **Parameter Binding Mechanism**
   - What we know: The system uses parameterized queries to prevent SQL injection
   - What's unclear: Exact mechanism for binding method reference values to parameters
   - Recommendation: Examine the query executor implementation to understand parameterization

3. **Complex Expression Combinations**
   - What we know: Basic expressions work with the current system
   - What's unclear: How complex expressions with multiple operators interact
   - Recommendation: Look at more complex test cases to understand advanced scenarios

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | JUnit 5.10.3 with Mockito |
| Config file | pom.xml |
| Quick run command | `mvn test -Dtest=WhereImplTest` |
| Full suite command | `mvn test` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| CORE-01 | User can construct SELECT queries with FROM clause using type-safe expressions | unit | `mvn test -Dtest=WhereImplTest#where_WithValidPredicate_ShouldAddCondition` | ✅ |
| CORE-01 | User can apply WHERE conditions using method references (e.g., User::getId) | unit | `mvn test -Dtest=WhereImplTest#where_WithPath_ShouldReturnPathOperator` | ✅ |
| CORE-02 | User can apply ORDER BY clauses with type-safe property references | unit | `mvn test -Dtest=WhereImplTest#orderBy_WithPathCollection_ShouldReturnOrderOperator` | ✅ |
| CORE-03 | User can apply GROUP BY clauses with type-safe property references | unit | `mvn test -Dtest=WhereImplTest#groupBy_WithSingleExpression_ShouldAddGrouping` | ✅ |
| TYPE-01 | User can use method references (e.g., User::getId) for type-safe attribute reference | unit | `mvn test -Dtest=WhereImplTest#where_WithPath_ShouldReturnPathOperator` | ✅ |
| TYPE-02 | Entity metamodel provides table and property metadata for type safety | unit | `mvn test -Dtest=WhereImplTest#getQueryStructure_ShouldReturnStructure` | ✅ |
| TYPE-03 | Expression system provides type-safe eq/ne/gt/lt/in operations | unit | `mvn test -Dtest=WhereImplTest#where_WithNumberPath_ShouldReturnNumberOperator` | ✅ |

### Sampling Rate
- **Per task commit:** `mvn test -Dtest=WhereImplTest`
- **Per wave merge:** `mvn test`
- **Phase gate:** Full suite green before `/gsd:verify-work`

### Wave 0 Gaps
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java` — covers CORE-02, TYPE-02
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java` — covers CORE-03
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java` — covers CORE-01
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java` — covers TYPE-03

## Sources

### Primary (HIGH confidence)
- nextentity codebase - ExpressionBuilder interface and implementation
- nextentity codebase - Path interface and method reference patterns
- nextentity codebase - WhereImplTest - demonstrates usage patterns
- nextentity codebase - Employee entity - usage examples

### Secondary (MEDIUM confidence)
- JPA/Jakarta EE specifications - for entity annotations

### Tertiary (LOW confidence)
- General Java DSL patterns - for architectural understanding

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH - Based on existing project dependencies and implementations
- Architecture: HIGH - Clear patterns visible in existing codebase
- Pitfalls: HIGH - Observed directly from code and test patterns
- Expression system: HIGH - Well-documented in existing interfaces and tests

**Research date:** 2026-03-22
**Valid until:** 2026-04-22 (30 days for stable)