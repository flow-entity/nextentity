# Phase 1: Foundation & Core Types - Validation Strategy

**Phase:** 01
**Slug:** foundation-core-types
**Created:** 2026-03-22

## Overview

This validation strategy ensures Phase 1 delivers type-safe SQL query building with SELECT/FROM/WHERE, ORDER BY, GROUP BY, and method reference-based property access.

## Requirements Coverage

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| CORE-01 | User can construct SELECT queries with FROM clause using type-safe expressions | unit | `mvn test -Dtest=WhereImplTest#where_WithValidPredicate_ShouldAddCondition` | ✅ |
| CORE-01 | User can apply WHERE conditions using method references | unit | `mvn test -Dtest=WhereImplTest#where_WithPath_ShouldReturnPathOperator` | ✅ |
| CORE-02 | User can apply ORDER BY clauses with type-safe property references | unit | `mvn test -Dtest=WhereImplTest#orderBy_WithPathCollection_ShouldReturnOrderOperator` | ✅ |
| CORE-03 | User can apply GROUP BY clauses with type-safe property references | unit | `mvn test -Dtest=WhereImplTest#groupBy_WithSingleExpression_ShouldAddGrouping` | ✅ |
| TYPE-01 | User can use method references for type-safe attribute reference | unit | `mvn test -Dtest=WhereImplTest#where_WithPath_ShouldReturnPathOperator` | ✅ |
| TYPE-02 | Entity metamodel provides table and property metadata | unit | `mvn test -Dtest=WhereImplTest#getQueryStructure_ShouldReturnStructure` | ✅ |
| TYPE-03 | Expression system provides type-safe eq/ne/gt/lt/in operations | unit | `mvn test -Dtest=WhereImplTest#where_WithNumberPath_ShouldReturnNumberOperator` | ✅ |

## Sampling Rate

- **Per task commit:** `mvn test -Dtest=WhereImplTest`
- **Per wave merge:** `mvn test`
- **Phase gate:** Full suite green before `/gsd:verify-work`

## Wave 0 Gaps (Test Files to Create)

- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/OrderByStepTest.java` — covers CORE-02, TYPE-02
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/GroupByStepTest.java` — covers CORE-03
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/SelectStepTest.java` — covers CORE-01
- [ ] `nextentity-core/src/test/java/io/github/nextentity/core/expression/ExpressionBuilderImplTest.java` — covers TYPE-03

## Success Criteria (from ROADMAP.md)

1. User can construct SELECT queries with FROM clause using type-safe expressions
2. User can apply WHERE conditions using method references (e.g., `User::getId`)
3. User can apply ORDER BY clauses with type-safe property references
4. User can apply GROUP BY clauses with type-safe property references
5. Entity metamodel provides table and property metadata for type safety

## Verification Checklist

Before marking Phase 1 complete:

- [ ] All 7 requirements have passing tests
- [ ] Wave 0 gap test files created and passing
- [ ] `mvn test` passes full suite
- [ ] Method references work for all property types (String, Number, Boolean)
- [ ] Fluent API chaining works: select → where → orderBy → groupBy

---
*Last updated: 2026-03-22*
