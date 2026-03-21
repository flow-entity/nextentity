# NextEntity - Java SQL DSL Framework

## What This Is

NextEntity 是一个 Java SQL DSL（Domain Specific Language）框架，为开发者提供类型安全的流式 API 来构建和执行数据库查询。它支持 JDBC 和 JPA（Hibernate）两种后端，带有 Spring Boot 自动配置，可无缝集成到 Spring 项目或作为独立库使用。

## Core Value

为 Java 开发者提供编译时类型安全的 SQL 查询构建能力，避免字符串拼接 SQL 的错误，同时保持代码的简洁性和可读性。

## Requirements

### Validated

(None yet — ship to validate)

### Active

- [ ] 类型安全的 SQL DSL 构建器（select/from/where/orderBy/groupBy/limit）
- [ ] 支持 MySQL、PostgreSQL、SQL Server 等多种数据库方言
- [ ] Repository 接口抽象（CRUD 操作）
- [ ] Spring Boot 自动配置支持
- [ ] 独立 Jar 包使用方式（无需 Spring）
- [ ] 实体元模型（Metamodel）用于类型安全属性引用

### Out of Scope

- [完整 ORM 功能] — 专注于 SQL 构建，对象关系映射交由 Hibernate 处理
- [代码生成工具] — 初期使用手动定义实体，后期可考虑注解处理器
- [异步查询执行] — 第一版专注于同步 API

## Context

- **技术环境**: Java 17+，Maven 多模块项目
- **竞品参考**: jOOQ（类型安全 SQL）、MyBatis-Plus（Repository 抽象）、Spring Data JPA（Repository 模式）
- **架构决策**: 核心模块使用 Java 模块系统（Java 9+ modules）

## Constraints

- **Java 版本**: Java 17+ — 使用 record、pattern matching 等新特性
- **Spring 版本**: Spring Boot 3.2.0+ — 支持最新的自动配置机制
- **多模块结构**: core 模块（核心 DSL）和 spring 模块（Spring 集成）分离

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| 使用 Java 模块系统 | 更好的封装性和依赖管理 | — Pending |
| 支持 JDBC 和 JPA 双后端 | 满足不同项目需求，JDBC 轻量，JPA 功能丰富 | — Pending |
| Repository 接口设计 | 类似 Spring Data 的使用习惯，降低学习成本 | — Pending |

---
*Last updated: 2026-03-22 after initial questioning*
