# NextEntity 集成测试用例添加计划

## 项目现状分析

### 当前测试覆盖率
- **指令覆盖率**: 67.76% (目标: 80%+)
- **分支覆盖率**: 59.76%
- **行覆盖率**: 74.92%

### 现有集成测试文件 (16个)
| 文件名 | 测试范围 |
|--------|----------|
| AggregateFunctionsIntegrationTest | 聚合函数 (COUNT, SUM, AVG, MAX, MIN, GROUP BY) |
| BatchOperationsIntegrationTest | 批量操作 (insertAll, updateAll, deleteAll) |
| ComplexPredicateIntegrationTest | 复杂谓词 (AND, OR, NOT) |
| CrudOperationsIntegrationTest | CRUD 操作 |
| EdgeCasesIntegrationTest | 边缘情况 |
| ErrorHandlingIntegrationTest | 错误处理 |
| ExceptionPropagationIntegrationTest | 异常传播 |
| JoinOperationsIntegrationTest | 关联查询 |
| JpaSpecificFeaturesIntegrationTest | JPA 特性 |
| NumericOperationsIntegrationTest | 数值操作 |
| QueryBuilderIntegrationTest | 查询构建器 |
| QueryOperationsIntegrationTest | 查询操作 |
| SlicePaginationIntegrationTest | 分页 |
| StringOperationsIntegrationTest | 字符串操作 |
| SubqueryIntegrationTest | 子查询 |
| TransactionalOperationsIntegrationTest | 事务操作 |

### 测试基础设施
- **测试框架**: JUnit 5 + AssertJ
- **数据库**: Testcontainers (MySQL, PostgreSQL)
- **参数化测试**: `@ArgumentsSource(IntegrationTestProvider.class)`
- **测试实体**: Employee, Department, EmployeeStatus

---

## 待添加的集成测试

基于对代码的分析，以下是需要添加的测试类别：

### Phase 1: Select API 完整测试 (优先级: 高)

**文件**: `SelectApiIntegrationTest.java`

测试 `Select` 接口的所有方法变体：
- [ ] `select(Class<R> projectionType)` - 投影类型查询
- [ ] `select(Path... paths)` - 单/多路径查询 (2-10 个参数)
- [ ] `select(TypedExpression... expressions)` - 表达式查询
- [ ] `selectDistinct` 所有变体
- [ ] `select(Collection<Path>)` - 集合参数
- [ ] `select(List<TypedExpression>)` - 表达式列表

**估计用例数**: 25-30 个

---

### Phase 2: Fetch 关联查询测试 (优先级: 高)

**文件**: `FetchOperationsIntegrationTest.java`

测试关联实体加载：
- [ ] `fetch(List<PathExpression>)` - 指定关联属性加载
- [ ] 单级关联加载
- [ ] 多级关联加载
- [ ] 空列表处理
- [ ] 非实体属性 fetch 警告

**估计用例数**: 10-15 个

---

### Phase 3: Lock 锁定机制测试 (优先级: 高)

**文件**: `LockModeIntegrationTest.java`

测试锁定机制：
- [ ] `lock(LockModeType.PESSIMISTIC_READ)` - 悲观读锁
- [ ] `lock(LockModeType.PESSIMISTIC_WRITE)` - 悲观写锁
- [ ] `lock(LockModeType.OPTIMISTIC)` - 乐观锁
- [ ] `lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)` - 乐观锁强制递增
- [ ] 锁超时处理
- [ ] 死锁场景

**估计用例数**: 15-20 个

---

### Phase 4: Update API 完整测试 (优先级: 高)

**文件**: `UpdateApiIntegrationTest.java`

测试 `Update` 接口所有方法：
- [ ] `updateNonNullColumn(T entity)` - 非空列更新
- [ ] `patch(T entity)` - 部分字段更新
- [ ] `insert(Iterable<T>)` vs `insert(T)` 对比
- [ ] 更新返回值验证
- [ ] 批量更新性能测试

**估计用例数**: 15-20 个

---

### Phase 5: Having 子句测试 (优先级: 中)

**文件**: `HavingClauseIntegrationTest.java`

测试 HAVING 条件：
- [ ] `having(predicate)` 基本用法
- [ ] HAVING + GROUP BY 组合
- [ ] HAVING + 聚合函数
- [ ] 复杂 HAVING 条件

**估计用例数**: 10-15 个

---

### Phase 6: Stream 流式查询测试 (优先级: 中)

**文件**: `StreamQueryIntegrationTest.java`

测试流式查询：
- [ ] `stream()` 基本用法
- [ ] `stream()` 资源自动关闭
- [ ] 大数据量流式处理
- [ ] 流式处理与分页对比

