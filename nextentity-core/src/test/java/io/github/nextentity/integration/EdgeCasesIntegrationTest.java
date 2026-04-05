package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
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

import static org.assertj.core.api.Assertions.*;

///
 /// Edge cases integration tests.
 /// <p>
 /// 测试s edge cases including:
 /// - Empty result sets
 /// - Null value handling
 /// - Boundary values
 /// - Special character handling
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Edge Cases Integration Tests")
public class EdgeCasesIntegrationTest {

///
     /// 测试s query with no matching results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list when no matches")
    void shouldReturnEmptyListWhenNoMatches(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .list();

        // Then
        assertThat(employees).isEmpty();
    }

///
     /// 测试s count with no matching results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return zero count when no matches")
    void shouldReturnZeroCountWhenNoMatches(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .count();

        // Then
        assertThat(count).isZero();
    }

///
     /// 测试s exist with no matching results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return false for exist when no matches")
    void shouldReturnFalseForExistWhenNoMatches(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .exists();

        // Then
        assertThat(exists).isFalse();
    }

///
     /// 测试s first with no matching results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return null for first when no matches")
    void shouldReturnNullForFirstWhenNoMatches(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .first();

        // Then
        assertThat(employee).isNull();
    }

///
     /// 测试s getSingle with no matching results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return null for getSingle when no matches")
    void shouldReturnNullForGetSingleWhenNoMatches(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .single();

        // Then
        assertThat(employee).isNull();
    }

///
     /// 测试s query with null field value.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle null field value")
    void shouldHandleNullFieldValue(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getId).eq(9001L)
                .list();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getEmail()).isNull();

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s IS NULL query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query IS NULL")
    void shouldQueryIsNull(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getEmail).isNull()
                .list();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(e -> e.getId() == 9002L);

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s IS NOT NULL query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query IS NOT NULL")
    void shouldQueryIsNotNull(IntegrationTestContext context) {
        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getEmail).isNotNull()
                .list();

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).allMatch(e -> e.getEmail() != null);
    }

///
     /// 测试s empty list in IN clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty list in IN clause")
    void shouldHandleEmptyListInClause(IntegrationTestContext context) {
        // When
        List<Long> emptyList = Collections.emptyList();
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(emptyList)
                .list();

        // Then - should return empty or handle gracefully
        assertThat(employees).isEmpty();
    }

///
     /// 测试s empty list in NOT IN clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty list in NOT IN clause")
    void shouldHandleEmptyListNotInClause(IntegrationTestContext context) {
        // When
        List<Long> emptyList = Collections.emptyList();
        long count = context.queryEmployees()
                .where(Employee::getId).notIn(emptyList)
                .count();

        // Then - should return all employees (none excluded)
        assertThat(count).isPositive();
    }

///
     /// 测试s zero offset.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero offset")
    void shouldHandleZeroOffset(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then
        assertThat(employees).hasSize(5);
    }

///
     /// 测试s large offset.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle large offset")
    void shouldHandleLargeOffset(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(10000, 5);

        // Then
        assertThat(employees).isEmpty();
    }

///
     /// 测试s zero limit.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero limit")
    void shouldHandleZeroLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(0);

        // Then - behavior may vary, just ensure no exception
        assertThat(employees).isNotNull();
    }

///
     /// 测试s negative offset.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle negative offset")
    void shouldHandleNegativeOffset(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(-1, 5);

        // Then - should handle gracefully (may treat as 0 or return all)
        assertThat(employees).isNotNull();
    }

///
     /// 测试s special characters in string query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle special characters in string")
    void shouldHandleSpecialCharactersInString(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getName).eq("O'Brien-Smith")
                .list();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("O'Brien-Smith");

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s unicode characters in string query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle unicode characters in string")
    void shouldHandleUnicodeCharactersInString(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getName).eq("张三")
                .list();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.getFirst().getName()).isEqualTo("张三");

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s long string within column limit.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle long string within limit")
    void shouldHandleLongStringWithinLimit(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(9005L)
                .single();

        // Then
        assertThat(found.getName()).isEqualTo(longName);

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s boundary salary values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle boundary salary values")
    void shouldHandleBoundarySalaryValues(IntegrationTestContext context) {
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
                context.getUpdateExecutor().insert(maxEmployee, Employee.class));

        // Cleanup
        context.getUpdateExecutor().delete(maxEmployee, Employee.class);
    }

///
     /// 测试s zero salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle zero salary")
    void shouldHandleZeroSalary(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getSalary).eq(0.0)
                .list();

        // Then
        assertThat(found).anyMatch(e -> e.getId() == 9007L);

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s negative salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle negative salary")
    void shouldHandleNegativeSalary(IntegrationTestContext context) {
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

        context.getUpdateExecutor().insert(employee, Employee.class);

        // When
        List<Employee> found = context.queryEmployees()
                .where(Employee::getSalary).lt(0.0)
                .list();

        // Then
        assertThat(found).anyMatch(e -> e.getId() == 9008L);

        // Cleanup
        context.getUpdateExecutor().delete(employee, Employee.class);
    }

///
     /// 测试s getSingle with multiple results throws exception.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should throw exception for getSingle with multiple results")
    void shouldThrowExceptionForGetSingleWithMultipleResults(IntegrationTestContext context) {
        // When/Then
        assertThatThrownBy(() -> context.queryEmployees().single())
                .isInstanceOf(IllegalStateException.class);
    }

///
     /// 测试s slice with empty results.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle slice with empty results")
    void shouldHandleSliceWithEmptyResults(IntegrationTestContext context) {
        // When
        var slice = context.queryEmployees()
                .where(Employee::getId).eq(999999L)
                .slice(0, 10);

        // Then
        assertThat(slice.data()).isEmpty();
        assertThat(slice.total()).isZero();
    }

///
     /// 测试s ordering with null values.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle ordering with potential null values")
    void shouldHandleOrderingWithNullValues(IntegrationTestContext context) {
        // When - ordering by a field that might have nulls
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getEmail).asc()
                .list();

        // Then - should complete without error
        assertThat(employees).isNotNull();
    }

///
     /// 测试s batch insert with empty list.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle batch insert with empty list")
    void shouldHandleBatchInsertEmptyList(IntegrationTestContext context) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().insertAll(new ArrayList<>(), Employee.class));
    }

///
     /// 测试s duplicate ID insert should fail.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail on duplicate ID insert")
    void shouldFailOnDuplicateIdInsert(IntegrationTestContext context) {
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
                context.getUpdateExecutor().insert(duplicate, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

///
     /// 测试s department with null budget.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle department with null budget")
    void shouldHandleDepartmentWithNullBudget(IntegrationTestContext context) {
        // Given
        Department dept = new Department();
        dept.setId(9999L);
        dept.setName("Test Dept");
        dept.setLocation("Test Location");
        dept.setBudget(null); // Null budget
        dept.setActive(true);

        context.getUpdateExecutor().insert(dept, Department.class);

        // When
        Department found = context.queryDepartments()
                .where(Department::getId).eq(9999L)
                .single();

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getBudget()).isNull();

        // Cleanup
        context.getUpdateExecutor().delete(dept, Department.class);
    }
}

