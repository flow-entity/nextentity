---
phase: 01-foundation-core-types
plan: 02
type: execute
wave: 1
depends_on: []
files_modified:
  - nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/expression/PathOperatorImplTest.java
autonomous: true
requirements:
  - TYPE-01
  - TYPE-02
  - TYPE-03

must_haves:
  truths:
    - User can use method references (such as User::getId) for type-safe attribute reference
    - Entity metamodel provides table and property metadata for type safety
    - Expression system provides type-safe eq/ne/gt/lt/in operations
  artifacts:
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java"
      provides: "Expression building operations testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java"
      provides: "String operation testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/expression/PathOperatorImplTest.java"
      provides: "Path operation testing"
  key_links:
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java"
      to: "ExpressionBuilder"
      via: "type-safe operations"
      pattern: "ExpressionBuilder.*eq.*ne.*gt.*lt.*in"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java"
      to: "StringOperator"
      via: "string-specific operations"
      pattern: "StringOperator.*like.*substring.*trim"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/expression/PathOperatorImplTest.java"
      to: "PathOperator"
      via: "path navigation operations"
      pattern: "PathOperator.*get.*"
---

<objective>
Create comprehensive test coverage for the expression building system and path operations. This plan focuses on testing the core expression system that enables type-safe operations (eq, ne, gt, lt, in, etc.) and method reference-based attribute access.

Purpose: Establish complete test coverage for expression building and path operations to ensure type safety
Output: Test files covering TYPE-01, TYPE-02, and TYPE-03 requirements
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

From nextentity-core/src/main/java/io/github/nextentity/api/StringOperator.java:
```java
public interface StringOperator<T, B> extends ExpressionBuilder<T, String, B> {
    B like(String pattern);
    B notLike(String pattern);
    B startsWith(String prefix);
    B endsWith(String suffix);
    B contains(String substring);
    B length();
    B substring(int beginIndex);
    B substring(int beginIndex, int endIndex);
    B trim();
    B upper();
    B lower();
    StringOperator<T, B> concat(String value);
}
```

From nextentity-core/src/main/java/io/github/nextentity/api/PathOperator.java:
```java
public interface PathOperator<T, U, B> extends ExpressionBuilder<T, U, B> {
    <V> PathOperator<T, V, B> get(Path<U, V> path);
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
}
```
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create Expression Builder Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java</files>
  <action>Enhance the existing ExpressionBuilderImplTest with comprehensive tests for all expression operations (eq, ne, gt, lt, in, isNull, etc.) across different data types. Test both value-based and collection-based operations, null handling, and edge cases. Follow existing test patterns in the codebase.</action>
  <verify>mvn test -Dtest=ExpressionBuilderImplTest</verify>
  <done>All expression operations are thoroughly tested for type safety and correctness</done>
</task>

<task type="auto">
  <name>Task 2: Create String Operator Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/expression/StringOperatorImplTest.java</files>
  <action>Create comprehensive unit tests for StringOperator functionality including string-specific operations (like, startsWith, endsWith, contains, etc.). Test with various string values, patterns, and edge cases. Follow the existing test patterns from WhereImplTest and other existing tests in the codebase.</action>
  <verify>mvn test -Dtest=StringOperatorImplTest</verify>
  <done>All string operation test cases pass with method reference support</done>
</task>

<task type="auto">
  <name>Task 3: Create Path Operator Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/expression/PathOperatorImplTest.java</files>
  <action>Create comprehensive unit tests for PathOperator functionality including nested property access using the 'get' method. Test with various path traversals and ensure proper type safety. Follow the existing test patterns and ensure proper query structure generation for path operations.</action>
  <verify>mvn test -Dtest=PathOperatorImplTest</verify>
  <done>All path operation test cases pass with method reference support</done>
</task>

</tasks>

<verification>
- mvn test -Dtest=ExpressionBuilderImplTest passes
- mvn test -Dtest=StringOperatorImplTest passes
- mvn test -Dtest=PathOperatorImplTest passes
- All TYPE-01, TYPE-02, TYPE-03 requirements covered by tests
</verification>

<success_criteria>
- Expression system provides type-safe operations (eq, ne, gt, lt, in, etc.)
- Users can use method references (such as User::getId) for type-safe attribute reference
- Entity metamodel provides table and property metadata for type safety
- String operations work correctly with method reference-based access
- Path operations support nested property access with type safety
</success_criteria>

<output>
After completion, create `.planning/phases/01-foundation-core-types/02-SUMMARY.md`
</output>