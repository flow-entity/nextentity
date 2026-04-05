# NextEntity 指南文档

本目录包含 NextEntity SQL DSL 框架的完整使用指南。

## 文档列表

### 入门指南

| 文档 | 描述 |
|------|------|
| [快速入门](getting-started.md) | 项目介绍、环境配置、基本使用 |
| [Spring Boot 集成](spring-integration.md) | Spring Boot 自动配置和依赖注入 |
| [JDBC/JPA 后端](jdbc-jpa-backends.md) | **重要**：后端功能差异对比 |

### ⚠️ 后端选择提醒

NextEntity 支持两种后端，功能有差异：

| 后端 | 适用场景 | 注意事项 |
|------|----------|----------|
| **JDBC** | 简单 CRUD、高性能 | 不支持懒加载、缓存等 JPA 特性 |
| **JPA** | 复杂关联、需要 ORM 特性 | 支持完整 JPA 功能 |

选择后端前请务必阅读 [JDBC/JPA 后端指南](jdbc-jpa-backends.md)。

### 核心功能

| 文档 | 描述 |
|------|------|
| [查询构建](query-building.md) | 条件运算符、排序、分页 |
| [CRUD 操作](crud-operations.md) | 插入、更新、删除、批量操作 |
| [投影](projections.md) | 字段选择、DTO 投影、Distinct |
| [聚合](aggregations.md) | Count、Sum、Avg、Max、Min |

### 进阶主题

| 文档 | 描述 |
|------|------|
| [关联查询](associations.md) | 实体关联、Fetch 操作、N+1 问题 |
| [Repository 模式](repository-pattern.md) | 最佳实践、自定义方法、事务处理 |

---

## 学习路径

按照以下顺序阅读，逐步掌握 NextEntity：

```
快速入门 → 后端选择 → Spring集成 → 查询构建 → CRUD → 投影 → 关联 → 聚合 → 最佳实践
```

| 步骤 | 文档 | 内容 |
|:----:|------|------|
| 1 | [快速入门](getting-started.md) | 项目介绍、环境配置 |
| 2 | [JDBC/JPA 后端](jdbc-jpa-backends.md) | ⚠️ 重要：后端功能差异 |
| 3 | [Spring Boot 集成](spring-integration.md) | Spring Boot 配置 |
| 4 | [查询构建](query-building.md) | 条件、排序、分页 |
| 5 | [CRUD 操作](crud-operations.md) | 增删改查、批量操作 |
| 6 | [投影](projections.md) | 字段选择、DTO 投影 |
| 7 | [关联查询](associations.md) | Fetch、N+1 问题 |
| 8 | [聚合](aggregations.md) | Count、Sum、Avg 等 |
| 9 | [Repository 模式](repository-pattern.md) | 最佳实践、自定义方法 |

每个文档末尾的"下一步"会引导您进入下一篇。

---

## 其他资源

- [示例代码](../../nextentity-examples) - 完整示例项目
- [README](../../README.md) - 项目概述

---

## 贡献

欢迎对文档提出改进建议，请提交 Issue 或 Pull Request。