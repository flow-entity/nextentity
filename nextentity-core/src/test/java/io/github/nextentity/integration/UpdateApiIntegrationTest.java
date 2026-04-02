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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Update API integration tests.
 * <p>
 * Tests update operations including:
 * - patch operation (partial update)
 * - insert operations
 * - update operations
 * - delete operations
 * - transaction operations
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Update API Integration Tests")
public class UpdateApiIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    // ========================================
    // 1. Insert Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single employee")
    void shouldInsertSingleEmployee(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9001L, "Test Insert");

        // When
        context.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = context.queryEmployees()
                .where(Employee::getId).eq(9001L)
                .single();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Test Insert");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert multiple employees")
    void shouldInsertMultipleEmployees(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(9010L, "Test 1"));
        employees.add(createTestEmployee(9011L, "Test 2"));
        employees.add(createTestEmployee(9012L, "Test 3"));

        // When
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long count = context.queryEmployees()
                .where(Employee::getId).in(9010L, 9011L, 9012L)
                .count();
        assertThat(count).isEqualTo(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with null fields")
    void shouldInsertEmployeeWithNullFields(IntegrationTestContext context) {
        // Given
        Employee employee = new Employee();
        employee.setId(9020L);
        employee.setName("Null Fields Test");
        employee.setEmail(null);
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);

        // When
        context.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = context.queryEmployees()
                .where(Employee::getId).eq(9020L)
                .single();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Null Fields Test");
        assertThat(inserted.getEmail()).isNull();
    }

    // ========================================
    // 2. Update Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update single employee")
    void shouldUpdateSingleEmployee(IntegrationTestContext context) {
        // Given
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();
        String originalName = employee.getName();
        employee.setName("Updated Name");

        // When
        context.getUpdateExecutor().update(employee, Employee.class);
        Employee updated = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();
        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getName()).isNotEqualTo(originalName);

        // Verify in database
        Employee fromDb = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();
        assertThat(fromDb.getName()).isEqualTo("Updated Name");
    }

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
        List<Employee> updated = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list();


        // Then
        assertThat(updated).hasSize(employees.size());
        for (Employee emp : updated) {
            assertThat(emp.getSalary()).isGreaterThanOrEqualTo(50000.0 + 1000);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employee status")
    void shouldUpdateEmployeeStatus(IntegrationTestContext context) {
        // Given
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();
        employee.setStatus(EmployeeStatus.INACTIVE);

        // When
        context.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();
        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    // ========================================
    // 3. Delete Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete single employee")
    void shouldDeleteSingleEmployee(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9030L, "To Delete");
        context.getUpdateExecutor().insert(employee, Employee.class);

        // Verify inserted
        assertThat(context.queryEmployees().where(Employee::getId).eq(9030L).exists()).isTrue();

        // When
        context.getUpdateExecutor().delete(employee, Employee.class);

        // Then
        assertThat(context.queryEmployees().where(Employee::getId).eq(9030L).exists()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple employees")
    void shouldDeleteMultipleEmployees(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(9040L, "Delete 1"));
        employees.add(createTestEmployee(9041L, "Delete 2"));
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // When
        context.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then
        long count = context.queryEmployees()
                .where(Employee::getId).in(9040L, 9041L)
                .count();
        assertThat(count).isZero();
    }

    // ========================================
    // 4. Transaction Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should do in transaction with result")
    void shouldDoInTransactionWithResult(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9050L, "Transaction Test");

        // When
        Employee result = context.getUpdateExecutor().doInTransaction(() -> {
            context.getUpdateExecutor().insert(employee, Employee.class);
            return context.queryEmployees()
                    .where(Employee::getId).eq(9050L)
                    .single();
        });

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Transaction Test");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should do in transaction with runnable")
    void shouldDoInTransactionWithRunnable(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9051L, "Runnable Transaction");

        // When
        context.getUpdateExecutor().doInTransaction(() -> {
            context.getUpdateExecutor().insert(employee, Employee.class);
        });

        // Then
        assertThat(context.queryEmployees().where(Employee::getId).eq(9051L).exists()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should rollback on exception")
    void shouldRollbackOnException(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(9052L, "Rollback Test");

        // When
        assertThatThrownBy(() ->
                context.getUpdateExecutor().doInTransaction(() -> {
                    context.getUpdateExecutor().insert(employee, Employee.class);
                    throw new RuntimeException("Force rollback");
                })
        ).isInstanceOf(RuntimeException.class);

        // Then - employee should not exist due to rollback
        assertThat(context.queryEmployees().where(Employee::getId).eq(9052L).exists()).isFalse();
    }

    // ========================================
    // 5. Empty Batch Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty insert batch")
    void shouldHandleEmptyInsertBatch(IntegrationTestContext context) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().insertAll(new ArrayList<>(), Employee.class)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty update batch")
    void shouldHandleEmptyUpdateBatch(IntegrationTestContext context) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().updateAll(new ArrayList<>(), Employee.class)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty delete batch")
    void shouldHandleEmptyDeleteBatch(IntegrationTestContext context) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().deleteAll(new ArrayList<>(), Employee.class)
        );
    }

    // ========================================
    // 6. Department Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert department")
    void shouldInsertDepartment(IntegrationTestContext context) {
        // Given
        Department department = new Department(9060L, "Test Dept", "Location", 100000.0, true);

        // When
        context.getUpdateExecutor().insert(department, Department.class);

        // Then
        Department inserted = context.queryDepartments()
                .where(Department::getId).eq(9060L)
                .single();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Test Dept");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update department")
    void shouldUpdateDepartment(IntegrationTestContext context) {
        // Given
        Department department = context.queryDepartments()
                .where(Department::getId).eq(1L)
                .single();
        department.setBudget(department.getBudget() + 50000);

        // When
        context.getUpdateExecutor().update(department, Department.class);

        // Then
        Department updated = context.queryDepartments()
                .where(Department::getId).eq(1L)
                .single();
        assertThat(updated.getBudget()).isGreaterThan(100000.0);
    }

    // ========================================
    // 7. Duplicate Key Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail on duplicate ID insert")
    void shouldFailOnDuplicateIdInsert(IntegrationTestContext context) {
        // Given - employee with ID 1 already exists
        Employee duplicate = createTestEmployee(1L, "Duplicate");

        // When/Then
        assertThatThrownBy(() ->
                context.getUpdateExecutor().insert(duplicate, Employee.class)
        ).isInstanceOf(RuntimeException.class);
    }

    // ========================================
    // 8. Complex Transaction Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle complex transaction")
    void shouldHandleComplexTransaction(IntegrationTestContext context) {
        // When
        Long count = context.getUpdateExecutor().doInTransaction(() -> {
            // Insert
            Employee e1 = createTestEmployee(9070L, "Complex 1");
            context.getUpdateExecutor().insert(e1, Employee.class);

            // Update
            Employee e2 = context.queryEmployees().where(Employee::getId).eq(1L).single();
            e2.setSalary(e2.getSalary() + 1000);
            context.getUpdateExecutor().update(e2, Employee.class);

            // Query
            return context.queryEmployees().count();
        });

        // Then
        assertThat(count).isGreaterThanOrEqualTo(12L);
    }

    // ========================================
    // 9. Status Enum Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with all statuses")
    void shouldInsertEmployeeWithAllStatuses(IntegrationTestContext context) {
        // Given
        Employee active = createTestEmployee(9080L, "Active Status");
        active.setStatus(EmployeeStatus.ACTIVE);

        Employee inactive = createTestEmployee(9081L, "Inactive Status");
        inactive.setStatus(EmployeeStatus.INACTIVE);

        // When
        context.getUpdateExecutor().insert(active, Employee.class);
        context.getUpdateExecutor().insert(inactive, Employee.class);

        // Then
        Employee activeFromDb = context.queryEmployees().where(Employee::getId).eq(9080L).single();
        Employee inactiveFromDb = context.queryEmployees().where(Employee::getId).eq(9081L).single();

        assertThat(activeFromDb.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(inactiveFromDb.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    // ========================================
    // Helper Methods
    // ========================================

    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail("test" + id + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}
