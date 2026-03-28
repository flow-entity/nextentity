package io.github.nextentity.integration;

import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.Paths;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for SimpleExpression asc() and desc() default methods.
 * <p>
 * Tests default methods in SimpleExpression interface including:
 * - asc(): Returns Order for ascending sort
 * - desc(): Returns Order for descending sort
 * <p>
 * These tests verify that the Order objects returned by asc()/desc()
 * can be used with orderBy(Order) method.
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.SimpleExpression
 */
@DisplayName("SimpleExpression Sort Order Integration Tests")
public class SimpleExpressionSortOrderIntegrationTest {

    // ==================== asc() Tests ====================

    /**
     * Tests asc() method on string field.
     * The asc() method returns an Order object that can be used with orderBy(Order).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field ascending using asc()")
    void shouldOrderByStringAsc(IntegrationTestContext context) {
        // Given - use asc() to create Order from SimpleExpression
        Order<Employee> nameAscOrder = Paths.get(Employee::getName).asc();

        // When - pass Order to orderBy
        List<Employee> employees = context.queryEmployees()
                .orderBy(nameAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isGreaterThanOrEqualTo(0);
        }
    }

    /**
     * Tests asc() method on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field ascending using asc()")
    void shouldOrderByNumericAsc(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryAscOrder = Paths.get(Employee::getSalary).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Tests asc() method on ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by ID field ascending using asc()")
    void shouldOrderByIdAsc(IntegrationTestContext context) {
        // Given
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(idAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId())
                    .isGreaterThan(employees.get(i - 1).getId());
        }
    }

    // ==================== desc() Tests ====================

    /**
     * Tests desc() method on string field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by string field descending using desc()")
    void shouldOrderByStringDesc(IntegrationTestContext context) {
        // Given
        Order<Employee> nameDescOrder = Paths.get(Employee::getName).desc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(nameDescOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isLessThanOrEqualTo(0);
        }
    }

    /**
     * Tests desc() method on numeric field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by numeric field descending using desc()")
    void shouldOrderByNumericDesc(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryDescOrder = Paths.get(Employee::getSalary).desc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryDescOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    /**
     * Tests desc() method on ID field.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by ID field descending using desc()")
    void shouldOrderByIdDesc(IntegrationTestContext context) {
        // Given
        Order<Employee> idDescOrder = Paths.get(Employee::getId).desc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(idDescOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId())
                    .isLessThan(employees.get(i - 1).getId());
        }
    }

    // ==================== Combined with Filter Tests ====================

    /**
     * Tests asc() combined with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine asc() with where clause")
    void shouldCombineAscWithWhere(IntegrationTestContext context) {
        // Given
        Order<Employee> nameAscOrder = Paths.get(Employee::getName).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(nameAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getName().compareTo(employees.get(i - 1).getName()))
                    .isGreaterThanOrEqualTo(0);
        }
    }

    /**
     * Tests desc() combined with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine desc() with where clause")
    void shouldCombineDescWithWhere(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryDescOrder = Paths.get(Employee::getSalary).desc();

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(salaryDescOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getSalary())
                    .isLessThanOrEqualTo(employees.get(i - 1).getSalary());
        }
    }

    // ==================== Count and Exist Tests ====================

    /**
     * Tests count with asc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with asc() ordering")
    void shouldCountWithAscOrdering(IntegrationTestContext context) {
        // Given
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        long count = context.queryEmployees()
                .orderBy(idAscOrder)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    /**
     * Tests exist with desc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with desc() ordering")
    void shouldCheckExistWithDescOrdering(IntegrationTestContext context) {
        // Given
        Order<Employee> idDescOrder = Paths.get(Employee::getId).desc();

        // When
        boolean exists = context.queryEmployees()
                .orderBy(idDescOrder)
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    // ==================== GetFirst/GetSingle Tests ====================

    /**
     * Tests getFirst with asc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with asc() ordering returns minimum")
    void shouldGetFirstWithAscReturnsMinimum(IntegrationTestContext context) {
        // Given
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        Employee first = context.queryEmployees()
                .orderBy(idAscOrder)
                .getFirst();

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(1L);
    }

    /**
     * Tests getFirst with desc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with desc() ordering returns maximum")
    void shouldGetFirstWithDescReturnsMaximum(IntegrationTestContext context) {
        // Given
        Order<Employee> idDescOrder = Paths.get(Employee::getId).desc();

        // And - get max id for verification
        long maxId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).desc()
                .getFirst();

        // When
        Employee first = context.queryEmployees()
                .orderBy(idDescOrder)
                .getFirst();

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(maxId);
    }

    /**
     * Tests limit with asc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with asc() ordering")
    void shouldLimitWithAscOrdering(IntegrationTestContext context) {
        // Given
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(idAscOrder)
                .limit(5);

        // Then
        assertThat(employees).hasSize(5);
        assertThat(employees.get(0).getId()).isEqualTo(1L);
    }

    /**
     * Tests offset with desc() ordering.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset with desc() ordering")
    void shouldOffsetWithDescOrdering(IntegrationTestContext context) {
        // Given
        Order<Employee> idDescOrder = Paths.get(Employee::getId).desc();
        long totalCount = context.queryEmployees().count();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(idDescOrder)
                .offset(2);

        // Then
        assertThat(employees).hasSize((int) totalCount - 2);
    }

    // ==================== Multiple Order Tests ====================

    /**
     * Tests using multiple Order objects with orderBy(Order, Order).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use orderBy with two Order objects")
    void shouldUseOrderByWithTwoOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryDescOrder = Paths.get(Employee::getSalary).desc();
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryDescOrder, idAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests using three Order objects.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should use orderBy with three Order objects")
    void shouldUseOrderByWithThreeOrders(IntegrationTestContext context) {
        // Given
        Order<Employee> salaryDescOrder = Paths.get(Employee::getSalary).desc();
        Order<Employee> nameAscOrder = Paths.get(Employee::getName).asc();
        Order<Employee> idAscOrder = Paths.get(Employee::getId).asc();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(salaryDescOrder, nameAscOrder, idAscOrder)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }
}