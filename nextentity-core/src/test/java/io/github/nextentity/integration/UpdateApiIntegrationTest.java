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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    // ========================================
    // 1. Insert Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert single employee")
    void shouldInsertSingleEmployee(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(9001L, "Test Insert");

        // When
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = config.queryEmployees()
                .where(Employee::getId).eq(9001L)
                .getSingle();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Test Insert");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert multiple employees")
    void shouldInsertMultipleEmployees(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(9010L, "Test 1"));
        employees.add(createTestEmployee(9011L, "Test 2"));
        employees.add(createTestEmployee(9012L, "Test 3"));

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long count = config.queryEmployees()
                .where(Employee::getId).in(9010L, 9011L, 9012L)
                .count();
        assertThat(count).isEqualTo(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with null fields")
    void shouldInsertEmployeeWithNullFields(DbConfig config) {
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
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then
        Employee inserted = config.queryEmployees()
                .where(Employee::getId).eq(9020L)
                .getSingle();
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
    void shouldUpdateSingleEmployee(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        String originalName = employee.getName();
        employee.setName("Updated Name");

        // When
        Employee updated = config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getName()).isNotEqualTo(originalName);

        // Verify in database
        Employee fromDb = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        assertThat(fromDb.getName()).isEqualTo("Updated Name");
    }

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
        List<Employee> updated = config.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        assertThat(updated).hasSize(employees.size());
        for (Employee emp : updated) {
            assertThat(emp.getSalary()).isGreaterThanOrEqualTo(50000.0 + 1000);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update employee status")
    void shouldUpdateEmployeeStatus(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        employee.setStatus(EmployeeStatus.INACTIVE);

        // When
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee updated = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }

    // ========================================
    // 3. Patch Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should patch employee")
    void shouldPatchEmployee(DbConfig config) {
        // Given
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        Double newSalary = employee.getSalary() + 5000;
        employee.setSalary(newSalary);

        // When
        Employee patched = config.getUpdateExecutor().patch(employee, Employee.class);

        // Then
        assertThat(patched.getSalary()).isEqualTo(newSalary);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should patch employee with partial fields")
    @Disabled("BUG: PostgreSQL cannot determine data type for null parameters in JDBC patch operation")
    void shouldPatchEmployeeWithPartialFields(DbConfig config) {
        // Given - get original employee
        Employee original = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();
        String originalEmail = original.getEmail();

        // Create a new employee with only id and changed name
        Employee partial = new Employee();
        partial.setId(1L);
        partial.setName("Patched Name Only");

        // When
        Employee patched = config.getUpdateExecutor().patch(partial, Employee.class);

        // Then
        assertThat(patched.getName()).isEqualTo("Patched Name Only");
        // Other fields should remain unchanged or be updated depending on implementation
    }

    // ========================================
    // 4. Delete Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete single employee")
    void shouldDeleteSingleEmployee(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(9030L, "To Delete");
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Verify inserted
        assertThat(config.queryEmployees().where(Employee::getId).eq(9030L).exist()).isTrue();

        // When
        config.getUpdateExecutor().delete(employee, Employee.class);

        // Then
        assertThat(config.queryEmployees().where(Employee::getId).eq(9030L).exist()).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple employees")
    void shouldDeleteMultipleEmployees(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(9040L, "Delete 1"));
        employees.add(createTestEmployee(9041L, "Delete 2"));
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // When
        config.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then
        long count = config.queryEmployees()
                .where(Employee::getId).in(9040L, 9041L)
                .count();
        assertThat(count).isZero();
    }

    // ========================================
    // 5. Transaction Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should do in transaction with result")
    void shouldDoInTransactionWithResult(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(9050L, "Transaction Test");

        // When
        Employee result = config.getUpdateExecutor().doInTransaction(() -> {
            config.getUpdateExecutor().insert(employee, Employee.class);
            return config.queryEmployees()
                    .where(Employee::getId).eq(9050L)
                    .getSingle();
        });

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Transaction Test");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should do in transaction with runnable")
    void shouldDoInTransactionWithRunnable(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(9051L, "Runnable Transaction");

        // When
        config.getUpdateExecutor().doInTransaction(() -> {
            config.getUpdateExecutor().insert(employee, Employee.class);
        });

        // Then
        assertThat(config.queryEmployees().where(Employee::getId).eq(9051L).exist()).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should rollback on exception")
    @Disabled("BUG: Transaction rollback not working correctly in JDBC implementation - data persists after exception")
    void shouldRollbackOnException(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(9052L, "Rollback Test");

        // When
        assertThatThrownBy(() ->
                config.getUpdateExecutor().doInTransaction(() -> {
                    config.getUpdateExecutor().insert(employee, Employee.class);
                    throw new RuntimeException("Force rollback");
                })
        ).isInstanceOf(RuntimeException.class);

        // Then - employee should not exist due to rollback
        assertThat(config.queryEmployees().where(Employee::getId).eq(9052L).exist()).isFalse();
    }

    // ========================================
    // 6. Empty Batch Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty insert batch")
    void shouldHandleEmptyInsertBatch(DbConfig config) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().insertAll(new ArrayList<>(), Employee.class)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty update batch")
    void shouldHandleEmptyUpdateBatch(DbConfig config) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().updateAll(new ArrayList<>(), Employee.class)
        );
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty delete batch")
    void shouldHandleEmptyDeleteBatch(DbConfig config) {
        // When/Then
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().deleteAll(new ArrayList<>(), Employee.class)
        );
    }

    // ========================================
    // 7. Department Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert department")
    void shouldInsertDepartment(DbConfig config) {
        // Given
        Department department = new Department(9060L, "Test Dept", "Location", 100000.0, true);

        // When
        config.getUpdateExecutor().insert(department, Department.class);

        // Then
        Department inserted = config.queryDepartments()
                .where(Department::getId).eq(9060L)
                .getSingle();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Test Dept");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update department")
    void shouldUpdateDepartment(DbConfig config) {
        // Given
        Department department = config.queryDepartments()
                .where(Department::getId).eq(1L)
                .getSingle();
        department.setBudget(department.getBudget() + 50000);

        // When
        config.getUpdateExecutor().update(department, Department.class);

        // Then
        Department updated = config.queryDepartments()
                .where(Department::getId).eq(1L)
                .getSingle();
        assertThat(updated.getBudget()).isGreaterThan(100000.0);
    }

    // ========================================
    // 8. Duplicate Key Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail on duplicate ID insert")
    void shouldFailOnDuplicateIdInsert(DbConfig config) {
        // Given - employee with ID 1 already exists
        Employee duplicate = createTestEmployee(1L, "Duplicate");

        // When/Then
        assertThatThrownBy(() ->
                config.getUpdateExecutor().insert(duplicate, Employee.class)
        ).isInstanceOf(RuntimeException.class);
    }

    // ========================================
    // 9. Complex Transaction Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle complex transaction")
    void shouldHandleComplexTransaction(DbConfig config) {
        // When
        Long count = config.getUpdateExecutor().doInTransaction(() -> {
            // Insert
            Employee e1 = createTestEmployee(9070L, "Complex 1");
            config.getUpdateExecutor().insert(e1, Employee.class);

            // Update
            Employee e2 = config.queryEmployees().where(Employee::getId).eq(1L).getSingle();
            e2.setSalary(e2.getSalary() + 1000);
            config.getUpdateExecutor().update(e2, Employee.class);

            // Query
            return config.queryEmployees().count();
        });

        // Then
        assertThat(count).isGreaterThanOrEqualTo(12L);
    }

    // ========================================
    // 10. Status Enum Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert employee with all statuses")
    void shouldInsertEmployeeWithAllStatuses(DbConfig config) {
        // Given
        Employee active = createTestEmployee(9080L, "Active Status");
        active.setStatus(EmployeeStatus.ACTIVE);

        Employee inactive = createTestEmployee(9081L, "Inactive Status");
        inactive.setStatus(EmployeeStatus.INACTIVE);

        // When
        config.getUpdateExecutor().insert(active, Employee.class);
        config.getUpdateExecutor().insert(inactive, Employee.class);

        // Then
        Employee activeFromDb = config.queryEmployees().where(Employee::getId).eq(9080L).getSingle();
        Employee inactiveFromDb = config.queryEmployees().where(Employee::getId).eq(9081L).getSingle();

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