# v2.1.x 变更记录

_从 v2.0.0 到 v2.1.3 的所有变更。_

## 迁移指南

从 v2.0.0 升级到 v2.1.x 需注意：

1. **启用自动配置**：NextEntity 自动配置默认不再启用。
   需在 `application.yml` 中添加：
   ```yaml
   nextentity:
     enabled: true
   ```

2. **SqlLogger 多容器限制**：如在同一 JVM 运行多个 Spring 容器，
   SqlLogger 的静态配置会影响所有容器。建议在容器启动后立即设置配置。

## Features

- feat: 引入 EntityOperations 接口，合并查询与持久化操作 `ebcb920`
- feat: 引入 QueryContext 接口封装查询操作共享依赖 `196139c`
- feat: 可配置的分页自动排序行为 `f6be841`
- feat: 数据库方言驱动的插入策略和自增 ID 集成测试 `51d440c`
- feat: NextEntity 自动配置需显式启用（需设置 `nextentity.enabled=true`）`cc0b1a7`
- feat: 引入可配置的 JDBC/JPA 执行参数和 SQL 日志系统 `d9aac44`
- feat: 添加 SQL Server 方言锁模式支持和测试容器集成 `569691e`
- feat: 分页查询自动添加主键排序（可配置）`aa52562`
- feat: 实现 EntityTemplate 和条件更新删除流式 API `8cc04cd`

## Performance

- perf: 将整数字面量直接嵌入 SQL 以减少参数数量 `d3f1f25`

## Bug Fixes

- fix: COUNT 子查询移除不必要的 ORDER BY 和 OFFSET/LIMIT `7b1f750`
- fix: 修复测试边界条件和冗余代码 `cb3ab45`

## Refactoring

- refactor: QueryBuilder 重命名为 EntityQuery 并简化 JdbcUpdateExecutor `e9afb98`
- refactor: NextEntityFactory 重命名为 NextEntityContext `02dddfd`
- refactor: UpdateExecutor 使用 EntityContext 替代 Class<T> 参数 `b429e01`
- refactor: QueryContext 添加 Entity 泛型和实体类型访问方法 `4f02018`
- refactor: 移除 WhereImpl 中冗余的 lockModeType 字段 `60cae91`
- refactor: 优化 single 方法实现和改进废弃文档 `e0dfeaa`
- refactor: 改进泛型类型安全性和优化 examples 模块配置 `d3610e5`
- refactor: AbstractRepository 改用构造器注入 `c1b35bb`
- refactor: 统一版本号和代码改进 `14564b1`
- refactor: 清理 TypeCastUtil 空白注释行 `90d222f`
- refactor: 代码清理和注释翻译 `11721c8`
- refactor: 提取 AbstractBatchStatementBuilder 批量语句构建基类 `69e6426`
- refactor: 简化 DefaultSqlBuilder，委托 SQL 构建至各 StatementBuilder `5664cc5`
- refactor: 提取 ConditionalUpdate/DeleteStatementBuilder 条件语句构建器 `3ba15db`
- refactor: 将 appendLimitOffset 从接口默认方法改为抽象方法 `914c5aa`
- refactor: 统一方法命名和构造器参数顺序 `f68e9e9`
- refactor: 简化条件 SQL 构建器注释和实现 `f817b6b`
- refactor: 简化 API 包注释，消除重复示例 `3a4c251`
- refactor: 移除废弃 API 和实现类 `a60d399`

## Tests

- test: 添加测试后缓存清理以增强测试隔离性 `273be48`
- test: 修复测试边界条件和冗余代码 `cb3ab45`

## Documentation

- docs: API 文档使用 Java 25 Markdown Documentation Comments (JEP 467)
- docs: 文档全面翻译为中文
- docs: 新增 JDBC/JPA 后端功能对比文档

## Build

- build: 升级 Spring Boot 到 4.0.3

---

## 版本明细

### v2.1.3

- 实现 EntityTemplate 统一查询与持久化操作入口
- 条件更新删除流式 API 完善（UpdateSetStep/DeleteWhereStep）
- 简化 API 包注释，消除重复示例
- 移除 Collector.java 中废弃方法（getList/getFirst/limit/offset 等）
- 重构 JDBC/JPA 层：整合 PersistExecutor，移除冗余实现类
- 代码净减少 1364 行，结构更清晰

### v2.1.2

- 引入 EntityOperations 接口，提供查询与持久化操作的统一入口
- QueryBuilder 重命名为 EntityQuery
- NextEntityFactory 重命名为 EntityContext
- 修复版本标注和文档格式问题

### v2.1.1

- 可配置的分页自动排序行为
- SQL 日志系统和执行参数配置
- 性能优化：整数字面量嵌入 SQL

### v2.1.0

- NextEntity 自动配置需显式启用
- SQL Server 方言锁模式支持
- 分页查询自动添加主键排序