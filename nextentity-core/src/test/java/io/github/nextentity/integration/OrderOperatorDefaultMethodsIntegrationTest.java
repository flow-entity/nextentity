package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for OrderOperator default methods.
 * <p>
 * Tests default methods in OrderOperator interface that delegate to asc():
 * - orderBy(Collection): Sort by collection without explicit asc()/desc()
 * - count(): Count without explicit asc()/desc()
 * - getList(int, int, LockModeType): Get list without explicit asc()/desc()
 * - exist(int): Check existence without explicit asc()/desc()
 * - slice(int, int): Slice without explicit asc()/desc()
 * - getPage(Pageable): Get page without explicit asc()/desc()
 * - map(Function): Map without explicit asc()/desc()
 * <p>
 * These methods are triggered when calling methods on OrderOperator directly
 * without first calling asc() or desc().
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.OrderOperator
 */
@DisplayName("OrderOperator Default Methods Integration Tests")
public class OrderOperatorDefaultMethodsIntegrationTest {

    // ==================== count() Tests ====================

    /**
     * Tests count() without explicit asc()/desc().
     * This triggers OrderOperator.count() default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count without explicit sort order")
    void shouldCountWithoutExplicitSortOrder(IntegrationTestContext context) {
        // When - count() on OrderOperator triggers default method
        long count = context.queryEmployees()
                .orderBy(Employee::getId)
                .count();

        // Then
        assertThat(count).isPositive();
    }

    // ==================== exist(int) Tests ====================

    /**
     * Tests exist(int) without explicit asc()/desc().
     * This triggers OrderOperator.exist(int) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with offset without explicit sort order")
    void shouldCheckExistWithOffset(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId)
                .exist(0);

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exist(int) with offset exceeding count.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist with offset exceeding count return false")
    void shouldCheckExistWithOffsetExceedingCount(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId)
                .exist((int) totalCount + 10);

        // Then
        assertThat(exists).isFalse();
    }

    // ==================== slice(int, int) Tests ====================

    /**
     * Tests slice(int, int) without explicit asc()/desc().
     * This triggers OrderOperator.slice(int, int) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice without explicit sort order")
    void shouldSliceWithoutExplicitSortOrder(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(0, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
    }

    /**
     * Tests slice with non-zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with offset without explicit sort order")
    void shouldSliceWithOffset(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(2, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.offset()).isEqualTo(2);
    }

    // ==================== getPage(Pageable) Tests ====================

    /**
     * Tests getPage(Pageable) without explicit asc()/desc().
     * This triggers OrderOperator.getPage(Pageable) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getPage without explicit sort order")
    void shouldGetPageWithoutExplicitSortOrder(IntegrationTestContext context) {
        // Given
        Pageable pageable = createPageable(1, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId)
                .getPage(pageable);

        // Then
        assertThat(page.getItems()).isNotEmpty();
    }

    // ==================== map(Function) Tests ====================

    /**
     * Tests map(Function) without explicit asc()/desc().
     * This triggers OrderOperator.map(Function) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map without explicit sort order")
    void shouldMapWithoutExplicitSortOrder(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .orderBy(Employee::getId)
                .map(Employee::getName)
                .limit(5);

        // Then
        assertThat(names).hasSize(5);
    }

    // ==================== getList(int, int, LockModeType) Tests ====================

    /**
     * Tests getList(int, int, LockModeType) without explicit asc()/desc().
     * This triggers OrderOperator.getList(int, int, LockModeType) default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getList without explicit sort order")
    void shouldGetListWithoutExplicitSortOrder(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId)
                .getList(0, 5, null);

        // Then
        assertThat(employees).hasSize(5);
    }

    // ==================== asSubQuery() Tests ====================

    /**
     * Tests asSubQuery() without explicit asc()/desc().
     * This triggers OrderOperator.asSubQuery() default method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should convert to subquery without explicit sort order")
    void shouldConvertToSubqueryWithoutExplicitSortOrder(IntegrationTestContext context) {
        // When
        var subQuery = context.queryEmployees()
                .orderBy(Employee::getId)
                .asSubQuery();

        // Then
        assertThat(subQuery).isNotNull();
    }

    // ==================== Combined Tests ====================

    /**
     * Tests combining orderBy with where clause without explicit asc()/desc().
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should combine orderBy with where without explicit sort order")
    void shouldCombineOrderByWithWhere(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(Employee::getId)
                .limit(5);

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(e -> e.getSalary() > 50000.0);
    }

    /**
     * Creates a Pageable instance.
     */
    private Pageable createPageable(int page, int size) {
        return new Pageable() {
            @Override
            public int page() {
                return page;
            }

            @Override
            public int size() {
                return size;
            }
        };
    }
}