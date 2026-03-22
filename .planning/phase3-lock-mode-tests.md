# Phase 3: Lock 锁定机制测试

## 目标
测试数据库锁定机制，包括悲观锁和乐观锁。

## 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/LockModeIntegrationTest.java`

## 背景
`WhereImpl.lock(LockModeType lockModeType)` 方法支持设置锁定模式。

## LockModeType 类型
参考 `io.github.nextentity.api.model.LockModeType`:
- NONE
- PESSIMISTIC_READ
- PESSIMISTIC_WRITE
- OPTIMISTIC
- OPTIMISTIC_FORCE_INCREMENT

## 测试用例清单

### 1. 悲观读锁 (PESSIMISTIC_READ)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 1.1 | `shouldLockWithPessimisticRead` | 悲观读锁基本用法 |
| 1.2 | `shouldQueryWithPessimisticRead` | 查询 + 悲观读锁 |
| 1.3 | `shouldGetSingleWithPessimisticRead` | getSingle + 悲观读锁 |

### 2. 悲观写锁 (PESSIMISTIC_WRITE)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 2.1 | `shouldLockWithPessimisticWrite` | 悲观写锁基本用法 |
| 2.2 | `shouldUpdateWithPessimisticWrite` | 更新 + 悲观写锁 |
| 2.3 | `shouldDeleteWithPessimisticWrite` | 删除 + 悲观写锁 |

### 3. 乐观锁 (OPTIMISTIC)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 3.1 | `shouldLockWithOptimistic` | 乐观锁基本用法 |
| 3.2 | `shouldDetectOptimisticLockConflict` | 乐观锁冲突检测 |
| 3.3 | `shouldHandleOptimisticLockException` | 乐观锁异常处理 |

### 4. 乐观锁强制递增 (OPTIMISTIC_FORCE_INCREMENT)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 4.1 | `shouldLockWithOptimisticForceIncrement` | 强制递增乐观锁 |
| 4.2 | `shouldIncrementVersionOnForce` | 版本号自动递增验证 |

### 5. 锁与事务组合

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 5.1 | `shouldLockInTransaction` | 事务内锁定 |
| 5.2 | `shouldReleaseLockAfterTransaction` | 事务后释放锁 |
| 5.3 | `shouldLockWithTimeout` | 锁超时处理 |

### 6. JDBC vs JPA 锁定差异

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 6.1 | `shouldHandleJdbcPessimisticLock` | JDBC 悲观锁实现 |
| 6.2 | `shouldHandleJpaPessimisticLock` | JPA 悲观锁实现 |

## 代码模板

```java
package io.github.nextentity.integration;

import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lock mode integration tests.
 * Tests pessimistic and optimistic locking mechanisms.
 */
@DisplayName("Lock Mode Integration Tests")
public class LockModeIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic read lock")
    void shouldQueryWithPessimisticRead(DbConfig config) {
        // Given
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .lock(LockModeType.PESSIMISTIC_READ)
                .getList();
        // Then
        assertThat(employees).hasSize(1);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with pessimistic write")
    void shouldLockWithPessimisticWrite(DbConfig config) {
        // Given
        // When
        Employee employee = config.getUpdateExecutor().doInTransaction(() ->
            config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .lock(LockModeType.PESSIMISTIC_WRITE)
                .getSingle()
        );
        // Then
        assertThat(employee).isNotNull();
    }

    // ... 其他测试方法
}
```

## 注意事项
- 锁定测试需要在事务中执行
- 需要验证 Employee 实体是否有 @Version 注解（乐观锁需要）
- 并发锁测试可能需要使用 @Disabled 标记（环境依赖）
- JPA 和 JDBC 实现可能有差异

## 预期覆盖率提升
- WhereImpl.lock 方法: 90%+
- LockModeTypeAdapter: 85%+