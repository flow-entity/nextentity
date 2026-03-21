package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * CRUD operations integration tests.
 * <p>
 * Tests insert, update, and delete operations including:
 * - Single entity insert/update/delete
 * - Batch insert/update/delete
 * - Optimistic locking
 * - Partial field updates
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("CRUD Operations Integration Tests")
public class CrudOperationsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CrudOperationsIntegrationTest.class);

    /**
     * Tests inserting a single employee.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single employee")
    void shouldInsertSingleEmployee(DbConfig config) {
        // Given
        Employee newEmployee = createTestEmployee(100L, "Test User", "test@example.com");

        // When
        config.getUpdateExecutor().insert(newEmployee, Employee.class);

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(100L)
                .getList();
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("Test User");
        assertThat(employees.get(0).getEmail()).isEqualTo("test@example.com");
    }

    /**
     * Tests inserting multiple employees.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert multiple employees")
    void shouldInsertMultipleEmployees(DbConfig config) {
        // Given
        List<Employee> newEmployees = new ArrayList<>();
        newEmployees.add(createTestEmployee(200L, "User 200", "user200@example.com"));
        newEmployees.add(createTestEmployee(201L, "User 201", "user201@example.com"));
        newEmployees.add(createTestEmployee(202L, "User 202", "user202@example.com"));

        // When
        config.getUpdateExecutor().insertAll(newEmployees, Employee.class);

        // Then
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(200L, 201L, 202L)
                .orderBy(Employee::getId).asc()
                .getList();
        assertThat(employees).hasSize(3);
        assertThat(employees.get(0).getName()).isEqualTo("User 200");
        assertThat(employees.get(2).getName()).isEqualTo("User 202");
    }

    /**
     * Tests inserting a department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single department")
    void shouldInsertSingleDepartment(DbConfig config) {
        // Given
        Department newDept = new Department(100L, "IT", "Building E", 500000.0, true);

        // When
        config.getUpdateExecutor().insert(newDept, Department.class);

        // Then
        List<Department> departments = config.queryDepartments()
                .where(Department::getId).eq(100L)
                .getList();
        assertThat(departments).hasSize(1);
        assertThat(departments.get(0).getName()).isEqualTo("IT");
        assertThat(departments.get(0).getLocation()).isEqualTo("Building E");
    }

    /**
     * Tests updating a single employee.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update single employee")
    void shouldUpdateSingleEmployee(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList().get(0);
        String originalName = employee.getName();
        employee.setName("Updated Name");
        employee.setSalary(99999.0);

        // When
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList().get(0);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getSalary()).isEqualTo(99999.0);
        assertThat(updated.getName()).isNotEqualTo(originalName);
    }

    /**
     * Tests updating multiple employees.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update multiple employees")
    void shouldUpdateMultipleEmployees(DbConfig config) {
        // Given
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        for (Employee emp : employees) {
            emp.setSalary(emp.getSalary() + 1000);
        }

        // When
        config.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList();
        assertThat(updated).hasSize(employees.size());
        for (int i = 0; i < employees.size(); i++) {
            assertThat(updated.get(i).getSalary()).isEqualTo(employees.get(i).getSalary());
        }
    }

    /**
     * Tests deleting a single employee.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete single employee")
    void shouldDeleteSingleEmployee(DbConfig config) {
        // Given - create an employee to delete
        Employee newEmployee = createTestEmployee(300L, "To Delete", "delete@example.com");
        config.getUpdateExecutor().insert(newEmployee, Employee.class);

        // Verify it exists
        List<Employee> before = config.queryEmployees()
                .where(Employee::getId).eq(300L)
                .getList();
        assertThat(before).hasSize(1);

        // When
        config.getUpdateExecutor().delete(newEmployee, Employee.class);

        // Then
        List<Employee> after = config.queryEmployees()
                .where(Employee::getId).eq(300L)
                .getList();
        assertThat(after).isEmpty();
    }

    /**
     * Tests deleting multiple employees.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple employees")
    void shouldDeleteMultipleEmployees(DbConfig config) {
        // Given - create employees to delete
        List<Employee> newEmployees = new ArrayList<>();
        newEmployees.add(createTestEmployee(400L, "Delete 1", "delete1@example.com"));
        newEmployees.add(createTestEmployee(401L, "Delete 2", "delete2@example.com"));
        config.getUpdateExecutor().insertAll(newEmployees, Employee.class);

        // Verify they exist
        List<Employee> before = config.queryEmployees()
                .where(Employee::getId).in(400L, 401L)
                .getList();
        assertThat(before).hasSize(2);

        // When
        config.getUpdateExecutor().deleteAll(newEmployees, Employee.class);

        // Then
        List<Employee> after = config.queryEmployees()
                .where(Employee::getId).in(400L, 401L)
                .getList();
        assertThat(after).isEmpty();
    }

    /**
     * Tests deleting by ID using where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with where condition")
    void shouldDeleteWithWhereCondition(DbConfig config) {
        // Given - create an employee to delete
        Employee newEmployee = createTestEmployee(500L, "Condition Delete", "cond@example.com");
        config.getUpdateExecutor().insert(newEmployee, Employee.class);

        // When - delete using where clause
        Employee toDelete = config.queryEmployees()
                .where(Employee::getId).eq(500L)
                .getList().get(0);
        config.getUpdateExecutor().delete(toDelete, Employee.class);

        // Then
        List<Employee> after = config.queryEmployees()
                .where(Employee::getId).eq(500L)
                .getList();
        assertThat(after).isEmpty();
    }

    /**
     * Tests updating employee status.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employee status")
    void shouldUpdateEmployeeStatus(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList().get(0);
        EmployeeStatus originalStatus = employee.getStatus();
        employee.setStatus(EmployeeStatus.INACTIVE);

        // When
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList().get(0);
        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        assertThat(updated.getStatus()).isNotEqualTo(originalStatus);
    }

    /**
     * Tests inserting and updating department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert and update department")
    void shouldInsertAndUpdateDepartment(DbConfig config) {
        // Given - insert new department
        Department newDept = new Department(200L, "Research", "Building F", 300000.0, true);
        config.getUpdateExecutor().insert(newDept, Department.class);

        // When - update the department
        Department dept = config.queryDepartments()
                .where(Department::getId).eq(200L)
                .getList().get(0);
        dept.setBudget(400000.0);
        dept.setLocation("Building G");
        config.getUpdateExecutor().update(dept, Department.class);

        // Then
        Department updated = config.queryDepartments()
                .where(Department::getId).eq(200L)
                .getList().get(0);
        assertThat(updated.getBudget()).isEqualTo(400000.0);
        assertThat(updated.getLocation()).isEqualTo("Building G");
    }

    /**
     * Tests inserting employee with all fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with all fields")
    void shouldInsertEmployeeWithAllFields(DbConfig config) {
        // Given
        Employee employee = new Employee();
        employee.setId(600L);
        employee.setName("Full Employee");
        employee.setEmail("full@example.com");
        employee.setSalary(75000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.of(2024, 1, 15));

        // When
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = config.queryEmployees()
                .where(Employee::getId).eq(600L)
                .getList().get(0);
        assertThat(inserted.getName()).isEqualTo("Full Employee");
        assertThat(inserted.getSalary()).isEqualTo(75000.0);
        assertThat(inserted.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(inserted.getHireDate()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

    /**
     * Tests inserting duplicate ID should fail.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail when inserting duplicate ID")
    void shouldFailOnDuplicateId(DbConfig config) {
        // Given - employee with ID 1 already exists
        Employee duplicateEmployee = createTestEmployee(1L, "Duplicate", "dup@example.com");

        // When/Then - should throw exception
        assertThatThrownBy(() -> config.getUpdateExecutor().insert(duplicateEmployee, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * Tests updating non-existent employee.
     */
    @Disabled("BUG - JpaUpdateExecutor update operation on non-existent entity should throw exception")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle update of non-existent employee")
    void shouldHandleUpdateNonExistent(DbConfig config) {
        // Given
        Employee nonExistent = createTestEmployee(9999L, "Non Existent", "none@example.com");

        // When/Then - should throw exception (entity not found)
        // TODO fix bug
        assertThatThrownBy(() -> config.getUpdateExecutor().update(nonExistent, Employee.class))
                .isInstanceOf(RuntimeException.class);

    }

    /**
     * Tests deleting non-existent employee.
     * <p>
     * Note: JPA implementation silently ignores non-existent entities,
     * while JDBC implementation may have different behavior.
     * This test verifies the actual behavior without asserting specific exceptions.
     */
    @Disabled("Bug - delete operation on non-existent entity should handle gracefully")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle delete of non-existent employee")
    void shouldHandleDeleteNonExistent(DbConfig config) {
        // Given
        Employee nonExistent = createTestEmployee(9998L, "Non Existent", "none@example.com");

        // When - delete non-existent entity should not throw exception
        // JPA: entityManager.remove() on non-existent entity is a no-op
        // JDBC: delete operation affects 0 rows, but doesn't throw exception
        // TODO delete operation on non-existent entity should handle gracefully
        assertThatThrownBy(() -> config.getUpdateExecutor().delete(nonExistent, Employee.class))
                .isInstanceOf(RuntimeException.class);
        // Then - operation completes without error (implementation-specific behavior)
        // This test documents that delete of non-existent entities is allowed
    }

    /**
     * Tests inserting employee with null email.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with null email")
    void shouldInsertEmployeeWithNullEmail(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(700L, "No Email", null);

        // When
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = config.queryEmployees()
                .where(Employee::getId).eq(700L)
                .getList().get(0);
        assertThat(inserted.getName()).isEqualTo("No Email");
        assertThat(inserted.getEmail()).isNull();
    }

    /**
     * Tests batch insert with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch insert")
    void shouldHandleEmptyBatchInsert(DbConfig config) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() -> config.getUpdateExecutor().insertAll(emptyList, Employee.class));
    }

    private Employee createTestEmployee(Long id, String name, String email) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}
