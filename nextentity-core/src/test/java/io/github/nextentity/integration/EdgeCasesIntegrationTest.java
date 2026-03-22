package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Edge cases integration tests.
 * <p>
 * Tests edge cases including:
 * - Empty result sets
 * - Null value handling
 * - Boundary values
 * - Special character handling
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Edge Cases Integration Tests")
public class EdgeCasesIntegrationTest {

    /**
     * Tests query with no matching results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyListWhenNoMatches(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .getList();

        // Then
        assertThat(employees).isEmpty();
    }

    /**
     * Tests count with no matching results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero count when no matches")
    void shouldReturnZeroCountWhenNoMatches(DbConfig config) {
        // When
        long count = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .count();

        // Then
        assertThat(count).isZero();
    }

    /**
     * Tests exist with no matching results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return false for exist when no matches")
    void shouldReturnFalseForExistWhenNoMatches(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .exist();

        // Then
        assertThat(exists).isFalse();
    }

    /**
     * Tests first with no matching results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for first when no matches")
    void shouldReturnEmptyOptionalForFirstWhenNoMatches(DbConfig config) {
        // When
        Optional<Employee> employee = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .first();

        // Then
        assertThat(employee).isEmpty();
    }

    /**
     * Tests getSingle with no matching results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return null for getSingle when no matches")
    void shouldReturnNullForGetSingleWhenNoMatches(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .getSingle();

        // Then
        assertThat(employee).isNull();
    }

    /**
     * Tests query with null field value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null field value")
    void shouldHandleNullFieldValue(DbConfig config) {
        // Given - Insert employee with null email
        Employee employee = new Employee();
        employee.setId(9001L);
        employee.setName("Null Email Employee");
        employee.setEmail(null);
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getId).eq(9001L)
                .getList();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getEmail()).isNull();

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests IS NULL query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query IS NULL")
    void shouldQueryIsNull(DbConfig config) {
        // Given - Insert employee with null email
        Employee employee = new Employee();
        employee.setId(9002L);
        employee.setName("Null Email Employee 2");
        employee.setEmail(null);
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getEmail).isNull()
                .getList();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getId() == 9002L);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests IS NOT NULL query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query IS NOT NULL")
    void shouldQueryIsNotNull(DbConfig config) {
        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getEmail).isNotNull()
                .getList();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(e -> e.getEmail() != null);
    }

    /**
     * Tests empty list in IN clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty list in IN clause")
    void shouldHandleEmptyListInClause(DbConfig config) {
        // When
        List<Long> emptyList = Collections.emptyList();
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(emptyList)
                .getList();

        // Then - should return empty or handle gracefully
        assertThat(employees).isEmpty();
    }

    /**
     * Tests empty list in NOT IN clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty list in NOT IN clause")
    void shouldHandleEmptyListNotInClause(DbConfig config) {
        // When
        List<Long> emptyList = Collections.emptyList();
        long count = config.queryEmployees()
                .where(Employee::getId).notIn(emptyList)
                .count();

        // Then - should return all employees (none excluded)
        assertThat(count).isPositive();
    }

    /**
     * Tests zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero offset")
    void shouldHandleZeroOffset(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(0, 5);

        // Then
        assertThat(employees).hasSize(5);
    }

    /**
     * Tests large offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle large offset")
    void shouldHandleLargeOffset(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(10000, 5);

        // Then
        assertThat(employees).isEmpty();
    }

    /**
     * Tests zero limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero limit")
    void shouldHandleZeroLimit(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(0, 0);

        // Then - behavior may vary, just ensure no exception
        assertThat(employees).isNotNull();
    }

    /**
     * Tests negative offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle negative offset")
    void shouldHandleNegativeOffset(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(-1, 5);

        // Then - should handle gracefully (may treat as 0 or return all)
        assertThat(employees).isNotNull();
    }

    /**
     * Tests special characters in string query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle special characters in string")
    void shouldHandleSpecialCharactersInString(DbConfig config) {
        // Given - Insert employee with special characters in name
        Employee employee = new Employee();
        employee.setId(9003L);
        employee.setName("O'Brien-Smith");
        employee.setEmail("special@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getName).eq("O'Brien-Smith")
                .getList();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("O'Brien-Smith");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests unicode characters in string query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle unicode characters in string")
    void shouldHandleUnicodeCharactersInString(DbConfig config) {
        // Given - Insert employee with unicode in name
        Employee employee = new Employee();
        employee.setId(9004L);
        employee.setName("张三");
        employee.setEmail("unicode@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getName).eq("张三")
                .getList();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("张三");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests long string within column limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle long string within limit")
    void shouldHandleLongStringWithinLimit(DbConfig config) {
        // Given - Use a string that fits within typical VARCHAR(100) limit
        String longName = "A".repeat(50); // 50 characters, within typical limits
        Employee employee = new Employee();
        employee.setId(9005L);
        employee.setName(longName);
        employee.setEmail("longname@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(9005L)
                .getSingle();

        // Then
        assertThat(found.getName()).isEqualTo(longName);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests boundary salary values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle boundary salary values")
    void shouldHandleBoundarySalaryValues(DbConfig config) {
        // Given - Insert employee with max salary
        Employee maxEmployee = new Employee();
        maxEmployee.setId(9006L);
        maxEmployee.setName("Max Salary");
        maxEmployee.setEmail("max@example.com");
        maxEmployee.setSalary(Double.MAX_VALUE);
        maxEmployee.setActive(true);
        maxEmployee.setStatus(EmployeeStatus.ACTIVE);
        maxEmployee.setDepartmentId(1L);
        maxEmployee.setHireDate(LocalDate.now());

        // When/Then - should handle without overflow
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().insert(maxEmployee, Employee.class));

        // Cleanup
        config.getUpdateExecutor().delete(maxEmployee, Employee.class);
    }

    /**
     * Tests zero salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero salary")
    void shouldHandleZeroSalary(DbConfig config) {
        // Given
        Employee employee = new Employee();
        employee.setId(9007L);
        employee.setName("Zero Salary");
        employee.setEmail("zero@example.com");
        employee.setSalary(0.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getSalary).eq(0.0)
                .getList();

        // Then
        assertThat(found).anyMatch(e -> e.getId() == 9007L);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests negative salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle negative salary")
    void shouldHandleNegativeSalary(DbConfig config) {
        // Given
        Employee employee = new Employee();
        employee.setId(9008L);
        employee.setName("Negative Salary");
        employee.setEmail("negative@example.com");
        employee.setSalary(-1000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = config.queryEmployees()
                .where(Employee::getSalary).lt(0.0)
                .getList();

        // Then
        assertThat(found).anyMatch(e -> e.getId() == 9008L);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests getSingle with multiple results throws exception.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for getSingle with multiple results")
    void shouldThrowExceptionForGetSingleWithMultipleResults(DbConfig config) {
        // When/Then
        assertThatThrownBy(() -> config.queryEmployees().getSingle())
                .isInstanceOf(IllegalStateException.class);
    }

    /**
     * Tests requireSingle throws exception when no results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for requireSingle when no results")
    void shouldThrowExceptionForRequireSingleWhenNoResults(DbConfig config) {
        // When/Then
        assertThatThrownBy(() -> config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .requireSingle())
                .isInstanceOf(NullPointerException.class);
    }

    /**
     * Tests slice with empty results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle slice with empty results")
    void shouldHandleSliceWithEmptyResults(DbConfig config) {
        // When
        var slice = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .slice(0, 10);

        // Then
        assertThat(slice.data()).isEmpty();
        assertThat(slice.total()).isZero();
    }

    /**
     * Tests page with empty results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle page with empty results")
    void shouldHandlePageWithEmptyResults(DbConfig config) {
        // Given
        var pageable = new io.github.nextentity.api.model.Pageable() {
            @Override
            public int page() {
                return 1;
            }

            @Override
            public int size() {
                return 10;
            }
        };

        // When
        var page = config.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .getPage(pageable);

        // Then
        assertThat(page.getItems()).isEmpty();
        assertThat(page.getTotal()).isZero();
    }

    /**
     * Tests ordering with null values.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle ordering with potential null values")
    void shouldHandleOrderingWithNullValues(DbConfig config) {
        // When - ordering by a field that might have nulls
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getEmail).asc()
                .getList();

        // Then - should complete without error
        assertThat(employees).isNotNull();
    }

    /**
     * Tests batch insert with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle batch insert with empty list")
    void shouldHandleBatchInsertEmptyList(DbConfig config) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().insertAll(new ArrayList<>(), Employee.class));
    }

    /**
     * Tests duplicate ID insert should fail.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail on duplicate ID insert")
    void shouldFailOnDuplicateIdInsert(DbConfig config) {
        // Given
        Employee duplicate = new Employee();
        duplicate.setId(1L); // Already exists
        duplicate.setName("Duplicate");
        duplicate.setEmail("dup@example.com");
        duplicate.setSalary(50000.0);
        duplicate.setActive(true);
        duplicate.setStatus(EmployeeStatus.ACTIVE);
        duplicate.setDepartmentId(1L);
        duplicate.setHireDate(LocalDate.now());

        // When/Then
        assertThatThrownBy(() ->
                config.getUpdateExecutor().insert(duplicate, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * Tests department with null budget.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle department with null budget")
    void shouldHandleDepartmentWithNullBudget(DbConfig config) {
        // Given
        Department dept = new Department();
        dept.setId(9999L);
        dept.setName("Test Dept");
        dept.setLocation("Test Location");
        dept.setBudget(null); // Null budget
        dept.setActive(true);

        config.getUpdateExecutor().insert(dept, Department.class);

        // When
        Department found = config.queryDepartments()
                .where(Department::getId).eq(9999L)
                .getSingle();

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getBudget()).isNull();

        // Cleanup
        config.getUpdateExecutor().delete(dept, Department.class);
    }
}