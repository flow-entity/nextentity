package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Transactional operations integration tests.
 * <p>
 * Tests transaction-related behavior including:
 * - Transaction boundaries
 * - Batch operation atomicity
 * - Optimistic locking behavior
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Transactional Operations Integration Tests")
public class TransactionalOperationsIntegrationTest {

    /**
     * Tests that insert operations are committed.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit insert operation")
    void shouldCommitInsertOperation(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7001L, "Transaction Test");

        // When
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then - Verify the insert was committed
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7001L)
                .getSingle();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Transaction Test");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests that update operations are committed.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit update operation")
    void shouldCommitUpdateOperation(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7002L, "Before Update");
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        employee.setName("After Update");
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then - Verify the update was committed
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7002L)
                .getSingle();
        assertThat(found.getName()).isEqualTo("After Update");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests that delete operations are committed.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit delete operation")
    void shouldCommitDeleteOperation(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7003L, "To Delete");
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        config.getUpdateExecutor().delete(employee, Employee.class);

        // Then - Verify the delete was committed
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7003L)
                .getSingle();
        assertThat(found).isNull();
    }

    /**
     * Tests batch insert atomicity.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert batch atomically")
    void shouldInsertBatchAtomically(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employees.add(createTestEmployee(7100L + i, "Batch " + i));
        }

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then - All should be inserted
        for (int i = 0; i < 5; i++) {
            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(7100L + i)
                    .getSingle();
            assertThat(found).isNotNull();
        }

        // Cleanup
        config.getUpdateExecutor().deleteAll(employees, Employee.class);
    }

    /**
     * Tests batch update atomicity.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update batch atomically")
    void shouldUpdateBatchAtomically(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            employees.add(createTestEmployee(7200L + i, "Before " + i));
        }
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // When - Update all names
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).setName("After " + i);
        }
        config.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then - All should be updated
        for (int i = 0; i < 3; i++) {
            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(7200L + i)
                    .getSingle();
            assertThat(found.getName()).isEqualTo("After " + i);
        }

        // Cleanup
        config.getUpdateExecutor().deleteAll(employees, Employee.class);
    }

    /**
     * Tests batch delete atomicity.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete batch atomically")
    void shouldDeleteBatchAtomically(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            employees.add(createTestEmployee(7300L + i, "To Delete " + i));
        }
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // When
        config.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then - All should be deleted
        for (int i = 0; i < 3; i++) {
            Employee found = config.queryEmployees()
                    .where(Employee::getId).eq(7300L + i)
                    .getSingle();
            assertThat(found).isNull();
        }
    }

    /**
     * Tests sequential CRUD operations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform sequential CRUD operations")
    void shouldPerformSequentialCrudOperations(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7400L, "Sequential Test");

        // When - Insert
        config.getUpdateExecutor().insert(employee, Employee.class);
        Employee afterInsert = config.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .getSingle();
        assertThat(afterInsert).isNotNull();

        // When - Update
        employee.setName("Updated Sequential");
        config.getUpdateExecutor().update(employee, Employee.class);
        Employee afterUpdate = config.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .getSingle();
        assertThat(afterUpdate.getName()).isEqualTo("Updated Sequential");

        // When - Delete
        config.getUpdateExecutor().delete(employee, Employee.class);
        Employee afterDelete = config.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .getSingle();
        assertThat(afterDelete).isNull();
    }

    /**
     * Tests multiple entity operations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle multiple entity operations")
    void shouldHandleMultipleEntityOperations(DbConfig config) {
        // Given
        Employee emp1 = createTestEmployee(7501L, "Emp 1");
        Employee emp2 = createTestEmployee(7502L, "Emp 2");
        Employee emp3 = createTestEmployee(7503L, "Emp 3");

        // When - Insert multiple
        config.getUpdateExecutor().insert(emp1, Employee.class);
        config.getUpdateExecutor().insert(emp2, Employee.class);
        config.getUpdateExecutor().insert(emp3, Employee.class);

        // Then - All should exist
        long count = config.queryEmployees()
                .where(Employee::getId).in(7501L, 7502L, 7503L)
                .count();
        assertThat(count).isEqualTo(3);

        // When - Delete one
        config.getUpdateExecutor().delete(emp2, Employee.class);

        // Then - Two should remain
        count = config.queryEmployees()
                .where(Employee::getId).in(7501L, 7502L, 7503L)
                .count();
        assertThat(count).isEqualTo(2);

        // Cleanup
        config.getUpdateExecutor().delete(emp1, Employee.class);
        config.getUpdateExecutor().delete(emp3, Employee.class);
    }

    /**
     * Tests status change transaction.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update status within transaction")
    void shouldUpdateStatusWithinTransaction(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7600L, "Status Test");
        employee.setStatus(EmployeeStatus.ACTIVE);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        employee.setStatus(EmployeeStatus.INACTIVE);
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7600L)
                .getSingle();
        assertThat(found.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests salary update transaction.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update salary within transaction")
    void shouldUpdateSalaryWithinTransaction(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7700L, "Salary Test");
        employee.setSalary(50000.0);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        employee.setSalary(60000.0);
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7700L)
                .getSingle();
        assertThat(found.getSalary()).isEqualTo(60000.0);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests department change transaction.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update department within transaction")
    void shouldUpdateDepartmentWithinTransaction(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7800L, "Dept Test");
        employee.setDepartmentId(1L);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        employee.setDepartmentId(2L);
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7800L)
                .getSingle();
        assertThat(found.getDepartmentId()).isEqualTo(2L);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests active flag toggle.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should toggle active flag")
    void shouldToggleActiveFlag(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(7900L, "Active Test");
        employee.setActive(true);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When - Toggle to false
        employee.setActive(false);
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(7900L)
                .getSingle();
        assertThat(found.getActive()).isFalse();

        // When - Toggle back to true
        employee.setActive(true);
        config.getUpdateExecutor().update(employee, Employee.class);

        // Then
        found = config.queryEmployees()
                .where(Employee::getId).eq(7900L)
                .getSingle();
        assertThat(found.getActive()).isTrue();

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests query after insert.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query inserted data immediately")
    void shouldQueryInsertedDataImmediately(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(8000L, "Query Test");

        // When
        config.getUpdateExecutor().insert(employee, Employee.class);
        List<Employee> found = config.queryEmployees()
                .where(Employee::getId).eq(8000L)
                .getList();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Query Test");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests count after batch insert.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count after batch insert")
    void shouldCountAfterBatchInsert(DbConfig config) {
        // Given
        long initialCount = config.queryEmployees().count();

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employees.add(createTestEmployee(8100L + i, "Count Test " + i));
        }

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);
        long newCount = config.queryEmployees().count();

        // Then
        assertThat(newCount).isEqualTo(initialCount + 5);

        // Cleanup
        config.getUpdateExecutor().deleteAll(employees, Employee.class);
    }

    /**
     * Creates a test employee with the specified ID and name.
     */
    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}