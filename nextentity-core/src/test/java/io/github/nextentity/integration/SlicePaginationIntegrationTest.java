package io.github.nextentity.integration;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// Slice and pagination integration tests.
 /// <p>
 /// 测试s pagination functionality including:
 /// - slice() 方法
 /// - Pageable parameters
 /// - Page results
 /// - getList(offset, limit) boundary conditions
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Slice Pagination Integration Tests")
public class SlicePaginationIntegrationTest {

    private static final int TOTAL_EMPLOYEES = 12;

///
     /// 测试s basic slice functionality.
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

///
     /// 测试s slice with offset.
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
        assertThat(slice.data().getFirst().getId()).isEqualTo(allEmployees.get(5).getId());
    }

///
     /// 测试s slice at the end of data.
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

///
     /// 测试s slice with limit larger than total.
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

///
     /// 测试s slice with offset at end.
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

///
     /// 测试s getList with offset and limit.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get list with offset and limit")
    void shouldGetListWithOffsetAndLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(3, 5);

        // Then
        assertThat(employees).hasSize(5);
    }

///
     /// 测试s limit 方法.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit results")
    void shouldLimitResults(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then
        assertThat(employees).hasSize(5);
    }

///
     /// 测试s offset 方法.
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
                .list(5, TOTAL_EMPLOYEES);

        // Then
        assertThat(employees).hasSize(TOTAL_EMPLOYEES - 5);
        assertThat(employees.getFirst().getId()).isEqualTo(allEmployees.get(5).getId());
    }

///
     /// 测试s slice with where condition.
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

///
     /// 测试s slice with order by.
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

///
     /// 测试s slice total count with filter.
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
}

