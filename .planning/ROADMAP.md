# NextEntity Roadmap

**Core Value:** 为 Java 开发者提供编译时类型安全的 SQL 查询构建能力，避免字符串拼接 SQL 的错误，同时保持代码的简洁性和可读性

**Version:** 1.0
**Created:** 2026-03-22
**Granularity:** Fine

## Phases

- [ ] **Phase 1: Foundation & Core Types** - Establish basic query building and type safety foundation
- [ ] **Phase 2: Database Dialect Support** - Implement multi-database compatibility with SQL generation
- [ ] **Phase 3: Repository Abstraction** - Build CRUD operations and data access layer
- [ ] **Phase 4: Parameter Binding & Security** - Implement parameterized queries and SQL injection prevention
- [ ] **Phase 5: Aggregation & Pagination** - Add aggregate functions and pagination capabilities
- [ ] **Phase 6: Dynamic Query Building** - Enable dynamic condition queries
- [ ] **Phase 7: Spring Integration** - Provide Spring Boot auto-configuration support
- [ ] **Phase 8: Type Safety Validation** - Complete compile-time type safety validation

## Phase Details

### Phase 1: Foundation & Core Types
**Goal**: Users can build basic type-safe SQL queries with SELECT/FROM/WHERE clauses
**Depends on**: Nothing (first phase)
**Requirements**: CORE-01, CORE-02, CORE-03, TYPE-01, TYPE-02, TYPE-03
**Success Criteria** (what must be TRUE):
  1. User can construct SELECT queries with FROM clause using type-safe expressions
  2. User can apply WHERE conditions using method references (e.g., User::getId)
  3. User can apply ORDER BY clauses with type-safe property references
  4. User can apply GROUP BY clauses with type-safe property references
  5. Entity metamodel provides table and property metadata for type safety
**Plans**: TBD

### Phase 2: Database Dialect Support
**Goal**: Application works consistently across multiple database platforms
**Depends on**: Phase 1
**Requirements**: DB-01, DB-02, DB-03, DB-04
**Success Criteria** (what must be TRUE):
  1. User can connect to MySQL database and execute queries successfully
  2. User can connect to PostgreSQL database and execute queries successfully
  3. User can connect to SQL Server database and execute queries successfully
  4. Framework automatically handles database-specific identifier quoting
**Plans**: TBD

### Phase 3: Repository Abstraction
**Goal**: Users can perform CRUD operations through a high-level Repository interface
**Depends on**: Phase 1, Phase 2
**Requirements**: REPO-01, REPO-02, REPO-03, REPO-04, REPO-05, REPO-06
**Success Criteria** (what must be TRUE):
  1. User can insert entities through Repository interface
  2. User can update entities through Repository interface
  3. User can delete entities through Repository interface
  4. User can query entities with basic selection through Repository interface
  5. User can query entities by ID through Repository interface
  6. User can perform paginated queries through Repository interface
**Plans**: TBD

### Phase 4: Parameter Binding & Security
**Goal**: Users can build secure parameterized queries that prevent SQL injection
**Depends on**: Phase 1, Phase 2
**Requirements**: CORE-06
**Success Criteria** (what must be TRUE):
  1. User can bind parameters to queries safely without SQL injection risk
  2. Parameters are properly escaped and validated before query execution
  3. Dynamic values from user input are handled securely
**Plans**: TBD

### Phase 5: Aggregation & Pagination
**Goal**: Users can perform complex queries with aggregation functions and pagination
**Depends on**: Phase 1, Phase 2, Phase 3
**Requirements**: CORE-04, CORE-05, REPO-06
**Success Criteria** (what must be TRUE):
  1. User can apply COUNT, SUM, AVG, MAX, MIN aggregate functions to queries
  2. User can use LIMIT/OFFSET for pagination in queries
  3. Repository interface supports paginated results with offset and limit
**Plans**: TBD

### Phase 6: Dynamic Query Building
**Goal**: Users can build conditional queries dynamically based on runtime parameters
**Depends on**: Phase 1, Phase 3
**Requirements**: REPO-07
**Success Criteria** (what must be TRUE):
  1. User can build conditional WHERE clauses based on runtime parameters
  2. Repository interface allows building queries with optional filters
  3. Query conditions can be added or skipped based on input values
**Plans**: TBD

### Phase 7: Spring Integration
**Goal**: Users can seamlessly integrate the framework with Spring Boot applications
**Depends on**: Phase 1, Phase 2, Phase 3
**Requirements**: SPR-01, SPR-02, SPR-03, SPR-04, SPR-05
**Success Criteria** (what must be TRUE):
  1. User can enable framework with @EnableNextEntity annotation
  2. User can configure framework through application.yml properties
  3. User can mark Repository interfaces with @Repository annotation
  4. @Transactional annotations work properly with NextEntity repositories
  5. Spring Boot auto-configuration sets up framework beans automatically
**Plans**: TBD

### Phase 8: Type Safety Validation
**Goal**: Framework provides complete compile-time type safety validation
**Depends on**: Phase 1
**Requirements**: TYPE-04
**Success Criteria** (what must be TRUE):
  1. Invalid query constructions fail at compile time rather than runtime
  2. Type mismatches in query conditions are caught at compile time
  3. Property references to non-existent fields are flagged at compile time
**Plans**: TBD

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Core Types | 0/5 | Not started | - |
| 2. Database Dialect Support | 0/4 | Not started | - |
| 3. Repository Abstraction | 0/6 | Not started | - |
| 4. Parameter Binding & Security | 0/3 | Not started | - |
| 5. Aggregation & Pagination | 0/3 | Not started | - |
| 6. Dynamic Query Building | 0/3 | Not started | - |
| 7. Spring Integration | 0/5 | Not started | - |
| 8. Type Safety Validation | 0/3 | Not started | - |