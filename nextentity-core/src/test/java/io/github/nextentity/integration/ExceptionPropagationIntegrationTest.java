package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exception propagation integration tests.
 * <p>
 * Tests exception handling and propagation including:
 * - Constraint violation exceptions
 * - Invalid query exceptions
 * - Data type mismatch exceptions
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Exception Propagation Integration Tests")
public class ExceptionPropagationIntegrationTest {

    /**
     * Tests duplicate primary key exception.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception on duplicate primary key")
    void shouldThrowExceptionOnDuplicatePrimaryKey(DbConfig config) {
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
        assertThatThrownBy(() -> config.getUpdateExecutor().insert(duplicate, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * Tests getSingle with multiple results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for getSingle with multiple results")
    void shouldThrowExceptionForGetSingleWithMultipleResults(DbConfig config) {
        // When/Then - query that returns multiple results
        assertThatThrownBy(() -> config.queryEmployees().getSingle())
                .isInstanceOf(IllegalStateException.class);
    }

    /**
     * Tests requireSingle with no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for requireSingle with no results")
    void shouldThrowExceptionForRequireSingleWithNoResults(DbConfig config) {
        // When/Then - query that returns no results
        assertThatThrownBy(() -> config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .requireSingle())
                .isInstanceOf(NullPointerException.class);
    }

    /**
     * Tests invalid query returns null for getSingle with no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return null for getSingle with no results")
    void shouldReturnNullForGetSingleWithNoResults(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .getSingle();

        // Then
        assertThat(employee).isNull();
    }

    /**
     * Tests first with results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return first result")
    void shouldReturnFirstResult(DbConfig config) {
        // When
        var employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .first();

        // Then
        assertThat(employee).isPresent();
        assertThat(employee.get().getId()).isEqualTo(1L);
    }

    /**
     * Tests first with no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for first with no results")
    void shouldReturnEmptyOptionalForFirstWithNoResults(DbConfig config) {
        // When
        var employee = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .first();

        // Then
        assertThat(employee).isEmpty();
    }

    /**
     * Tests exist with results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return true for exist with results")
    void shouldReturnTrueForExistWithResults(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exist with no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return false for exist with no results")
    void shouldReturnFalseForExistWithNoResults(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .exist();

        // Then
        assertThat(exists).isFalse();
    }

    /**
     * Tests count with no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero count for no results")
    void shouldReturnZeroCountForNoResults(DbConfig config) {
        // When
        long count = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .count();

        // Then
        assertThat(count).isZero();
    }

    /**
     * Tests empty list for query with no matches.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list for no matches")
    void shouldReturnEmptyListForNoMatches(DbConfig config) {
        // When
        var employees = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .getList();

        // Then
        assertThat(employees).isEmpty();
    }

    /**
     * Tests getFirst with results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first from ordered results")
    void shouldGetFirstFromOrderedResults(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getFirst();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }
}