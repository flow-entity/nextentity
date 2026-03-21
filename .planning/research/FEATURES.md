# Feature Landscape

**Domain:** Java SQL DSL Frameworks
**Researched:** 2026-03-22

## Table Stakes

Features users expect. Missing = product feels incomplete.

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Type-safe Query Building | Prevent runtime errors from malformed queries | High | Essential for modern Java frameworks |
| Fluent API | Intuitive, readable query construction | Medium | Method chaining for SELECT, FROM, WHERE, etc. |
| SQL Dialect Support | Compatibility with major databases | High | MySQL, PostgreSQL, Oracle, SQL Server |
| Basic CRUD Operations | Core insert, update, delete, select | Low | Standard for data access frameworks |
| Transaction Management | ACID compliance for data integrity | Medium | Required for production systems |
| Connection Pooling | Efficient database connection management | Medium | Integration with HikariCP, etc. |
| Parameter Binding | Protection against SQL injection | Low | Required for security |
| Result Mapping | Convert result sets to domain objects | Medium | Essential for object-relational mapping |
| Pagination Support | Limit and offset capabilities | Low | Needed for large datasets |
| Join Support | Inner, outer, cross join operations | High | Complex query capabilities |

## Differentiators

Features that set product apart. Not expected, but valued.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| Compile-time SQL Validation | Eliminate SQL syntax errors before runtime | Very High | jOOQ's key differentiator |
| Code Generation | Automatic entity/model generation from schema | High | jOOQ's schema reflection capability |
| Advanced SQL Functions | Window functions, CTEs, subqueries | High | Sophisticated query capabilities |
| Reactive Support | Non-blocking async operations | High | Modern application requirements |
| Custom SQL Support | Escape hatch for vendor-specific SQL | Medium | Flexibility for complex scenarios |
| Query Optimization Hints | Performance tuning capabilities | Medium | Advanced optimization features |
| Multi-tenancy Support | Database isolation strategies | High | Enterprise requirement |
| Auditing & Encryption | Security features built-in | High | Compliance requirements |
| Schema Migration Integration | Liquibase/Flyway integration | Medium | Full lifecycle management |
| Batch Operation Optimizations | Bulk insert/update/delete efficiency | Medium | Performance improvements |

## Anti-Features

Features to explicitly NOT build.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| Full ORM Capabilities | Overlaps with JPA/Hibernate | Focus on SQL DSL, delegate ORM to others |
| Automatic Schema Generation | Prone to production issues | Manual schema management preferred |
| Complex Session Management | Adds unnecessary complexity | Simple connection handling |
| Heavy Annotation Processing | Performance overhead | Minimal annotations |
| Dynamic Proxy Creation | Runtime complexity | Static compilation approach |
| Automatic Lazy Loading | Performance surprises | Explicit loading patterns |
| Complex Caching Strategies | Can cause staleness issues | Simple caching layer or none |

## Feature Dependencies

```
Type-safe Query Building → Fluent API (Fluent API depends on type safety)
Basic CRUD Operations → Connection Management (CRUD requires connections)
Join Support → Basic CRUD Operations (Advanced queries need basic operations)
Result Mapping → Basic CRUD Operations (Mapping builds on query execution)
Parameter Binding → Basic CRUD Operations (Security needed for all operations)
Transaction Management → Basic CRUD Operations (All operations need transactions)
Compile-time SQL Validation → Type-safe Query Building (Validation requires typing)
Code Generation → Type-safe Query Building (Generation produces typed classes)
Reactive Support → Connection Pooling (Async operations need async pools)
```

## MVP Recommendation

Prioritize:
1. Basic CRUD Operations - Foundation for everything else
2. Type-safe Query Building - Core differentiator
3. Fluent API - Developer experience
4. Parameter Binding - Security requirement
5. Result Mapping - Essential for usability
6. Connection Pooling - Performance requirement

Defer: [Advanced SQL Functions]: Complexity too high for MVP

## Sources

- Java persistence framework documentation and community knowledge
- NOTE: This research relies on training data and could benefit from official documentation verification for current versions