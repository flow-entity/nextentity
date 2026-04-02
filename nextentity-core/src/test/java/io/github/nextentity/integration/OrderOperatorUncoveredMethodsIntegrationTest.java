package io.github.nextentity.integration;

import io.github.nextentity.api.OrderOperator;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Sliceable;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrderOperator default methods that were not covered.
 * <p>
 * Tests default methods in OrderOperator interface including:
 * - orderBy(Collection&lt;Path&gt;): Sort by collection of paths without explicit asc()/desc()
 * - slice(Sliceable): Perform slice operation without explicit asc()/desc()
 * - orderBy(List&lt;Order&gt;): Sort by list of orders without explicit asc()/desc()
 * <p>
 * These methods are triggered when calling methods on OrderOperator directly
 * without first calling asc() or desc().
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.OrderOperator
 */
@DisplayName("OrderOperator Uncovered Methods Integration Tests")
public class OrderOperatorUncoveredMethodsIntegrationTest {

    // ==================== orderBy(Collection<Path>) Tests ====================

    /**
     * Tests orderBy(Collection&lt;Path&gt;) without explicit asc()/desc().
     * This triggers OrderOperator.orderBy(Collection) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection without explicit sort order")
    void shouldOrderByCollectionWithoutExplicitSortOrder(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getId);

        // When - orderBy(Collection) on OrderOperator triggers default method
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(paths)
                .limit(5);

        // Then
        assertThat(employees).isNotEmpty();
        // Verify ordering by ID ascending (default behavior)
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId())
                    .isGreaterThanOrEqualTo(employees.get(i - 1).getId());
        }
    }

    /**
     * Tests orderBy(Collection&lt;Path&gt;) with multiple paths.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection with multiple paths")
    void shouldOrderByCollectionWithMultiplePaths(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getDepartmentId);
        paths.add(Employee::getName);

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(paths)
                .limit(10);

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests orderBy(Collection&lt;Path&gt;) returns OrderOperator for chaining.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection return OrderOperator for chaining")
    void shouldOrderByCollectionReturnOrderOperatorForChaining(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getId);

        // When
        OrderOperator<Employee, Employee> orderOperator = context.queryEmployees()
                .orderBy(paths);

        // Then - can chain additional operations
        List<Employee> employees = orderOperator.limit(5);
        assertThat(employees).isNotEmpty();
    }

    // ==================== slice(Sliceable) Tests ====================

    /**
     * Tests slice(Sliceable) without explicit asc()/desc().
     * This triggers OrderOperator.slice(Sliceable) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with Sliceable without explicit sort order")
    void shouldSliceWithSliceableWithoutExplicitSortOrder(IntegrationTestContext context) {
        // Given
        Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(0, 5);

        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(sliceable);

        // Then
        assertThat(slice).isNotNull();
        assertThat(slice.data()).isNotEmpty();
        assertThat(slice.total()).isPositive();
    }

    /**
     * Tests slice(Sliceable) with custom result type.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with Sliceable return custom result type")
    void shouldSliceWithSliceableReturnCustomResultType(IntegrationTestContext context) {
        // Given - a custom sliceable that returns a String summary
        Sliceable<Employee, String> sliceable = new Sliceable<>() {
            @Override
            public int offset() {
                return 0;
            }

            @Override
            public int limit() {
                return 5;
            }

            @Override
            public String collect(List<Employee> data, long total) {
                return "Found " + data.size() + " of " + total + " total records";
            }
        };

        // When
        String result = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(sliceable);

        // Then
        assertThat(result).startsWith("Found");
        assertThat(result).contains("total records");
    }

    /**
     * Tests slice(Sliceable) with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with Sliceable and where clause")
    void shouldSliceWithSliceableAndWhereClause(IntegrationTestContext context) {
        // Given
        Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(0, 5);

        // When
        Slice<Employee> slice = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(Employee::getId)
                .slice(sliceable);

        // Then
        assertThat(slice).isNotNull();
        assertThat(slice.data()).isNotEmpty();
        assertThat(slice.data()).allMatch(e -> e.getSalary() > 50000.0);
    }

    // ==================== orderBy(List<Order>) Tests ====================

    /**
     * Tests orderBy(List&lt;Order&gt;) with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy empty List of Orders work")
    void shouldOrderByEmptyListOfOrdersWork(IntegrationTestContext context) {
        // Given
        List<Order<Employee>> orders = new ArrayList<>();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId)
                .orderBy(orders)
                .limit(5);

        // Then
        assertThat(employees).isNotEmpty();
    }

    // ==================== Combined Tests ====================

    /**
     * Tests chaining orderBy(Collection) with other operations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain orderBy Collection with limit")
    void shouldChainOrderByCollectionWithLimit(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getSalary);

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(paths)
                .limit(3);

        // Then
        assertThat(employees).hasSize(3);
    }

    /**
     * Tests that OrderOperator methods delegate correctly to asc().
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should OrderOperator methods delegate to asc correctly")
    void shouldOrderOperatorMethodsDelegateToAscCorrectly(IntegrationTestContext context) {
        // Given
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getId);

        // When - using OrderOperator directly (triggers default methods)
        long countDirect = context.queryEmployees()
                .orderBy(Employee::getId)
                .count();

        // And - using explicit asc()
        long countExplicit = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .count();

        // Then - should be the same
        assertThat(countDirect).isEqualTo(countExplicit);
    }

    /**
     * Tests slice(Sliceable) with pagination parameters.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with Sliceable handle pagination")
    void shouldSliceWithSliceableHandlePagination(IntegrationTestContext context) {
        // Given - a sliceable that respects pagination
        Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(0, 5);

        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(sliceable);

        // Then
        assertThat(slice.data()).hasSize(5);
    }

    // ==================== Helper Methods ====================

    private <T> Sliceable<T, Slice<T>> createSliceable(int offset, int limit) {
        return new Sliceable<>() {
            @Override
            public int offset() {
                return offset;
            }

            @Override
            public int limit() {
                return limit;
            }

            @Override
            public Slice<T> collect(List<T> data, long total) {
                return new Slice<>() {
                    @Override
                    public List<T> data() {
                        return data;
                    }

                    @Override
                    public long total() {
                        return total;
                    }

                    @Override
                    public int offset() {
                        return offset;
                    }

                    @Override
                    public int limit() {
                        return limit;
                    }
                };
            }
        };
    }
}

