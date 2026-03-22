# Phase 4: Update API 完整测试

## 目标
测试 `Update` 接口和 `UpdateExecutor` 接口的所有方法。

## 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/UpdateApiIntegrationTest.java`

## 背景
需要测试的接口方法：
- `Update<T>.updateNonNullColumn(T entity)` - 非空列更新
- `UpdateExecutor.patch(T entity, Class<T> entityType)` - 部分字段更新

## 测试用例清单

### 1. updateNonNullColumn 测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 1.1 | `shouldUpdateNonNullColumns` | 只更新非 null 字段 |
| 1.2 | `shouldPreserveNullValues` | null 值不被更新 |
| 1.3 | `shouldUpdateNonNullWithAllFields` | 所有字段都有值时更新 |
| 1.4 | `shouldUpdateNonNullWithPartialFields` | 部分字段有值时更新 |

### 2. patch 操作测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 2.1 | `shouldPatchEntity` | patch 基本用法 |
| 2.2 | `shouldPatchPartialFields` | 部分字段 patch |
| 2.3 | `shouldPatchWithNullValues` | patch 包含 null 值 |
| 2.4 | `shouldPatchReturnUpdated` | patch 返回更新后实体 |

### 3. insert 操作补充测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 3.1 | `shouldInsertWithGeneratedId` | 自动生成 ID |
| 3.2 | `shouldInsertWithVersion` | insert 带版本号 |
| 3.3 | `shouldInsertWithNullFields` | insert 包含 null 字段 |

### 4. update 操作补充测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 4.1 | `shouldUpdateReturnEntity` | update 返回更新后实体 |
| 4.2 | `shouldUpdateVersionIncrement` | 版本号自动递增 |
| 4.3 | `shouldUpdateWithConcurrencyCheck` | 并发检查 |

### 5. delete 操作补充测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 5.1 | `shouldDeleteCascade` | 级联删除（如有） |
| 5.2 | `shouldDeleteOrphanRemoval` | 孤儿删除 |

### 6. 事务操作测试

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 6.1 | `shouldDoInTransactionWithResult` | doInTransaction 返回结果 |
| 6.2 | `shouldDoInTransactionWithRunnable` | doInTransaction 无返回值 |
| 6.3 | `shouldRollbackOnException` | 异常时回滚 |
| 6.4 | `shouldCommitOnSuccess` | 成功时提交 |

## 代码模板

```java
package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Update API integration tests.
 * Tests update operations including updateNonNullColumn and patch.
 */
@DisplayName("Update API Integration Tests")
public class UpdateApiIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update non-null columns only")
    void shouldUpdateNonNullColumns(DbConfig config) {
        // Given
        Employee original = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        Double originalSalary = original.getSalary();

        Employee partial = new Employee();
        partial.setId(1L);
        partial.setName("Updated Name");
        // salary is null, should not be updated

        // When
        // Note: Need to verify the API for updateNonNullColumn
        // This test pattern depends on the actual API

        // Then
        // Verify only name was updated, salary remains unchanged
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should patch entity")
    void shouldPatchEntity(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        Double newSalary = employee.getSalary() + 10000;
        employee.setSalary(newSalary);

        // When
        Employee patched = config.getUpdateExecutor().patch(employee, Employee.class);

        // Then
        assertThat(patched.getSalary()).isEqualTo(newSalary);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should do in transaction with result")
    void shouldDoInTransactionWithResult(DbConfig config) {
        // Given
        Employee newEmployee = createTestEmployee(9001L, "Transaction Test");

        // When
        Employee result = config.getUpdateExecutor().doInTransaction(() -> {
            config.getUpdateExecutor().insert(newEmployee, Employee.class);
            return config.queryEmployees()
                    .where(Employee::getId).eq(9001L)
                    .getSingle();
        });

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Transaction Test");
    }

    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail("test" + id + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}
```

## 注意事项
- updateNonNullColumn 可能需要通过 Repository 接口调用
- 需要验证 patch 方法的行为（是否与 updateNonNullColumn 相同）
- 版本号测试需要实体有 @Version 字段

## 预期覆盖率提升
- UpdateExecutor 接口实现: 85%+
- JpaUpdateExecutor: 85%+
- JdbcUpdateExecutor: 85%+