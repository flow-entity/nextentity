package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
 /// Exception propagation integration tests.
 /// <p>
 /// 测试s exception handling and propagation including:
 /// - Constraint violation exceptions
 /// - Invalid query exceptions
 /// - Data type mismatch exceptions
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Exception Propagation Integration Tests")
public class ExceptionPropagationIntegrationTest {

///
     /// 测试s duplicate primary key exception.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception on duplicate primary key")
    void shouldThrowExceptionOnDuplicatePrimaryKey(IntegrationTestContext context) {
        // Given - employee with ID 1 already exists
        Employee duplicate = new Employee();
        duplicate.setId(1L); // Existing ID
        duplicate.setName("Duplicate");
        duplicate.setEmail("duplicate@example.com");
        duplicate.setSalary(50000.0);
        duplicate.setActive(true);
        duplicate.setStatus(EmployeeStatus.ACTIVE);
        duplicate.setDepartmentId(1L);
        duplicate.setHireDate(LocalDate.now());

        // When/Then - should throw exception
        assertThatThrownBy(() -> context.getUpdateExecutor().insert(duplicate, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

///
     /// 测试s getSingle with multiple results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for getSingle with multiple results")
    void shouldThrowExceptionForGetSingleWithMultipleResults(IntegrationTestContext context) {
        // When/Then - query that returns multiple results
        assertThatThrownBy(() -> context.queryEmployees().single())
                .isInstanceOf(IllegalStateException.class);
    }

///
     /// 测试s invalid query returns null for getSingle with no results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return null for getSingle with no results")
    void shouldReturnNullForGetSingleWithNoResults(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .single();

        // Then
        assertThat(employee).isNull();
    }

///
     /// 测试s first with results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return first result")
    void shouldReturnFirstResult(IntegrationTestContext context) {
        // When
        var employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .first();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

///
     /// 测试s first with no results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for first with no results")
    void shouldReturnEmptyOptionalForFirstWithNoResults(IntegrationTestContext context) {
        // When
        var employee = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .first();

        // Then
        assertThat(employee).isNull();
    }

///
     /// 测试s exist with results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return true for exist with results")
    void shouldReturnTrueForExistWithResults(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .exists();

        // Then
        assertThat(exists).isTrue();
    }

///
     /// 测试s exist with no results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return false for exist with no results")
    void shouldReturnFalseForExistWithNoResults(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .exists();

        // Then
        assertThat(exists).isFalse();
    }

///
     /// 测试s count with no results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero count for no results")
    void shouldReturnZeroCountForNoResults(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .count();

        // Then
        assertThat(count).isZero();
    }

///
     /// 测试s empty list for query with no matches.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list for no matches")
    void shouldReturnEmptyListForNoMatches(IntegrationTestContext context) {
        // When
        var employees = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .list();

        // Then
        assertThat(employees).isEmpty();
    }

///
     /// 测试s getFirst with results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first from ordered results")
    void shouldGetFirstFromOrderedResults(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }
}
