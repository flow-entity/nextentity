package io.github.nextentity.integration;

import io.github.nextentity.api.EntityRoot;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// Numeric 操作s integration tests.
/// <p>
/// 测试s numeric 操作s including:
/// - Numeric comparisons (gt, ge, lt, le, between)
/// - Arithmetic 操作s (add, subtract, multiply, divide, mod)
/// - Numeric sorting
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
@DisplayName("Numeric Operations Integration Tests")
public class NumericOperationsIntegrationTest {

    private static final double SALARY_THRESHOLD = 60000.0;

    ///
    /// 测试s greater than comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with greater than")
    void shouldFilterWithGreaterThan(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
    }

    ///
    /// 测试s greater than or equal comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with greater than or equal")
    void shouldFilterWithGreaterThanOrEqual(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(SALARY_THRESHOLD)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
    }

    ///
    /// 测试s less than comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with less than")
    void shouldFilterWithLessThan(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).lt(SALARY_THRESHOLD)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
    }

    ///
    /// 测试s less than or equal comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with less than or equal")
    void shouldFilterWithLessThanOrEqual(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).le(SALARY_THRESHOLD)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
    }

    ///
    /// 测试s between comparison.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between")
    void shouldFilterWithBetween(IntegrationTestContext context) {
        // Given
        double minSalary = 55000.0;
        double maxSalary = 75000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(minSalary, maxSalary)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
    }

    ///
    /// 测试s between with boundary values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle between with boundary values")
    void shouldHandleBetweenBoundary(IntegrationTestContext context) {
        // Given
        double minSalary = 60000.0;
        double maxSalary = 70000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).between(minSalary, maxSalary)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
    }

    ///
    /// 测试s numeric IN clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with numeric IN clause")
    void shouldFilterWithNumericIn(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).in(1L, 2L, 3L)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getDepartmentId() == 1L || e.getDepartmentId() == 2L || e.getDepartmentId() == 3L);
    }

    ///
    /// 测试s numeric NOT IN clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with numeric NOT IN clause")
    void shouldFilterWithNumericNotIn(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).notIn(1L, 2L)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L && e.getDepartmentId() != 2L);
    }

    ///
    /// 测试s ordering by numeric field ascending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field ascending")
    void shouldOrderByNumericAsc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    ///
    /// 测试s ordering by numeric field descending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field descending")
    void shouldOrderByNumericDesc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    ///
    /// 测试s ordering by ID.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by ID ascending")
    void shouldOrderByIdAsc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId())
                    .isGreaterThan(employees.get(i - 1).getId());
        }
    }

    ///
    /// 测试s multiple numeric conditions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple numeric conditions")
    void shouldFilterWithMultipleNumericConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(50000.0)
                .where(Employee::getSalary).le(80000.0)
                .where(Employee::getDepartmentId).eq(1L)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0 && e.getDepartmentId() == 1L);
    }

    ///
    /// 测试s selecting numeric field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select numeric field")
    void shouldSelectNumericField(IntegrationTestContext context) {
        // When
        List<Double> salaries = context.queryEmployees()
                .select(Employee::getSalary)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(salaries).isNotEmpty();
        assertThat(salaries).doesNotContainNull();
    }

    ///
    /// 测试s selecting ID field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select ID field")
    void shouldSelectIdField(IntegrationTestContext context) {
        // When
        List<Long> ids = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(ids).isNotEmpty();
        assertThat(ids).doesNotContainNull();
    }

    ///
    /// 测试s SUM aggregation on numeric field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum numeric field")
    void shouldSumNumericField(IntegrationTestContext context) {
        // When
        Number sum = context.queryEmployees()
                .select(Path.of(Employee::getSalary).sum())
                .single();

        // Then
        assertThat(sum).isNotNull();
        assertThat(sum.doubleValue()).isPositive();
    }

    ///
    /// 测试s AVG aggregation on numeric field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should average numeric field")
    void shouldAverageNumericField(IntegrationTestContext context) {
        // When
        Number avg = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .single();

        // Then
        assertThat(avg).isNotNull();
        assertThat(avg.doubleValue()).isPositive();
    }

    ///
    /// 测试s MAX aggregation on numeric field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find max of numeric field")
    void shouldFindMaxNumericField(IntegrationTestContext context) {
        // When
        Number max = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .single();

        // Then
        assertThat(max).isNotNull();
        // Verify it's actually the max
        List<Employee> employees = context.queryEmployees().list();
        double expectedMax = employees.stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertThat(max.doubleValue()).isEqualTo(expectedMax);
    }

    ///
    /// 测试s MIN aggregation on numeric field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find min of numeric field")
    void shouldFindMinNumericField(IntegrationTestContext context) {
        // When
        Number min = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .single();

        // Then
        assertThat(min).isNotNull();
        // Verify it's actually the min
        List<Employee> employees = context.queryEmployees().list();
        double expectedMin = employees.stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(0);
        assertThat(min.doubleValue()).isEqualTo(expectedMin);
    }

    ///
    /// 测试s numeric comparison with ID field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with ID comparison")
    void shouldFilterWithIdComparison(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).gt(5L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() > 5L);
    }

    ///
    /// 测试s selecting multiple numeric fields.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select multiple numeric fields")
    void shouldSelectMultipleNumericFields(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Employee::getId, Employee::getSalary)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get0() != null && t.get1() != null);
    }

    ///
    /// 测试s numeric filter with aggregation.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with numeric filter")
    void shouldAggregateWithNumericFilter(IntegrationTestContext context) {
        // When
        Number avg = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .where(Employee::getDepartmentId).eq(1L)
                .single();

        // Then
        assertThat(avg).isNotNull();
        assertThat(avg.doubleValue()).isPositive();
    }

    ///
    /// 测试s distinct numeric values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct numeric values")
    void shouldSelectDistinctNumeric(IntegrationTestContext context) {
        // When
        List<Long> deptIds = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(deptIds).isNotEmpty();
        assertThat(deptIds).doesNotHaveDuplicates();
    }

    ///
    /// 测试s count of numeric filtered results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count numeric filtered results")
    void shouldCountNumericFiltered(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    ///
    /// 测试s numeric comparison with exist.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with numeric filter")
    void shouldCheckExistenceWithNumericFilter(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getSalary).gt(100000.0)
                .exists();

        // Then - depends on test data
        // Just verify it returns a boolean
        assertThat(exists).isFalse();
    }

    ///
    /// 测试s ordering by multiple numeric fields.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by multiple numeric fields")
    void shouldOrderByMultipleNumericFields(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();

        Long prevDeptId = null;
        Double prevSalary = null;
        for (Employee emp : employees) {
            if (prevDeptId != null && prevDeptId.equals(emp.getDepartmentId())) {
                assertThat(emp.getSalary()).isLessThanOrEqualTo(prevSalary);
            }
            prevDeptId = emp.getDepartmentId();
            prevSalary = emp.getSalary();
        }
    }

    ///
    /// 测试s numeric 操作s with first result.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with numeric filter")
    void shouldGetFirstWithNumericFilter(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .orderBy(Employee::getSalary).desc()
                .first();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getSalary()).isGreaterThan(SALARY_THRESHOLD);
    }

    // ==================== Arithmetic Operations ====================

    ///
    /// 测试s addition 操作 in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should add value to numeric field in SELECT")
    void shouldAddValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double bonus = 5000.0;

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).add(bonus))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary + bonus);
    }

    ///
    /// 测试s addition 操作 with literal expression in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should add literal expression to numeric field in SELECT")
    void shouldAddLiteralValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        var bonus = EntityRoot.<Employee>of().literal(5000.0);

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).add(bonus))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary + 5000.0);
    }

    ///
    /// 测试s subtraction 操作 in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should subtract value from numeric field in SELECT")
    void shouldSubtractValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double deduction = 1000.0;

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).subtract(deduction))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary - deduction);
    }

    ///
    /// 测试s subtraction 操作 with literal expression in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should subtract literal expression from numeric field in SELECT")
    void shouldSubtractLiteralValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        var deduction = EntityRoot.<Employee>of().literal(1000.0);

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).subtract(deduction))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary - 1000.0);
    }

    ///
    /// 测试s multiplication 操作 in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should multiply numeric field in SELECT")
    void shouldMultiplyValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double multiplier = 1.1; // 10% raise

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).multiply(multiplier))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary * multiplier);
    }

    ///
    /// 测试s multiplication 操作 with literal expression in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should multiply numeric field with literal expression in SELECT")
    void shouldMultiplyLiteralValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        var multiplier = EntityRoot.<Employee>of().literal(1.1); // 10% raise

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).multiply(multiplier))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary * 1.1);
    }

    ///
    /// 测试s division 操作 in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should divide numeric field in SELECT")
    void shouldDivideValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double divisor = 12.0; // Monthly salary

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).divide(divisor))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary / divisor);
    }

    ///
    /// 测试s division 操作 with literal expression in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should divide numeric field with literal expression in SELECT")
    void shouldDivideLiteralValueInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        var divisor = EntityRoot.<Employee>of().literal(12.0); // Monthly salary

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).divide(divisor))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary / 12.0);
    }

    ///
    /// 测试s modulo 操作 in SELECT clause with ID field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should modulo numeric field in SELECT")
    void shouldModuloValueInSelect(IntegrationTestContext context) {
        // Given
        Long modValue = 3L;

        // When
        List<Long> results = context.queryEmployees()
                .select(Path.of(Employee::getId).mod(modValue))
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        // Verify modulo results are in expected range [0, modValue)
        assertThat(results).allMatch(n -> n >= 0 && n < modValue);
    }

    ///
    /// 测试s modulo 操作 with literal expression in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should modulo numeric field with literal expression in SELECT")
    void shouldModuloLiteralValueInSelect(IntegrationTestContext context) {
        // Given
        var modValue = EntityRoot.<Employee>of().literal(3L);

        // When
        List<Long> results = context.queryEmployees()
                .select(Path.of(Employee::getId).mod(modValue))
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        // Verify modulo results are in expected range [0, 3)
        assertThat(results).allMatch(n -> n >= 0 && n < 3);
    }

    ///
    /// 测试s chained arithmetic 操作s in SELECT.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain arithmetic operations in SELECT")
    void shouldChainArithmeticOperationsInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        // (salary + 1000) * 1.1 - 500
        Double expected = (originalSalary + 1000.0) * 1.1 - 500.0;

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).add(1000.0).multiply(1.1).subtract(500.0))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(expected);
    }

    ///
    /// 测试s arithmetic in HAVING clause with aggregation.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic with group by and aggregation")
    void shouldUseArithmeticWithGroupBy(IntegrationTestContext context) {
        // Given - average salary by department, multiplied by 1.1
        Double multiplier = 1.1;

        // When - Get average adjusted salary per department
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).multiply(multiplier).avg())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get0() != null && t.get1() != null && t.get1() > 0);
    }

    ///
    /// 测试s arithmetic 操作 in SELECT clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform arithmetic with expression in SELECT")
    void shouldPerformArithmeticWithExpressionInSelect(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double bonus = 1000.0;

        // When - Select salary + literal using expression
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).add(EntityRoot.<Employee>of().literal(bonus)))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalSalary + bonus);
    }

    ///
    /// 测试s arithmetic 操作 in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic in WHERE clause")
    void shouldUseArithmeticInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 70000.0;
        Double bonus = 10000.0;

        // When - Find employees where salary + bonus > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() + bonus > threshold);
    }

    ///
    /// 测试s arithmetic 操作 with literal expression in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic with literal expression in WHERE clause")
    void shouldUseArithmeticWithLiteralInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 70000.0;
        var bonus = EntityRoot.<Employee>of().literal(10000.0);

        // When - Find employees where salary + literal(bonus) > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() + 10000.0 > threshold);
    }

    ///
    /// 测试s subtraction in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use subtraction in WHERE clause")
    void shouldUseSubtractionInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 50000.0;
        Double deduction = 5000.0;

        // When - Find employees where salary - deduction >= threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).subtract(deduction).ge(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() - deduction >= threshold);
    }

    ///
    /// 测试s subtraction with literal expression in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use subtraction with literal expression in WHERE clause")
    void shouldUseSubtractionWithLiteralInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 50000.0;
        var deduction = EntityRoot.<Employee>of().literal(5000.0);

        // When - Find employees where salary - literal(deduction) >= threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).subtract(deduction).ge(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() - 5000.0 >= threshold);
    }

    ///
    /// 测试s multiplication in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use multiplication in WHERE clause")
    void shouldUseMultiplicationInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 100000.0;
        Double multiplier = 2.0;

        // When - Find employees where salary * multiplier > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).multiply(multiplier).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() * multiplier > threshold);
    }

    ///
    /// 测试s multiplication with literal expression in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use multiplication with literal expression in WHERE clause")
    void shouldUseMultiplicationWithLiteralInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 100000.0;
        var multiplier = EntityRoot.<Employee>of().literal(2.0);

        // When - Find employees where salary * literal(multiplier) > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).multiply(multiplier).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() * 2.0 > threshold);
    }

    ///
    /// 测试s division in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use division in WHERE clause")
    void shouldUseDivisionInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 5000.0; // Monthly salary threshold
        Double months = 12.0;

        // When - Find employees where salary / 12 > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).divide(months).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() / months > threshold);
    }

    ///
    /// 测试s division with literal expression in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use division with literal expression in WHERE clause")
    void shouldUseDivisionWithLiteralInWhereClause(IntegrationTestContext context) {
        // Given
        double threshold = 5000.0; // Monthly salary threshold
        var months = EntityRoot.<Employee>of().literal(12.0);

        // When - Find employees where salary / literal(12) > threshold
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).divide(months).gt(threshold)
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() / 12.0 > threshold);
    }

    ///
    /// 测试s modulo in WHERE clause with ID field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use modulo in WHERE clause")
    void shouldUseModuloInWhereClause(IntegrationTestContext context) {
        // Given
        Long modValue = 2L;

        // When - Find employees where id % 2 = 0 (even IDs)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).mod(modValue).eq(0L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() % modValue == 0);
    }

    ///
    /// 测试s modulo in WHERE clause with literal expression.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use modulo with literal expression in WHERE clause")
    void shouldUseModuloWithLiteralInWhereClause(IntegrationTestContext context) {
        // Given
        var modValue = EntityRoot.<Employee>of().literal(2L);

        // When - Find employees where id % literal(2) = 0 (even IDs)
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).mod(modValue).eq(0L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() % 2 == 0);
    }

    ///
    /// 测试s combined arithmetic 操作s in WHERE clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use combined arithmetic in WHERE clause")
    void shouldUseCombinedArithmeticInWhereClause(IntegrationTestContext context) {
        // Given - salary + 10000 > 80000
        Double bonus = 10000.0;
        double threshold = 80000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() + bonus > threshold);
    }

    ///
    /// 测试s arithmetic with aggregation functions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic with aggregation")
    void shouldUseArithmeticWithAggregation(IntegrationTestContext context) {
        // Given
        Double multiplier = 1.1; // 10% increase

        // When - Select sum(salary * multiplier) as total projected payroll
        Double result = context.queryEmployees()
                .select(Path.of(Employee::getSalary).multiply(multiplier).sum())
                .single();

        // Then
        assertThat(result).isNotNull();
        // Verify it's the sum of all salaries * multiplier (with delta for floating point precision)
        Double totalSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).sum())
                .single();
        assertThat(result).isCloseTo(totalSalary * multiplier, org.assertj.core.data.Offset.offset(0.01));
    }

    ///
    /// 测试s arithmetic with count and filter.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count results with arithmetic filter")
    void shouldCountResultsWithArithmeticFilter(IntegrationTestContext context) {
        // Given
        Double threshold = 70000.0;
        Double bonus = 5000.0;

        // When
        long count = context.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    ///
    /// 测试s conditional arithmetic 操作s (addIfNotNull).
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional add with null value")
    void shouldHandleConditionalAddWithNull(IntegrationTestContext context) {
        // Given
        Double nullBonus = null;

        // When - addIfNotNull with null should not change the value
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).addIfNotNull(nullBonus))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(employees.getFirst().getSalary());
    }

    ///
    /// 测试s conditional arithmetic 操作s with non-null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional add with non-null value")
    void shouldHandleConditionalAddWithNonNull(IntegrationTestContext context) {
        // Given
        Double bonus = 5000.0;

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).addIfNotNull(bonus))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(employees.getFirst().getSalary() + bonus);
    }

    ///
    /// 测试s complex arithmetic expression with multiple 操作s.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle complex arithmetic expression")
    void shouldHandleComplexArithmeticExpression(IntegrationTestContext context) {
        // Given - Calculate: (salary * 12 + 1000) / 12 - 100
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        Double originalSalary = employees.getFirst().getSalary();
        Double expected = (originalSalary * 12.0 + 1000.0) / 12.0 - 100.0;

        // When
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).multiply(12.0).add(1000.0).divide(12.0).subtract(100.0))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(expected);
    }

    ///
    /// 测试s arithmetic with distinct select.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct arithmetic results")
    void shouldSelectDistinctArithmeticResults(IntegrationTestContext context) {
        // Given - departmentId % 3 should have limited distinct values
        Long modValue = 3L;

        // When
        List<Long> distinctResults = context.queryEmployees()
                .selectDistinct(Path.of(Employee::getDepartmentId).mod(modValue))
                .list();

        // Then
        assertThat(distinctResults).isNotEmpty();
        assertThat(distinctResults).doesNotHaveDuplicates();
        assertThat(distinctResults).allMatch(n -> n >= 0 && n < modValue);
    }

    ///
    /// 测试s arithmetic 操作s on integer ID field.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform arithmetic on ID field")
    void shouldPerformArithmeticOnIdField(IntegrationTestContext context) {
        // Given
        Long offset = 100L;

        // When
        List<Long> results = context.queryEmployees()
                .select(Path.of(Employee::getId).add(offset))
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertThat(results).isNotEmpty();
        List<Long> originalIds = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .list();
        for (int i = 0; i < results.size(); i++) {
            assertThat(results.get(i)).isEqualTo(originalIds.get(i) + offset);
        }
    }

    ///
    /// 测试s conditional subtract with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional subtract with null value")
    void shouldHandleConditionalSubtractWithNull(IntegrationTestContext context) {
        // Given
        Double nullDeduction = null;

        // When - subtractIfNotNull with null should not change the value
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).subtractIfNotNull(nullDeduction))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(employees.getFirst().getSalary());
    }

    ///
    /// 测试s conditional multiply with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional multiply with null value")
    void shouldHandleConditionalMultiplyWithNull(IntegrationTestContext context) {
        // Given
        Double nullMultiplier = null;

        // When - multiplyIfNotNull with null should not change the value
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).multiplyIfNotNull(nullMultiplier))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(employees.getFirst().getSalary());
    }

    ///
    /// 测试s conditional divide with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional divide with null value")
    void shouldHandleConditionalDivideWithNull(IntegrationTestContext context) {
        // Given
        Double nullDivisor = null;

        // When - divideIfNotNull with null should not change the value
        List<Double> results = context.queryEmployees()
                .select(Path.of(Employee::getSalary).divideIfNotNull(nullDivisor))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(employees.getFirst().getSalary());
    }

    ///
    /// 测试s conditional mod with null value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional mod with null value")
    void shouldHandleConditionalModWithNull(IntegrationTestContext context) {
        // Given
        Long nullModValue = null;

        // When - modIfNotNull with null should not change the value
        List<Long> results = context.queryEmployees()
                .select(Path.of(Employee::getId).modIfNotNull(nullModValue))
                .where(Employee::getId).eq(1L)
                .list();

        // Then
        List<Long> originalIds = context.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getId).eq(1L)
                .list();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst()).isEqualTo(originalIds.getFirst());
    }
}
