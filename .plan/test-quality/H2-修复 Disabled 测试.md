# 任务 H2: 修复或移除 @Disabled 测试

## 优先级：高

## 问题描述
在 `CrudOperationsIntegrationTest.java` 中存在一个被 `@Disabled` 的测试用例，该测试长期禁用且无修复计划。

## 具体位置

**文件**：`CrudOperationsIntegrationTest.java:363`
**测试方法**：`shouldHandleDeleteNonExistent`

```java
/**
 * Tests deleting non-existent employee.
 */
@Disabled("TODO: Bug - delete operation on non-existent entity should handle gracefully")
@ParameterizedTest
@ArgumentsSource(IntegrationTestProvider.class)
@DisplayName("Should handle delete of non-existent employee")
void shouldHandleDeleteNonExistent(DbConfig config) {
    // Given
    Employee nonExistent = createTestEmployee(9998L, "Non Existent", "none@example.com");

    // When/Then - may throw exception or have no effect
    assertThrows(RuntimeException.class, () -> {
        config.getUpdateExecutor().delete(nonExistent, Employee.class);
    });
}
```

## 问题分析

该测试被禁用的原因是："Bug - delete operation on non-existent entity should handle gracefully"

这表明删除不存在的实体时，当前实现可能：
1. 不抛出异常（静默失败）
2. 抛出非预期的异常
3. 影响其他记录

## 解决方案

### 方案 A：修复实现使其符合测试预期（推荐）
修改 `UpdateExecutor.delete()` 方法，使其在删除不存在的实体时抛出明确的异常（如 `EntityNotFoundException`）。

**修改位置**：`nextentity-core` 或 `nextentity-jdbc` 中的 UpdateExecutor 实现

### 方案 B：修改测试预期
如果业务逻辑允许静默失败，则修改测试断言：

```java
@Test
void shouldHandleDeleteNonExistent(DbConfig config) {
    // Given
    Employee nonExistent = createTestEmployee(9998L, "Non Existent", "none@example.com");

    // When - delete should not throw exception even if entity doesn't exist
    int affectedRows = config.getUpdateExecutor().delete(nonExistent, Employee.class);

    // Then - should affect 0 rows
    assertThat(affectedRows).isEqualTo(0);
}
```

### 方案 C：移除测试
如果该行为未定义且不需要测试，则完全移除该测试方法。

## 执行步骤

1. 确认 `UpdateExecutor.delete()` 当前对不存在实体的处理行为
2. 与团队确认期望的行为（抛异常 vs 静默失败）
3. 根据期望行为选择方案 A/B/C
4. 实施修改并移除 `@Disabled` 注解
5. 运行集成测试验证修复

## 验收标准

- `@Disabled` 注解被移除
- 测试通过且行为符合预期
- 相关文档/JavaDoc 更新说明删除操作的行为
