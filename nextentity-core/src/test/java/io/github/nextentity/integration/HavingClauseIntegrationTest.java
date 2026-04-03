package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// Having clause integration tests.
 /// <p>
 /// 测试s HAVING clause functionality including:
 /// - HAVING with aggregate functions
 /// - HAVING with GROUP BY
 /// - Complex HAVING conditions
 /// <p>
 /// Note: For GROUP BY + HAVING, the select must come BEFORE groupBy.
 /// All paths in select must use get() wrapper.
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Having Clause Integration Tests")
public class HavingClauseIntegrationTest {

    // ========================================
    // 1. Basic HAVING with COUNT
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count gt")
    void shouldFilterGroupsWithHavingCountGt(IntegrationTestContext context) {
        // When - Find departments with more than 2 employees
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(2L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst().get1()).isGreaterThan(2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count ge")
    void shouldFilterGroupsWithHavingCountGe(IntegrationTestContext context) {
        // When - Find departments with at least 2 employees
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().ge(2L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() >= 2L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count eq")
    void shouldFilterGroupsWithHavingCountEq(IntegrationTestContext context) {
        // When - Find departments with exactly 5 employees
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().eq(5L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().get0()).isEqualTo(1L);
        assertThat(results.getFirst().get1()).isEqualTo(5L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having count lt")
    void shouldFilterGroupsWithHavingCountLt(IntegrationTestContext context) {
        // When - Find departments with less than 3 employees
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().lt(3L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

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
    void shouldFilterGroupsWithHavingSumGt(IntegrationTestContext context) {
        // When - Find departments where total salary > 250000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).sum().gt(250000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 250000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having sum ge")
    void shouldFilterGroupsWithHavingSumGe(IntegrationTestContext context) {
        // When - Find departments where total salary >= 200000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).sum().ge(200000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

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
    void shouldFilterGroupsWithHavingAvgGt(IntegrationTestContext context) {
        // When - Find departments where average salary > 55000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).avg().gt(55000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 55000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having avg between")
    void shouldFilterGroupsWithHavingAvgBetween(IntegrationTestContext context) {
        // When - Find departments where average salary between 50000 and 60000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).avg().between(50000.0, 60000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

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
    void shouldFilterGroupsWithHavingMaxGt(IntegrationTestContext context) {
        // When - Find departments where max salary > 70000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).max())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).max().gt(70000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 70000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter groups with having min gt")
    void shouldFilterGroupsWithHavingMinGt(IntegrationTestContext context) {
        // When - Find departments where min salary > 55000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).min())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).min().gt(55000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 5. HAVING with WHERE + GROUP BY
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine where group by having")
    void shouldCombineWhereGroupByHaving(IntegrationTestContext context) {
        // When - Find active employees grouped by department with count > 1
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(1L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get1() > 1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine where with aggregate having")
    void shouldCombineWhereWithAggregateHaving(IntegrationTestContext context) {
        // When - Find departments with active employees having total salary > 100000
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).sum())
                .where(Employee::getActive).eq(true)
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getSalary).sum().gt(100000.0))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 6. HAVING with ORDER BY
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with order by")
    void shouldCombineHavingWithOrderBy(IntegrationTestContext context) {
        // When - Find departments with count > 1, ordered by department id
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(1L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

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
    void shouldCombineHavingWithLimit(IntegrationTestContext context) {
        // When - Find departments with count > 0, limit 2
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .list(2);

        // Then
        assertThat(results).hasSize(2);
    }

    // ========================================
    // 8. HAVING with Pagination
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with pagination")
    void shouldCombineHavingWithPagination(IntegrationTestContext context) {
        // When - Find departments with count > 0, page 1 (offset 0, limit 2)
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .list(2);

        // Then
        assertThat(results).hasSize(2);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine having with offset")
    void shouldCombineHavingWithOffset(IntegrationTestContext context) {
        // When - Find departments with count > 0, page 2 (offset 1, limit 2)
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .list(1, 2);

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 9. HAVING with Count
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with having clause")
    void shouldCountWithHavingClause(IntegrationTestContext context) {
        // When - Count departments with employees > 1
        long count = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(1L))
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
    void shouldCheckExistenceWithHaving(IntegrationTestContext context) {
        // When - Check if departments with employees > 3 exist
        boolean exists = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .having(Path.of(Employee::getId).count().gt(3L))
                .exists();

        // Then
        assertThat(exists).isTrue();
    }

    // ========================================
    // 11. HAVING with Group By Status
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by status with having")
    void shouldGroupByStatusWithHaving(IntegrationTestContext context) {
        // When - Group by status with count > 0
        List<Tuple2<EmployeeStatus, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getStatus), Path.of(Employee::getId).count())
                .groupBy(Employee::getStatus)
                .having(Path.of(Employee::getId).count().gt(0L))
                .list();

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
    void shouldGroupByMultipleColumnsWithHaving(IntegrationTestContext context) {
        // When - Group by department and active status with count > 0
        var results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getActive), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId, Employee::getActive)
                .having(Path.of(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ========================================
    // 13. HAVING with Count Distinct
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use having with count distinct")
    void shouldUseHavingWithCountDistinct(IntegrationTestContext context) {
        // When - Group by active status with distinct department count > 2
        var results = context.queryEmployees()
                .select(Path.of(Employee::getActive), Path.of(Employee::getDepartmentId).countDistinct())
                .groupBy(Employee::getActive)
                .having(Path.of(Employee::getDepartmentId).countDistinct().gt(2L))
                .list();

        // Then
        assertThat(results).isNotNull();
    }

    // ========================================
    // 14. HAVING with Department
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use having on department query")
    void shouldUseHavingOnDepartmentQuery(IntegrationTestContext context) {
        // When - Group departments by active status with budget sum > 100000
        var results = context.queryDepartments()
                .select(Path.of(Department::getActive), Path.of(Department::getBudget).sum())
                .groupBy(Department::getActive)
                .having(Path.of(Department::getBudget).sum().gt(100000.0))
                .list();

        // Then
        assertThat(results).isNotNull();
    }
}

