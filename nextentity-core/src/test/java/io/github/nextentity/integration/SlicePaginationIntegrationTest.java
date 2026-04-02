package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.core.Pages;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slice and pagination integration tests.
 * <p>
 * Tests pagination functionality including:
 * - slice() method
 * - Pageable parameters
 * - Page results
 * - getList(offset, limit) boundary conditions
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Slice Pagination Integration Tests")
public class SlicePaginationIntegrationTest {

    private static final int TOTAL_EMPLOYEES = 12;

    /**
     * Tests basic slice functionality.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice results with offset and limit")
    void shouldSliceResults(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(0, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.total()).isEqualTo(TOTAL_EMPLOYEES);
        assertThat(slice.offset()).isEqualTo(0);
        assertThat(slice.limit()).isEqualTo(5);
    }

    /**
     * Tests slice with offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with non-zero offset")
    void shouldSliceWithOffset(IntegrationTestContext context) {
        // Given
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list();

        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(5, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.offset()).isEqualTo(5);
        assertThat(slice.data().get(0).getId()).isEqualTo(allEmployees.get(5).getId());
    }

    /**
     * Tests slice at the end of data.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle slice at end of data")
    void shouldHandleSliceAtEnd(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(10, 5);

        // Then
        assertThat(slice.data()).hasSize(2); // Only 2 remaining
        assertThat(slice.total()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests slice with limit larger than total.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle slice with limit larger than total")
    void shouldHandleSliceWithLargeLimit(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(0, 100);

        // Then
        assertThat(slice.data()).hasSize(TOTAL_EMPLOYEES);
        assertThat(slice.total()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests slice with offset at end.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle slice with offset at end")
    void shouldHandleSliceWithOffsetAtEnd(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(100, 5);

        // Then
        assertThat(slice.data()).isEmpty();
        assertThat(slice.total()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests getPage with Pageable.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get page with Pageable")
    void shouldGetPageWithPageable(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(1, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).hasSize(5);
        assertThat(page.getTotal()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests getPage for second page.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get second page")
    void shouldGetSecondPage(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(2, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).hasSize(5);
        assertThat(page.getTotal()).isEqualTo(TOTAL_EMPLOYEES);

        // Verify it's actually the second page
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list();
        assertThat(page.getItems().get(0).getId()).isEqualTo(allEmployees.get(5).getId());
    }

    /**
     * Tests getPage for last page.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get last page with partial results")
    void shouldGetLastPage(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(3, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).hasSize(2); // 12 - 10 = 2
        assertThat(page.getTotal()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests getPage with page beyond data.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle page beyond data")
    void shouldHandlePageBeyondData(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(10, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).isEmpty();
        assertThat(page.getTotal()).isEqualTo(TOTAL_EMPLOYEES);
    }

    /**
     * Tests getList with offset and limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get list with offset and limit")
    void shouldGetListWithOffsetAndLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window(3, 5);

        // Then
        assertThat(employees).hasSize(5);
    }

    /**
     * Tests limit method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit results")
    void shouldLimitResults(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .limit(5);

        // Then
        assertThat(employees).hasSize(5);
    }

    /**
     * Tests offset method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset results")
    void shouldOffsetResults(IntegrationTestContext context) {
        // Given
        List<Employee> allEmployees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list();

        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window(5, TOTAL_EMPLOYEES);

        // Then
        assertThat(employees).hasSize(TOTAL_EMPLOYEES - 5);
        assertThat(employees.get(0).getId()).isEqualTo(allEmployees.get(5).getId());
    }

    /**
     * Tests slice with where condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with where condition")
    void shouldSliceWithWhereCondition(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .slice(0, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.data()).allMatch(Employee::getActive);
    }

    /**
     * Tests page with where condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get page with where condition")
    void shouldGetPageWithWhereCondition(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(1, 3);

        // When
        Page<Employee> page = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).hasSize(3);
        assertThat(page.getItems()).allMatch(e -> e.getDepartmentId() == 1L);
    }

    /**
     * Tests slice with order by.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with correct ordering")
    void shouldSliceWithOrdering(IntegrationTestContext context) {
        // When
        Slice<Employee> sliceAsc = context.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .slice(0, 3);

        Slice<Employee> sliceDesc = context.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .slice(0, 3);

        // Then
        assertThat(sliceAsc.data().get(0).getSalary())
                .isLessThan(sliceAsc.data().get(2).getSalary());
        assertThat(sliceDesc.data().get(0).getSalary())
                .isGreaterThan(sliceDesc.data().get(2).getSalary());
    }

    /**
     * Tests slice total count with filter.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should calculate correct total with filter")
    void shouldCalculateCorrectTotalWithFilter(IntegrationTestContext context) {
        // Given
        long expectedCount = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // When
        Slice<Employee> slice = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .slice(0, 2);

        // Then
        assertThat(slice.total()).isEqualTo(expectedCount);
    }

    /**
     * Tests page with different page sizes.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle different page sizes")
    void shouldHandleDifferentPageSizes(IntegrationTestContext context) {
        // When - Page size 1
        Page<Employee> page1 = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(Pages.pageable(1, 1));

        // Then
        assertThat(page1.getItems()).hasSize(1);
        assertThat(page1.getTotal()).isEqualTo(TOTAL_EMPLOYEES);

        // When - Page size 12
        Page<Employee> page2 = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(Pages.pageable(1, 12));

        // Then
        assertThat(page2.getItems()).hasSize(TOTAL_EMPLOYEES);
        assertThat(page2.getTotal()).isEqualTo(TOTAL_EMPLOYEES);
    }
}

