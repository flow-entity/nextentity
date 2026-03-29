package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrderByStep multiple parameter orderBy methods.
 * <p>
 * Tests default methods in OrderByStep interface including:
 * - orderBy(Order): Sort by single Order object
 * - orderBy(Order, Order): Sort by two Order objects
 * - orderBy(Order, Order, Order): Sort by three Order objects
 * - orderBy(Path, Path): Sort by two paths
 * - orderBy(Path, Path, Path): Sort by three paths
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.OrderByStep
 */
@DisplayName("OrderByStep Multiple Parameters Integration Tests")
public class OrderByStepMultipleParametersIntegrationTest {

    // ==================== orderBy(Order) Tests ====================

    /**
     * Tests orderBy(Order) with a single Order object.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by single Order object")
    void shouldOrderBySingleOrder(IntegrationTestContext context) {
        // Given
        Order<Employee> order = Path.of(Employee::getName).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(order)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isGreaterThanOrEqualTo(0);
        }
    }

    // ==================== orderBy(Order, Order) Tests ====================

    /**
     * Tests orderBy(Order, Order) with two Order objects.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by two Order objects")
    void shouldOrderByTwoOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryOrder = Path.of(Employee::getSalary).desc();
        Order<Employee> nameOrder = Path.of(Employee::getName).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryOrder, nameOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        // Verify salary is in descending order
        for (int i = 1; i < employees.size(); i++) {
            Double current = employees.get(i).getSalary();
            Double previous = employees.get(i - 1).getSalary();
            assertThat(current).isLessThanOrEqualTo(previous);
        }
    }

    /**
     * Tests orderBy(Order, Order) with both ascending orders.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by two ascending Order objects")
    void shouldOrderByTwoAscendingOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> idOrder = Path.of(Employee::getId).asc();
        Order<Employee> nameOrder = Path.of(Employee::getName).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(idOrder, nameOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees.get(0).getId()).isEqualTo(1L);
    }

    // ==================== orderBy(Order, Order, Order) Tests ====================

    /**
     * Tests orderBy(Order, Order, Order) with three Order objects.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by three Order objects")
    void shouldOrderByThreeOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryOrder = Path.of(Employee::getSalary).desc();
        Order<Employee> nameOrder = Path.of(Employee::getName).asc();
        Order<Employee> idOrder = Path.of(Employee::getId).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryOrder, nameOrder, idOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== orderBy(Path, Path) Tests ====================

    /**
     * Tests orderBy(Path, Path) with two paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by two paths")
    void shouldOrderByTwoPaths(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary, Employee::getName)
                .asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests orderBy(Path, Path) with descending order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by two paths descending")
    void shouldOrderByTwoPathsDescending(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary, Employee::getId)
                .desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        // Verify descending order
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    // ==================== orderBy(Path, Path, Path) Tests ====================

    /**
     * Tests orderBy(Path, Path, Path) with three paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by three paths")
    void shouldOrderByThreePaths(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getDepartmentId, Employee::getSalary, Employee::getName)
                .asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests orderBy(Path, Path, Path) with descending order.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by three paths descending")
    void shouldOrderByThreePathsDescending(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getDepartmentId, Employee::getSalary, Employee::getId)
                .desc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== Combined with Filter Tests ====================

    /**
     * Tests combining multiple Order objects with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine multiple orders with where clause")
    void shouldCombineMultipleOrdersWithWhere(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryOrder = Path.of(Employee::getSalary).desc();
        Order<Employee> nameOrder = Path.of(Employee::getName).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(salaryOrder, nameOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    /**
     * Tests count with multiple Order objects.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with multiple Order objects")
    void shouldCountWithMultipleOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryOrder = Path.of(Employee::getSalary).desc();
        Order<Employee> idOrder = Path.of(Employee::getId).asc();

        // When
        long count = context.queryEmployees()
                .orderBy(salaryOrder, idOrder)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    /**
     * Tests limit with multiple paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with multiple paths")
    void shouldLimitWithMultiplePaths(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getSalary, Employee::getName)
                .desc()
                .limit(5);

        // Then
        assertThat(employees).hasSize(5);
    }
}