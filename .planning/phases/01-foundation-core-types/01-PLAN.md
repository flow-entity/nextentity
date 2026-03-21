---
phase: 01-foundation-core-types
plan: 01
type: execute
wave: 1
depends_on: []
files_modified:
  - nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java
autonomous: true
requirements:
  - CORE-01
  - CORE-02
  - CORE-03
  - TYPE-01
  - TYPE-02
  - TYPE-03

must_haves:
  truths:
    - User can construct SELECT queries with FROM clause using type-safe expressions
    - User can apply WHERE conditions using method references (e.g., User::getId)
    - User can apply ORDER BY clauses with type-safe property references
    - User can apply GROUP BY clauses with type-safe property references
    - Entity metamodel provides table and property metadata for type safety
  artifacts:
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java"
      provides: "ORDER BY clause testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java"
      provides: "GROUP BY clause testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java"
      provides: "SELECT clause testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java"
      provides: "Expression building testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java"
      provides: "String operation testing"
  key_links:
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java"
      to: "WhereImpl"
      via: "method reference-based ordering"
      pattern: "orderBy.*Employee::"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java"
      to: "WhereImpl"
      via: "method reference-based grouping"
      pattern: "groupBy.*Employee::"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java"
      to: "WhereImpl"
      via: "method reference-based selection"
      pattern: "select.*Employee::"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java"
      to: "ExpressionBuilder"
      via: "type-safe operations"
      pattern: "ExpressionBuilder.*"
---

<objective>
Create foundational test coverage for core type-safe query building functionality. This plan establishes comprehensive tests for SELECT, ORDER BY, GROUP BY clauses and expression building systems using method references, ensuring type safety across all query components.
</objective>

<execution_context>
@C:/Users/HuangHaHa/.claude/get-shit-done/workflows/execute-plan.md
@C:/Users/HuangHaHa/.claude/get-shit-done/templates/summary.md
</execution_context>

<context>
@.planning/PROJECT.md
@.planning/ROADMAP.md
@.planning/STATE.md

# Key interfaces extracted from existing codebase:
<interfaces>
From nextentity-core/src/main/java/io/github/nextentity/api/Path.java:
```java
public interface Path<T, U> {
    Class<U> getType();
    String getAttributeName();
}
```

From nextentity-core/src/main/java/io/github/nextentity/api/ExpressionBuilder.java:
```java
public interface ExpressionBuilder<T, U, B> {
    B eq(U value);
    B ne(U value);
    B gt(U value);
    B ge(U value);
    B lt(U value);
    B le(U value);
    B in(U... values);
    B in(Collection<U> values);
    B isNull();
    B isNotNull();
    B eqIfNotNull(U value);
    B neIfNotNull(U value);
}
```

From nextentity-core/src/main/java/io/github/nextentity/api/NumberOperator.java:
```java
public interface NumberOperator<T, U extends Number, B> extends ExpressionBuilder<T, U, B> {
    B add(U value);
    B subtract(U value);
    B multiply(U value);
    B divide(U value);
    B mod(U value);
    NumberOperator<T, U, B> add(TypedExpression<T, U> expression);
    NumberOperator<T, U, B> subtract(TypedExpression<T, U> expression);
    NumberOperator<T, U, B> multiply(TypedExpression<T, U> expression);
    NumberOperator<T, U, B> divide(TypedExpression<T, U> expression);
    NumberOperator<T, U, B> mod(TypedExpression<T, U> expression);
}
```

From nextentity-core/src/main/java/io/github/nextentity/core/WhereImpl.java:
```java
public class WhereImpl<T, R> implements Select<T>, WhereStep<T, R>, GroupByStep<T, R>, OrderByStep<T, R> {
    public <U extends Comparable<U>> ExpressionBuilder.PathOperator<T, U, ? extends OrderOperator<T, R>> orderBy(Path<T, U>... paths) { ... }
    public WhereImpl<T, R> groupBy(Path<T, ?>... paths) { ... }
    public <U> ExpressionBuilder.PathOperator<T, U, ? extends WhereStep<T, R>> where(Path<T, U> path) { ... }
}
```

