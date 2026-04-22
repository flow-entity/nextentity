package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// 聚合函数集成测试。
/// <p>
/// 测试聚合函数包括：
/// - COUNT
/// - SUM
/// - AVG
/// - MAX
/// - MIN
/// - GROUP BY
/// <p>
/// 这些测试使用 Testcontainers 针对 MySQL 和 PostgreSQL 运行。
///
/// @author HuangChengwei
@DisplayName("Aggregate Functions Integration Tests")
public class AggregateFunctionsIntegrationTest {

    /// 测试 COUNT 聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees with count()")
    void shouldCountEmployees(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees().count();

        // Then
        assertEquals(12, count);
    }

    /// 测试带有表达式的 COUNT。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with count expression")
    void shouldCountWithExpression(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(Path.of(Employee::getId).count())
                .single();

        // Then
        assertEquals(12L, count);
    }

    /// 测试 COUNT DISTINCT。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count distinct department IDs")
    void shouldCountDistinct(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId).countDistinct())
                .single();

        // Then
        assertEquals(5L, count);
    }

    /// 测试 SUM 聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum employee salaries")
    void shouldSumSalaries(IntegrationTestContext context) {
        // When
        Number sum = context.queryEmployees()
                .select(Path.of(Employee::getSalary).sum())
                .single();

        // Then
        assertNotNull(sum);
        double expectedSum = context.queryEmployees().list().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, sum.doubleValue(), 0.01);
    }

    /// 测试 AVG 聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should calculate average salary")
    void shouldCalculateAverageSalary(IntegrationTestContext context) {
        // When
        Number avg = context.queryEmployees()
                .select(Path.of(Employee::getSalary).avg())
                .single();

        // Then
        assertNotNull(avg);
        double expectedAvg = context.queryEmployees().list().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
        assertEquals(expectedAvg, avg.doubleValue(), 0.01);
    }

    /// 测试 MAX 聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find maximum salary")
    void shouldFindMaxSalary(IntegrationTestContext context) {
        // When
        Number max = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .single();

        // Then
        assertNotNull(max);
        double expectedMax = context.queryEmployees().list().stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertEquals(expectedMax, max.doubleValue(), 0.01);
    }

    /// 测试 MIN 聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find minimum salary")
    void shouldFindMinSalary(IntegrationTestContext context) {
        // When
        Number min = context.queryEmployees()
                .select(Path.of(Employee::getSalary).min())
                .single();

        // Then
        assertNotNull(min);
        double expectedMin = context.queryEmployees().list().stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(0);
        assertEquals(expectedMin, min.doubleValue(), 0.01);
    }

    /// 测试单个查询中的多个聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return multiple aggregations")
    void shouldReturnMultipleAggregations(IntegrationTestContext context) {
        // When
        Tuple aggregations = context.queryEmployees()
                .select(
                        Path.of(Employee::getSalary).min(),
                        Path.of(Employee::getSalary).max()
                )
                .single();

        // Then
        assertNotNull(aggregations);
        Number min = aggregations.get(0);
        Number max = aggregations.get(1);

        List<Employee> employees = context.queryEmployees().list();
        double expectedMin = employees.stream().mapToDouble(Employee::getSalary).min().orElse(0);
        double expectedMax = employees.stream().mapToDouble(Employee::getSalary).max().orElse(0);

        assertEquals(expectedMin, min.doubleValue(), 0.01);
        assertEquals(expectedMax, max.doubleValue(), 0.01);
    }

    /// 测试单列分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department ID")
    void shouldGroupByDepartmentId(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify first group (Department 1 should have 5 employees)
        Tuple2<Long, Long> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());
        assertEquals(5L, dept1.get1());
    }

    /// 测试带有聚合的分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with count")
    void shouldGroupByWithCount(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getId).count())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify total count matches
        long totalCount = results.stream().mapToLong(Tuple2::get1).sum();
        assertEquals(12, totalCount);
    }

    /// 测试带有 SUM 的分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with sum of salaries")
    void shouldGroupByWithSum(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).sum())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify department 1 salary sum
        Tuple2<Long, Double> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());

        double expectedSum = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, dept1.get1(), 0.01);
    }

    /// 测试带有 AVG 的分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by department with average salary")
    void shouldGroupByWithAvg(IntegrationTestContext context) {
        // When
        List<Tuple2<Long, Double>> results = context.queryEmployees()
                .select(Path.of(Employee::getDepartmentId), Path.of(Employee::getSalary).avg())
                .groupBy(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(results);
        assertEquals(5, results.size());

        // Verify department 1 average salary
        Tuple2<Long, Double> dept1 = results.get(0);
        assertEquals(1L, dept1.get0());

        double expectedAvg = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0);
        assertEquals(expectedAvg, dept1.get1(), 0.01);
    }

    /// 测试多列分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by multiple columns")
    void shouldGroupByMultipleColumns(IntegrationTestContext context) {
        // When
        List<?> results = context.queryEmployees()
                .select(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getId).count()
                )
                .groupBy(Employee::getDepartmentId, Employee::getActive)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify total count
        long totalCount = results.stream()
                .mapToLong(t -> ((Tuple) t).<Long>get(2))
                .sum();
        assertEquals(12, totalCount);
    }

    /// 测试带有 WHERE 条件的聚合。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should aggregate with WHERE condition")
    void shouldAggregateWithWhereCondition(IntegrationTestContext context) {
        // When - count active employees
        Long activeCount = context.queryEmployees()
                .select(Path.of(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .single();

        // Then
        assertNotNull(activeCount);
        long expectedActive = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .list().size();
        assertEquals(expectedActive, activeCount);
    }

    /// 测试 ID 字段上的 MAX。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find max ID")
    void shouldFindMaxId(IntegrationTestContext context) {
        // When
        Number maxId = context.queryEmployees()
                .select(Path.of(Employee::getId).max())
                .single();

        // Then
        assertNotNull(maxId);
        long expectedMax = context.queryEmployees().list().stream()
                .mapToLong(Employee::getId)
                .max()
                .orElse(0);
        assertEquals(expectedMax, maxId.longValue());
    }

    /// 测试 ID 字段上的 MIN。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find min ID")
    void shouldFindMinId(IntegrationTestContext context) {
        // When
        Number minId = context.queryEmployees()
                .select(Path.of(Employee::getId).min())
                .single();

        // Then
        assertNotNull(minId);
        long expectedMin = context.queryEmployees().list().stream()
                .mapToLong(Employee::getId)
                .min()
                .orElse(0);
        assertEquals(expectedMin, minId.longValue());
    }

    /// 测试带有活动状态过滤的 COUNT。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count active employees")
    void shouldCountActiveEmployees(IntegrationTestContext context) {
        // When
        Long count = context.queryEmployees()
                .select(Path.of(Employee::getId).count())
                .where(Employee::getActive).eq(true)
                .single();

        // Then
        assertNotNull(count);
        long expectedCount = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .list().size();
        assertEquals(expectedCount, count);
    }

    /// 测试带有部门过滤的 SUM。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should sum salaries for department")
    void shouldSumSalariesForDepartment(IntegrationTestContext context) {
        // When
        Number sum = context.queryEmployees()
                .select(Path.of(Employee::getSalary).sum())
                .where(Employee::getDepartmentId).eq(1L)
                .single();

        // Then
        assertNotNull(sum);
        double expectedSum = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list().stream()
                .mapToDouble(Employee::getSalary)
                .sum();
        assertEquals(expectedSum, sum.doubleValue(), 0.01);
    }

    /// 测试按员工状态分组。
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by employee status")
    void shouldGroupByStatus(IntegrationTestContext context) {
        // When
        List<Tuple2<EmployeeStatus, Long>> results = context.queryEmployees()
                .select(Path.of(Employee::getStatus), Path.of(Employee::getId).count())
                .groupBy(Employee::getStatus)
                .list();

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify total count
        long totalCount = results.stream().mapToLong(Tuple2::get1).sum();
        assertEquals(12, totalCount);
    }
}