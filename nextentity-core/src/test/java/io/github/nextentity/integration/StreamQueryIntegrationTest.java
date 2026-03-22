package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Collector API integration tests.
 * <p>
 * Tests collector functionality including:
 * - getList() operations
 * - first() and single() operations
 * - map() transformations
 * - count() and exist() operations
 * - pagination with slice()
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Collector API Integration Tests")
public class StreamQueryIntegrationTest {

    // ========================================
    // 1. Basic List Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get all employees")
    void shouldGetAllEmployees(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees().getList();

        // Then
        assertThat(employees).hasSize(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get employees with limit")
    void shouldGetEmployeesWithLimit(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees().limit(5);

        // Then
        assertThat(employees).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get employees with offset")
    void shouldGetEmployeesWithOffset(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .offset(5);

        // Then
        assertThat(employees).hasSize(7);
        assertThat(employees.get(0).getId()).isEqualTo(6L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get employees with pagination")
    void shouldGetEmployeesWithPagination(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(2, 3);

        // Then
        assertThat(employees).hasSize(3);
        assertThat(employees.get(0).getId()).isEqualTo(3L);
    }

    // ========================================
    // 2. First and Single Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first employee")
    void shouldGetFirstEmployee(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getFirst();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first employee as optional")
    void shouldGetFirstEmployeeAsOptional(DbConfig config) {
        // When
        Optional<Employee> employee = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(employee).isPresent();
        assertThat(employee.get().getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with offset")
    void shouldGetFirstWithOffset(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getFirst(2);

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(3L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single employee")
    void shouldGetSingleEmployee(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getSingle();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single as optional")
    void shouldGetSingleAsOptional(DbConfig config) {
        // When
        Optional<Employee> employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .single();

        // Then
        assertThat(employee).isPresent();
        assertThat(employee.get().getId()).isEqualTo(1L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty optional for non-existing")
    void shouldReturnEmptyOptionalForNonExisting(DbConfig config) {
        // When
        Optional<Employee> employee = config.queryEmployees()
                .where(Employee::getId).eq(999L)
                .single();

        // Then
        assertThat(employee).isEmpty();
    }

    // ========================================
    // 3. Map Transformation
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map employees to names")
    void shouldMapEmployeesToNames(DbConfig config) {
        // When
        List<String> names = config.queryEmployees()
                .map(Employee::getName)
                .getList();

        // Then
        assertThat(names).hasSize(12);
        assertThat(names).allMatch(name -> name != null && !name.isEmpty());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map employees to salaries")
    void shouldMapEmployeesToSalaries(DbConfig config) {
        // When
        List<Double> salaries = config.queryEmployees()
                .map(Employee::getSalary)
                .getList();

        // Then
        assertThat(salaries).hasSize(12);
        assertThat(salaries).allMatch(s -> s > 0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map with filter")
    void shouldMapWithFilter(DbConfig config) {
        // When
        List<String> names = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .map(Employee::getName)
                .getList();

        // Then
        assertThat(names).isNotEmpty();
    }

    // ========================================
    // 4. Count and Exist Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees")
    void shouldCountEmployees(DbConfig config) {
        // When
        long count = config.queryEmployees().count();

        // Then
        assertThat(count).isEqualTo(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with filter")
    void shouldCountWithFilter(DbConfig config) {
        // When
        long count = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .count();

        // Then
        assertThat(count).isGreaterThan(0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check exist")
    void shouldCheckExist(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check not exist")
    void shouldCheckNotExist(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(999L)
                .exist();

        // Then
        assertThat(exists).isFalse();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exist with offset")
    void shouldExistWithOffset(DbConfig config) {
        // When
        boolean exists1 = config.queryEmployees().exist(0);
        boolean exists2 = config.queryEmployees().exist(100);

        // Then
        assertThat(exists1).isTrue();
        assertThat(exists2).isFalse();
    }

    // ========================================
    // 5. Chained Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain where and map")
    void shouldChainWhereAndMap(DbConfig config) {
        // When
        List<String> names = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .map(Employee::getName)
                .getList();

        // Then
        assertThat(names).hasSize(5);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain order by and limit")
    void shouldChainOrderByAndLimit(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getSalary).desc()
                .limit(3);

        // Then
        assertThat(employees).hasSize(3);
        assertThat(employees.get(0).getSalary())
                .isGreaterThanOrEqualTo(employees.get(1).getSalary());
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain multiple where clauses")
    void shouldChainMultipleWhereClauses(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getSalary).gt(60000.0)
                .orderBy(Employee::getName).asc()
                .limit(5);

        // Then
        assertThat(employees).isNotEmpty();
        assertThat(employees.size()).isLessThanOrEqualTo(5);
    }

    // ========================================
    // 6. Slice Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice employees")
    void shouldSliceEmployees(DbConfig config) {
        // When
        var slice = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(0, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.total()).isEqualTo(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with offset")
    void shouldSliceWithOffset(DbConfig config) {
        // When
        var slice = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(5, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.offset()).isEqualTo(5);
    }

    // ========================================
    // 7. Map to Primitive
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map to id list")
    void shouldMapToIdList(DbConfig config) {
        // When
        List<Long> ids = config.queryEmployees()
                .map(Employee::getId)
                .getList();

        // Then
        assertThat(ids).hasSize(12);
        assertThat(ids).contains(1L, 2L, 3L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map and count")
    void shouldMapAndCount(DbConfig config) {
        // When
        long count = config.queryEmployees()
                .map(Employee::getName)
                .count();

        // Then
        assertThat(count).isEqualTo(12);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map and limit")
    void shouldMapAndLimit(DbConfig config) {
        // When
        List<String> names = config.queryEmployees()
                .map(Employee::getName)
                .limit(3);

        // Then
        assertThat(names).hasSize(3);
    }

    // ========================================
    // 8. Require Single
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should require single")
    void shouldRequireSingle(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .requireSingle();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
    }
}