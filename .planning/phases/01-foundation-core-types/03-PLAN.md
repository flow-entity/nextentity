---
phase: 01-foundation-core-types
plan: 03
type: execute
wave: 2
depends_on:
  - "01"
  - "02"
files_modified:
  - nextentity-core/src/test/java/io/github/nextentity/core/MetamodelTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/QueryStructureTest.java
  - nextentity-core/src/test/java/io/github/nextentity/core/EntityPathTest.java
autonomous: true
requirements:
  - CORE-01
  - TYPE-02
  - TYPE-03

must_haves:
  truths:
    - User can construct SELECT queries with FROM clause using type-safe expressions
    - Entity metamodel provides table and property metadata for type safety
    - Expression system provides type-safe eq/ne/gt/lt/in operations
  artifacts:
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/MetamodelTest.java"
      provides: "Metamodel testing for entity metadata"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/QueryStructureTest.java"
      provides: "Query structure testing"
    - path: "nextentity-core/src/test/java/io/github/nextentity/core/EntityPathTest.java"
      provides: "Entity path testing"
  key_links:
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/MetamodelTest.java"
      to: "Metamodel interface"
      via: "entity metadata retrieval"
      pattern: "metamodel.*entityType"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/QueryStructureTest.java"
      to: "QueryStructure"
      via: "query component management"
      pattern: "queryStructure.*from.*where.*orderBy"
    - from: "nextentity-core/src/test/java/io/github/nextentity/core/EntityPathTest.java"
      to: "EntityPath"
      via: "typed path expressions"
      pattern: "EntityPath.*method reference"
---

<objective>
Complete the foundation by testing core infrastructure components: metamodel for entity metadata, query structure for internal representation, and entity paths for type-safe property access. This plan ties together all components needed for type-safe query building.
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
From nextentity-core/src/main/java/io/github/nextentity/api/model/Metamodel.java:
```java
public interface Metamodel {
    <T> EntityType<T> entityType(Class<T> entityClass);
}
```

From nextentity-core/src/main/java/io/github/nextentity/api/model/EntityType.java:
```java
public interface EntityType<T> extends EntitySchema<T> {
    Class<T> getJavaType();
    String getTableName();
    Attribute<T, ?> getId();
    Attribute<T, ?> getVersion();
    List<Attribute<T, ?>> getAttributes();
}
```

From nextentity-core/src/main/java/io/github/nextentity/core/QueryStructure.java:
```java
public record QueryStructure(
    FromEntity from,
    ExpressionNode where,
    ImmutableList<ExpressionNode> groupBy,
    ImmutableList<SortExpression> orderBy,
    ExpressionNode having,
    Integer offset,
    Integer limit,
    LockModeType lockType
) { ... }
```

From nextentity-core/src/test/java/io/github/nextentity/integration/entity/Employee.java:
```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    private String email;
    private Double salary;
    private Boolean active;
    private EmployeeStatus status;
    private Long departmentId;
}
```
</interfaces>
</context>

<tasks>

<task type="auto">
  <name>Task 1: Create Metamodel Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/MetamodelTest.java</files>
  <action>Create comprehensive unit tests for the metamodel functionality that provides entity metadata. Test entity type retrieval, table name mapping, attribute extraction, and primary key identification. Use the Employee entity as the test subject and verify that the metamodel correctly extracts all required metadata for type-safe operations.</action>
  <verify>mvn test -Dtest=MetamodelTest</verify>
  <done>Metamodel correctly provides entity metadata including table names and attributes</done>
</task>

<task type="auto">
  <name>Task 2: Create Query Structure Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/QueryStructureTest.java</files>
  <action>Create comprehensive unit tests for the QueryStructure record that holds the internal representation of queries. Test the creation, modification, and access patterns for all query components (FROM, WHERE, ORDER BY, GROUP BY, etc.). Verify that query structure maintains integrity across different operations. Pay special attention to FROM clause handling - test QueryStructure.of(entityType) creates proper FromEntity with correct type information.</action>
  <verify>mvn test -Dtest=QueryStructureTest</verify>
  <done>QueryStructure properly manages all query components with immutable updates</done>
</task>

<task type="auto">
  <name>Task 3: Create Entity Path Test Suite</name>
  <files>nextentity-core/src/test/java/io/github/nextentity/core/EntityPathTest.java</files>
  <action>Create comprehensive unit tests for entity path functionality that enables type-safe property access. Test the creation and usage of typed path expressions from method references. Verify that paths correctly represent entity properties and maintain type safety throughout query building operations.</action>
  <verify>mvn test -Dtest=EntityPathTest</verify>
  <done>Entity paths provide type-safe property access from method references</done>
</task>

</tasks>

<verification>
- mvn test -Dtest=MetamodelTest passes
- mvn test -Dtest=QueryStructureTest passes
- mvn test -Dtest=EntityPathTest passes
- All CORE-01, TYPE-02, TYPE-03 requirements fully validated
</verification>

<success_criteria>
- Entity metamodel provides table and property metadata for type safety (TYPE-02)
- Users can construct SELECT queries with FROM clause using type-safe expressions (CORE-01)
- Expression system provides type-safe operations integrated with entity paths (TYPE-03)
- All core infrastructure components work together for type-safe query building
</success_criteria>

<output>
After completion, create `.planning/phases/01-foundation-core-types/03-SUMMARY.md`
</output>