# Phase 5-10: 中低优先级测试计划

## Phase 5: Having 子句测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/HavingClauseIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 5.1 | `shouldHavingWithCount` | HAVING + COUNT 过滤 |
| 5.2 | `shouldHavingWithSum` | HAVING + SUM 过滤 |
| 5.3 | `shouldHavingWithAvg` | HAVING + AVG 过滤 |
| 5.4 | `shouldHavingWithComparison` | HAVING + 比较操作 |
| 5.5 | `shouldHavingWithMultipleConditions` | HAVING + 多条件 |
| 5.6 | `shouldHavingWithGroupBy` | HAVING + GROUP BY 组合 |

---

## Phase 6: Stream 流式查询测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/StreamQueryIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 6.1 | `shouldStreamAllResults` | stream() 基本用法 |
| 6.2 | `shouldStreamWithWhere` | stream + WHERE 条件 |
| 6.3 | `shouldStreamWithLimit` | stream + LIMIT |
| 6.4 | `shouldProcessLargeDataset` | 大数据集流式处理 |
| 6.5 | `shouldStreamCloseResources` | 流关闭资源验证 |
| 6.6 | `shouldStreamWithMap` | stream + map 操作 |

### 代码模板

```java
@ParameterizedTest
@ArgumentsSource(IntegrationTestProvider.class)
@DisplayName("Should stream all results")
void shouldStreamAllResults(DbConfig config) {
    // Given
    // When
    try (var stream = config.queryEmployees().stream()) {
        long count = stream.count();
        // Then
        assertThat(count).isEqualTo(12);
    }
}
```

---

## Phase 7: Projection 投影测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/ProjectionQueryIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 7.1 | `shouldProjectToDto` | 投影到 DTO 类 |
| 7.2 | `shouldProjectToRecord` | 投影到 Record (Java 17+) |
| 7.3 | `shouldProjectToInterface` | 投影到接口 |
| 7.4 | `shouldProjectNestedDto` | 嵌套 DTO 投影 |
| 7.5 | `shouldProjectWithExpression` | 表达式投影 |
| 7.6 | `shouldProjectDistinctDto` | 去重投影 |
| 7.7 | `shouldProjectWithWhere` | 投影 + WHERE |
| 7.8 | `shouldProjectWithOrderBy` | 投影 + ORDER BY |

### 需要创建的 DTO 类

```java
// EmployeeDto.java
public class EmployeeDto {
    private Long id;
    private String name;
    private Double salary;

    // constructors, getters, setters
}

// EmployeeSummary.java
public record EmployeeSummary(Long id, String name) {}
```

---

## Phase 8: Type Converter 测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/TypeConverterIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 8.1 | `shouldConvertEnumToString` | Enum → String 转换 |
| 8.2 | `shouldConvertStringToEnum` | String → Enum 转换 |
| 8.3 | `shouldConvertLocalDateTime` | LocalDateTime 转换 |
| 8.4 | `shouldConvertNumberTypes` | Number 类型转换 |
| 8.5 | `shouldHandleNullConversion` | null 值转换 |
| 8.6 | `shouldConvertBoolean` | Boolean 转换 |

---

## Phase 9: Distinct 完整测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/DistinctOperationsIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 9.1 | `shouldDistinctSingleField` | 单字段 DISTINCT |
| 9.2 | `shouldDistinctMultipleFields` | 多字段 DISTINCT |
| 9.3 | `shouldDistinctWithCount` | DISTINCT + COUNT |
| 9.4 | `shouldDistinctWithOrderBy` | DISTINCT + ORDER BY |
| 9.5 | `shouldDistinctWithWhere` | DISTINCT + WHERE |
| 9.6 | `shouldDistinctWithGroupBy` | DISTINCT + GROUP BY |
| 9.7 | `shouldCountDistinct` | COUNT(DISTINCT ...) |
| 9.8 | `shouldDistinctEntity` | 实体 DISTINCT |

---

## Phase 10: 边缘场景增强测试

### 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/EdgeCaseEnhancedIntegrationTest.java`

### 测试用例

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 10.1 | `shouldHandleEmptyResultSet` | 空结果集处理 |
| 10.2 | `shouldHandleNullParameter` | null 参数处理 |
| 10.3 | `shouldHandleLongString` | 超长字符串 |
| 10.4 | `shouldHandleSpecialCharacters` | 特殊字符 (SQL 注入防护) |
| 10.5 | `shouldHandleLargeNumber` | 超大数值 |
| 10.6 | `shouldHandleNegativeValues` | 负数值 |
| 10.7 | `shouldHandleZeroValues` | 零值 |
| 10.8 | `shouldHandleUnicodeCharacters` | Unicode 字符 |
| 10.9 | `shouldHandleDateBoundary` | 日期边界值 |
| 10.10 | `shouldHandleConcurrentAccess` | 并发访问 |

### SQL 注入防护测试示例

```java
@ParameterizedTest
@ArgumentsSource(IntegrationTestProvider.class)
@DisplayName("Should prevent SQL injection")
void shouldPreventSqlInjection(DbConfig config) {
    // Given
    String maliciousName = "Alice'; DROP TABLE employee; --";

    // When - Try to find employee with malicious name
    List<Employee> employees = config.queryEmployees()
            .where(Employee::getName).eq(maliciousName)
            .getList();

    // Then - Should return empty, not cause error
    assertThat(employees).isEmpty();

    // Verify table still exists
    long count = config.queryEmployees().count();
    assertThat(count).isGreaterThan(0);
}
```

---

## 实施顺序建议

1. **Phase 5 (Having)** - 与现有 AggregateFunctionsIntegrationTest 配合
2. **Phase 7 (Projection)** - 扩展 select 功能测试
3. **Phase 6 (Stream)** - 独立功能
4. **Phase 9 (Distinct)** - 扩展现有测试
5. **Phase 8 (Type Converter)** - 需要新的测试实体
6. **Phase 10 (Edge Cases)** - 最后补充

---

## 总体注意事项

1. 每个测试文件都需要：
   - 使用 `@ArgumentsSource(IntegrationTestProvider.class)` 支持多数据库
   - 遵循 Given-When-Then 结构
   - 使用 AssertJ 断言
   - 有清晰的 `@DisplayName` 注解

2. 发现 BUG 时的处理：
   - 使用 `@Disabled` 注解标记
   - 在注释中说明 BUG 现象
   - 不修改测试用例来绕过 BUG

3. 测试数据管理：
   - 使用 9000+ 范围的 ID 避免冲突
   - 每个测试方法前数据会自动重置

---

## 覆盖率预期

| 阶段 | 覆盖模块 | 预期提升 |
|------|----------|----------|
| Phase 5 | GroupByStep, HavingStep | +2% |
| Phase 6 | QueryExecutor.stream | +1% |
| Phase 7 | Select projection | +3% |
| Phase 8 | TypeConverter | +2% |
| Phase 9 | Select distinct | +1% |
| Phase 10 | 异常处理分支 | +1% |

**总预期覆盖率**: 从 67.76% 提升到 80%+