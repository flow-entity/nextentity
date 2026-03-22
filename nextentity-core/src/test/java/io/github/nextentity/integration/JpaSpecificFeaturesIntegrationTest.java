package io.github.nextentity.integration;

import io.github.nextentity.api.model.LockModeType;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA-specific features integration tests.
 * <p>
 * Tests JPA-specific functionality including:
 * - JPA Criteria API integration
 * - EntityManager interaction
 * - Lock modes
 * - JPA caching behavior
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 * Note: Some tests are only applicable for JPA implementation.
 *
 * @author HuangChengwei
 */
@DisplayName("JPA-Specific Features Integration Tests")
public class JpaSpecificFeaturesIntegrationTest {

    /**
     * Tests query with PESSIMISTIC_READ lock mode.
     * This test is primarily for JPA implementation.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic read lock")
    void shouldQueryWithPessimisticReadLock(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList(0, 1, LockModeType.PESSIMISTIC_READ)
                .get(0);

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    /**
     * Tests query with PESSIMISTIC_WRITE lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic write lock")
    void shouldQueryWithPessimisticWriteLock(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList(0, 1, LockModeType.PESSIMISTIC_WRITE)
                .get(0);

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    /**
     * Tests query with OPTIMISTIC lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic lock")
    void shouldQueryWithOptimisticLock(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList(0, 1, LockModeType.OPTIMISTIC)
                .get(0);

        // Then
        assertThat(employee).isNotNull();
    }

    /**
     * Tests query with OPTIMISTIC_FORCE_INCREMENT lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic force increment lock")
    void shouldQueryWithOptimisticForceIncrementLock(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList(0, 1, LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .get(0);

        // Then
        assertThat(employee).isNotNull();
    }

    /**
     * Tests query with null lock mode (no lock).
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query without lock mode")
    void shouldQueryWithoutLockMode(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList(0, 1, null)
                .get(0);

        // Then
        assertThat(employee).isNotNull();
    }

    /**
     * Tests first with lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with lock mode")
    void shouldGetFirstWithLockMode(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first(0, LockModeType.PESSIMISTIC_READ)
                .orElse(null);

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    /**
     * Tests single with lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with lock mode")
    void shouldGetSingleWithLockMode(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single(0, LockModeType.PESSIMISTIC_READ)
                .orElse(null);

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    /**
     * Tests update after pessimistic lock.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update after pessimistic lock")
    void shouldUpdateAfterPessimisticLock(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(5001L, "Lock Update Test");
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When - Lock and update
        Employee locked = config.queryEmployees()
                .where(Employee::getId).eq(5001L)
                .getList(0, 1, LockModeType.PESSIMISTIC_WRITE)
                .get(0);

        locked.setName("Updated After Lock");
        config.getUpdateExecutor().update(locked, Employee.class);

        // Then
        Employee updated = config.queryEmployees()
                .where(Employee::getId).eq(5001L)
                .getSingle();
        assertThat(updated.getName()).isEqualTo("Updated After Lock");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests list with lock mode.
     * Note: Requires active transaction.
     */
    @Disabled("Requires active transaction context")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get list with lock mode")
    void shouldGetListWithLockMode(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList(0, 3, LockModeType.PESSIMISTIC_READ);

        // Then
        assertThat(employees).hasSize(3);
    }

    /**
     * Tests JPA implementation type detection.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should detect implementation type")
    void shouldDetectImplementationType(DbConfig config) {
        // When/Then - Just verify the config has an impl type
        assertThat(config.toString()).isNotEmpty();
        System.out.println("Implementation: " + config);
    }

    /**
     * Tests query execution for JPA.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should execute JPA query")
    void shouldExecuteJpaQuery(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
    }

    /**
     * Tests JPA fetch behavior.
     * Note: This test documents fetch behavior but doesn't assert on laziness.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA fetch behavior")
    void shouldHandleJpaFetchBehavior(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(1);
        // Note: Department association may or may not be loaded depending on JPA settings
    }

    /**
     * Tests JPA entity state management.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should manage JPA entity state")
    void shouldManageJpaEntityState(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(5002L, "State Test");
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When - Query and modify
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(5002L)
                .getSingle();
        found.setName("Modified State");
        config.getUpdateExecutor().update(found, Employee.class);

        // Then
        Employee verified = config.queryEmployees()
                .where(Employee::getId).eq(5002L)
                .getSingle();
        assertThat(verified.getName()).isEqualTo("Modified State");

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests JPA transaction scope.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA transaction scope")
    void shouldHandleJpaTransactionScope(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(5003L, "Transaction Scope Test");

        // When - Insert in one transaction
        config.getUpdateExecutor().insert(employee, Employee.class);

        // Then - Should be visible in new transaction
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(5003L)
                .getSingle();
        assertThat(found).isNotNull();

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests JPA query caching behavior.
     * Note: This test documents caching behavior.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA query caching")
    void shouldHandleJpaQueryCaching(DbConfig config) {
        // When - Execute same query twice
        List<Employee> first = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        List<Employee> second = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then - Results should be consistent
        assertThat(first).hasSameSizeAs(second);
    }

    /**
     * Tests JPA null handling.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA null values")
    void shouldHandleJpaNullValues(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(5004L, "Null Test");
        employee.setEmail(null);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(5004L)
                .getSingle();

        // Then
        assertThat(found.getEmail()).isNull();

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests JPA enum handling.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA enum values")
    void shouldHandleJpaEnumValues(DbConfig config) {
        // Given
        Employee employee = createTestEmployee(5005L, "Enum Test");
        employee.setStatus(EmployeeStatus.INACTIVE);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        Employee found = config.queryEmployees()
                .where(Employee::getStatus).eq(EmployeeStatus.INACTIVE)
                .where(Employee::getId).eq(5005L)
                .getSingle();

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
    }

    /**
     * Tests JPA date handling.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle JPA date values")
    void shouldHandleJpaDateValues(DbConfig config) {
        // Given
        LocalDate hireDate = LocalDate.of(2024, 6, 15);
        Employee employee = createTestEmployee(5006L, "Date Test");
        employee.setHireDate(hireDate);
        config.getUpdateExecutor().insert(employee, Employee.class);

        // When
        Employee found = config.queryEmployees()
                .where(Employee::getId).eq(5006L)
                .getSingle();

        // Then
        assertThat(found.getHireDate()).isEqualTo(hireDate);

        // Cleanup
        config.getUpdateExecutor().delete(employee, Employee.class);
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