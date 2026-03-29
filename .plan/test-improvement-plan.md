# NextEntity 测试用例改进计划

> 基于 2026-03-29 测试质量审查报告生成

## 概述

| 项目 | 当前状态 |
|------|----------|
| 测试类总数 | 64 个 |
| 总体评分 | 4.1/5.0 |
| 主要问题 | 边界条件覆盖不足、已知 Bug 待修复 |

---

## 改进优先级矩阵

| 优先级 | 类别 | 数量 | 预估工作量 |
|--------|------|------|------------|
| 🔴 P0 | 关键 Bug | 1 | 2-4 小时 |
| 🔴 P0 | 数值边界测试 | 2 | 1-2 小时 |
| 🟡 P1 | 命名规范增强 | 5 | 2-3 小时 |
| 🟡 P1 | 代码质量 | 3 | 1-2 小时 |
| 🟢 P2 | 性能边界测试 | 2 | 2-3 小时 |
| 🟢 P2 | 测试基础设施 | 2 | 3-4 小时 |

---

## P0: 高优先级改进项

### 1. 修复 JoinOperationsIntegrationTest Bug

**问题描述**：fetch 操作未能正确加载关联实体

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/integration/JoinOperationsIntegrationTest.java:54`

**具体步骤**：

1. 分析 `fetch()` 方法的实现逻辑
2. 检查关联实体加载的 SQL 生成是否正确
3. 验证 JPA/Hibernate 的关联映射配置
4. 编写修复代码并验证测试通过
5. 确保不影响其他关联查询功能

**验证标准**：
```java
// 修复后应通过的测试
@Test
void fetch_shouldLoadRelatedEntity() {
    List<Employee> employees = queryEmployees()
        .fetch(Employee::getDepartment)
        .getList();

    for (Employee emp : employees) {
        assertNotNull(emp.getDepartment());  // 当前此处失败
    }
}
```

**责任人**：需要深入理解 JPA Metamodel 的开发者

---

### 2. 为 NumberConverter 添加极值测试

**问题描述**：缺少对 `MAX_VALUE`、`MIN_VALUE` 等数值极值的转换测试

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/core/converter/NumberConverterTest.java`

**具体步骤**：

1. 在 `NumberConverterTest` 中添加新的 `@Nested` 类 `BoundaryValues`
2. 创建极值测试方法：
   ```java
   @Nested
   class BoundaryValues {

       @Test
       void convertIntegerMaxValue() {
           // 测试 Integer.MAX_VALUE 转换
       }

       @Test
       void convertIntegerMinValue() {
           // 测试 Integer.MIN_VALUE 转换
       }

       @Test
       void convertLongMaxValue() {
           // 测试 Long.MAX_VALUE 转换及可能的溢出处理
       }

       @Test
       void convertDoubleToInteger_overflow() {
           // 测试超出 Integer 范围的 Double 值
       }

       @Test
       void convertBigDecimal_maxPrecision() {
           // 测试高精度 BigDecimal 转换
       }
   }
   ```
3. 验证所有边界场景的预期行为
4. 文档化精度丢失的处理策略

**验证标准**：
- 极值转换结果符合预期（精确转换或预期溢出行为）
- 异常场景抛出正确的异常类型

---

### 3. 为数值运算表达式添加边界测试

**问题描述**：`NumberOperatorImplTest` 缺少数值运算的边界条件验证

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/core/expression/NumberOperatorImplTest.java`

**具体步骤**：

1. 添加 `@Nested` 类 `ArithmeticBoundary`
2. 创建测试方法：
   ```java
   @Nested
   class ArithmeticBoundary {

       @Test
       void add_withOverflowHandling() {
           // 测试 Integer.MAX_VALUE + 1 的处理
       }

       @Test
       void subtract_withUnderflowHandling() {
           // 测试 Integer.MIN_VALUE - 1 的处理
       }

       @Test
       void multiply_withLargeValues() {
           // 测试大数乘法
       }

       @Test
       void divide_byZero() {
           // 测试除零行为（应抛出异常或返回特定值）
       }

       @Test
       void modulo_withNegativeValues() {
           // 测试负数取模
       }
   }
   ```

---

## P1: 中优先级改进项

### 4. 添加 @DisplayName 注解增强可读性

**问题描述**：测试方法虽命名良好，但缺少 `@DisplayName` 注解

**涉及文件**（按模块分组）：

| 模块 | 文件 |
|------|------|
| core | `ExpressionBuilderImplTest.java`, `PredicateImplTest.java` |
| expression | `NumberOperatorImplTest.java`, `StringOperatorImplTest.java` |
| util | `ImmutableListTest.java`, `LazyTest.java` |

**具体步骤**：

1. 为每个测试类添加类级别的 `@DisplayName`：
   ```java
   @DisplayName("NumberOperatorImpl 数值运算操作测试")
   class NumberOperatorImplTest { ... }
   ```

2. 为每个 `@Nested` 类添加描述性 `@DisplayName`
3. 为关键测试方法添加 `@DisplayName` 说明测试意图

**示例**：
```java
@Nested
@DisplayName("加法运算")
class AddOperation {

