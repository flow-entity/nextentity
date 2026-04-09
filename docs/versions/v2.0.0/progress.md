# v2.0.0 进度跟踪

## 待办 (TODO)

_暂无待办项，当前所有需求已完成。发布前检查：_

- [ ] 最终 API 审查
- [ ] 更新 README.md 适配 2.0 API
- [ ] 编写 1.x → 2.0 迁移指南
- [ ] 发布到 Maven Central 并打 tag

## 进行中 (In Progress)

_暂无进行中的任务。_

## 已完成 (Done)

### 项目结构重构
- [x] 重构为 nextentity-core / nextentity-spring 多模块 `7aad8d9`
- [x] 升级 Java 到 25，统一 pom 配置 `404618c`

### API 重命名与简化
- [x] EntityAttribute → EntityPath `c6834d9`
- [x] 提取 SelectStep 接口，Select → QueryBuilder `7d66c87`
- [x] selectExpr → select, groupByExpr → groupBy `bc403f3` `2f37730`
- [x] slice → window `a7ea671`
- [x] 移除 Page/Pageable，用 Collector.exists(offset) 替代 `8b7ab8d`

### 新功能
- [x] Collector 流式 API + 条件批量操作 `755c14b` `64da99b`
- [x] Entity/Persistable 接口和 PathRef `6d544b6`
- [x] PersistableRepository `41bb96e` `4c8be4c`
- [x] Spring Boot 自动配置 `deaf14f`
- [x] 嵌套路径查询 `4d01fb8`
- [x] Slice/Window map 和 to 转换方法 `994cf6d`
- [x] 废弃方法兼容旧版本 API `24ddb7d`

### API 设计优化
- [x] 合并异常体系为 NextEntityException `73966c4`
- [x] 使用标准 JPA LockModeType `72846ae`
- [x] 简化 update API 返回 void `edd18b8`
- [x] 简化 AbstractRepository API `454acdc`
- [x] 简化 API 接口设计 `fb12d56` `aa8dd8c`
- [x] Path API 结构重组 `35db13f`
- [x] Expression API 添加泛型参数 `4759522`
- [x] updateWhereStep/deleteWhereStep 移至 UpdateExecutor `c643378`

### 测试
- [x] Spring Boot 集成测试基础设施 `55784cb` `355f1f3`
- [x] 核心 API 集成测试 `32186ee` `6fd8178` `74725ef`
- [x] 谓词链式集成测试 `b69afee`
- [x] 投影查询集成测试 `8cd89d2`
- [x] 算术操作集成测试 `b61c9be`
- [x] Pages/Tuples/Updaters 集成测试 `7341816`
- [x] API 默认方法集成测试 `5a62781` `0d2cd06` `375dfa3`
- [x] selectDistinct 测试 `1146498` `29b9f05`
- [x] 嵌套路径集成测试 `d1fac1b`
- [x] Examples 模块集成测试 `1a2c1c7` `4b01a01`

### Bug 修复
- [x] 修正 NOT 谓词操作实现 `423a667`
- [x] 修正 HAVING 子句 SQL 生成顺序 `98787b4`
- [x] 解决 detached entity 删除问题 `99af066`
- [x] 改进 JPA update/delete 悲观锁支持 `f43b275`
- [x] 改进 managed entity 处理 `0a0ded0`
- [x] 启用之前禁用的测试 `3e2687e`

### 文档
- [x] JavaDoc 文档 `3a73b9c`
- [x] Javadoc 转 Markdown Documentation Comments `9e7843a`
- [x] API 文档翻译为中文 `f09ce6c`
- [x] 文档重构和示例对齐 `4bce11e` `c8da426`
- [x] 聚合示例更新 `5eb3aba` `6d49d20`
