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

    /**
     * Tests batch insert of 100 records.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert 100 records in batch")
    void shouldInsertLargeBatch(DbConfig config) {
        // Given
        List<Employee> employees = createTestEmployees(1000, BATCH_SIZE);

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        List<Employee> inserted = config.queryEmployees()
                .where(Employee::getId).ge(1000L)
                .orderBy(Employee::getId).asc()
                .getList();
        assertThat(inserted).hasSize(BATCH_SIZE);
    }

    /**
     * Tests batch insert of 200 records.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert 200 records in batch")
    void shouldInsertLargerBatch(DbConfig config) {
        // Given
        List<Employee> employees = createTestEmployees(2000, 200);

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long count = config.queryEmployees()
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
    void shouldUpdateBatch(DbConfig config) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(3000, 20);
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Modify all employees
        for (Employee emp : employees) {
            emp.setSalary(emp.getSalary() + 10000);
        }

        // When
        config.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = config.queryEmployees()
                .where(Employee::getId).ge(3000L)
                .getList();
        assertThat(updated).allMatch(e -> e.getSalary() >= 60000.0);
    }

    /**
     * Tests batch delete.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete multiple records in batch")
    void shouldDeleteBatch(DbConfig config) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(4000, 15);
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Verify inserted
        long countBefore = config.queryEmployees()
                .where(Employee::getId).ge(4000L)
                .count();
        assertThat(countBefore).isEqualTo(15);

        // When
        config.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then
        long countAfter = config.queryEmployees()
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
    void shouldHandleEmptyBatchInsert(DbConfig config) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().insertAll(emptyList, Employee.class));
    }

    /**
     * Tests batch update with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch update")
    void shouldHandleEmptyBatchUpdate(DbConfig config) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().updateAll(emptyList, Employee.class));
    }

    /**
     * Tests batch delete with empty list.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch delete")
    void shouldHandleEmptyBatchDelete(DbConfig config) {
        // Given
        List<Employee> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                config.getUpdateExecutor().deleteAll(emptyList, Employee.class));
    }

    /**
     * Tests batch insert with single element.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle single element batch insert")
    void shouldHandleSingleElementBatchInsert(DbConfig config) {
        // Given
        List<Employee> singleList = new ArrayList<>();
        singleList.add(createTestEmployee(5000L, "Single Employee"));

        // When
        config.getUpdateExecutor().insertAll(singleList, Employee.class);

        // Then
        Employee inserted = config.queryEmployees()
                .where(Employee::getId).eq(5000L)
                .getSingle();
        assertThat(inserted).isNotNull();
        assertThat(inserted.getName()).isEqualTo("Single Employee");
    }

    /**
     * Tests batch operations with varying data.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert batch with varying data")
    void shouldInsertBatchWithVaryingData(DbConfig config) {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(createTestEmployee(6000L, "Employee A"));
        employees.add(createTestEmployeeWithNulls(6001L, "Employee B"));
        employees.add(createTestEmployee(6002L, "Employee C"));

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        List<Employee> inserted = config.queryEmployees()
                .where(Employee::getId).in(6000L, 6001L, 6002L)
                .orderBy(Employee::getId).asc()
                .getList();
        assertThat(inserted).hasSize(3);
    }

    /**
     * Tests batch update partial fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update batch with partial field changes")
    void shouldUpdateBatchPartialFields(DbConfig config) {
        // Given - Insert test employees
        List<Employee> employees = createTestEmployees(7000, 10);
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Modify only status
        for (Employee emp : employees) {
            emp.setStatus(EmployeeStatus.INACTIVE);
        }

        // When
        config.getUpdateExecutor().updateAll(employees, Employee.class);

        // Then
        List<Employee> updated = config.queryEmployees()
                .where(Employee::getId).ge(7000L)
                .getList();
        assertThat(updated).allMatch(e -> e.getStatus() == EmployeeStatus.INACTIVE);
    }

    /**
     * Tests large batch insert and query performance.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle large batch operations efficiently")
    void shouldHandleLargeBatchEfficiently(DbConfig config) {
        // Given
        int largeBatchSize = 50;
        List<Employee> employees = createTestEmployees(8000, largeBatchSize);

        // When
        long startInsert = System.currentTimeMillis();
        config.getUpdateExecutor().insertAll(employees, Employee.class);
        long insertTime = System.currentTimeMillis() - startInsert;

        // Then
        long count = config.queryEmployees()
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
    void shouldInsertAndDeleteBatchSequentially(DbConfig config) {
        // Given
        List<Employee> employees = createTestEmployees(9000, 25);

        // When - Insert
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then - Verify insert
        long countAfterInsert = config.queryEmployees()
                .where(Employee::getId).ge(9000L)
                .count();
        assertThat(countAfterInsert).isEqualTo(25);

        // When - Delete
        config.getUpdateExecutor().deleteAll(employees, Employee.class);

        // Then - Verify delete
        long countAfterDelete = config.queryEmployees()
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
    void shouldInsertBatchWithoutAffectingExistingData(DbConfig config) {
        // Given
        long originalCount = config.queryEmployees().count();
        List<Employee> employees = createTestEmployees(10000, 10);

        // When
        config.getUpdateExecutor().insertAll(employees, Employee.class);

        // Then
        long newCount = config.queryEmployees().count();
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