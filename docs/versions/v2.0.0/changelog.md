# v2.0.0 变更记录

_从 v1.2.1 到 v2.0.0 的所有变更。_

## Breaking Changes

- 重构项目结构为 nextentity-core / nextentity-spring 多模块 `7aad8d9`
- EntityAttribute → EntityPath `c6834d9`
- 提取 SelectStep 接口，Select → QueryBuilder `7d66c87`
- selectExpr → select, groupByExpr → groupBy `bc403f3` `2f37730`
- slice → window `a7ea671`
- 移除 Page/Pageable，用 Collector.exists(offset) 替代 `8b7ab8d`
- 合并异常体系为 NextEntityException `73966c4`
- 使用标准 JPA LockModeType 替代自定义枚举 `72846ae`
- updateNonNullColumn → patch `427ead4`
- update API 返回值改为 void `edd18b8`
- Path API 结构重组 `35db13f`
- Expression API 添加泛型参数 `4759522`
- SimpleExpression 默认方法改为抽象方法 `aa8dd8c`

## Features

- feat: Collector 流式 API + 条件批量更新/删除 `755c14b` `64da99b`
- feat: Entity/Persistable 接口和 PathRef 增强 `6d544b6`
- feat: PersistableRepository（基于 ID 的查询方法）`41bb96e` `4c8be4c`
- feat: Spring Boot 自动配置 `deaf14f`
- feat: 支持嵌套路径查询 `4d01fb8`
- feat: Slice/Window 接口添加 map 和 to 转换方法 `994cf6d`
- feat: 添加废弃方法以兼容旧版本 API `24ddb7d`
- feat: JpaTransactionTemplate 和 Spring 集成测试上下文 `888dcf0`
- feat: nextentity-examples 模块 `863b6cf`

## Bug Fixes

- fix: 修正 NOT 谓词操作实现 `423a667`
- fix: 修正 HAVING 子句 SQL 生成顺序 `98787b4`
- fix: 解决 detached entity 删除问题 `99af066`
- fix: 改进 JPA update/delete 悲观锁支持 `f43b275`
- fix: 改进 managed entity 处理和表名修正 `0a0ded0`
- fix: 启用之前禁用的测试并添加 @EntityAttribute 支持 `3e2687e`

## Refactoring

- refactor: 简化 AbstractRepository API `454acdc`
- refactor: 简化 API 接口设计 `fb12d56`
- refactor: 重命名 select/groupBy 方法 `2f37730`
- refactor: 提取 SelectStep 接口 `7d66c87`
- refactor: 替换匿名 Pageable 实现 `0dc0975`
- refactor: 移除 Page/Pageable 类 `8b7ab8d`
- refactor: 重命名 slice → window `a7ea671`
- refactor: 简化 Collector API `64da99b`
- refactor: 更新示例使用 AbstractRepository `fed63d4`
- refactor: 使用查询 API 替代 Java streams 进行聚合 `69e4958`
- refactor: updateWhereStep/deleteWhereStep 移至 UpdateExecutor `c643378`

## Tests

- test: Spring Boot 集成测试基础设施 `55784cb` `355f1f3`
- test: 核心 API 全面集成测试 `32186ee` `6fd8178` `74725ef`
- test: 谓词链、投影、算术操作测试 `b69afee` `8cd89d2` `b61c9be`
- test: Pages/Tuples/Updaters 集成测试 `7341816`
- test: API 默认方法测试 `5a62781` `0d2cd06` `375dfa3`
- test: selectDistinct 测试 `1146498` `29b9f05`
- test: 嵌套路径集成测试 `d1fac1b`
- test: nextentity-examples 集成测试 `1a2c1c7` `4b01a01`

## Documentation

- docs: JavaDoc 文档 `3a73b9c`
- docs: Javadoc 转 Markdown Documentation Comments (JEP 467) `9e7843a`
- docs: API 文档翻译为中文 `f09ce6c`
- docs: Javadoc 注释翻译为中文 `bc315b3`
- docs: 文档重构和示例对齐 `4bce11e` `c8da426`
- docs: 聚合示例更新 `5eb3aba` `6d49d20`

## Build

- build: 升级 Java 到 25，统一 pom 配置 `404618c`
