package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.github.nextentity.core.util.Paths.get;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Aggregate function integration tests.
 * <p>
 * Tests aggregate functions including:
 * - COUNT
 * - SUM
 * - AVG
 * - MAX
 * - MIN
 * - GROUP BY
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Aggregate Functions Integration Tests")
public class AggregateFunctionsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(AggregateFunctionsIntegrationTest.class);

    /**
     * Tests COUNT aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees with count()")
    void shouldCountEmployees(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees().count();

        // Then
        assertEquals(12, count);
    }

    /**
     * Tests COUNT with expression.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with count expression")
    void shouldCountWithExpression(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(get(Employee::getId).count())
                .getSingle();

        // Then
        assertEquals(12L, count);
    }

    /**
     * Tests COUNT DISTINCT.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count distinct department IDs")
    void shouldCountDistinct(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(get(Employee::getDepartmentId).countDistinct())
                .getSingle();

        // Then
        assertEquals(5L, count);
    }

    /**
     * Tests SUM aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum employee salaries")
    void shouldSumSalaries(IntegrationTestContext context) {
        // When
        Number sum = context.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .getSingle();

        // Then
        assertNotNull(sum);
        double expectedSum = context.queryEmployees().getList().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, sum.doubleValue(), 0.01);
    }

    /**
     * Tests AVG aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should calculate average salary")
    void shouldCalculateAverageSalary(IntegrationTestContext context) {
        // When
        Number avg = context.queryEmployees()
                .select(get(Employee::getSalary).avg())
                .getSingle();

        // Then
        assertNotNull(avg);
        double expectedAvg = context.queryEmployees().getList().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
        assertEquals(expectedAvg, avg.doubleValue(), 0.01);
    }

    /**
     * Tests MAX aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find maximum salary")
    void shouldFindMaxSalary(IntegrationTestContext context) {
        // When
        Number max = context.queryEmployees()
                .select(get(Employee::getSalary).max())
                .getSingle();

        // Then
        assertNotNull(max);
        double expectedMax = context.queryEmployees().getList().stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertEquals(expectedMax, max.doubleValue(), 0.01);
    }

    /**
     * Tests MIN aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find minimum salary")
    void shouldFindMinSalary(IntegrationTestContext context) {
        // When
        Number min = context.queryEmployees()
                .select(get(Employee::getSalary).min())
                .getSingle();

        // Then
        assertNotNull(min);
        double expectedMin = context.queryEmployees().getList().stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(0);
        assertEquals(expectedMin, min.doubleValue(), 0.01);
    }

    /**
     * Tests multiple aggregations in single query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return multiple aggregations")
    void shouldReturnMultipleAggregations(IntegrationTestContext context) {
        // When
        Tuple aggregations = context.queryEmployees()
                .select(
                        get(Employee::getSalary).min(),
                        get(Employee::getSalary).max()
                )
                .getSingle();

        // Then
        assertNotNull(aggregations);
        Number min = aggregations.get(0);
        Number max = aggregations.get(1);

        List<Employee> employees = context.queryEmployees().getList();
        double expectedMin = employees.stream().mapToDouble(Employee::getSalary).min().orElse(0);
        double expectedMax = employees.stream().mapToDouble(Employee::getSalary).max().orElse(0);

        assertEquals(expectedMin, min.doubleValue(), 0.01);
        assertEquals(expectedMax, max.doubleValue(), 0.01);
    }

    /**
     * Tests GROUP BY single column.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department ID")
    void shouldGroupByDepartmentId(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify first group (Department 1 should have 5 employees)
        Tuple2<Long, Long> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());
        assertEquals(5L, dept1.get1());
    }

    /**
     * Tests GROUP BY with aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with count")
    void shouldGroupByWithCount(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(results);
        assertTrue(results.size() > 0);

        // Verify total count matches
        long totalCount = results.stream().mapToLong(t -> t.get1()).sum();
        assertEquals(12, totalCount);
    }

    /**
     * Tests GROUP BY with SUM.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with sum of salaries")
    void shouldGroupByWithSum(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify department 1 salary sum
        Tuple2<Long, Double> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());

        double expectedSum = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, dept1.get1(), 0.01);
    }

    /**
     * Tests GROUP BY with AVG.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with average salary")
    void shouldGroupByWithAvg(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(get(Employee::getDepartmentId), get(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify department 1 average salary
        Tuple2<Long, Double> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());

        double expectedAvg = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
        assertEquals(expectedAvg, dept1.get1(), 0.01);
    }

    /**
     * Tests GROUP BY multiple columns.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by multiple columns")
    void shouldGroupByMultipleColumns(IntegrationTestContext context) {
        // When
        List<?> results = context.queryEmployees()
                .select(
                        get(Employee::getDepartmentId),
                        get(Employee::getActive),
                        get(Employee::getId).count()
                )
                .groupBy(Employee::getDepartmentId, Employee::getActive)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(results);
        assertTrue(results.size() > 0);

        // Verify total count
        long totalCount = results.stream()
                .mapToLong(t -> ((Tuple)t).<Long>get(2))
                .sum();
        assertEquals(12, totalCount);
    }

    /**
     * Tests aggregation with WHERE condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with WHERE condition")
    void shouldAggregateWithWhereCondition(IntegrationTestContext context) {
        // When - count active employees
        Long activeCount = context.queryEmployees()
                .select(get(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .getSingle();

        // Then
        assertNotNull(activeCount);
        long expectedActive = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .getList().size();
        assertEquals(expectedActive, activeCount);
    }

    /**
     * Tests MAX on ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find max ID")
    void shouldFindMaxId(IntegrationTestContext context) {
        // When
        Number maxId = context.queryEmployees()
                .select(get(Employee::getId).max())
                .getSingle();

        // Then
        assertNotNull(maxId);
        long expectedMax = context.queryEmployees().getList().stream()
                .mapToLong(Employee::getId)
                .max()
                .orElse(0);
        assertEquals(expectedMax, maxId.longValue());
    }

    /**
     * Tests MIN on ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find min ID")
    void shouldFindMinId(IntegrationTestContext context) {
        // When
        Number minId = context.queryEmployees()
                .select(get(Employee::getId).min())
                .getSingle();

        // Then
        assertNotNull(minId);
        long expectedMin = context.queryEmployees().getList().stream()
                .mapToLong(Employee::getId)
                .min()
                .orElse(0);
        assertEquals(expectedMin, minId.longValue());
    }

    /**
     * Tests COUNT with active status filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count active employees")
    void shouldCountActiveEmployees(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(get(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .getSingle();

        // Then
        assertNotNull(count);
        long expectedCount = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .getList().size();
        assertEquals(expectedCount, count);
    }

    /**
     * Tests SUM with department filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum salaries for department")
    void shouldSumSalariesForDepartment(IntegrationTestContext context) {
        // When
        Number sum = context.queryEmployees()
                .select(get(Employee::getSalary).sum())
                .where(Employee::getDepartmentId).eq(1L)
                .getSingle();

        // Then
        assertNotNull(sum);
        double expectedSum = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, sum.doubleValue(), 0.01);
    }

    /**
     * Tests GROUP BY with employee status.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by employee status")
    void shouldGroupByStatus(IntegrationTestContext context) {
        // When
        List<Tuple2<EmployeeStatus, Long>> results = context.queryEmployees()
                .select(get(Employee::getStatus), get(Employee::getId).count())
                .groupBy(Employee::getStatus)
                .getList();

        // Then
        assertNotNull(results);
        assertTrue(results.size() > 0);

        // Verify total count
        long totalCount = results.stream().mapToLong(t -> t.get1()).sum();
        assertEquals(12, totalCount);
    }
}
