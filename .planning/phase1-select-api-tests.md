# Phase 1: Select API 完整测试

## 目标
测试 `Select` 接口的所有方法变体，确保覆盖 `QueryBuilder.java` 中的所有 select 方法。

## 文件位置
`nextentity-core/src/test/java/io/github/nextentity/integration/SelectApiIntegrationTest.java`

## 测试用例清单

### 1. 投影类型选择 (select + selectDistinct)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 1.1 | `shouldSelectWithProjectionType` | select(Class<R> projectionType) 投影到 DTO |
| 1.2 | `shouldSelectDistinctWithProjectionType` | selectDistinct(Class<R> projectionType) |
| 1.3 | `shouldSelectEntityToSameType` | select 同类型实体返回自身 |
| 1.4 | `shouldSelectDistinctEntity` | selectDistinct 实体去重 |

### 2. 单路径选择

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 2.1 | `shouldSelectSinglePath` | select(Path<T, R> path) 单字段 |
| 2.2 | `shouldSelectDistinctSinglePath` | selectDistinct(Path<T, R> path) 单字段去重 |
| 2.3 | `shouldSelectPathWithWhere` | 单路径 + WHERE 条件 |
| 2.4 | `shouldSelectPathWithOrderBy` | 单路径 + ORDER BY |

### 3. 双路径选择 (Tuple2)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 3.1 | `shouldSelectTwoPaths` | select(Path a, Path b) |
| 3.2 | `shouldSelectDistinctTwoPaths` | selectDistinct(Path a, Path b) |
| 3.3 | `shouldSelectTwoPathsWithCondition` | 双路径 + WHERE |
| 3.4 | `shouldSelectTwoExpressions` | select(TypedExpression a, TypedExpression b) |

### 4. 三路径选择 (Tuple3)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 4.1 | `shouldSelectThreePaths` | select(Path a, Path b, Path c) |
| 4.2 | `shouldSelectDistinctThreePaths` | selectDistinct(Path a, Path b, Path c) |
| 4.3 | `shouldSelectThreeExpressions` | select(TypedExpression a, b, c) |

### 5. 四路径选择 (Tuple4)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 5.1 | `shouldSelectFourPaths` | select(Path a, b, c, d) |
| 5.2 | `shouldSelectDistinctFourPaths` | selectDistinct(Path a, b, c, d) |

### 6. 五路径选择 (Tuple5)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 6.1 | `shouldSelectFivePaths` | select(5 个 Path) |
| 6.2 | `shouldSelectDistinctFivePaths` | selectDistinct(5 个 Path) |

### 7. 六路径选择 (Tuple6)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 7.1 | `shouldSelectSixPaths` | select(6 个 Path) |
| 7.2 | `shouldSelectDistinctSixPaths` | selectDistinct(6 个 Path) |

### 8. 七路径选择 (Tuple7)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 8.1 | `shouldSelectSevenPaths` | select(7 个 Path) |
| 8.2 | `shouldSelectDistinctSevenPaths` | selectDistinct(7 个 Path) |

### 9. 八路径选择 (Tuple8)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 9.1 | `shouldSelectEightPaths` | select(8 个 Path) |
| 9.2 | `shouldSelectDistinctEightPaths` | selectDistinct(8 个 Path) |

### 10. 九路径选择 (Tuple9)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 10.1 | `shouldSelectNinePaths` | select(9 个 Path) |
| 10.2 | `shouldSelectDistinctNinePaths` | selectDistinct(9 个 Path) |

### 11. 十路径选择 (Tuple10)

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 11.1 | `shouldSelectTenPaths` | select(10 个 Path) |
| 11.2 | `shouldSelectDistinctTenPaths` | selectDistinct(10 个 Path) |

### 12. 集合参数选择

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 12.1 | `shouldSelectCollectionOfPaths` | select(Collection<Path>) |
| 12.2 | `shouldSelectDistinctCollectionOfPaths` | selectDistinct(Collection<Path>) |
| 12.3 | `shouldSelectEmptyPathCollection` | 空集合参数 |
| 12.4 | `shouldSelectListOfExpressions` | select(List<TypedExpression>) |

### 13. 表达式选择

| 编号 | 测试方法 | 描述 |
|------|----------|------|
| 13.1 | `shouldSelectTypedExpression` | select(TypedExpression) |
| 13.2 | `shouldSelectDistinctTypedExpression` | selectDistinct(TypedExpression) |
| 13.3 | `shouldSelectMixedExpressionsAndPaths` | 混合表达式和路径 |

## 代码模板

```java
package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Select API integration tests.
 * Tests all select method variants from the Select interface.
 */
@DisplayName("Select API Integration Tests")
public class SelectApiIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select single path")
    void shouldSelectSinglePath(DbConfig config) {
        // Given
        // When
        List<String> names = config.queryEmployees()
                .select(Employee::getName)
                .orderBy(Employee::getId).asc()
                .getList();
        // Then
        assertThat(names).hasSize(12);
        assertThat(names.get(0)).isEqualTo("Alice Johnson");
    }

    // ... 其他测试方法
}
```

## 预期覆盖率提升
- QueryBuilder.select* 方法: 90%+
- Select 接口实现: 85%+

## 依赖
- 无前置依赖，可优先实施