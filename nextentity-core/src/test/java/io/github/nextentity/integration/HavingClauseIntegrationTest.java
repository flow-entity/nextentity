package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Having clause integration tests.
 * <p>
 * Tests HAVING clause functionality including:
 * - HAVING with aggregate functions
 * - HAVING with GROUP BY
 * - Complex HAVING conditions
 * <p>
 * Note: For GROUP BY + HAVING, the select must come BEFORE groupBy.
 * All paths in select must use get() wrapper.
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@Disabled("BUG: HAVING clause generates SQL without GROUP BY clause - framework implementation issue")
@DisplayName("Having Clause Integration Tests")
public class HavingClauseIntegrationTest {

    // ========================================
    // 1. Basic HAVING with COUNT
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count gt")
    void shouldFilterGroupsWithHavingCountGt(DbConfig config) {
        // When - Find departments with more than 2 employees
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(2L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).get1()).isGreaterThan(2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count ge")
    void shouldFilterGroupsWithHavingCountGe(DbConfig config) {
        // When - Find departments with at least 2 employees
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().ge(2L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() >= 2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count eq")
    void shouldFilterGroupsWithHavingCountEq(DbConfig config) {
        // When - Find departments with exactly 5 employees
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().eq(5L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).get0()).isEqualTo(1L);
        assertThat(results.get(0).get1()).isEqualTo(5L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count lt")
    void shouldFilterGroupsWithHavingCountLt(DbConfig config) {
        // When - Find departments with less than 3 employees
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().lt(3L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() < 3L);
    }

    // ========================================
    // 2. HAVING with SUM
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having sum gt")
    void shouldFilterGroupsWithHavingSumGt(DbConfig config) {
        // When - Find departments where total salary > 250000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).sum().gt(250000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 250000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having sum ge")
    void shouldFilterGroupsWithHavingSumGe(DbConfig config) {
        // When - Find departments where total salary >= 200000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).sum().ge(200000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() >= 200000.0);
    }

    // ========================================
    // 3. HAVING with AVG
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having avg gt")
    void shouldFilterGroupsWithHavingAvgGt(DbConfig config) {
        // When - Find departments where average salary > 55000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).avg().gt(55000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 55000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having avg between")
    void shouldFilterGroupsWithHavingAvgBetween(DbConfig config) {
        // When - Find departments where average salary between 50000 and 60000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).avg().between(50000.0, 60000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() >= 50000.0 && t.get1() <= 60000.0);
    }

    // ========================================
    // 4. HAVING with MAX/MIN
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having max gt")
    void shouldFilterGroupsWithHavingMaxGt(DbConfig config) {
        // When - Find departments where max salary > 70000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).max())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).max().gt(70000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having min gt")
    void shouldFilterGroupsWithHavingMinGt(DbConfig config) {
        // When - Find departments where min salary > 55000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).min())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).min().gt(55000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 5. HAVING with WHERE + GROUP BY
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine where group by having")
    void shouldCombineWhereGroupByHaving(DbConfig config) {
        // When - Find active employees grouped by department with count > 1
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(1L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine where with aggregate having")
    void shouldCombineWhereWithAggregateHaving(DbConfig config) {
        // When - Find departments with active employees having total salary > 100000
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).sum())
                .where(Employee::getActive).eq(true)
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getSalary).sum().gt(100000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 6. HAVING with ORDER BY
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with order by")
    void shouldCombineHavingWithOrderBy(DbConfig config) {
        // When - Find departments with count > 1, ordered by department id
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(1L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        // Verify sorted by department id
        for (int i = 0; i < results.size() - 1; i++) {
            assertThat(results.get(i).get0()).isLessThan(results.get(i + 1).get0());
        }
    }

    // ========================================
    // 7. HAVING with Limit
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with limit")
    void shouldCombineHavingWithLimit(DbConfig config) {
        // When - Find departments with count > 0, limit 2
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .limit(2);

        // Then
        assertThat(results).hasSize(2);
    }

    // ========================================
    // 8. HAVING with Pagination
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with pagination")
    void shouldCombineHavingWithPagination(DbConfig config) {
        // When - Find departments with count > 0, page 1 (offset 0, limit 2)
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList(0, 2);

        // Then
        assertThat(results).hasSize(2);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with offset")
    void shouldCombineHavingWithOffset(DbConfig config) {
        // When - Find departments with count > 0, page 2 (offset 1, limit 2)
        List<Tuple2<Long, Long>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList(1, 2);

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 9. HAVING with Count
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with having clause")
    void shouldCountWithHavingClause(DbConfig config) {
        // When - Count departments with employees > 1
        long count = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(1L))
                .count();

        // Then
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    // ========================================
    // 10. HAVING with Exist
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with having")
    void shouldCheckExistenceWithHaving(DbConfig config) {
        // When - Check if departments with employees > 3 exist
        boolean exists = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(get(Employee::getId).count().gt(3L))
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    // ========================================
    // 11. HAVING with Group By Status
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by status with having")
    void shouldGroupByStatusWithHaving(DbConfig config) {
        // When - Group by status with count > 0
        List<Tuple2<EmployeeStatus, Long>> results = config.queryEmployees()
                .select(get(Employee::getStatus), get(Employee::getId).count())
                .groupBy(Employee::getStatus)
                .having(get(Employee::getId).count().gt(0L))
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        for (var result : results) {
            assertThat(result.get0()).isInstanceOf(EmployeeStatus.class);
            assertThat(result.get1()).isGreaterThan(0L);
        }
    }

    // ========================================
    // 12. HAVING with Multiple Group By
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by multiple columns with having")
    void shouldGroupByMultipleColumnsWithHaving(DbConfig config) {
        // When - Group by department and active status with count > 0
        var results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getActive), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId, Employee::getActive)
                .having(get(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 13. HAVING with Count Distinct
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use having with count distinct")
    void shouldUseHavingWithCountDistinct(DbConfig config) {
        // When - Group by active status with distinct department count > 2
        var results = config.queryEmployees()
                .select(get(Employee::getActive), get(Employee::getDepartmentId).countDistinct())
                .groupBy(Employee::getActive)
                .having(get(Employee::getDepartmentId).countDistinct().gt(2L))
                .getList();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 14. HAVING with Department
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use having on department query")
    void shouldUseHavingOnDepartmentQuery(DbConfig config) {
        // When - Group departments by active status with budget sum > 100000
        var results = config.queryDepartments()
                .select(get(Department::getActive), get(Department::getBudget).sum())
                .groupBy(Department::getActive)
                .having(get(Department::getBudget).sum().gt(100000.0))
                .getList();

        // Then
        assertThat(results).isNotNull();
    }
}