# Phase 2: Fetch 关联查询测试

## 目标
测试关联实体加载功能，验证 `fetch` 操作的正确性。

## 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/FetchOperationsIntegrationTest.java`

## 背景
`QueryBuilder.fetch(List<PathExpression<T, ?>> expressions)` 用于指定要加载的关联实体。

## 测试用例清单

### 1. 基本关联加载

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 1.1 | `shouldFetchAssociatedEntity` | fetch 加载关联实体 |
| 1.2 | `shouldFetchMultipleAssociations` | fetch 多个关联 |
| 1.3 | `shouldFetchWithEmptyList` | fetch 空列表处理 |
| 1.4 | `shouldFetchWithNullList` | fetch null 处理 |

### 2. 嵌套关联

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 2.1 | `shouldFetchNestedAssociation` | 多级关联加载 |
| 2.2 | `shouldFetchDeepNestedAssociation` | 深层嵌套关联 |

### 3. Fetch 与查询组合

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 3.1 | `shouldFetchWithWhere` | fetch + WHERE 条件 |
| 3.2 | `shouldFetchWithPagination` | fetch + 分页 |
| 3.3 | `shouldFetchWithSelect` | fetch + select 组合 |

### 4. 边缘情况

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 4.1 | `shouldHandleFetchNonEntityAttribute` | fetch 非实体属性 |
| 4.2 | `shouldFetchWithNoAssociations` | 无关联实体时 fetch |
| 4.3 | `shouldFetchNullAssociation` | 关联为 null 时处理 |

## 代码模板

```java
package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fetch operations integration tests.
 * Tests entity association loading with fetch operations.
 */
@DisplayName("Fetch Operations Integration Tests")
public class FetchOperationsIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with empty list")
    void shouldFetchWithEmptyList(DbConfig config) {
        // Given
        // When
        List<Employee> employees = config.queryEmployees()
                .fetch(Collections.emptyList())
                .getList();
        // Then
        assertThat(employees).isNotEmpty();
    }

    // ... 其他测试方法
}
```

## 注意事项
- 当前测试实体 Employee 和 Department 的关联关系需要验证
- fetch 非实体属性时会打印警告日志
- 可能需要添加更多测试实体来验证多级关联

## 预期覆盖率提升
- QueryBuilder.fetch 方法: 90%+