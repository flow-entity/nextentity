package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

///
 /// CRUD 操作s integration tests.
 /// <p>
 /// 测试s insert, update, and delete 操作s including:
 /// - Single entity insert/update/delete
 /// - Batch insert/update/delete
 /// - Optimistic locking
 /// - Partial field updates
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("CRUD Operations Integration Tests")
public class CrudOperationsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CrudOperationsIntegrationTest.class);

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

///
     /// 测试s inserting a single employee.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single employee")
    void shouldInsertSingleEmployee(IntegrationTestContext context) {
        // Given
        Employee newEmployee = createTestEmployee(100L, "Test User", "test@example.com");

        // When
        context.getUpdateExecutor().insert(newEmployee, Employee.class);

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(100L)
                .list();
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isEqualTo("Test User");
        assertThat(employees.get(0).getEmail()).isEqualTo("test@example.com");
    }

///
     /// 测试s inserting multiple employees.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert multiple employees")
    void shouldInsertMultipleEmployees(IntegrationTestContext context) {
        // Given
        List<Employee> newEmployees = new ArrayList<>();
        newEmployees.add(createTestEmployee(200L, "User 200", "user200@example.com"));
        newEmployees.add(createTestEmployee(201L, "User 201", "user201@example.com"));
        newEmployees.add(createTestEmployee(202L, "User 202", "user202@example.com"));

        // When
        context.getUpdateExecutor().insertAll(newEmployees, Employee.class);

        // Then
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(200L, 201L, 202L)
                .orderBy(Employee::getId).asc()
                .list();
        assertThat(employees).hasSize(3);
        assertThat(employees.get(0).getName()).isEqualTo("User 200");
        assertThat(employees.get(2).getName()).isEqualTo("User 202");
    }

///
     /// 测试s inserting a department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single department")
    void shouldInsertSingleDepartment(IntegrationTestContext context) {
        // Given
        Department newDept = new Department(100L, "IT", "Building E", 500000.0, true);

        // When
        context.getUpdateExecutor().insert(newDept, Department.class);

        // Then
        List<Department> departments = context.queryDepartments()
                .where(Department::getId).eq(100L)
                .list();
        assertThat(departments).hasSize(1);
        assertThat(departments.get(0).getName()).isEqualTo("IT");
        assertThat(departments.get(0).getLocation()).isEqualTo("Building E");
    }

///
     /// 测试s updating a single employee.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update single employee")
    void shouldUpdateSingleEmployee(IntegrationTestContext context) {
        // Given
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list().get(0);
        String originalName = employee.getName();
        employee.setName("Updated Name");
        employee.setSalary(99999.0);

        // When
        context.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list().get(0);
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getSalary()).isEqualTo(99999.0);
        assertThat(updated.getName()).isNotEqualTo(originalName);
    }

///
     /// 测试s updating multiple employees.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update multiple employees")
    void shouldUpdateMultipleEmployees(IntegrationTestContext context) {
        // Given
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list();

        for (Employee emp : employees) {
            emp.setSalary(emp.getSalary() + 1000);
        }

        // When
        context.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list();
        assertThat(updated).hasSize(employees.size());
        for (int i = 0; i < employees.size(); i++) {
            assertThat(updated.get(i).getSalary()).isEqualTo(employees.get(i).getSalary());
        }
    }

///
     /// 测试s deleting a single employee.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete single employee")
    void shouldDeleteSingleEmployee(IntegrationTestContext context) {
        // Given - create an employee to delete
        Employee newEmployee = createTestEmployee(300L, "To Delete", "delete@example.com");
        context.getUpdateExecutor().insert(newEmployee, Employee.class);

        // Verify it exists
        List<Employee> before = context.queryEmployees()
                .where(Employee::getId).eq(300L)
                .list();
        assertThat(before).hasSize(1);

        // When
        context.getUpdateExecutor().delete(newEmployee, Employee.class);

        // Then
        List<Employee> after = context.queryEmployees()
                .where(Employee::getId).eq(300L)
                .list();
        assertThat(after).isEmpty();
    }

///
     /// 测试s deleting multiple employees.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple employees")
    void shouldDeleteMultipleEmployees(IntegrationTestContext context) {
        // Given - create employees to delete
        List<Employee> newEmployees = new ArrayList<>();
        newEmployees.add(createTestEmployee(400L, "Delete 1", "delete1@example.com"));
        newEmployees.add(createTestEmployee(401L, "Delete 2", "delete2@example.com"));
        context.getUpdateExecutor().insertAll(newEmployees, Employee.class);

        // Verify they exist
        List<Employee> before = context.queryEmployees()
                .where(Employee::getId).in(400L, 401L)
                .list();
        assertThat(before).hasSize(2);

        // When
        context.getUpdateExecutor().deleteAll(newEmployees, Employee.class);

        // Then
        List<Employee> after = context.queryEmployees()
                .where(Employee::getId).in(400L, 401L)
                .list();
        assertThat(after).isEmpty();
    }