From nextentity-core/src/main/java/io/github/nextentity/core/QueryBuilder.java:
```java
public class QueryBuilder<T> extends WhereImpl<T, T> implements Select<T>, FetchStep<T> {
    public QueryBuilder(Metamodel metamodel, QueryExecutor executor, Class<T> entityType) {
        this(QueryStructure.of(entityType), metamodel, executor);
    }
}
```

From nextentity-core/src/main/java/io/github/nextentity/core/expression/QueryStructure.java:
```java
public record QueryStructure(
    Selected select,
    From from,
    ExpressionNode where,
    ImmutableList<ExpressionNode> groupBy,
    ImmutableList<SortExpression> orderBy,
    ExpressionNode having,
    Integer offset,
    Integer limit,
    LockModeType lockType
) { ... }
```

From nextentity-core/src/main/java/io/github/nextentity/core/expression/From.java:
```java
public sealed interface From permits FromEntity, FromSubQuery {
}
```

From nextentity-core/src/main/java/io/github/nextentity/core/expression/FromEntity.java:
```java
public record FromEntity(Class<?> type) implements From {
}
```

From nextentity-core/src/test/java/io/github/nextentity/integration/entity/Employee.java:
```java
public class Employee {
    public Long getId() { ... }
    public String getName() { ... }
    public String getEmail() { ... }
    public Double getSalary() { ... }
    public Boolean getActive() { ... }
    public EmployeeStatus getStatus() { ... }
    public Long getDepartmentId() { ... }
}
```
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create ORDER BY Clause Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java</files>
  <action>Create comprehensive unit tests for ORDER BY clause functionality using method reference-based property access. Test sorting with various data types (String, Number, Boolean) and combinations of ascending/descending orders. Follow the existing test patterns from WhereImplTest and other existing tests in the codebase. Cover both single path and multiple path ordering scenarios.</action>
  <verify>mvn test -Dtest=OrderByStepTest</verify>
  <done>All ORDER BY test cases pass with method reference support for different data types</done>
</task>

<task type="auto">
  <name>Task 2: Create GROUP BY Clause Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java</files>
  <action>Create comprehensive unit tests for GROUP BY clause functionality using method reference-based property access. Test grouping with various data types and combinations of paths. Follow the existing test patterns from WhereImplTest and ensure proper query structure generation for GROUP BY operations. Include tests for single and multiple path grouping.</action>
  <verify>mvn test -Dtest=GroupByStepTest</verify>
  <done>All GROUP BY test cases pass with method reference support for different data types</done>
</task>

<task type="auto">
  <name>Task 3: Create SELECT Clause Test Suite with FROM clause testing</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java</files>
  <action>Create comprehensive unit tests for SELECT clause functionality using method reference-based property access. Test selecting specific columns, entity selection, and proper FROM clause handling. Follow the existing test patterns and ensure proper query structure generation for SELECT operations. Include tests for distinct selection and multiple column selection. Specifically test that FROM clause is properly constructed from the initial QueryBuilder creation with entity type. Test QueryStructure.from() returns correct FromEntity with proper type, verify QueryBuilder initializes QueryStructure.of(entityType) correctly to establish the FROM clause.</action>
  <verify>mvn test -Dtest=SelectStepTest</verify>
  <done>All SELECT test cases pass with method reference support for different data types and FROM clause properly tested</done>
</task>

</tasks>

<verification>
- mvn test -Dtest=OrderByStepTest passes
- mvn test -Dtest=GroupByStepTest passes
- mvn test -Dtest=SelectStepTest passes
- All CORE-01, CORE-02, CORE-03 requirements covered by tests
- All TYPE-01, TYPE-02, TYPE-03 requirements covered by tests
</verification>

<success_criteria>
- Users can build type-safe SQL queries with SELECT/FROM/WHERE/ORDER BY/GROUP BY clauses using method references
- Entity metamodel provides table and property metadata for type safety
- Expression system provides type-safe operations (eq, ne, gt, lt, in, etc.)
- All method references work correctly (e.g., Employee::getName, Employee::getSalary)
- FROM clause is correctly established when creating queries with QueryBuilder(entityType)
</success_criteria>

<output>
After completion, create `.planning/phases/01-foundation-core-types/01-SUMMARY.md`
</output>