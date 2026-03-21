# Domain Pitfalls

**Domain:** Java SQL DSL Framework
**Researched:** 2026-03-22
**Confidence:** HIGH

## Critical Pitfalls

Mistakes that cause rewrites or major issues.

### Pitfall 1: Database Dialect Detection and Handling Issues
**What goes wrong:** Incomplete or unreliable database dialect detection leads to invalid SQL syntax for different databases.
**Why it happens:** SQL dialects differ significantly between databases (identifiers, pagination, function names, reserved keywords), making it hard to abstract correctly.
**Consequences:** Framework generates invalid SQL for specific databases, leading to runtime errors and poor user experience.
**Prevention:** Implement robust dialect detection based on multiple metadata sources (driver name, database name, version) rather than just one indicator. Use comprehensive SQL builders that properly implement each dialect's nuances.
**Detection:** Users report SQL syntax errors when switching databases or using advanced features.

### Pitfall 2: Generic Type Resolution Failure in Repository Classes
**What goes wrong:** Unable to determine entity type from generic parameters in repository classes, causing runtime configuration exceptions.
**Why it happens:** Java's type erasure makes it difficult to determine concrete types at runtime, especially when extending generic classes.
**Consequences:** Framework fails to initialize repositories with proper entity type, throwing RepositoryConfigurationException.
**Prevention:** Use Spring's ResolvableType utility or similar approaches to introspect generic types at class creation time. Provide alternative constructors that accept explicit Class objects when automatic resolution fails.
**Detection:** RepositoryConfigurationExceptions during application startup.

### Pitfall 3: Unsafe Type Casting Leading to Runtime Exceptions
**What goes wrong:** Overuse of unchecked casting in expression building leads to ClassCastException at runtime.
**Why it happens:** Complex generic hierarchies in SQL DSLs require frequent type manipulation, tempting developers to use unsafe casts.
**Consequences:** Runtime ClassCastException when users perform certain operations, breaking type safety promises.
**Prevention:** Minimize unsafe casting, provide clear warnings when unsafe methods are used, and implement proper type validation where possible.
**Detection:** ClassCastException thrown during query execution or building.

### Pitfall 4: Inconsistent SQL Identifier Quoting Across Databases
**What goes wrong:** Using wrong identifier quoting syntax for different databases (` vs [] vs "").
**Why it happens:** Each database has different conventions for identifier quoting and escaping, which are often overlooked during development.
**Consequences:** SQL syntax errors when tables/columns contain reserved words or special characters.
**Prevention:** Implement database-specific identifier quoting methods in each dialect-specific SQL builder.
**Detection:** SQL syntax errors with reserved words or special character identifiers.

### Pitfall 5: Incorrect Pagination Syntax Implementation
**What goes wrong:** Different databases have different pagination syntax (LIMIT vs OFFSET/FETCH FIRST vs ROW_NUMBER()), leading to invalid SQL.
**Why it happens:** Pagination is one of the most inconsistent features across SQL databases.
**Consequences:** Pagination queries fail with syntax errors on certain databases.
**Prevention:** Implement database-specific pagination logic in each SQL builder class (as seen in MySQL vs SQL Server vs PostgreSQL implementations).
**Detection:** Errors when using limit/offset clauses with pagination.

## Moderate Pitfalls

### Pitfall 6: Complex Expression Building with Poor Error Messages
**What goes wrong:** When users build complex queries with nested expressions, error messages are vague and unhelpful.
**Why it happens:** Expression trees are complex, and debugging complex fluent APIs is difficult for users.
**Prevention:** Provide clear, descriptive error messages with context about the failed expression or operation.
**Detection:** Users report difficulty debugging complex queries.

### Pitfall 7: Performance Issues with Batch Operations
**What goes wrong:** Poorly optimized batch operations lead to inefficient SQL generation and execution.
**Why it happens:** Different databases optimize batch operations differently, and naive batch implementations can create suboptimal SQL.
**Prevention:** Implement database-specific batch optimization strategies and proper parameter binding.
**Detection:** Slow performance during bulk operations like insertAll, updateAll.

### Pitfall 8: Transaction Management Confusion
**What goes wrong:** Mixing transaction management approaches (programmatic vs declarative) causes inconsistent behavior.
**Why it happens:** Supporting both JPA and JDBC backends requires careful transaction coordination.
**Prevention:** Clearly define transaction boundaries and ensure consistent behavior across backends.
**Detection:** TransactionRequiredException thrown unexpectedly or data inconsistency issues.

## Minor Pitfalls

### Pitfall 9: Reflection Performance Issues
**What goes wrong:** Heavy reliance on reflection for entity mapping causes performance degradation.
**Why it happens:** Entity metadata introspection and dynamic query building requires significant reflection usage.
**Prevention:** Cache reflection results and minimize repeated reflection calls.
**Detection:** Slower startup times or query building performance.

### Pitfall 10: Limited Subquery Support
**What goes wrong:** Advanced subquery features may not work consistently across different database dialects.
**Why it happens:** Subqueries have different capabilities and syntax variations across databases.
**Prevention:** Thoroughly test subquery functionality across all supported databases.
**Detection:** SQL syntax errors with complex subquery expressions.

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| Type Safety | Generic type resolution failures | Implement robust fallback mechanisms for type resolution |
| Database Abstraction | Dialect-specific SQL generation bugs | Develop comprehensive test suite covering all supported databases |
| Performance | Inefficient batch operations | Profile batch operations early and optimize for each database type |
| Transaction Management | Mixed transaction handling | Define clear transaction strategy and document expected behavior |

## Sources

- Analysis of NextEntity codebase including SqlDialectSelector, SQL builders, and repository classes
- Understanding of Java generics limitations and reflection usage patterns
- Knowledge of SQL dialect differences (MySQL, SQL Server, PostgreSQL)
- Best practices for type-safe SQL frameworks like jOOQ