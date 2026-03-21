# Requirements: NextEntity - Java SQL DSL Framework

**Defined:** 2026-03-22
**Core Value:** 为 Java 开发者提供编译时类型安全的 SQL 查询构建能力，避免字符串拼接 SQL 的错误，同时保持代码的简洁性和可读性

## v1 Requirements

### 核心查询构建

- [ ] **CORE-01**: 用户可以使用类型安全的 SELECT/FROM/WHERE 子句构建查询
- [ ] **CORE-02**: 用户可以使用类型安全的 ORDER BY 子句
- [ ] **CORE-03**: 用户可以使用类型安全的 GROUP BY 子句
- [ ] **CORE-04**: 用户可以使用 LIMIT/OFFSET 进行分页
- [ ] **CORE-05**: 用户可以使用聚合函数（COUNT, SUM, AVG, MAX, MIN）
- [ ] **CORE-06**: 用户可以使用参数化查询防止 SQL 注入

### Repository 抽象

- [ ] **REPO-01**: 用户可以通过 Repository 接口执行 insert 操作
- [ ] **REPO-02**: 用户可以通过 Repository 接口执行 update 操作
- [ ] **REPO-03**: 用户可以通过 Repository 接口执行 delete 操作
- [ ] **REPO-04**: 用户可以通过 Repository 接口执行 select 查询
- [ ] **REPO-05**: 用户可以通过 Repository 按 ID 查询实体
- [ ] **REPO-06**: 用户可以通过 Repository 执行分页查询
- [ ] **REPO-07**: 用户可以通过 Repository 构建动态条件查询

### 类型安全系统

- [ ] **TYPE-01**: 用户可以使用方法引用（如 User::getId）进行类型安全的属性引用
- [ ] **TYPE-02**: 实体元模型（Metamodel）提供表名和属性元数据
- [ ] **TYPE-03**: 表达式系统提供类型安全的 eq/ne/gt/lt/in 等操作
- [ ] **TYPE-04**: 编译时验证查询类型正确性

### 多数据库支持

- [ ] **DB-01**: 支持 MySQL 方言（版本 8.0+）
- [ ] **DB-02**: 支持 PostgreSQL 方言（版本 12+）
- [ ] **DB-03**: 支持 SQL Server 方言（版本 2019+）
- [ ] **DB-04**: 自动处理 SQL 标识符引用（反引号/双引号/方括号）

### Spring 集成

- [ ] **SPR-01**: 提供 Spring Boot 自动配置
- [ ] **SPR-02**: 提供 @EnableNextEntity 注解启用框架
- [ ] **SPR-03**: 支持 @Repository 注解标记 Repository 接口
- [ ] **SPR-04**: 支持 @Transactional 事务管理
- [ ] **SPR-05**: 支持 application.yml 配置数据源

## v2 Requirements

### JOIN 操作

- **JOIN-01**: 支持 INNER JOIN 多表查询
- **JOIN-02**: 支持 LEFT JOIN 和 RIGHT JOIN
- **JOIN-03**: 支持 JOIN ON 条件构建

### 高级查询

- **ADV-01**: 支持子查询
- **ADV-02**: 支持 UNION/INTERSECT/EXCEPT 集合操作
- **ADV-03**: 支持 CASE WHEN 表达式

### 批量操作

- **BATCH-01**: 支持批量插入
- **BATCH-02**: 支持批量更新
- **BATCH-03**: 支持批量删除

## Out of Scope

| Feature | Reason |
|---------|--------|
| 完整 ORM 功能 | 专注于 SQL 构建，对象关系映射交由 Hibernate 处理 |
| 代码生成工具 | 初期使用手动定义实体，后期可考虑注解处理器 |
| 异步查询执行 | 第一版专注于同步 API |
| 实时 SQL 验证 IDE 插件 | 复杂性过高，defer 到成熟版本 |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| CORE-01 | Phase 1 | Pending |
| CORE-02 | Phase 1 | Pending |
| CORE-03 | Phase 1 | Pending |
| CORE-04 | Phase 5 | Pending |
| CORE-05 | Phase 5 | Pending |
| CORE-06 | Phase 4 | Pending |
| REPO-01 | Phase 3 | Pending |
| REPO-02 | Phase 3 | Pending |
| REPO-03 | Phase 3 | Pending |
| REPO-04 | Phase 3 | Pending |
| REPO-05 | Phase 3 | Pending |
| REPO-06 | Phase 5 | Pending |
| REPO-07 | Phase 6 | Pending |
| TYPE-01 | Phase 1 | Pending |
| TYPE-02 | Phase 1 | Pending |
| TYPE-03 | Phase 1 | Pending |
| TYPE-04 | Phase 8 | Pending |
| DB-01 | Phase 2 | Pending |
| DB-02 | Phase 2 | Pending |
| DB-03 | Phase 2 | Pending |
| DB-04 | Phase 2 | Pending |
| SPR-01 | Phase 7 | Pending |
| SPR-02 | Phase 7 | Pending |
| SPR-03 | Phase 7 | Pending |
| SPR-04 | Phase 7 | Pending |
| SPR-05 | Phase 7 | Pending |

**Coverage:**
- v1 requirements: 25 total
- Mapped to phases: 25
- Unmapped: 0 ✓

---
*Requirements defined: 2026-03-22*
*Last updated: 2026-03-22 after roadmap creation*