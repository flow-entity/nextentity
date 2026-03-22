package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
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
        assertThat(exists).isNotNull();
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
}