# Research Summary

## Executive Summary

The NextEntity Java SQL DSL framework should be built with Java 17+, leveraging a layered architecture that emphasizes type safety and fluent API design. The research indicates that successful SQL DSL frameworks like jOOQ follow a multi-layered approach with distinct components for fluent API, query building, expression handling, metamodel management, and execution. The key differentiator for such frameworks is compile-time SQL validation through type-safe expression systems, which prevents runtime errors from malformed queries. However, critical pitfalls exist around database dialect handling, generic type resolution, and type casting that must be carefully addressed. The recommended approach prioritizes basic CRUD operations, type-safe query building, and parameter binding as foundational elements for an MVP.

Based on the combined research, the framework should focus on type safety as its core value proposition while maintaining compatibility with multiple database dialects. The architecture must carefully handle Java's type erasure challenges while providing a fluent API that's intuitive for developers. The recommended stack includes Java 17+, Maven for build management, JUnit 5 for testing, and integration with popular connection pools like HikariCP.

The primary risks involve database-specific SQL generation inconsistencies, particularly around identifier quoting, pagination syntax, and complex expression handling. These risks can be mitigated through comprehensive dialect-specific implementations and thorough testing across supported databases. The framework should avoid becoming a full ORM, focusing instead on SQL DSL capabilities that complement existing solutions like JPA.

## Key Findings

### From STACK.md
- Java 17+ with Maven 3.9+ provides the foundational technology stack
- Core dependencies include JDBC API 4.3+, database drivers for MySQL, PostgreSQL, SQL Server
- Essential libraries: jOOQ 3.18+ (for reference implementation), Apache Commons Lang, SLF4J, HikariCP
- Testing infrastructure requires Testcontainers 1.19+ for multi-database testing support

### From FEATURES.md
- Table stakes: Type-safe query building, fluent API, SQL dialect support, basic CRUD, transaction management
- Differentiators: Compile-time SQL validation, code generation, advanced SQL functions, reactive support
- Anti-features to avoid: Full ORM capabilities, automatic schema generation, heavy annotation processing
- MVP should prioritize: Basic CRUD, type-safe query building, fluent API, parameter binding, result mapping

### From ARCHITECTURE.md
- Five-layer architecture: Fluent API → Query Builder → Expression Builder → Metamodel → Execution
- Component boundaries emphasize separation between public API and internal implementation
- Key patterns: Fluent Builder, Type-Safe Expressions, Metamodel Abstraction
- Anti-patterns: String-based queries, leaking internal types, complex expression trees
- Scalability considerations for query planning, connection pooling, and memory usage

### From PITFALLS.md
- Critical pitfalls: Database dialect detection issues, generic type resolution failures, unsafe type casting
- Moderate pitfalls: Poor error messages, batch operation performance, transaction management confusion
- Prevention strategies: Robust dialect detection, Spring's ResolvableType utility, minimize unsafe casting
- Phase-specific warnings for type safety, database abstraction, performance, and transaction management

## Implications for Roadmap

### Suggested Phase Structure

1. **Foundation Phase** — Establish core architecture and basic operations
   - Implement basic CRUD operations with JDBC execution layer
   - Set up metamodel system for type-safe entity representations
   - Establish database dialect detection and basic SQL generation
   - *Features from FEATURES.md:* Basic CRUD, parameter binding, connection pooling

2. **Fluent API Phase** — Build type-safe query building capabilities
   - Implement fluent builder pattern for query construction
   - Create expression builder layer with type-safe operators
   - Develop comprehensive path/property expression system
   - *Features from FEATURES.md:* Type-safe query building, fluent API, result mapping

3. **Database Compatibility Phase** — Ensure multi-database support
   - Implement database-specific SQL generation for MySQL, PostgreSQL, SQL Server
   - Handle dialect-specific pagination, identifier quoting, and syntax variations
   - Create comprehensive test suite with Testcontainers
   - *Features from FEATURES.md:* SQL dialect support, join support, pagination

4. **Advanced Features Phase** — Add differentiating capabilities
   - Implement advanced SQL functions (window functions, CTEs, subqueries)
   - Add transaction management and batch operation optimizations
   - Consider reactive support for non-blocking operations
   - *Features from FEATURES.md:* Advanced SQL functions, reactive support, batch optimizations

### Research Flags
- Needs research: Database Compatibility Phase (due to critical dialect handling pitfalls)
- Standard patterns: Foundation Phase and Fluent API Phase (well-established in industry)
- Needs research: Advanced Features Phase (due to complexity of advanced SQL features)

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | HIGH | Well-established Java ecosystem technologies with clear version recommendations |
| Features | MEDIUM | Good understanding of table stakes, but differentiators need market validation |
| Architecture | HIGH | Clear layered pattern established by successful frameworks like jOOQ |
| Pitfalls | HIGH | Critical issues well-documented with specific prevention strategies |

Gaps identified: Need for market validation of differentiating features beyond type safety, and more detailed investigation of reactive support patterns in SQL DSL contexts.

## Sources

- STACK.md: Java platform evolution trends 2025, industry best practices for SQL DSL frameworks
- FEATURES.md: Java persistence framework documentation and community knowledge
- ARCHITECTURE.md: Understanding of jOOQ and similar Java SQL DSL patterns, Java generics and fluent API design
- PITFALLS.md: Analysis of NextEntity codebase, knowledge of SQL dialect differences, best practices for type-safe SQL frameworks