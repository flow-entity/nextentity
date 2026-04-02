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
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Batch operations integration tests.
 * <p>
 * Tests batch operations including:
 * - Large batch insert (100+ records)
 * - Batch update performance
 * - Batch delete
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Batch Operations Integration Tests")
public class BatchOperationsIntegrationTest {

    private static final int BATCH_SIZE = 100;

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    /**
     * Tests batch insert of 100 records.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert 100 records in batch")
    void shouldInsertLargeBatch(IntegrationTestContext context) {
        // Given
        List<Employee> employees = createTestEmployees(1000, BATCH_SIZE);

        // When
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        List<Employee> inserted = context.queryEmployees()
                .where(Employee::getId).ge(1000L)
                .orderBy(Employee::getId).asc()
                .list();
        assertThat(inserted).hasSize(BATCH_SIZE);
    }

    /**
     * Tests batch insert of 200 records.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert 200 records in batch")
    void shouldInsertLargerBatch(IntegrationTestContext context) {
        // Given
        List<Employee> employees = createTestEmployees(2000, 200);

        // When
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long count = context.queryEmployees()
                .where(Employee::getId).ge(2000L)
                .count();
        assertThat(count).isEqualTo(200);
    }

    /**
     * Tests batch update.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update multiple records in batch")
    void shouldUpdateBatch(IntegrationTestContext context) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(3000, 20);
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Modify all employees
        for (Employee emp : employees) {
            emp.setSalary(emp.getSalary() + 10000);
        }

        // When
        context.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = context.queryEmployees()
                .where(Employee::getId).ge(3000L)
                .list();
        assertThat(updated).allMatch(e -> e.getSalary() >= 60000.0);
    }

    /**
     * Tests batch delete.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple records in batch")
    void shouldDeleteBatch(IntegrationTestContext context) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(4000, 15);
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Verify inserted
        long countBefore = context.queryEmployees()
                .where(Employee::getId).ge(4000L)
                .count();
        assertThat(countBefore).isEqualTo(15);

        // When
        context.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then
        long countAfter = context.queryEmployees()
                .where(Employee::getId).ge(4000L)
                .count();
        assertThat(countAfter).isZero();
    }

    /**
     * Tests batch insert with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch insert")
    void shouldHandleEmptyBatchInsert(IntegrationTestContext context) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().insertAll(emptyList, Employee.class));
    }

    /**
     * Tests batch update with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch update")
    void shouldHandleEmptyBatchUpdate(IntegrationTestContext context) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().updateAll(emptyList, Employee.class));
    }

    /**
     * Tests batch delete with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch delete")
    void shouldHandleEmptyBatchDelete(IntegrationTestContext context) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().deleteAll(emptyList, Employee.class));
    }

    /**
     * Tests batch insert with single element.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle single element batch insert")
    void shouldHandleSingleElementBatchInsert(IntegrationTestContext context) {
        // Given
        List<Employee> singleList = new ArrayList<>();
        singleList.add(createTestEmployee(5000L, "Single Employee"));

        // When
        context.getUpdateExecutor().insertAll(singleList, Employee.class);

        // Then
        Employee inserted = context.queryEmployees()
                .where(Employee::getId).eq(5000L)
                .single();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Single Employee");
    }

    /**
     * Tests batch operations with varying data.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert batch with varying data")
    void shouldInsertBatchWithVaryingData(IntegrationTestContext context) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(6000L, "Employee A"));
        employees.add(createTestEmployeeWithNulls(6001L, "Employee B"));
        employees.add(createTestEmployee(6002L, "Employee C"));

        // When
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        List<Employee> inserted = context.queryEmployees()
                .where(Employee::getId).in(6000L, 6001L, 6002L)
                .orderBy(Employee::getId).asc()
                .list();
        assertThat(inserted).hasSize(3);
    }

    /**
     * Tests batch update partial fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update batch with partial field changes")
    void shouldUpdateBatchPartialFields(IntegrationTestContext context) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(7000, 10);
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Modify only status
        for (Employee emp : employees) {
            emp.setStatus(EmployeeStatus.INACTIVE);
        }

        // When
        context.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = context.queryEmployees()
                .where(Employee::getId).ge(7000L)
                .list();
        assertThat(updated).allMatch(e -> e.getStatus() == EmployeeStatus.INACTIVE);
    }

    /**
     * Tests large batch insert and query performance.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle large batch operations efficiently")
    void shouldHandleLargeBatchEfficiently(IntegrationTestContext context) {
        // Given
        int largeBatchSize = 50;
        List<Employee> employees = createTestEmployees(8000, largeBatchSize);

        // When
        long startInsert = System.currentTimeMillis();
        context.getUpdateExecutor().insertAll(employees, Employee.class);
        long insertTime = System.currentTimeMillis() - startInsert;

        // Then
        long count = context.queryEmployees()
                .where(Employee::getId).ge(8000L)
                .count();
        assertThat(count).isEqualTo(largeBatchSize);

        // Log performance (for informational purposes)
        System.out.println("Batch insert of " + largeBatchSize + " records took: " + insertTime + "ms");
    }

    /**
     * Tests batch insert followed by batch delete.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert and delete batch sequentially")
    void shouldInsertAndDeleteBatchSequentially(IntegrationTestContext context) {
        // Given
        List<Employee> employees = createTestEmployees(9000, 25);

        // When - Insert
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then - Verify insert
        long countAfterInsert = context.queryEmployees()
                .where(Employee::getId).ge(9000L)
                .count();
        assertThat(countAfterInsert).isEqualTo(25);

        // When - Delete
        context.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then - Verify delete
        long countAfterDelete = context.queryEmployees()
                .where(Employee::getId).ge(9000L)
                .count();
        assertThat(countAfterDelete).isZero();
    }

    /**
     * Tests batch insert with existing data.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert batch without affecting existing data")
    void shouldInsertBatchWithoutAffectingExistingData(IntegrationTestContext context) {
        // Given
        long originalCount = context.queryEmployees().count();
        List<Employee> employees = createTestEmployees(10000, 10);

        // When
        context.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long newCount = context.queryEmployees().count();
        assertThat(newCount).isEqualTo(originalCount + 10);
    }

    /**
     * Creates a list of test employees.
     */
    private List<Employee> createTestEmployees(long startId, int count) {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            employees.add(createTestEmployee(startId + i, "Batch Employee " + i));
        }
        return employees;
    }

    /**
     * Creates a test employee with specified ID and name.
     */
    private Employee createTestEmployee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail("batch" + id + "@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }

    /**
     * Creates a test employee with some null fields.
     */
    private Employee createTestEmployeeWithNulls(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(null); // Null email
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());
        return employee;
    }
}