    @Test
    @DisplayName("当两个正整数相加时，应返回正确结果")
    void add_positiveIntegers_returnsCorrectSum() { ... }

    @Test
    @DisplayName("当加法导致溢出时，应按定义策略处理")
    void add_overflowHandledGracefully() { ... }
}
```

---

### 5. 消除测试中的魔法数字

**问题描述**：`SimpleAttributeTest.java` 中存在硬编码 ordinal 值（3、5 等）

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/core/meta/SimpleAttributeTest.java`
- `nextentity-core/src/test/java/io/github/nextentity/core/reflect/schema/SimpleAttributeTest.java`

**具体步骤**：

1. 将魔法数字提取为常量：
   ```java
   private static final int EXPECTED_ORDINAL_FOR_ID = 0;
   private static final int EXPECTED_ORDINAL_FOR_NAME = 1;
   // 或使用枚举定义属性顺序
   ```

2. 或使用 `@ValueSource` 参数化：
   ```java
   @ParameterizedTest
   @ValueSource(ints = {0, 1, 2, 3})
   void getAttributeByOrdinal(int ordinal) {
       // 统一的验证逻辑
   }
   ```

3. 添加常量的文档注释说明其含义

---

### 6. 增加 null 参数验证测试

**问题描述**：多个测试类缺少对 null 输入的明确验证场景

**涉及文件**（需增强）：

| 文件 | 当前状态 |
|------|----------|
| `ExpressionBuilderImplTest.java` | 部分 null 测试 |
| `PathOperatorImplTest.java` | 需增加 null path 测试 |
| `StringOperatorImplTest.java` | 需增加 null 字符串测试 |

**具体步骤**：

1. 为每个操作类添加 `@Nested` 类 `NullInputHandling`
2. 创建统一的 null 测试模板：
   ```java
   @Nested
   @DisplayName("Null 输入处理")
   class NullInputHandling {

       @Test
       @DisplayName("当 path 为 null 时，应抛出 NullPointerException")
       void nullPath_throwsNullPointerException() {
           assertThatThrownBy(() -> operator.get(null))
               .isInstanceOf(NullPointerException.class);
       }

       @Test
       @DisplayName("当 value 为 null 时，isNull 方法应返回 true")
       void nullValue_isNullReturnsTrue() { ... }
   }
   ```

---

### 7. 优化测试数据初始化时机

**问题描述**：`ApplicationContexts` 中初始化逻辑过于复杂，职责不清晰

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/integration/config/IntegrationTestContext.java`
- `nextentity-core/src/test/java/io/github/nextentity/integration/config/ApplicationContexts.java`

**具体步骤**：

1. 分析当前的静态初始化块职责
2. 将数据库连接配置与 DDL 执行分离
3. 使用 JUnit 5 扩展机制替代静态初始化：
   ```java
   public class IntegrationTestExtension implements BeforeAllCallback, AfterAllCallback {
       @Override
       void beforeAll(ExtensionContext context) {
           // 初始化容器和上下文
       }

       @Override
       void afterAll(ExtensionContext context) {
           // 清理资源
       }
   }
   ```
4. 确保 `@BeforeEach` 的测试数据重置逻辑高效

---

### 8. 增加异常处理边界测试

**问题描述**：部分异常场景测试覆盖不完整

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/core/util/ExceptionsTest.java`
- `nextentity-core/src/test/java/io/github/nextentity/core/util/IteratorsTest.java`

**具体步骤**：

1. 在 `ExceptionsTest` 中增加更多异常包装场景
2. 在 `IteratorsTest` 中增加空迭代器和单元素迭代器的异常行为测试
3. 确保异常消息包含足够上下文信息

---

## P2: 低优先级改进项

### 9. 增加大数据集性能边界测试

**问题描述**：缺少对大数据量处理的性能验证

**涉及文件**（新增）：
- `nextentity-core/src/test/java/io/github/nextentity/integration/LargeDatasetIntegrationTest.java`

**具体步骤**：

1. 创建新的性能边界测试类
2. 定义测试场景：
   ```java
   @Nested
   class LargeDatasetScenarios {

       @Test
       void queryWithLargeResultSet() {
           // 测试 10000+ 条记录的查询
       }

       @Test
       void batchInsert_largeDataset() {
           // 测试批量插入 5000 条记录
       }

       @Test
       void pagination_withLargeOffset() {
           // 测试深分页（offset > 10000）
       }

       @Test
       void streamQuery_memoryEfficiency() {
           // 验证流式查询不占用过多内存
       }
   }
   ```

