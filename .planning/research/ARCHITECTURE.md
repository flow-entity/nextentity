# Architecture Patterns

**Domain:** Java SQL DSL Framework
**Researched:** 2026-03-22

## Recommended Architecture

The architecture of Java SQL DSL frameworks like jOOQ typically follows a layered approach that emphasizes type safety and fluent API design:

```
┌─────────────────────────────────────────────────────────┐
│                    Fluent API Layer                     │
│  (Repository, Select, Update, Delete interfaces)       │
├─────────────────────────────────────────────────────────┤
│                   Query Builder Layer                   │
│  (WhereStep, OrderByStep, GroupByStep, etc.)           │
├─────────────────────────────────────────────────────────┤
│                 Expression Builder Layer                │
│  (ExpressionBuilder, NumberOperator, StringOperator)   │
├─────────────────────────────────────────────────────────┤
│                  Metamodel Layer                        │
│  (EntityRoot, Path, TypedExpression, etc.)             │
├─────────────────────────────────────────────────────────┤
│                  Execution Layer                        │
│  (QueryExecutor, UpdateExecutor, Connection handling)  │
└─────────────────────────────────────────────────────────┘
```

### Component Boundaries

| Component | Responsibility | Communicates With |
|-----------|---------------|-------------------|
| Repository Interface | Public API for database operations | Query Builder Layer, Metamodel |
| Query Builder | Fluent query construction | Expression Builder, Metamodel |
| Expression Builder | Type-safe expression construction | Metamodel, Execution Layer |
| Metamodel | Entity metadata management | Entity classes, Execution Layer |
| Execution Engine | SQL generation and execution | Database connections, Repository |
| Path/Expression Types | Type-safe property references | Entity classes, Expression Builder |

### Data Flow

Data flows through the system in a chain:

1. **Entity Definition** → Metamodel creates typed paths
2. **Method References** (e.g., `User::getId`) → Convert to typed expressions
3. **Fluent API Calls** → Build query structure in memory
4. **Expression Tree** → Serialize to SQL with parameter binding
5. **Execution Engine** → Execute against database connection
6. **Results** → Map back to entity objects

## Patterns to Follow

### Pattern 1: Fluent Builder Pattern
**What:** Chained method calls that return builder instances
**When:** Constructing complex queries in a readable manner
**Example:**
```java
userRepository.select()
    .fetch(u -> u.id, u -> u.name, u -> u.email)
    .where(u -> u.status).eq(UserStatus.ACTIVE)
    .orderBy(u -> u.createdDate).desc()
    .limit(10);
```

### Pattern 2: Type-Safe Expression System
**What:** Generic type parameters ensuring compile-time safety
**When:** Building expressions that reference entity properties
**Example:**
```java
// Type-safe property access
Path<User, Long> idPath = User::getId;
NumberPath<User, Long> idExpr = entityRoot.number(idPath);

// Type-safe operations
BaseWhereStep<User, User> query = select.where(u -> u.age).ge(18);
```

### Pattern 3: Metamodel Abstraction
**What:** Centralized metadata about entities and their relationships
**When:** Managing entity schema information for type safety
**Example:**
```java
public interface EntityRoot<T> {
    <U> EntityPath<T, U> get(Path<T, U> path);
    StringPath<T> get(Path.StringRef<T> path);
    <U extends Number> NumberPath<T, U> get(Path.NumberRef<T, U> path);
}
```

## Anti-Patterns to Avoid

### Anti-Pattern 1: String-based Queries
**What:** Using raw strings for property names
**Why bad:** No compile-time checking, refactor-breaking
**Instead:** Use method references and type-safe paths

### Anti-Pattern 2: Leaking Internal Types
**What:** Exposing low-level query objects to users
**Why bad:** Breaks encapsulation, makes API complex
**Instead:** Provide high-level interfaces like Repository

### Anti-Pattern 3: Complex Expression Trees
**What:** Allowing arbitrarily complex nested expressions
**Why bad:** Performance issues, debugging difficulties
**Instead:** Provide well-defined expression operators

## Scalability Considerations

| Concern | At 100 users | At 10K users | At 1M users |
|---------|--------------|--------------|-------------|
| Query Planning | Simple caching | Prepared statement caching | Query plan optimization |
| Connection Pooling | Basic pooling | Advanced tuning | Sharding support |
| Memory Usage | Basic object reuse | Object pooling | Streaming results |
| SQL Generation | Compile-time validation | Runtime optimization | Distributed query planning |

## Sources

- Current NextEntity codebase structure
- Understanding of jOOQ and similar Java SQL DSL patterns
- Java generics and fluent API design patterns