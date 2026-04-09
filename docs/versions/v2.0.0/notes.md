# v2.0.0 备注

## 技术决策

<!-- 记录重要的技术决策及其原因 -->

### Collector API 设计
- **决策**：引入 Collector 流式 API 替代原有分散的批量操作方法
- **原因**：原有 API 需要手动构建 Where 条件，代码不够流畅。Collector 支持链式调用，语义更清晰

### 异常体系统一
- **决策**：所有异常继承 NextEntityException
- **原因**：1.x 异常散落在各处，调用方难以统一捕获

### JPA LockModeType 标准化
- **决策**：移除自定义 LockModeType，改用标准 JPA LockModeType
- **原因**：避免和 JPA 标准冲突，减少用户的学习成本

### Spring Boot 自动配置
- **决策**：通过 @AutoConfiguration 自动检测 DataSource/EntityManager
- **原因**：降低用户接入成本，开箱即用

## 踩坑记录

<!-- 开发过程中遇到的问题和解决方案 -->

### detached entity 删除问题
- **问题**：JPA 删除 detached entity 时抛出异常
- **解决**：先 merge 再删除，参考 commit `99af066`

### HAVING 子句 SQL 生成顺序
- **问题**：HAVING 子句生成位置不对导致 SQL 语法错误
- **解决**：修正 SQL 生成顺序，参考 commit `98787b4`

## 待讨论

<!-- 未来版本需要讨论的事项 -->

- [ ] 是否需要提供 Maven 插件自动生成 Repository 代码？
- [ ] 是否支持 Kotlin 扩展？
- [ ] R2DBC 响应式后端是否在 roadmap 中？
