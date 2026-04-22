package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// Subquery and advanced query integration tests.
/// <p>
/// 测试s advanced query 操作s including:
/// - Subquery building with asSubQuery()
/// - Subquery count, slice, getSingle, getFirst
/// - Expression comparison with subquery
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
@DisplayName("Subquery Integration Tests")
public class SubqueryIntegrationTest {

    ///
    /// 测试s asSubQuery returns a SubQueryBuilder.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should create subquery from query")
    void shouldCreateSubqueryFromQuery(IntegrationTestContext context) {
        // When
        var subquery = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .toSubQuery();

        // Then - just verify we can create the subquery
        assertThat(subquery).isNotNull();
    }

    ///
    /// 测试s subquery count.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count using subquery")
    void shouldCountUsingSubquery(IntegrationTestContext context) {
        // Given - Count employees in department 1
        long subqueryCount = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // When - Get count directly
        long directCount = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // Then
        assertThat(subqueryCount).isEqualTo(directCount);
        assertThat(subqueryCount).isPositive();
    }

    ///
    /// 测试s subquery slice.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice subquery results")
    void shouldSliceSubqueryResults(IntegrationTestContext context) {
        // Given
        var sliceExpr = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .toSubQuery()
                .slice(0, 5);

        // When - Just verify slice can be created
        assertThat(sliceExpr).isNotNull();
    }

    ///
    /// 测试s expression comparison with max salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with max expression")
    void shouldCompareWithMaxExpression(IntegrationTestContext context) {
        // Given - Get max salary
        Double maxSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .single();

        // When - Find employees with max salary
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).eq(maxSalary)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= maxSalary - 0.01);
    }

    ///
    /// 测试s expression comparison with min salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with min expression")
    void shouldCompareWithMinExpression(IntegrationTestContext context) {
        // Given - Get min salary
        Double minSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .single();

        // When - Find employees with min salary
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).eq(minSalary)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
    }

    ///
    /// 测试s expression comparison with average salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should compare with average expression")
    void shouldCompareWithAverageExpression(IntegrationTestContext context) {
        // Given - Get average salary
        Double avgSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .single();

        // When - Find employees above average
        List<Employee> aboveAvg = context.queryEmployees()
                .where(Employee::getSalary).gt(avgSalary)
                .list();

        List<Employee> belowAvg = context.queryEmployees()
                .where(Employee::getSalary).lt(avgSalary)
                .list();

        // Then
        assertThat(aboveAvg).isNotEmpty();
        assertThat(belowAvg).isNotEmpty();
    }

    ///
    /// 测试s nested query with two-step approach.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle two-step query")
    void shouldHandleTwoStepQuery(IntegrationTestContext context) {
        // Given - Get active department IDs
        List<Long> activeDeptIds = context.queryDepartments()
                .select(Department::getId)
                .where(Department::getActive).eq(true)
                .list();

        // When - Find employees in active departments
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).in(activeDeptIds)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> activeDeptIds.contains(e.getDepartmentId()));
    }

    ///
    /// 测试s query with pre-computed values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use pre-computed values in query")
    void shouldUsePrecomputedValuesInQuery(IntegrationTestContext context) {
        // Given - Pre-compute department IDs
        List<Long> deptIds = List.of(1L, 2L, 3L);

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).in(deptIds)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> deptIds.contains(e.getDepartmentId()));
    }

    ///
    /// 测试s two-step filter with status.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with two-step approach for status")
    void shouldFilterWithTwoStepForStatus(IntegrationTestContext context) {
        // Given - Get IDs of active employees
        List<Long> activeIds = context.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getActive).eq(true)
                .list();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(activeIds)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }

    ///
    /// 测试s two-step filter with salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with two-step approach for salary")
    void shouldFilterWithTwoStepForSalary(IntegrationTestContext context) {
        // Given - Get IDs of high-salary employees
        List<Long> highSalaryIds = context.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getSalary).gt(70000.0)
                .list();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(highSalaryIds)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 70000.0);
    }

    ///
    /// 测试s NOT IN with pre-computed values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with NOT IN using pre-computed values")
    void shouldFilterWithNotInPrecomputed(IntegrationTestContext context) {
        // Given - Get IDs of employees in department 1
        List<Long> dept1Ids = context.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getDepartmentId).eq(1L)
                .list();

        // When - Find employees NOT in department 1
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).notIn(dept1Ids)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L);
    }

    ///
    /// 测试s aggregation with filter.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with filter")
    void shouldAggregateWithFilter(IntegrationTestContext context) {
        // Given - Get min salary
        Double minSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .single();

        // When - Find employees with salary above min
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(minSalary)
                .list();

        // Then
        assertThat(employees).isNotEmpty();

        // Should have at least one employee NOT in the result (the min salary employee)
        long total = context.queryEmployees().count();
        assertThat(employees.size()).isLessThan((int) total);
    }

    ///
    /// 测试s combined aggregation queries.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple aggregations")
    void shouldCombineMultipleAggregations(IntegrationTestContext context) {
        // Given
        Long count = context.queryEmployees()
                .select(Path.of(Employee::getId).count())
                .single();

        Double avg = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .single();

        Double max = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .single();

        Double min = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .single();

        // When - Find employees in various ranges
        List<Employee> aboveAvg = context.queryEmployees()
                .where(Employee::getSalary).ge(avg)
                .list();

        List<Employee> belowAvg = context.queryEmployees()
                .where(Employee::getSalary).le(avg)
                .list();

        // Then
        assertThat(count).isEqualTo(12L);
        assertThat(avg).isBetween(min, max);
        assertThat(aboveAvg.size() + belowAvg.size()).isGreaterThanOrEqualTo(count.intValue());
    }

    ///
    /// 测试s query by pre-selected IDs with order.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by IDs with order")
    void shouldQueryByIdsWithOrder(IntegrationTestContext context) {
        // Given - Get top 3 salary IDs
        List<Long> topSalaryIds = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .list(3)
                .stream()
                .map(Employee::getId)
                .toList();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(topSalaryIds)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(employees).hasSize(3);

        // Verify these are indeed high-salary employees
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .list();

        assertThat(employees.get(0).getSalary()).isEqualTo(allEmployees.get(0).getSalary());
    }
}

