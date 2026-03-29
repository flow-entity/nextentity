package io.github.nextentity.integration;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.model.*;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Select API integration tests.
 * <p>
 * Tests all select method variants from the Select interface including:
 * - select(Class) projection type selection
 * - select(Path) single path selection
 * - select(Path...) tuple selection (2-10 paths)
 * - selectDistinct variants
 * - select with TypedExpression
 * - select with Collection/List parameters
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Select API Integration Tests")
public class SelectApiIntegrationTest {

    // ========================================
    // 1. Single Path Selection
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select single path - name")
    void shouldSelectSinglePathName(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(names).hasSize(12);
        assertThat(names.getFirst()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select single path - salary")
    void shouldSelectSinglePathSalary(IntegrationTestContext context) {
        // When
        List<Double> salaries = context.queryEmployees()
                .select(Employee::getSalary)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(salaries).hasSize(12);
        assertThat(salaries).allMatch(s -> s != null && s > 0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select single path with where condition")
    void shouldSelectSinglePathWithWhere(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .select(Employee::getName)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(names).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct single path")
    void shouldSelectDistinctSinglePath(IntegrationTestContext context) {
        // When
        List<Long> deptIds = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(deptIds).hasSize(5);
        assertThat(deptIds).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    // ========================================
    // 2. Tuple2 Selection (Two Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select two paths as Tuple2")
    void shouldSelectTwoPaths(IntegrationTestContext context) {
        // When
        List<Tuple2<String, String>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getEmail)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple2<String, String> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo("Alice Johnson");
        assertThat(tuple.get1()).isEqualTo("alice@example.com");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct two paths")
    void shouldSelectDistinctTwoPaths(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Boolean>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        // Verify distinctness
        long distinctCount = results.stream().distinct().count();
        assertThat(results.size()).isEqualTo((int) distinctCount);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select two paths with condition")
    void shouldSelectTwoPathsWithCondition(IntegrationTestContext context) {
        // When
        List<Tuple2<String, Double>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getSalary).gt(70000.0)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 70000.0);
    }

    // ========================================
    // 3. Tuple3 Selection (Three Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select three paths as Tuple3")
    void shouldSelectThreePaths(IntegrationTestContext context) {
        // When
        List<Tuple3<String, String, Double>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple3<String, String, Double> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo("Alice Johnson");
        assertThat(tuple.get1()).isEqualTo("alice@example.com");
        assertThat(tuple.get2()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct three paths")
    void shouldSelectDistinctThreePaths(IntegrationTestContext context) {
        // When
        List<Tuple3<Long, Boolean, Double>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive, Employee::getSalary)
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 4. Tuple4 Selection (Four Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select four paths as Tuple4")
    void shouldSelectFourPaths(IntegrationTestContext context) {
        // When
        List<Tuple4<Long, String, String, Double>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail, Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple4<Long, String, String, Double> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo(1L);
        assertThat(tuple.get1()).isEqualTo("Alice Johnson");
        assertThat(tuple.get2()).isEqualTo("alice@example.com");
        assertThat(tuple.get3()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct four paths")
    void shouldSelectDistinctFourPaths(IntegrationTestContext context) {
        // When
        List<Tuple4<Long, Boolean, Double, String>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive, Employee::getSalary, Employee::getName)
                .getList();

        // Then
        assertThat(results).hasSize(12); // All names are distinct
    }

    // ========================================
    // 5. Tuple5 Selection (Five Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select five paths as Tuple5")
    void shouldSelectFivePaths(IntegrationTestContext context) {
        // When
        List<Tuple5<Long, String, String, Double, Boolean>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple5<Long, String, String, Double, Boolean> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo(1L);
        assertThat(tuple.get1()).isEqualTo("Alice Johnson");
        assertThat(tuple.get4()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct five paths")
    void shouldSelectDistinctFivePaths(IntegrationTestContext context) {
        // When
        List<Tuple5<Long, Boolean, String, Double, String>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive,
                        Employee::getName, Employee::getSalary, Employee::getEmail)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 6. Tuple6 Selection (Six Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select six paths as Tuple6")
    void shouldSelectSixPaths(IntegrationTestContext context) {
        // When
        List<Tuple6<Long, String, String, Double, Boolean, Long>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple6<Long, String, String, Double, Boolean, Long> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo(1L);
        assertThat(tuple.get5()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct six paths")
    void shouldSelectDistinctSixPaths(IntegrationTestContext context) {
        // When
        List<Tuple6<Long, Boolean, String, Double, String, Long>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive,
                        Employee::getName, Employee::getSalary, Employee::getEmail,
                        Employee::getId)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 7. Tuple7 Selection (Seven Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select seven paths as Tuple7")
    void shouldSelectSevenPaths(IntegrationTestContext context) {
        // When
        List<Tuple7<Long, String, String, Double, Boolean, Long, String>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId,
                        Employee::getName) // Reuse name for 7th
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple7<Long, String, String, Double, Boolean, Long, String> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo(1L);
        assertThat(tuple.get1()).isEqualTo(tuple.get6()); // Both are name
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct seven paths")
    void shouldSelectDistinctSevenPaths(IntegrationTestContext context) {
        // When
        List<Tuple7<Long, Boolean, String, Double, String, Long, String>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive,
                        Employee::getName, Employee::getSalary, Employee::getEmail,
                        Employee::getId, Employee::getName)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 8. Tuple8 Selection (Eight Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select eight paths as Tuple8")
    void shouldSelectEightPaths(IntegrationTestContext context) {
        // When
        List<Tuple8<Long, String, String, Double, Boolean, Long, String, Double>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId,
                        Employee::getName, Employee::getSalary)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct eight paths")
    void shouldSelectDistinctEightPaths(IntegrationTestContext context) {
        // When
        List<Tuple8<Long, Boolean, String, Double, String, Long, String, Double>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive,
                        Employee::getName, Employee::getSalary, Employee::getEmail,
                        Employee::getId, Employee::getName, Employee::getSalary)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 9. Tuple9 Selection (Nine Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select nine paths as Tuple9")
    void shouldSelectNinePaths(IntegrationTestContext context) {
        // When
        List<Tuple9<Long, String, String, Double, Boolean, Long, String, Double, Boolean>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId,
                        Employee::getName, Employee::getSalary, Employee::getActive)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct nine paths")
    void shouldSelectDistinctNinePaths(IntegrationTestContext context) {
        // When
        List<Tuple9<Long, Boolean, String, Double, String, Long, String, Double, Boolean>> results = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId, Employee::getActive,
                        Employee::getName, Employee::getSalary, Employee::getEmail,
                        Employee::getId, Employee::getName, Employee::getSalary,
                        Employee::getActive)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 10. Tuple10 Selection (Ten Paths)
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select ten paths as Tuple10")
    void shouldSelectTenPaths(IntegrationTestContext context) {
        // When
        List<Tuple10<Long, String, String, Double, Boolean, Long, String, Double, Boolean, Long>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId,
                        Employee::getName, Employee::getSalary, Employee::getActive,
                        Employee::getId)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple10<Long, String, String, Double, Boolean, Long, String, Double, Boolean, Long> tuple = results.getFirst();
        assertThat(tuple.get0()).isEqualTo(1L);
        assertThat(tuple.get0()).isEqualTo(tuple.get9()); // Both are ID
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct ten paths")
    void shouldSelectDistinctTenPaths(IntegrationTestContext context) {
        // When
        List<Tuple10<Long, String, String, Double, Boolean, Long, String, Double, Boolean, Long>> results = context.queryEmployees()
                .selectDistinct(Employee::getId, Employee::getName, Employee::getEmail,
                        Employee::getSalary, Employee::getActive, Employee::getDepartmentId,
                        Employee::getName, Employee::getSalary, Employee::getActive,
                        Employee::getId)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 11. Collection Parameter Selection
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with collection of paths")
    void shouldSelectWithCollectionOfPaths(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ?>> paths = new ArrayList<>();
        paths.add(Employee::getName);
        paths.add(Employee::getEmail);

        // When
        List<Tuple> results = context.queryEmployees()
                .select(paths)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple tuple = results.getFirst();
        assertThat((String) tuple.get(0)).isEqualTo("Alice Johnson");
        assertThat((String) tuple.get(1)).isEqualTo("alice@example.com");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with collection of paths")
    void shouldSelectDistinctWithCollectionOfPaths(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ?>> paths = new ArrayList<>();
        paths.add(Employee::getDepartmentId);
        paths.add(Employee::getActive);

        // When
        List<Tuple> results = context.queryEmployees()
                .selectDistinct(paths)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 12. TypedExpression Selection
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with typed expression - max")
    void shouldSelectWithTypedExpressionMax(IntegrationTestContext context) {
        // When
        Double maxSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .getSingle();

        // Then
        assertThat(maxSalary).isNotNull();
        assertThat(maxSalary).isGreaterThan(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with typed expression - min")
    void shouldSelectWithTypedExpressionMin(IntegrationTestContext context) {
        // When
        Double minSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .getSingle();

        // Then
        assertThat(minSalary).isNotNull();
        assertThat(minSalary).isGreaterThan(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with typed expression - count")
    void shouldSelectWithTypedExpressionCount(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(Path.of(Employee::getId).count())
                .getSingle();

        // Then
        assertThat(count).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with typed expression - sum")
    void shouldSelectWithTypedExpressionSum(IntegrationTestContext context) {
        // When
        Double sum = context.queryEmployees()
                .select(Path.of(Employee::getSalary).sum())
                .getSingle();

        // Then
        assertThat(sum).isNotNull();
        assertThat(sum).isGreaterThan(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with typed expression - avg")
    void shouldSelectWithTypedExpressionAvg(IntegrationTestContext context) {
        // When
        Double avg = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .getSingle();

        // Then
        assertThat(avg).isNotNull();
        assertThat(avg).isGreaterThan(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with typed expression")
    void shouldSelectDistinctWithTypedExpression(IntegrationTestContext context) {
        // When
        Long distinctDeptCount = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getDepartmentId).count())
                .getSingle();

        // Then
        assertThat(distinctDeptCount).isNotNull();
    }

    // ========================================
    // 13. Multiple TypedExpression Selection
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with two typed expressions")
    void shouldSelectWithTwoTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple2<Double, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        Tuple2<Double, Double> tuple = results.getFirst();
        assertThat(tuple.get0()).isLessThanOrEqualTo(tuple.get1());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with three typed expressions")
    void shouldSelectWithThreeTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple3<Double, Double, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isLessThanOrEqualTo(results.getFirst().get1());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with four typed expressions")
    void shouldSelectWithFourTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple4<Double, Double, Double, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with five typed expressions")
    void shouldSelectWithFiveTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple5<Double, Double, Double, Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with six typed expressions")
    void shouldSelectWithSixTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple6<Double, Double, Double, Long, Double, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with seven typed expressions")
    void shouldSelectWithSevenTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple7<Double, Double, Double, Long, Double, Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with eight typed expressions")
    void shouldSelectWithEightTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple8<Double, Double, Double, Long, Double, Long, Double, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with nine typed expressions")
    void shouldSelectWithNineTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple9<Double, Double, Double, Long, Double, Long, Double, Double, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with ten typed expressions")
    void shouldSelectWithTenTypedExpressions(IntegrationTestContext context) {
        // When
        List<Tuple10<Double, Double, Double, Long, Double, Long, Double, Double, Double, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
        assertThat(results.getFirst().get3()).isEqualTo(results.getFirst().get9());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with list of typed expressions")
    void shouldSelectDistinctWithListOfTypedExpressions(IntegrationTestContext context) {
        // Given
        List<Expression<Employee, ?>> expressions = new ArrayList<>();
        expressions.add(Path.of(Employee::getDepartmentId));
        expressions.add(Path.of(Employee::getActive));

        // When
        List<Tuple> results = context.queryEmployees()
                .selectDistinct(expressions)
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        // Verify distinctness - all tuples should be unique
        long distinctCount = results.stream().distinct().count();
        assertThat(results.size()).isEqualTo((int) distinctCount);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with list of typed expressions (non-distinct)")
    void shouldSelectWithListOfTypedExpressions(IntegrationTestContext context) {
        // Given
        List<Expression<Employee, ?>> expressions = new ArrayList<>();
        expressions.add(Path.of(Employee::getDepartmentId));
        expressions.add(Path.of(Employee::getActive));

        // When
        List<Tuple> results = context.queryEmployees()
                .select(expressions)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with list of typed expressions - aggregate functions")
    void shouldSelectWithListOfTypedExpressionsAggregate(IntegrationTestContext context) {
        // Given - using aggregate expressions as TypedExpression
        List<Expression<Employee, ?>> expressions = new ArrayList<>();
        expressions.add(Path.of(Employee::getSalary).max());
        expressions.add(Path.of(Employee::getSalary).min());
        expressions.add(Path.of(Employee::getSalary).avg());

        // When
        List<Tuple> results = context.queryEmployees()
                .select(expressions)
                .getList();

        // Then - aggregate results should produce one row
        assertThat(results).hasSize(1);
        Tuple tuple = results.getFirst();
        Double maxSalary = tuple.get(0);
        Double minSalary = tuple.get(1);
        Double avgSalary = tuple.get(2);
        assertThat(maxSalary).isNotNull();
        assertThat(minSalary).isNotNull();
        assertThat(avgSalary).isNotNull();
        assertThat(maxSalary).isGreaterThanOrEqualTo(minSalary);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with list of typed expressions - aggregate functions")
    void shouldSelectDistinctWithListOfTypedExpressionsAggregate(IntegrationTestContext context) {
        // Given - using aggregate expressions as TypedExpression
        List<Expression<Employee, ?>> expressions = new ArrayList<>();
        expressions.add(Path.of(Employee::getSalary).max());
        expressions.add(Path.of(Employee::getSalary).min());

        // When
        List<Tuple> results = context.queryEmployees()
                .selectDistinct(expressions)
                .getList();

        // Then - aggregate results should produce one row
        assertThat(results).hasSize(1);
        Tuple tuple = results.getFirst();
        Double maxSalary = tuple.get(0);
        Double minSalary = tuple.get(1);
        assertThat(maxSalary).isNotNull();
        assertThat(minSalary).isNotNull();
        assertThat(maxSalary).isGreaterThanOrEqualTo(minSalary);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with list of paths as typed expressions")
    void shouldSelectDistinctWithListOfPathsAsTypedExpressions(IntegrationTestContext context) {
        // Given - EntityPath extends TypedExpression
        List<Expression<Employee, ?>> expressions = new ArrayList<>();
        expressions.add(Path.of(Employee::getDepartmentId));
        expressions.add(Path.of(Employee::getName));

        // When
        List<Tuple> results = context.queryEmployees()
                .selectDistinct(expressions)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then - all department+name combinations should be distinct (12 employees)
        assertThat(results).hasSize(12);
    }

    // ========================================
    // 13b. selectDistinct with TypedExpression varargs
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with two typed expressions varargs")
    void shouldSelectDistinctWithTwoTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple2<Double, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isLessThanOrEqualTo(results.getFirst().get1());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with three typed expressions varargs")
    void shouldSelectDistinctWithThreeTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple3<Double, Double, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isLessThanOrEqualTo(results.getFirst().get1());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with four typed expressions varargs")
    void shouldSelectDistinctWithFourTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple4<Double, Double, Double, Long>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with five typed expressions varargs")
    void shouldSelectDistinctWithFiveTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple5<Double, Double, Double, Long, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with six typed expressions varargs")
    void shouldSelectDistinctWithSixTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple6<Double, Double, Double, Long, Double, Long>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with seven typed expressions varargs")
    void shouldSelectDistinctWithSevenTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple7<Double, Double, Double, Long, Double, Long, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with eight typed expressions varargs")
    void shouldSelectDistinctWithEightTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple8<Double, Double, Double, Long, Double, Long, Double, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with nine typed expressions varargs")
    void shouldSelectDistinctWithNineTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple9<Double, Double, Double, Long, Double, Long, Double, Double, Double>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with ten typed expressions varargs")
    void shouldSelectDistinctWithTenTypedExpressionsVarargs(IntegrationTestContext context) {
        // When
        List<Tuple10<Double, Double, Double, Long, Double, Long, Double, Double, Double, Long>> results = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count(),
                        Path.of(Employee::getSalary).sum(), Path.of(Employee::getDepartmentId).count(),
                        Path.of(Employee::getSalary).min(), Path.of(Employee::getSalary).max(),
                        Path.of(Employee::getSalary).avg(), Path.of(Employee::getId).count())
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get3()).isEqualTo(12L);
        assertThat(results.getFirst().get3()).isEqualTo(results.getFirst().get9());
    }

    // ========================================
    // 14. Projection Type Selection
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with projection type - same entity")
    void shouldSelectWithProjectionTypeSameEntity(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .select(Employee.class)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.getFirst().getName()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with projection type - same entity returns same query")
    void shouldSelectWithProjectionTypeSameEntityReturnsSame(IntegrationTestContext context) {
        // When - select same type should return entity
        List<Employee> employees = context.queryEmployees()
                .select(Employee.class)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).hasSize(12);
        assertThat(employees.getFirst()).isInstanceOf(Employee.class);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct with projection type")
    void shouldSelectDistinctWithProjectionType(IntegrationTestContext context) {
        // When
        List<Department> departments = context.queryDepartments()
                .selectDistinct(Department.class)
                .orderBy(Department::getId).asc()
                .getList();

        // Then
        assertThat(departments).hasSize(5);
    }

    // ========================================
    // 15. Selection with Conditions and Ordering
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with where and order by")
    void shouldSelectWithWhereAndOrderBy(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, String>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        // Verify ordering
        for (int i = 0; i < results.size() - 1; i++) {
            String current = results.get(i).get1();
            String next = results.get(i + 1).get1();
            assertThat(current.compareTo(next)).isLessThanOrEqualTo(0);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with pagination")
    void shouldSelectWithPagination(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, String>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .orderBy(Employee::getId).asc()
                .getList(0, 5);

        // Then
        assertThat(results).hasSize(5);
        assertThat(results.getFirst().get0()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select with limit")
    void shouldSelectWithLimit(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, String>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .orderBy(Employee::getId).asc()
                .limit(3);

        // Then
        assertThat(results).hasSize(3);
    }

    // ========================================
    // 16. Edge Cases
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle select with null values in data")
    void shouldHandleSelectWithNullValues(IntegrationTestContext context) {
        // Given - Employee 1 exists
        // When
        List<Tuple2<String, String>> results = context.queryEmployees()
                .select(Employee::getName, Employee::getEmail)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        // Email could be null
        assertThat(results.getFirst().get0()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list for no matches")
    void shouldReturnEmptyListForNoMatches(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, String>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .where(Employee::getId).eq(99999L)
                .getList();

        // Then
        assertThat(results).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select single result with getSingle")
    void shouldSelectSingleWithGetSingle(IntegrationTestContext context) {
        // When
        Tuple2<Long, String> result = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get0()).isEqualTo(1L);
        assertThat(result.get1()).isEqualTo("Alice Johnson");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select first result with first")
    void shouldSelectFirst(IntegrationTestContext context) {
        // When
        var result = context.queryEmployees()
                .select(Employee::getId, Employee::getName)
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().get0()).isEqualTo(1L);
    }
}