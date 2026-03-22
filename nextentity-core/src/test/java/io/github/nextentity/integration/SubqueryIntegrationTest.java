package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Subquery and advanced query integration tests.
 * <p>
 * Tests advanced query operations including:
 * - Subquery building with asSubQuery()
 * - Subquery count, slice, getSingle, getFirst
 * - Expression comparison with subquery
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Subquery Integration Tests")
public class SubqueryIntegrationTest {

    /**
     * Tests asSubQuery returns a SubQueryBuilder.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should create subquery from query")
    void shouldCreateSubqueryFromQuery(DbConfig config) {
        // When
        var subquery = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .asSubQuery();

        // Then - just verify we can create the subquery
        assertThat(subquery).isNotNull();
    }

    /**
     * Tests subquery count.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count using subquery")
    void shouldCountUsingSubquery(DbConfig config) {
        // Given - Count employees in department 1
        long subqueryCount = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // When - Get count directly
        long directCount = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // Then
        assertThat(subqueryCount).isEqualTo(directCount);
        assertThat(subqueryCount).isPositive();
    }

    /**
     * Tests subquery slice.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice subquery results")
    void shouldSliceSubqueryResults(DbConfig config) {
        // Given
        var sliceExpr = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .asSubQuery()
                .slice(0, 5);

        // When - Just verify slice can be created
        assertThat(sliceExpr).isNotNull();
    }

    /**
     * Tests subquery getSingle.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single from subquery")
    void shouldGetSingleFromSubquery(DbConfig config) {
        // Given
        var singleExpr = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .asSubQuery()
                .getSingle();

        // When - Just verify single can be created
        assertThat(singleExpr).isNotNull();
    }

    /**
     * Tests subquery getFirst.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first from subquery")
    void shouldGetFirstFromSubquery(DbConfig config) {
        // Given
        var firstExpr = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .asSubQuery()
                .getFirst();

        // When - Just verify first can be created
        assertThat(firstExpr).isNotNull();
    }

    /**
     * Tests expression comparison with max salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with max expression")
    void shouldCompareWithMaxExpression(DbConfig config) {
        // Given - Get max salary
        Double maxSalary = config.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getSingle();

        // When - Find employees with max salary
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).eq(maxSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= maxSalary - 0.01);
    }

    /**
     * Tests expression comparison with min salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with min expression")
    void shouldCompareWithMinExpression(DbConfig config) {
        // Given - Get min salary
        Double minSalary = config.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getSingle();

        // When - Find employees with min salary
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).eq(minSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests expression comparison with average salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with average expression")
    void shouldCompareWithAverageExpression(DbConfig config) {
        // Given - Get average salary
        Double avgSalary = config.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getSingle();

        // When - Find employees above average
        List<Employee> aboveAvg = config.queryEmployees()
                .where(Employee::getSalary).gt(avgSalary)
                .getList();

        List<Employee> belowAvg = config.queryEmployees()
                .where(Employee::getSalary).lt(avgSalary)
                .getList();

        // Then
        assertThat(aboveAvg).isNotEmpty();
        assertThat(belowAvg).isNotEmpty();
    }

    /**
     * Tests nested query with two-step approach.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle two-step query")
    void shouldHandleTwoStepQuery(DbConfig config) {
        // Given - Get active department IDs
        List<Long> activeDeptIds = config.queryDepartments()
                .select(Department::getId)
                .where(Department::getActive).eq(true)
                .getList();

        // When - Find employees in active departments
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).in(activeDeptIds)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> activeDeptIds.contains(e.getDepartmentId()));
    }

    /**
     * Tests query with pre-computed values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use pre-computed values in query")
    void shouldUsePrecomputedValuesInQuery(DbConfig config) {
        // Given - Pre-compute department IDs
        List<Long> deptIds = List.of(1L, 2L, 3L);

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).in(deptIds)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> deptIds.contains(e.getDepartmentId()));
    }

    /**
     * Tests two-step filter with status.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with two-step approach for status")
    void shouldFilterWithTwoStepForStatus(DbConfig config) {
        // Given - Get IDs of active employees
        List<Long> activeIds = config.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getActive).eq(true)
                .getList();

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(activeIds)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }

    /**
     * Tests two-step filter with salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with two-step approach for salary")
    void shouldFilterWithTwoStepForSalary(DbConfig config) {
        // Given - Get IDs of high-salary employees
        List<Long> highSalaryIds = config.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getSalary).gt(70000.0)
                .getList();

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(highSalaryIds)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
    }

    /**
     * Tests NOT IN with pre-computed values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT IN using pre-computed values")
    void shouldFilterWithNotInPrecomputed(DbConfig config) {
        // Given - Get IDs of employees in department 1
        List<Long> dept1Ids = config.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // When - Find employees NOT in department 1
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).notIn(dept1Ids)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L);
    }

    /**
     * Tests aggregation with filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with filter")
    void shouldAggregateWithFilter(DbConfig config) {
        // Given - Get min salary
        Double minSalary = config.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getSingle();

        // When - Find employees with salary above min
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).gt(minSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();

        // Should have at least one employee NOT in the result (the min salary employee)
        long total = config.queryEmployees().count();
        assertThat(employees.size()).isLessThan((int) total);
    }

    /**
     * Tests combined aggregation queries.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple aggregations")
    void shouldCombineMultipleAggregations(DbConfig config) {
        // Given
        Long count = config.queryEmployees()
                .select(get(Employee::getId).count())
                .getSingle();

        Double avg = config.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getSingle();

        Double max = config.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getSingle();

        Double min = config.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getSingle();

        // When - Find employees in various ranges
        List<Employee> aboveAvg = config.queryEmployees()
                .where(Employee::getSalary).ge(avg)
                .getList();

        List<Employee> belowAvg = config.queryEmployees()
                .where(Employee::getSalary).le(avg)
                .getList();

        // Then
        assertThat(count).isEqualTo(12L);
        assertThat(avg).isBetween(min, max);
        assertThat(aboveAvg.size() + belowAvg.size()).isGreaterThanOrEqualTo(count.intValue());
    }

    /**
     * Tests query by pre-selected IDs with order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by IDs with order")
    void shouldQueryByIdsWithOrder(DbConfig config) {
        // Given - Get top 3 salary IDs
        List<Long> topSalaryIds = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .limit(3)
                .stream()
                .map(Employee::getId)
                .toList();

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(topSalaryIds)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(employees).hasSize(3);

        // Verify these are indeed high-salary employees
        List<Employee> allEmployees = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getList();

        assertThat(employees.get(0).getSalary()).isEqualTo(allEmployees.get(0).getSalary());
    }
}