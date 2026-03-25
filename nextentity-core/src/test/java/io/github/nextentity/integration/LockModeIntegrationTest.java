package io.github.nextentity.integration;

import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Lock mode integration tests.
 * <p>
 * Tests pessimistic and optimistic locking mechanisms using
 * the getList method with LockModeType parameter.
 * <p>
 * Note: Lock operations require an active transaction.
 * All lock tests are wrapped in doInTransaction.
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Lock Mode Integration Tests")
public class LockModeIntegrationTest {

    // ========================================
    // 1. PESSIMISTIC_READ Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic read lock")
    void shouldQueryWithPessimisticRead(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with pessimistic read lock")
    void shouldGetSingleWithPessimisticRead(IntegrationTestContext context) {
        // When
        Employee employee = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getSingle(0, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    // ========================================
    // 2. PESSIMISTIC_WRITE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic write lock")
    void shouldQueryWithPessimisticWrite(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with pessimistic write lock")
    void shouldGetSingleWithPessimisticWrite(IntegrationTestContext context) {
        // When
        Employee employee = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getSingle(0, LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    // ========================================
    // 3. OPTIMISTIC Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic lock")
    void shouldQueryWithOptimistic(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.OPTIMISTIC)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic force increment lock")
    void shouldQueryWithOptimisticForceIncrement(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.OPTIMISTIC_FORCE_INCREMENT)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    // ========================================
    // 4. READ/WRITE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with read lock")
    void shouldQueryWithReadLock(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.READ)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with write lock")
    void shouldQueryWithWriteLock(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.WRITE)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    // ========================================
    // 5. NONE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with no lock")
    void shouldQueryWithNoLock(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, LockModeType.NONE)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    // ========================================
    // 6. Lock with Conditions
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with where condition")
    void shouldLockWithWhereCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getActive).eq(true)
                        .orderBy(Employee::getId).asc()
                        .getList(0, 10, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees).allMatch(Employee::getActive);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with multiple conditions")
    void shouldLockWithMultipleConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getActive).eq(true)
                        .where(Employee::getSalary).gt(50000.0)
                        .getList(0, 10, LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getActive()).isTrue();
            assertThat(emp.getSalary()).isGreaterThan(50000.0);
        }
    }

    // ========================================
    // 7. Lock with Pagination
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with pagination")
    void shouldLockWithPagination(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .orderBy(Employee::getId).asc()
                        .getList(0, 3, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(3);
    }

    // ========================================
    // 8. Lock with Offset
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with offset")
    void shouldLockWithOffset(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .orderBy(Employee::getId).asc()
                        .getList(2, 3, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(3);
        assertThat(employees.get(0).getId()).isEqualTo(3L);
    }

    // ========================================
    // 9. Lock with First
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with lock")
    void shouldGetFirstWithLock(IntegrationTestContext context) {
        // When
        Employee employee = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .orderBy(Employee::getId).asc()
                        .getFirst(0, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    // ========================================
    // 10. PESSIMISTIC_FORCE_INCREMENT Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic force increment lock")
    void shouldQueryWithPessimisticForceIncrement(IntegrationTestContext context) {
        // When & Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().doInTransaction(() -> {
                    context.queryEmployees()
                            .where(Employee::getId).eq(1L)
                            .getList(0, 10, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
                    return null;
                })
        );
    }

    // ========================================
    // 11. Lock with In Condition
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with in condition")
    void shouldLockWithInCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).in(1L, 2L, 3L)
                        .orderBy(Employee::getId).asc()
                        .getList(0, 10, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(3);
    }

    // ========================================
    // 12. Lock Department
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock department query")
    void shouldLockDepartmentQuery(IntegrationTestContext context) {
        // When
        List<Department> departments = context.getUpdateExecutor().doInTransaction(() ->
                context.queryDepartments()
                        .where(Department::getActive).eq(true)
                        .getList(0, 10, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(departments).isNotEmpty();
        assertThat(departments).allMatch(Department::getActive);
    }

    // ========================================
    // 13. Null Lock Mode
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with null lock mode")
    void shouldQueryWithNullLockMode(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .where(Employee::getId).eq(1L)
                        .getList(0, 10, null)
        );

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    // ========================================
    // 14. Limit with Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with lock mode")
    void shouldLimitWithLockMode(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .orderBy(Employee::getId).asc()
                        .limit(5, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(5);
    }

    // ========================================
    // 15. GetList with Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get list with lock mode")
    void shouldGetListWithLockMode(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.getUpdateExecutor().doInTransaction(() ->
                context.queryEmployees()
                        .getList(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(employees).hasSize(12);
    }
}