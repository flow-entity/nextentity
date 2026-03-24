package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.core.util.Paths;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Numeric operations integration tests.
 * <p>
 * Tests numeric operations including:
 * - Numeric comparisons (gt, ge, lt, le, between)
 * - Arithmetic operations (add, subtract, multiply, divide, mod)
 * - Numeric sorting
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Numeric Operations Integration Tests")
public class NumericOperationsIntegrationTest {

    private static final double SALARY_THRESHOLD = 60000.0;

    /**
     * Tests greater than comparison.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with greater than")
    void shouldFilterWithGreaterThan(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > SALARY_THRESHOLD);
    }

    /**
     * Tests greater than or equal comparison.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with greater than or equal")
    void shouldFilterWithGreaterThanOrEqual(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).ge(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() >= SALARY_THRESHOLD);
    }

    /**
     * Tests less than comparison.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with less than")
    void shouldFilterWithLessThan(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).lt(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() < SALARY_THRESHOLD);
    }

    /**
     * Tests less than or equal comparison.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with less than or equal")
    void shouldFilterWithLessThanOrEqual(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).le(SALARY_THRESHOLD)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() <= SALARY_THRESHOLD);
    }

    /**
     * Tests between comparison.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with between")
    void shouldFilterWithBetween(DbConfig config) {
        // Given
        double minSalary = 55000.0;
        double maxSalary = 75000.0;

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).between(minSalary, maxSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
    }

    /**
     * Tests between with boundary values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle between with boundary values")
    void shouldHandleBetweenBoundary(DbConfig config) {
        // Given
        double minSalary = 60000.0;
        double maxSalary = 70000.0;

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).between(minSalary, maxSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= minSalary && e.getSalary() <= maxSalary);
    }

    /**
     * Tests numeric IN clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with numeric IN clause")
    void shouldFilterWithNumericIn(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).in(1L, 2L, 3L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getDepartmentId() == 1L || e.getDepartmentId() == 2L || e.getDepartmentId() == 3L);
    }

    /**
     * Tests numeric NOT IN clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with numeric NOT IN clause")
    void shouldFilterWithNumericNotIn(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).notIn(1L, 2L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getDepartmentId() != 1L && e.getDepartmentId() != 2L);
    }

    /**
     * Tests ordering by numeric field ascending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field ascending")
    void shouldOrderByNumericAsc(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Tests ordering by numeric field descending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field descending")
    void shouldOrderByNumericDesc(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Tests ordering by ID.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by ID ascending")
    void shouldOrderByIdAsc(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId())
                    .isGreaterThan(employees.get(i - 1).getId());
        }
    }

    /**
     * Tests multiple numeric conditions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with multiple numeric conditions")
    void shouldFilterWithMultipleNumericConditions(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).ge(50000.0)
                .where(Employee::getSalary).le(80000.0)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e ->
                e.getSalary() >= 50000.0 && e.getSalary() <= 80000.0 && e.getDepartmentId() == 1L);
    }

    /**
     * Tests selecting numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select numeric field")
    void shouldSelectNumericField(DbConfig config) {
        // When
        List<Double> salaries = config.queryEmployees()
                .select(Employee::getSalary)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(salaries).isNotEmpty();
        assertThat(salaries).doesNotContainNull();
    }

    /**
     * Tests selecting ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select ID field")
    void shouldSelectIdField(DbConfig config) {
        // When
        List<Long> ids = config.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(ids).isNotEmpty();
        assertThat(ids).doesNotContainNull();
    }

    /**
     * Tests SUM aggregation on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum numeric field")
    void shouldSumNumericField(DbConfig config) {
        // When
        Number sum = config.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getSingle();

        // Then
        assertThat(sum).isNotNull();
        assertThat(sum.doubleValue()).isPositive();
    }

    /**
     * Tests AVG aggregation on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should average numeric field")
    void shouldAverageNumericField(DbConfig config) {
        // When
        Number avg = config.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getSingle();

        // Then
        assertThat(avg).isNotNull();
        assertThat(avg.doubleValue()).isPositive();
    }

    /**
     * Tests MAX aggregation on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find max of numeric field")
    void shouldFindMaxNumericField(DbConfig config) {
        // When
        Number max = config.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getSingle();

        // Then
        assertThat(max).isNotNull();
        // Verify it's actually the max
        List<Employee> employees = config.queryEmployees().getList();
        double expectedMax = employees.stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertThat(max.doubleValue()).isEqualTo(expectedMax);
    }

    /**
     * Tests MIN aggregation on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find min of numeric field")
    void shouldFindMinNumericField(DbConfig config) {
        // When
        Number min = config.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getSingle();

        // Then
        assertThat(min).isNotNull();
        // Verify it's actually the min
        List<Employee> employees = config.queryEmployees().getList();
        double expectedMin = employees.stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(0);
        assertThat(min.doubleValue()).isEqualTo(expectedMin);
    }

    /**
     * Tests numeric comparison with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with ID comparison")
    void shouldFilterWithIdComparison(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).gt(5L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() > 5L);
    }

    /**
     * Tests selecting multiple numeric fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select multiple numeric fields")
    void shouldSelectMultipleNumericFields(DbConfig config) {
        // When
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(Employee::getId, Employee::getSalary)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get0() != null && t.get1() != null);
    }

    /**
     * Tests numeric filter with aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with numeric filter")
    void shouldAggregateWithNumericFilter(DbConfig config) {
        // When
        Number avg = config.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .where(Employee::getDepartmentId).eq(1L)
                .getSingle();

        // Then
        assertThat(avg).isNotNull();
        assertThat(avg.doubleValue()).isPositive();
    }

    /**
     * Tests distinct numeric values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct numeric values")
    void shouldSelectDistinctNumeric(DbConfig config) {
        // When
        List<Long> deptIds = config.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(deptIds).isNotEmpty();
        assertThat(deptIds).doesNotHaveDuplicates();
    }

    /**
     * Tests count of numeric filtered results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count numeric filtered results")
    void shouldCountNumericFiltered(DbConfig config) {
        // When
        long count = config.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    /**
     * Tests numeric comparison with exist.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with numeric filter")
    void shouldCheckExistenceWithNumericFilter(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getSalary).gt(100000.0)
                .exist();

        // Then - depends on test data
        // Just verify it returns a boolean
        assertThat(exists).isFalse();
    }

    /**
     * Tests ordering by multiple numeric fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by multiple numeric fields")
    void shouldOrderByMultipleNumericFields(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .getList();

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

    /**
     * Tests numeric operations with first result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with numeric filter")
    void shouldGetFirstWithNumericFilter(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getSalary).gt(SALARY_THRESHOLD)
                .orderBy(Employee::getSalary).desc()
                .getFirst();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getSalary()).isGreaterThan(SALARY_THRESHOLD);
    }

    // ==================== Arithmetic Operations ====================

    /**
     * Tests addition operation in SELECT clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should add value to numeric field in SELECT")
    void shouldAddValueInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double bonus = 5000.0;

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).add(bonus))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalSalary + bonus);
    }

    /**
     * Tests subtraction operation in SELECT clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should subtract value from numeric field in SELECT")
    void shouldSubtractValueInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double deduction = 1000.0;

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).subtract(deduction))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalSalary - deduction);
    }

    /**
     * Tests multiplication operation in SELECT clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should multiply numeric field in SELECT")
    void shouldMultiplyValueInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double multiplier = 1.1; // 10% raise

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).multiply(multiplier))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalSalary * multiplier);
    }

    /**
     * Tests division operation in SELECT clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should divide numeric field in SELECT")
    void shouldDivideValueInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double divisor = 12.0; // Monthly salary

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).divide(divisor))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalSalary / divisor);
    }

    /**
     * Tests modulo operation in SELECT clause with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should modulo numeric field in SELECT")
    void shouldModuloValueInSelect(DbConfig config) {
        // Given
        Long modValue = 3L;

        // When
        List<Long> results = config.queryEmployees()
                .select(get(Employee::getId).mod(modValue))
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        // Verify modulo results are in expected range [0, modValue)
        assertThat(results).allMatch(n -> n >= 0 && n < modValue);
    }

    /**
     * Tests chained arithmetic operations in SELECT.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain arithmetic operations in SELECT")
    void shouldChainArithmeticOperationsInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        // (salary + 1000) * 1.1 - 500
        Double expected = (originalSalary + 1000.0) * 1.1 - 500.0;

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).add(1000.0).multiply(1.1).subtract(500.0))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(expected);
    }

    /**
     * Tests arithmetic in HAVING clause with aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic with group by and aggregation")
    void shouldUseArithmeticWithGroupBy(DbConfig config) {
        // Given - average salary by department, multiplied by 1.1
        Double multiplier = 1.1;

        // When - Get average adjusted salary per department
        List<Tuple2<Long, Double>> results = config.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).multiply(multiplier).avg())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(t -> t.get0() != null && t.get1() != null && t.get1() > 0);
    }

    /**
     * Tests arithmetic operation in SELECT clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform arithmetic with expression in SELECT")
    void shouldPerformArithmeticWithExpressionInSelect(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double bonus = 1000.0;

        // When - Select salary + literal using expression
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).add(Paths.<Employee>root().literal(bonus)))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalSalary + bonus);
    }

    /**
     * Tests arithmetic operation in WHERE clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic in WHERE clause")
    void shouldUseArithmeticInWhereClause(DbConfig config) {
        // Given
        double threshold = 70000.0;
        Double bonus = 10000.0;

        // When - Find employees where salary + bonus > threshold
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() + bonus > threshold);
    }

    /**
     * Tests subtraction in WHERE clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use subtraction in WHERE clause")
    void shouldUseSubtractionInWhereClause(DbConfig config) {
        // Given
        double threshold = 50000.0;
        Double deduction = 5000.0;

        // When - Find employees where salary - deduction >= threshold
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).subtract(deduction).ge(threshold)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() - deduction >= threshold);
    }

    /**
     * Tests multiplication in WHERE clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use multiplication in WHERE clause")
    void shouldUseMultiplicationInWhereClause(DbConfig config) {
        // Given
        double threshold = 100000.0;
        Double multiplier = 2.0;

        // When - Find employees where salary * multiplier > threshold
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).multiply(multiplier).gt(threshold)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() * multiplier > threshold);
    }

    /**
     * Tests division in WHERE clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use division in WHERE clause")
    void shouldUseDivisionInWhereClause(DbConfig config) {
        // Given
        double threshold = 5000.0; // Monthly salary threshold
        Double months = 12.0;

        // When - Find employees where salary / 12 > threshold
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).divide(months).gt(threshold)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() / months > threshold);
    }

    /**
     * Tests modulo in WHERE clause with ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use modulo in WHERE clause")
    void shouldUseModuloInWhereClause(DbConfig config) {
        // Given
        Long modValue = 2L;

        // When - Find employees where id % 2 = 0 (even IDs)
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).mod(modValue).eq(0L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getId() % modValue == 0);
    }

    /**
     * Tests combined arithmetic operations in WHERE clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use combined arithmetic in WHERE clause")
    void shouldUseCombinedArithmeticInWhereClause(DbConfig config) {
        // Given - salary + 10000 > 80000
        Double bonus = 10000.0;
        double threshold = 80000.0;

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() + bonus > threshold);
    }

    /**
     * Tests arithmetic with aggregation functions.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use arithmetic with aggregation")
    void shouldUseArithmeticWithAggregation(DbConfig config) {
        // Given
        Double multiplier = 1.1; // 10% increase

        // When - Select sum(salary * multiplier) as total projected payroll
        Double result = config.queryEmployees()
                .select(get(Employee::getSalary).multiply(multiplier).sum())
                .getSingle();

        // Then
        assertThat(result).isNotNull();
        // Verify it's the sum of all salaries * multiplier (with delta for floating point precision)
        Double totalSalary = config.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getSingle();
        assertThat(result).isCloseTo(totalSalary * multiplier, org.assertj.core.data.Offset.offset(0.01));
    }

    /**
     * Tests arithmetic with count and filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count results with arithmetic filter")
    void shouldCountResultsWithArithmeticFilter(DbConfig config) {
        // Given
        Double threshold = 70000.0;
        Double bonus = 5000.0;

        // When
        long count = config.queryEmployees()
                .where(Employee::getSalary).add(bonus).gt(threshold)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    /**
     * Tests conditional arithmetic operations (addIfNotNull).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional add with null value")
    void shouldHandleConditionalAddWithNull(DbConfig config) {
        // Given
        Double nullBonus = null;

        // When - addIfNotNull with null should not change the value
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).addIfNotNull(nullBonus))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(employees.get(0).getSalary());
    }

    /**
     * Tests conditional arithmetic operations with non-null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional add with non-null value")
    void shouldHandleConditionalAddWithNonNull(DbConfig config) {
        // Given
        Double bonus = 5000.0;

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).addIfNotNull(bonus))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(employees.get(0).getSalary() + bonus);
    }

    /**
     * Tests complex arithmetic expression with multiple operations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle complex arithmetic expression")
    void shouldHandleComplexArithmeticExpression(DbConfig config) {
        // Given - Calculate: (salary * 12 + 1000) / 12 - 100
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        Double originalSalary = employees.get(0).getSalary();
        Double expected = (originalSalary * 12.0 + 1000.0) / 12.0 - 100.0;

        // When
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).multiply(12.0).add(1000.0).divide(12.0).subtract(100.0))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(expected);
    }

    /**
     * Tests arithmetic with distinct select.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select distinct arithmetic results")
    void shouldSelectDistinctArithmeticResults(DbConfig config) {
        // Given - departmentId % 3 should have limited distinct values
        Long modValue = 3L;

        // When
        List<Long> distinctResults = config.queryEmployees()
                .selectDistinct(get(Employee::getDepartmentId).mod(modValue))
                .getList();

        // Then
        assertThat(distinctResults).isNotEmpty();
        assertThat(distinctResults).doesNotHaveDuplicates();
        assertThat(distinctResults).allMatch(n -> n >= 0 && n < modValue);
    }

    /**
     * Tests arithmetic operations on integer ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform arithmetic on ID field")
    void shouldPerformArithmeticOnIdField(DbConfig config) {
        // Given
        Long offset = 100L;

        // When
        List<Long> results = config.queryEmployees()
                .select(get(Employee::getId).add(offset))
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
        List<Long> originalIds = config.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .getList();
        for (int i = 0; i < results.size(); i++) {
            assertThat(results.get(i)).isEqualTo(originalIds.get(i) + offset);
        }
    }

    /**
     * Tests conditional subtract with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional subtract with null value")
    void shouldHandleConditionalSubtractWithNull(DbConfig config) {
        // Given
        Double nullDeduction = null;

        // When - subtractIfNotNull with null should not change the value
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).subtractIfNotNull(nullDeduction))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(employees.get(0).getSalary());
    }

    /**
     * Tests conditional multiply with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional multiply with null value")
    void shouldHandleConditionalMultiplyWithNull(DbConfig config) {
        // Given
        Double nullMultiplier = null;

        // When - multiplyIfNotNull with null should not change the value
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).multiplyIfNotNull(nullMultiplier))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(employees.get(0).getSalary());
    }

    /**
     * Tests conditional divide with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional divide with null value")
    void shouldHandleConditionalDivideWithNull(DbConfig config) {
        // Given
        Double nullDivisor = null;

        // When - divideIfNotNull with null should not change the value
        List<Double> results = config.queryEmployees()
                .select(get(Employee::getSalary).divideIfNotNull(nullDivisor))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(employees.get(0).getSalary());
    }

    /**
     * Tests conditional mod with null value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle conditional mod with null value")
    void shouldHandleConditionalModWithNull(DbConfig config) {
        // Given
        Long nullModValue = null;

        // When - modIfNotNull with null should not change the value
        List<Long> results = config.queryEmployees()
                .select(get(Employee::getId).modIfNotNull(nullModValue))
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        List<Long> originalIds = config.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getId).eq(1L)
                .getList();
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(originalIds.get(0));
    }
}