**估计用例数**: 8-10 个

---

### Phase 7: Projection 投影测试 (优先级: 中)

**文件**: `ProjectionQueryIntegrationTest.java`

测试投影查询：
- [ ] DTO 投影
- [ ] Record 投影 (Java 17+)
- [ ] 接口投影
- [ ] 嵌套投影
- [ ] 投影与关联查询组合

**估计用例数**: 15-20 个

---

### Phase 8: Type Converter 测试 (优先级: 中)

**文件**: `TypeConverterIntegrationTest.java`

测试类型转换：
- [ ] Enum 类型转换
- [ ] LocalDateTime 转换
- [ ] Number 类型转换
- [ ] 自定义类型转换
- [ ] null 值处理

**估计用例数**: 10-15 个

---

### Phase 9: Distinct 完整测试 (优先级: 中)

**文件**: `DistinctOperationsIntegrationTest.java`

测试 DISTINCT 操作：
- [ ] 单字段 DISTINCT
- [ ] 多字段 DISTINCT
- [ ] DISTINCT + 聚合函数
- [ ] DISTINCT + ORDER BY
- [ ] DISTINCT 效率对比

**估计用例数**: 10-12 个

---

### Phase 10: 边缘场景增强测试 (优先级: 低)

**文件**: `EdgeCaseEnhancedIntegrationTest.java`

补充边缘场景：
- [ ] 空结果集处理
- [ ] null 参数处理
- [ ] 超长字符串处理
- [ ] 特殊字符处理 (SQL 注入防护)
- [ ] 超大数值处理
- [ ] 并发访问测试

**估计用例数**: 15-20 个

---

## 实施计划

### 阶段划分

| 阶段 | 测试文件 | 优先级 | 估计用例数 | 估计工作量 | 状态 |
|------|----------|--------|------------|------------|------|
| 1 | SelectApiIntegrationTest | 高 | 25-30 | 2h | ✅ 完成 (156用例) |
| 2 | FetchOperationsIntegrationTest | 高 | 10-15 | 1.5h | ✅ 完成 (72用例) |
| 3 | LockModeIntegrationTest | 高 | 15-20 | 2h | ✅ 完成 (80用例) |
| 4 | UpdateApiIntegrationTest | 高 | 15-20 | 1.5h | ✅ 完成 (78用例, 2个BUG) |
| 5 | HavingClauseIntegrationTest | 中 | 10-15 | 1h | ✅ 完成 (88用例, 全部@Disabled - 框架BUG) |
| 6 | StreamQueryIntegrationTest | 中 | 8-10 | 1h | ✅ 完成 (108用例) |
| 7 | ProjectionQueryIntegrationTest | 中 | 15-20 | 2h | 待开始 |
| 8 | TypeConverterIntegrationTest | 中 | 10-15 | 1h | 待开始 |
| 9 | DistinctOperationsIntegrationTest | 中 | 10-12 | 1h | 待开始 |
| 10 | EdgeCaseEnhancedIntegrationTest | 低 | 15-20 | 1.5h | 待开始 |

**总计**: 约 133-177 个测试用例，预计工作量 14.5 小时

### 依赖关系

```
Phase 1 (Select API) ──┐
                       ├──> Phase 5 (Having)
Phase 4 (Update API) ──┘

Phase 1 (Select API) ──> Phase 6 (Stream)
Phase 1 (Select API) ──> Phase 7 (Projection)
Phase 1 (Select API) ──> Phase 9 (Distinct)
```

---

## 测试原则

1. **测试目标**: 找出潜在漏洞和错误，而非单纯追求覆盖率
2. **失败测试处理**: 如果是业务 BUG，先用 `@Disabled` 注解，后期修复
3. **测试模式**: 遵循 Given-When-Then 结构
4. **参数化**: 使用 `@ArgumentsSource(IntegrationTestProvider.class)` 支持多数据库
5. **断言**: 使用 AssertJ 流式断言

---

## 验收标准

1. 集成测试覆盖率达到 **80% 以上**
2. 所有测试用例通过（或标记 `@Disabled` 并说明原因）
3. 测试代码遵循现有代码风格
4. 测试用例有清晰的文档注释

---

## 风险评估

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 测试环境不稳定 | 中 | 使用 Testcontainers 保证环境一致性 |
| 数据库差异 | 中 | 参数化测试覆盖 MySQL 和 PostgreSQL |
| 测试数据污染 | 低 | 每个测试前重置数据 |
| 测试执行时间过长 | 低 | 控制单次测试数据量 |

---

## 后续步骤

确认此计划后，将按阶段依次实施，每完成一个阶段运行测试验证。