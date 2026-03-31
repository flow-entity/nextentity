package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.api.model.Tuple4;
import io.github.nextentity.api.model.Tuple5;
import io.github.nextentity.api.model.Tuple6;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for GroupByStep multiple parameter groupBy methods.
 * <p>
 * Tests default methods in GroupByStep interface including:
 * - groupBy(Path, Path, Path): Group by three paths
 * - groupBy(Path, Path, Path, Path): Group by four paths
 * - groupBy(Path, Path, Path, Path, Path): Group by five paths
 * - groupBy(Path, Path, Path, Path, Path, Path): Group by six paths
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.GroupByStep
 */
@DisplayName("GroupByStep Multiple Parameters Integration Tests")
public class GroupByStepMultipleParametersIntegrationTest {

    // ==================== groupBy(Path, Path, Path) Tests ====================

    /**
     * Tests groupBy with three paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by three paths")
    void shouldGroupByThreePaths(IntegrationTestContext context) {
        // When
        List<Tuple3<Long, Boolean, Long>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getId).count()
                )
                .groupBy(Employee::getDepartmentId, Employee::getActive, Employee::getStatus)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    /**
     * Tests groupBy with three paths and sum aggregation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by three paths with sum")
    void shouldGroupByThreePathsWithSum(IntegrationTestContext context) {
        // When
        List<Tuple3<Long, Boolean, Double>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getSalary).sum()
                )
                .groupBy(Employee::getDepartmentId, Employee::getActive, Employee::getStatus)
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ==================== groupBy(Path, Path, Path, Path) Tests ====================

    /**
     * Tests groupBy with four paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by four paths")
    void shouldGroupByFourPaths(IntegrationTestContext context) {
        // When
        List<Tuple4<Long, Boolean, EmployeeStatus, Long>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getStatus),
                        Path.of(Employee::getId).count()
                )
                .groupBy(
                        Employee::getDepartmentId,
                        Employee::getActive,
                        Employee::getStatus,
                        Employee::getHireDate
                )
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ==================== groupBy(Path, Path, Path, Path, Path) Tests ====================

    /**
     * Tests groupBy with five paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by five paths")
    void shouldGroupByFivePaths(IntegrationTestContext context) {
        // When
        List<Tuple5<Long, Boolean, EmployeeStatus, LocalDate, Long>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getStatus),
                        Path.of(Employee::getHireDate),
                        Path.of(Employee::getId).count()
                )
                .groupBy(
                        Employee::getDepartmentId,
                        Employee::getActive,
                        Employee::getStatus,
                        Employee::getHireDate,
                        Employee::getName
                )
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ==================== groupBy(Path, Path, Path, Path, Path, Path) Tests ====================

    /**
     * Tests groupBy with six paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should group by six paths")
    void shouldGroupBySixPaths(IntegrationTestContext context) {
        // When
        List<Tuple6<Long, Boolean, EmployeeStatus, LocalDate, String, Long>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getStatus),
                        Path.of(Employee::getHireDate),
                        Path.of(Employee::getName),
                        Path.of(Employee::getId).count()
                )
                .groupBy(
                        Employee::getDepartmentId,
                        Employee::getActive,
                        Employee::getStatus,
                        Employee::getHireDate,
                        Employee::getName,
                        Employee::getEmail
                )
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }

    // ==================== Combined with Having Tests ====================

    /**
     * Tests groupBy with three paths and having clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine groupBy with having clause")
    void shouldCombineGroupByWithHaving(IntegrationTestContext context) {
        // When
        List<Tuple3<Long, Boolean, Long>> results = context.queryEmployees()
                .selectExpr(
                        Path.of(Employee::getDepartmentId),
                        Path.of(Employee::getActive),
                        Path.of(Employee::getId).count()
                )
                .groupBy(Employee::getDepartmentId, Employee::getActive, Employee::getStatus)
                .having(Path.of(Employee::getId).count().gt(0L))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertThat(results).isNotEmpty();
    }
}