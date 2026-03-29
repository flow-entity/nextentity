package io.github.nextentity.integration;

import io.github.nextentity.api.EntityPath;
import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for FetchStep multiple parameter fetch methods.
 * <p>
 * Tests default methods in FetchStep interface including:
 * - fetch(PathExpression, PathExpression): Fetch two path expressions
 * - fetch(PathExpression, PathExpression, PathExpression): Fetch three path expressions
 * - fetch(Path, Path): Fetch two paths
 * - fetch(Path, Path, Path): Fetch three paths
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.FetchStep
 */
@DisplayName("FetchStep Multiple Parameters Integration Tests")
public class FetchStepMultipleParametersIntegrationTest {

    // ==================== fetch(PathExpression, PathExpression) Tests ====================

    /**
     * Tests fetch with two PathExpression parameters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch two path expressions")
    void shouldFetchTwoPathExpressions(IntegrationTestContext context) {
        // Given
        EntityPath<Employee, ?> departmentPath = EntityPath.of(Employee::getDepartment);

        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(departmentPath, Path.of(Employee::getDepartment))
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== fetch(PathExpression, PathExpression, PathExpression) Tests ====================

    /**
     * Tests fetch with three PathExpression parameters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch three path expressions")
    void shouldFetchThreePathExpressions(IntegrationTestContext context) {
        // Given
        EntityPath<Employee, ?> departmentPath = EntityPath.of(Employee::getDepartment);

        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(departmentPath, Path.of(Employee::getDepartment), Path.of(Employee::getDepartment))
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== fetch(Path, Path) Tests ====================

    /**
     * Tests fetch with two Path parameters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch two paths")
    void shouldFetchTwoPaths(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment, Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== fetch(Path, Path, Path) Tests ====================

    /**
     * Tests fetch with three Path parameters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch three paths")
    void shouldFetchThreePaths(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment, Employee::getDepartment, Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== Combined with where clause Tests ====================

    /**
     * Tests fetch combined with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine fetch with where clause")
    void shouldCombineFetchWithWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment, Employee::getDepartment)
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    /**
     * Tests fetch combined with limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine fetch with limit")
    void shouldCombineFetchWithLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment, Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .limit(5);

        // Then
        assertThat(employees).hasSize(5);
    }
}