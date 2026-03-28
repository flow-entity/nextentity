package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fetch operations integration tests.
 * <p>
 * Tests entity association loading with fetch operations including:
 * - fetch single association (JPA only)
 * - fetch with where conditions
 * - fetch with pagination
 * <p>
 * Note: Fetch operations are primarily for JPA implementations.
 * JDBC implementations do not support lazy loading of associations,
 * so department may be null in JDBC test results.
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Fetch Operations Integration Tests")
public class FetchOperationsIntegrationTest {

    // ========================================
    // 1. Basic Fetch Operations
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch single association")
    void shouldFetchSingleAssociation(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(1);
        Employee employee = employees.get(0);
        assertThat(employee.getName()).isNotNull();
        // Note: department may be null for JDBC implementation
        // JPA implementation should have department loaded
        if (employee.getDepartment() != null) {
            assertThat(employee.getDepartment().getName()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch association with multiple results")
    void shouldFetchAssociationWithMultipleResults(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(5);
        for (Employee emp : employees) {
            assertThat(emp.getName()).isNotNull();
            // JPA: department should be loaded; JDBC: department is null
            if (emp.getDepartment() != null) {
                assertThat(emp.getDepartment().getId()).isEqualTo(1L);
            }
        }
    }

    // ========================================
    // 2. Fetch with Collection Parameter
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with collection of paths")
    void shouldFetchWithCollectionOfPaths(IntegrationTestContext context) {
        // Given
        List<Path<Employee, ?>> paths = new ArrayList<>();
        paths.add(Employee::getDepartment);

        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(paths)
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getName()).isNotNull();
    }

    // ========================================
    // 3. Fetch with Query Conditions
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with where condition")
    void shouldFetchWithWhereCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getActive()).isTrue();
            assertThat(emp.getName()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with pagination")
    void shouldFetchWithPagination(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .getList(0, 3);

        // Then
        assertThat(employees).hasSize(3);
        for (Employee emp : employees) {
            assertThat(emp.getName()).isNotNull();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with limit")
    void shouldFetchWithLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .limit(5);

        // Then
        assertThat(employees).hasSize(5);
        for (Employee emp : employees) {
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 4. Fetch with Order By
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with order by")
    void shouldFetchWithOrderBy(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertThat(employees).hasSize(12);
        // Verify ordering
        for (int i = 0; i < employees.size() - 1; i++) {
            assertThat(employees.get(i).getSalary())
                    .isGreaterThanOrEqualTo(employees.get(i + 1).getSalary());
        }
        // Verify basic data
        for (Employee emp : employees) {
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 5. Query without Fetch
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query without fetch - lazy loading")
    void shouldQueryWithoutFetch(IntegrationTestContext context) {
        // When - query without fetch
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList();

        // Then
        assertThat(employees).hasSize(1);
        Employee employee = employees.get(0);
        assertThat(employee.getName()).isNotNull();
    }

    // ========================================
    // 6. Fetch with Count
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count with fetch")
    void shouldCountWithFetch(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .count();

        // Then
        assertThat(count).isEqualTo(12);
    }

    // ========================================
    // 7. Fetch with Exist
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with fetch")
    void shouldCheckExistenceWithFetch(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getId).eq(1L)
                .exist();

        // Then
        assertThat(exists).isTrue();
    }

    // ========================================
    // 8. Fetch with GetSingle
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with fetch")
    void shouldGetSingleWithFetch(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getId).eq(1L)
                .getSingle();

        // Then
        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(1L);
        assertThat(employee.getName()).isNotNull();
    }

    // ========================================
    // 9. Fetch with First
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with fetch")
    void shouldGetFirstWithFetch(IntegrationTestContext context) {
        // When
        var employeeOpt = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(employeeOpt).isPresent();
        Employee employee = employeeOpt.get();
        assertThat(employee.getId()).isEqualTo(1L);
        assertThat(employee.getName()).isNotNull();
    }

    // ========================================
    // 10. Fetch Department Query
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query departments")
    void shouldQueryDepartments(IntegrationTestContext context) {
        // When
        List<Department> departments = context.queryDepartments()
                .where(Department::getActive).eq(true)
                .getList();

        // Then
        assertThat(departments).isNotEmpty();
        assertThat(departments).allMatch(Department::getActive);
    }

    // ========================================
    // 11. Fetch with Department Filter
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch employees by department")
    void shouldFetchEmployeesByDepartment(IntegrationTestContext context) {
        // Given - Get department ID
        Long deptId = 1L;

        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(deptId)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getDepartmentId()).isEqualTo(deptId);
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 12. Fetch with Salary Range
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch employees with salary range")
    void shouldFetchEmployeesWithSalaryRange(IntegrationTestContext context) {
        // Given
        double minSalary = 60000.0;
        double maxSalary = 80000.0;

        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getSalary).ge(minSalary)
                .where(Employee::getSalary).le(maxSalary)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getSalary()).isBetween(minSalary, maxSalary);
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 13. Fetch with Name Filter
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with name like filter")
    void shouldFetchWithNameLikeFilter(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getName).like("A%")
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getName()).startsWith("A");
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 14. Fetch with In Condition
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with in condition")
    void shouldFetchWithInCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getId).in(1L, 2L, 3L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertThat(employees).hasSize(3);
        for (Employee emp : employees) {
            assertThat(emp.getId()).isIn(1L, 2L, 3L);
            assertThat(emp.getName()).isNotNull();
        }
    }

    // ========================================
    // 15. Fetch with Not In Condition
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with not in condition")
    void shouldFetchWithNotInCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).notIn(1L, 2L)
                .getList();

        // Then
        assertThat(employees).isNotEmpty();
        for (Employee emp : employees) {
            assertThat(emp.getDepartmentId()).isNotIn(1L, 2L);
            assertThat(emp.getName()).isNotNull();
        }
    }
}