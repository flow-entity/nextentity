# v2.0.0 实现方案

## 架构概述

```
nextentity (parent pom)
├── nextentity-core     ← 核心 DSL 引擎
│   ├── api/            ← 公共接口层
│   ├── core/           ← 实现层
│   ├── jdbc/           ← JDBC 后端
│   ├── jpa/            ← JPA 后端
│   └── meta/           ← 元数据
├── nextentity-spring   ← Spring 集成层
│   ├── NextEntityFactory
│   ├── AbstractRepository
│   ├── PersistableRepository
│   └── NextEntityAutoConfiguration
└── nextentity-examples ← 示例 + 集成测试
```

## 关键设计决策

### 1. API 命名简化

**问题**：1.x 的 API 命名冗余（`selectExpr`/`groupByExpr`），学习成本高。

**方案**：将 `selectExpr`/`groupByExpr` 重命名为 `select`/`groupBy`，原 `select`（无参）重命名为更明确的名称。通过 `SelectStep` 接口拆分构建步骤，让每个阶段的方法名语义清晰。

### 2. Collector 流式 API

**问题**：1.x 的批量更新/删除需要手动构建 Where 条件，代码不够流畅。

**方案**：引入 `Collector` 接口，支持链式条件批量操作：
```java
collector(entity)
    .where(e -> e.status().eq(Status.INACTIVE))
    .update(e -> e.set(Entity::status, Status.ACTIVE))
```

### 3. Spring Boot 自动配置

**问题**：用户需要手动配置 DataSource 和 EntityManager 注入到 Repository。

**方案**：`NextEntityAutoConfiguration` 通过 `@AutoConfiguration` 自动检测 DataSource/EntityManager 并创建 `DefaultNextEntityFactory` Bean，用户只需在 application.yml 中配置即可。

### 4. Persistable 接口

**问题**：有 ID 的实体需要每次手动传入 ID 进行查询。

**方案**：引入 `Persistable<ID>` 接口，实体实现此接口后可直接通过 `PersistableRepository` 进行基于 ID 的查询，无需重复传入 ID。

### 5. 嵌套路径查询

**问题**：查询关联实体属性需要手动 JOIN，代码繁琐。

**方案**：支持通过 `EntityPath` 的嵌套访问（如 `employee.department().name()`）自动生成 JOIN 查询。

## 模块职责

### nextentity-core

- **api/**：定义所有公共接口（QueryBuilder、Expression、EntityPath 等）
- **core/expression/**：表达式树实现，将 Java lambda 编译为 SQL 表达式
- **core/meta/**：实体元数据解析（字段映射、类型转换）
- **jdbc/**：JDBC 直连后端，SQL 生成和执行
- **jpa/**：JPA/Hibernate 后端，委托给 EntityManager

### nextentity-spring

- **NextEntityFactory**：工厂接口，创建查询/更新执行器
- **AbstractRepository**：Repository 基类，封装常用 CRUD
- **PersistableRepository**：基于 ID 的 Repository
- **NextEntityAutoConfiguration**：Spring Boot 自动配置

### nextentity-examples

- 实体定义（Employee、Department、Category、Product）
- Repository 实现
- 集成测试（验证核心功能在真实数据库上的行为）
