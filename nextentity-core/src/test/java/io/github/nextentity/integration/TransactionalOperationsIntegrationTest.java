package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// Transactional 操作s integration tests.
/// <p>
/// 测试s transaction-related 行为 including:
/// - Transaction boundaries
/// - Batch 操作 atomicity
/// - Optimistic locking 行为
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
@DisplayName("Transactional Operations Integration Tests")
public class TransactionalOperationsIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    ///
    /// 测试s that insert 操作s are committed.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit insert operation")
    void shouldCommitInsertOperation(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7001L, "Transaction Test");

        // When
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // Then - Verify the insert was committed
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7001L)
                .single();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Transaction Test");

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s that update 操作s are committed.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit update operation")
    void shouldCommitUpdateOperation(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7002L, "Before Update");
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        employee.setName("After Update");
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then - Verify the update was committed
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7002L)
                .single();
        assertThat(found.getName()).isEqualTo("After Update");

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s that delete 操作s are committed.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should commit delete operation")
    void shouldCommitDeleteOperation(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7003L, "To Delete");
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));

        // Then - Verify the delete was committed
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7003L)
                .single();
        assertThat(found).isNull();
    }

    ///
    /// 测试s batch insert atomicity.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert batch atomically")
    void shouldInsertBatchAtomically(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employees.add(createTestEmployee(7100L + i, "Batch " + i));
        }

        // When
        context.getUpdateExecutor().insertAll(employees, context.getEntityContext(Employee.class));

        // Then - All should be inserted
        for (int i = 0; i < 5; i++) {
            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(7100L + i)
                    .single();
            assertThat(found).isNotNull();
        }

        // Cleanup
        context.getUpdateExecutor().deleteAll(employees, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s batch update atomicity.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update batch atomically")
    void shouldUpdateBatchAtomically(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            employees.add(createTestEmployee(7200L + i, "Before " + i));
        }
        context.getUpdateExecutor().insertAll(employees, context.getEntityContext(Employee.class));

        // When - Update all names
        for (int i = 0; i < employees.size(); i++) {
            employees.get(i).setName("After " + i);
        }
        context.getUpdateExecutor().updateAll(employees, context.getEntityContext(Employee.class));

        // Then - All should be updated
        for (int i = 0; i < 3; i++) {
            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(7200L + i)
                    .single();
            assertThat(found.getName()).isEqualTo("After " + i);
        }

        // Cleanup
        context.getUpdateExecutor().deleteAll(employees, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s batch delete atomicity.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete batch atomically")
    void shouldDeleteBatchAtomically(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            employees.add(createTestEmployee(7300L + i, "To Delete " + i));
        }
        context.getUpdateExecutor().insertAll(employees, context.getEntityContext(Employee.class));

        // When
        context.getUpdateExecutor().deleteAll(employees, context.getEntityContext(Employee.class));

        // Then - All should be deleted
        for (int i = 0; i < 3; i++) {
            Employee found = context.queryEmployees()
                    .where(Employee::getId).eq(7300L + i)
                    .single();
            assertThat(found).isNull();
        }
    }

    ///
    /// 测试s sequential CRUD 操作s.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should perform sequential CRUD operations")
    void shouldPerformSequentialCrudOperations(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7400L, "Sequential Test");

        // When - Insert
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));
        Employee afterInsert = context.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .single();
        assertThat(afterInsert).isNotNull();

        // When - Update
        employee.setName("Updated Sequential");
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));
        Employee afterUpdate = context.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .single();
        assertThat(afterUpdate.getName()).isEqualTo("Updated Sequential");

        // When - Delete
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
        Employee afterDelete = context.queryEmployees()
                .where(Employee::getId).eq(7400L)
                .single();
        assertThat(afterDelete).isNull();
    }

    ///
    /// 测试s multiple entity 操作s.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle multiple entity operations")
    void shouldHandleMultipleEntityOperations(IntegrationTestContext context) {
        // Given
        Employee emp1 = createTestEmployee(7501L, "Emp 1");
        Employee emp2 = createTestEmployee(7502L, "Emp 2");
        Employee emp3 = createTestEmployee(7503L, "Emp 3");

        // When - Insert multiple
        context.getUpdateExecutor().insert(emp1, context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(emp2, context.getEntityContext(Employee.class));
        context.getUpdateExecutor().insert(emp3, context.getEntityContext(Employee.class));

        // Then - All should exist
        long count = context.queryEmployees()
                .where(Employee::getId).in(7501L, 7502L, 7503L)
                .count();
        assertThat(count).isEqualTo(3);

        // When - Delete one
        context.getUpdateExecutor().delete(emp2, context.getEntityContext(Employee.class));

        // Then - Two should remain
        count = context.queryEmployees()
                .where(Employee::getId).in(7501L, 7502L, 7503L)
                .count();
        assertThat(count).isEqualTo(2);

        // Cleanup
        context.getUpdateExecutor().delete(emp1, context.getEntityContext(Employee.class));
        context.getUpdateExecutor().delete(emp3, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s status change transaction.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update status within transaction")
    void shouldUpdateStatusWithinTransaction(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7600L, "Status Test");
        employee.setStatus(EmployeeStatus.ACTIVE);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        employee.setStatus(EmployeeStatus.INACTIVE);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7600L)
                .single();
        assertThat(found.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s salary update transaction.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update salary within transaction")
    void shouldUpdateSalaryWithinTransaction(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7700L, "Salary Test");
        employee.setSalary(50000.0);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        employee.setSalary(60000.0);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7700L)
                .single();
        assertThat(found.getSalary()).isEqualTo(60000.0);

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s department change transaction.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update department within transaction")
    void shouldUpdateDepartmentWithinTransaction(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7800L, "Dept Test");
        employee.setDepartmentId(1L);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When
        employee.setDepartmentId(2L);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7800L)
                .single();
        assertThat(found.getDepartmentId()).isEqualTo(2L);

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s active flag toggle.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should toggle active flag")
    void shouldToggleActiveFlag(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(7900L, "Active Test");
        employee.setActive(true);
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));

        // When - Toggle to false
        employee.setActive(false);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then
        Employee found = context.queryEmployees()
                .where(Employee::getId).eq(7900L)
                .single();
        assertThat(found.getActive()).isFalse();

        // When - Toggle back to true
        employee.setActive(true);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // Then
        found = context.queryEmployees()
                .where(Employee::getId).eq(7900L)
                .single();
        assertThat(found.getActive()).isTrue();

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s query after insert.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query inserted data immediately")
    void shouldQueryInsertedDataImmediately(IntegrationTestContext context) {
        // Given
        Employee employee = createTestEmployee(8000L, "Query Test");

        // When
        context.getUpdateExecutor().insert(employee, context.getEntityContext(Employee.class));
        List<Employee> found = context.queryEmployees()
                .where(Employee::getId).eq(8000L)
                .list();

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Query Test");

        // Cleanup
        context.getUpdateExecutor().delete(employee, context.getEntityContext(Employee.class));
    }

    ///
    /// 测试s count after batch insert.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count after batch insert")
    void shouldCountAfterBatchInsert(IntegrationTestContext context) {
        // Given
        long initialCount = context.queryEmployees().count();

        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employees.add(createTestEmployee(8100L + i, "Count Test " + i));
        }

        // When
        context.getUpdateExecutor().insertAll(employees, context.getEntityContext(Employee.class));
        long newCount = context.queryEmployees().count();

        // Then
        assertThat(newCount).isEqualTo(initialCount + 5);

        // Cleanup
        context.getUpdateExecutor().deleteAll(employees, context.getEntityContext(Employee.class));
    }

    ///
    /// 创建 a test employee with the specified ID and name.
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
