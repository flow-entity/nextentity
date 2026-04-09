# v2.0.0 需求

## 版本目标

NextEntity 2.0 是一次重大重构版本，目标是：
- 简化和统一 API 命名，提升开发者体验
- 引入流式 Collector API，支持条件批量操作
- 提供 Spring Boot 自动配置，降低集成门槛
- 升级到 Java 25 + Spring Boot 4.0.3
- 补全集成测试覆盖

## 目标环境

| 依赖 | 版本 |
|------|------|
| Java | 25 |
| Spring Boot | 4.0.3 |
| Maven Central | 通过 Sonatype Central Publishing 发布 |

## 功能需求

| # | 需求 | 优先级 | 状态 |
|---|------|--------|------|
| R1 | 重构项目结构为 nextentity-core / nextentity-spring 多模块 | P0 | ✅ 已完成 |
| R2 | 重命名 EntityAttribute → EntityPath | P0 | ✅ 已完成 |
| R3 | 提取 SelectStep 接口，重命名 Select → QueryBuilder | P0 | ✅ 已完成 |
| R4 | 简化 API：selectExpr → select, groupByExpr → groupBy | P0 | ✅ 已完成 |
| R5 | 重命名 slice → window | P0 | ✅ 已完成 |
| R6 | 引入 Collector 流式 API，支持条件批量更新/删除 | P0 | ✅ 已完成 |
| R7 | 添加 Entity/Persistable 接口和 PathRef 增强 | P1 | ✅ 已完成 |
| R8 | 实现 PersistableRepository（基于 ID 的查询方法） | P1 | ✅ 已完成 |
| R9 | 添加 Spring Boot 自动配置 | P1 | ✅ 已完成 |
| R10 | 支持嵌套路径查询（直接访问关联实体属性） | P1 | ✅ 已完成 |
| R11 | 为 Slice/Window 接口添加 map 和 to 转换方法 | P2 | ✅ 已完成 |
| R12 | 添加废弃方法以兼容旧版本 API | P2 | ✅ 已完成 |
| R13 | 补全集成测试覆盖（核心 API、示例模块） | P1 | ✅ 已完成 |
| R14 | API 文档翻译为中文 | P2 | ✅ 已完成 |
| R15 | 升级 Java 到 25，统一 pom 配置 | P0 | ✅ 已完成 |

## 破坏性变更

以下变更从 1.x 升级时需要修改代码：

1. **EntityAttribute → EntityPath**：注解名称变更
2. **Select → QueryBuilder**：类名变更
3. **selectExpr → select / groupByExpr → groupBy**：方法名简化
4. **slice → window**：概念重命名
5. **Page/Pageable 移除**：替换为 Collector.exists(offset)
6. **updateNonNullColumn → patch**：方法名变更
7. **异常体系统一**：所有异常继承 NextEntityException
8. **自定义 LockModeType 移除**：改用标准 JPA LockModeType