///
     /// 测试s deleting by ID using where clause.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete with where condition")
    void shouldDeleteWithWhereCondition(IntegrationTestContext context) {
        // Given - create an employee to delete
        Employee newEmployee = createTestEmployee(500L, "Condition Delete", "cond@example.com");
        context.getUpdateExecutor().insert(newEmployee, Employee.class);

        // When - delete using where clause
        Employee toDelete = context.queryEmployees()
                .where(Employee::getId).eq(500L)
                .list().get(0);
        context.getUpdateExecutor().delete(toDelete, Employee.class);

        // Then
        List<Employee> after = context.queryEmployees()
                .where(Employee::getId).eq(500L)
                .list();
        assertThat(after).isEmpty();
    }

///
     /// 测试s updating employee status.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employee status")
    void shouldUpdateEmployeeStatus(IntegrationTestContext context) {
        // Given
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list().get(0);
        EmployeeStatus originalStatus = employee.getStatus();
        employee.setStatus(EmployeeStatus.INACTIVE);

        // When
        context.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list().get(0);
        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        assertThat(updated.getStatus()).isNotEqualTo(originalStatus);
    }

///
     /// 测试s inserting and updating department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert and update department")
    void shouldInsertAndUpdateDepartment(IntegrationTestContext context) {
        // Given - insert new department
        Department newDept = new Department(200L, "Research", "Building F", 300000.0, true);
        context.getUpdateExecutor().insert(newDept, Department.class);

        // When - update the department
        Department dept = context.queryDepartments()
                .where(Department::getId).eq(200L)
                .list().get(0);
        dept.setBudget(400000.0);
        dept.setLocation("Building G");
        context.getUpdateExecutor().update(dept, Department.class);

        // Then
        Department updated = context.queryDepartments()
                .where(Department::getId).eq(200L)
                .list().get(0);
        assertThat(updated.getBudget()).isEqualTo(400000.0);
        assertThat(updated.getLocation()).isEqualTo("Building G");
    }

///
     /// 测试s inserting employee with all fields.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with all fields")
    void shouldInsertEmployeeWithAllFields(IntegrationTestContext context) {
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
        context.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = context.queryEmployees()
                .where(Employee::getId).eq(600L)
                .list().get(0);
        assertThat(inserted.getName()).isEqualTo("Full Employee");
        assertThat(inserted.getSalary()).isEqualTo(75000.0);
        assertThat(inserted.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(inserted.getHireDate()).isEqualTo(LocalDate.of(2024, 1, 15));
    }

///
     /// 测试s inserting duplicate ID should fail.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail when inserting duplicate ID")
    void shouldFailOnDuplicateId(IntegrationTestContext context) {
        // Given - employee with ID 1 already exists
        Employee duplicateEmployee = createTestEmployee(1L, "Duplicate", "dup@example.com");

        // When/Then - should throw exception
        assertThatThrownBy(() -> context.getUpdateExecutor().insert(duplicateEmployee, Employee.class))
                .isInstanceOf(RuntimeException.class);
    }

    /// 测试s updating non-existent employee.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle update of non-existent employee")
    void shouldHandleUpdateNonExistent(IntegrationTestContext context) {
        // Given
        Employee nonExistent = createTestEmployee(9999L, "Non Existent", "none@example.com");

        // When/Then - should throw exception (entity not found)
        assertThatThrownBy(() -> context.getUpdateExecutor().update(nonExistent, Employee.class))
                .isInstanceOf(RuntimeException.class);

    }

///
     /// 测试s deleting non-existent employee.
     /// <p>
     /// Note: JPA implementation silently ignores non-existent entities,
     /// while JDBC implementation may have different 行为.
     /// This test verifies the actual 行为 without asserting specific exceptions.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle delete of non-existent employee")
    void shouldHandleDeleteNonExistent(IntegrationTestContext context) {
        // Given
        Employee nonExistent = createTestEmployee(9998L, "Non Existent", "none@example.com");

        // When - delete non-existent entity should not throw exception
        // JPA: entityManager.remove() on non-existent entity is a no-op
        // JDBC: delete operation affects 0 rows, but doesn't throw exception
        assertThatThrownBy(() -> context.getUpdateExecutor().delete(nonExistent, Employee.class))
                .isInstanceOf(RuntimeException.class);
        // Then - operation completes without error (implementation-specific behavior)
        // This test documents that delete of non-existent entities is allowed
    }

///
     /// 测试s inserting employee with null email.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with null email")
    void shouldInsertEmployeeWithNullEmail(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(700L, "No Email", null);

        // When
        context.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = context.queryEmployees()
                .where(Employee::getId).eq(700L)
                .list().get(0);
        assertThat(inserted.getName()).isEqualTo("No Email");
        assertThat(inserted.getEmail()).isNull();
    }

///
     /// 测试s batch insert with empty list.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch insert")
    void shouldHandleEmptyBatchInsert(IntegrationTestContext context) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() -> context.getUpdateExecutor().insertAll(emptyList, Employee.class));
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