3. 定义性能基准阈值（如查询响应时间 < 500ms）

---

### 10. 扩展并发测试场景

**问题描述**：`LazyTest` 有基础并发测试，可扩展更多场景

**涉及文件**：
- `nextentity-core/src/test/java/io/github/nextentity/core/util/LazyTest.java`

**具体步骤**：

1. 在现有并发测试基础上添加：
   ```java
   @Nested
   @DisplayName("高并发场景")
   class HighConcurrencyScenarios {

       @Test
       void concurrentInitialization_withHighThreadCount() {
           // 100+ 线程并发访问
       }

       @Test
       void concurrentGet_withExceptionInInitialization() {
           // 初始化失败时的并发处理
       }

       @Test
       void concurrentReset() {
           // 并发重置场景
       }
   }
   ```

2. 使用 `ExecutorService` 和 `CountDownLatch` 进行精确控制
3. 验证线程安全性和性能

---

### 11. 统一测试实体定义

**问题描述**：部分测试类内联定义 `TestEntity`，造成重复

**涉及文件**（需检查）：
- 搜索所有内联定义的测试实体类

**具体步骤**：

1. 创建统一的测试实体支持类：
   ```java
   // nextentity-core/src/test/java/io/github/nextentity/test/TestEntities.java
   public final class TestEntities {

       public static final class SimpleTestEntity {
           private Long id;
           private String name;
           // ...
       }

       public static final class NestedTestEntity {
           private SimpleTestEntity nested;
           // ...
       }
   }
   ```

2. 将各测试类中的内联实体替换为统一引用
3. 确保测试实体覆盖所有必要属性类型

---

### 12. 使用 JUnit Tag 进行测试分类

**问题描述**：缺少测试分类，无法选择性运行特定类型测试

**涉及文件**：所有测试类

**具体步骤**：

1. 定义测试标签：
   ```java
   // nextentity-core/src/test/java/io/github/nextentity/test/TestTags.java
   public final class TestTags {
       public static final String UNIT = "unit";
       public static final String INTEGRATION = "integration";
       public static final String SLOW = "slow";
       public static final String DATABASE = "database";
   }
   ```

2. 为测试类添加标签：
   ```java
   @Tag(TestTags.UNIT)
   class NumberOperatorImplTest { ... }

   @Tag(TestTags.INTEGRATION)
   @Tag(TestTags.DATABASE)
   class QueryBuilderIntegrationTest { ... }

   @Tag(TestTags.SLOW)
   class LargeDatasetIntegrationTest { ... }
   ```

3. 配置 Maven Surefire 插件支持标签过滤：
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-surefire-plugin</artifactId>
       <configuration>
           <groups>unit</groups>  <!-- 默认只运行单元测试 -->
           <excludedGroups>slow</excludedGroups>
       </configuration>
   </plugin>
   ```

---

## 实施时间表

### 第一阶段（1 周）

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 修复 JoinOperationsIntegrationTest Bug | P0 | 待开始 |
| NumberConverter 极值测试 | P0 | 待开始 |
| NumberOperatorImpl 边界测试 | P0 | 待开始 |

### 第二阶段（2 周）

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 添加 @DisplayName 注解 | P1 | 待开始 |
| 消除魔法数字 | P1 | 待开始 |
| 增加 null 参数验证 | P1 | 待开始 |
| 优化测试数据初始化 | P1 | 待开始 |

### 第三阶段（3-4 周）

| 任务 | 优先级 | 状态 |
|------|--------|------|
| 大数据集性能测试 | P2 | 待开始 |
| 扩展并发测试 | P2 | 待开始 |
| 统一测试实体定义 | P2 | 待开始 |
| JUnit Tag 分类 | P2 | 待开始 |

---

## 验收标准

### P0 任务验收

- [ ] JoinOperationsIntegrationTest 所有测试通过
- [ ] NumberConverter 极值测试覆盖率 > 90%
- [ ] NumberOperatorImpl 边界场景完整覆盖

### P1 任务验收

- [ ] 所有核心测试类添加 @DisplayName
- [ ] 魔法数字数量减少 80%
- [ ] null 输入验证覆盖所有操作类

### P2 任务验收

- [ ] 性能边界测试定义基准阈值
- [ ] 并发测试覆盖 100+ 线程场景
- [ ] 测试实体统一管理
- [ ] Maven 支持按 Tag 运行测试

---

## 附录：测试覆盖度目标

| 模块 | 当前覆盖度 | 目标覆盖度 |
|------|------------|------------|
| core | ~85% | 90% |
| expression | ~80% | 85% |
| util | ~85% | 90% |
| converter | ~75% | 85% |
| meta | ~80% | 85% |
| integration | ~70% | 80% |

---

*文档生成日期：2026-03-29*
*最后更新：待实施后更新状态*