package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.integration.config.DbConfig;
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
    void shouldSliceResults(DbConfig config) {
        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldSliceWithOffset(DbConfig config) {
        // Given
        List<Employee> allEmployees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldHandleSliceAtEnd(DbConfig config) {
        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldHandleSliceWithLargeLimit(DbConfig config) {
        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldHandleSliceWithOffsetAtEnd(DbConfig config) {
        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldGetPageWithPageable(DbConfig config) {
        // Given
        Pageable pageable = createPageable(1, 5);

        // When
        Page<Employee> page = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

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
    void shouldGetSecondPage(DbConfig config) {
        // Given
        Pageable pageable = createPageable(2, 5);

        // When
        Page<Employee> page = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

        // Then
        assertThat(page.getItems()).hasSize(5);
        assertThat(page.getTotal()).isEqualTo(TOTAL_EMPLOYEES);

        // Verify it's actually the second page
        List<Employee> allEmployees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();
        assertThat(page.getItems().get(0).getId()).isEqualTo(allEmployees.get(5).getId());
    }

    /**
     * Tests getPage for last page.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get last page with partial results")
    void shouldGetLastPage(DbConfig config) {
        // Given
        Pageable pageable = createPageable(3, 5);

        // When
        Page<Employee> page = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

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
    void shouldHandlePageBeyondData(DbConfig config) {
        // Given
        Pageable pageable = createPageable(10, 5);

        // When
        Page<Employee> page = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

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
    void shouldGetListWithOffsetAndLimit(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(3, 5);

        // Then
        assertThat(employees).hasSize(5);
    }

    /**
     * Tests limit method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit results")
    void shouldLimitResults(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
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
    void shouldOffsetResults(DbConfig config) {
        // Given
        List<Employee> allEmployees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .offset(5);

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
    void shouldSliceWithWhereCondition(DbConfig config) {
        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldGetPageWithWhereCondition(DbConfig config) {
        // Given
        Pageable pageable = createPageable(1, 3);

        // When
        Page<Employee> page = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

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
    void shouldSliceWithOrdering(DbConfig config) {
        // When
        Slice<Employee> sliceAsc = config.queryEmployees()
                .orderBy(Employee::getSalary).asc()
                .slice(0, 3);

        Slice<Employee> sliceDesc = config.queryEmployees()
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
    void shouldCalculateCorrectTotalWithFilter(DbConfig config) {
        // Given
        long expectedCount = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .count();

        // When
        Slice<Employee> slice = config.queryEmployees()
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
    void shouldHandleDifferentPageSizes(DbConfig config) {
        // When - Page size 1
        Page<Employee> page1 = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(createPageable(1, 1));

        // Then
        assertThat(page1.getItems()).hasSize(1);
        assertThat(page1.getTotal()).isEqualTo(TOTAL_EMPLOYEES);

        // When - Page size 12
        Page<Employee> page2 = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(createPageable(1, 12));

        // Then
        assertThat(page2.getItems()).hasSize(TOTAL_EMPLOYEES);
        assertThat(page2.getTotal()).isEqualTo(TOTAL_EMPLOYEES);
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