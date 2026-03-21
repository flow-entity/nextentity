package io.github.nextentity.integration;

import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JOIN and fetch operations integration tests.
 * <p>
 * Tests association queries including:
 * - Fetch associated entities
 * - Left join queries
 * - Many-to-one associations
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("JOIN Operations Integration Tests")
public class JoinOperationsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(JoinOperationsIntegrationTest.class);

    /**
     * Tests fetching associated department for employees.
     */
    @Disabled("BUG - jpa fetched association is null")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch employee with department")
    void shouldFetchEmployeeWithDepartment(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertNotNull(employees);
        assertEquals(12, employees.size());

        // Verify departments are loaded
        Employee emp = employees.get(0);
        // TODO fix bug
        assertNotNull(emp.getDepartment());
        assertNotNull(emp.getDepartment().getId());
    }

    /**
     * Tests querying employees with department filter.
     */
    @Disabled("BUG - jpa fetched association is null")
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by department name")
    void shouldFilterEmployeesByDepartmentName(DbConfig config) {
        // When - query employees in department 1
        List<Employee> employees = config.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertEquals(5, employees.size());
        for (Employee emp : employees) {
            assertEquals(1L, emp.getDepartmentId());
            assertNotNull(emp.getDepartment());
            assertEquals(1L, emp.getDepartment().getId());
        }
    }

    /**
     * Tests querying departments and their employees.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query departments")
    void shouldQueryDepartments(DbConfig config) {
        // When
        List<Department> departments = config.queryDepartments()
                .orderBy(Department::getId).asc()
                .getList();

        // Then
        assertEquals(5, departments.size());

        // Verify first department
        Department dept = departments.get(0);
        assertEquals("Engineering", dept.getName());
        assertEquals("Building A", dept.getLocation());
    }

    /**
     * Tests employee count per department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees per department")
    void shouldCountEmployeesPerDepartment(DbConfig config) {
        // When - get employees for department 1
        List<Employee> dept1Employees = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertEquals(5, dept1Employees.size());

        // When - get employees for department 2
        List<Employee> dept2Employees = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(2L)
                .getList();

        // Then
        assertEquals(3, dept2Employees.size());
    }

    /**
     * Tests fetching with multiple associations.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch with multiple conditions")
    void shouldFetchWithMultipleConditions(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertNotNull(employees);
        assertTrue(employees.size() > 0);

        // Verify all are active and in department 1
        for (Employee emp : employees) {
            assertTrue(emp.getActive());
            assertEquals(1L, emp.getDepartmentId());
        }
    }

    /**
     * Tests salary statistics per department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should calculate salary statistics per department")
    void shouldCalculateSalaryStatsPerDepartment(DbConfig config) {
        // When - get max salary in department 1
        Number maxSalary = config.queryEmployees()
                .select(io.github.nextentity.core.util.Paths.get(Employee::getSalary).max())
                .where(Employee::getDepartmentId).eq(1L)
                .getSingle();

        // Then
        assertNotNull(maxSalary);

        // Verify max salary in department 1
        double expectedMax = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .getList().stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertEquals(expectedMax, maxSalary.doubleValue(), 0.01);
    }

    /**
     * Tests ordering employees by department and salary.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by department and salary")
    void shouldOrderByDepartmentAndSalary(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertEquals(12, employees.size());

        // Verify ordering
        Long prevDeptId = null;
        Double prevSalary = null;
        for (Employee emp : employees) {
            if (prevDeptId != null && prevDeptId.equals(emp.getDepartmentId())) {
                // Same department, salary should be descending
                assertTrue(prevSalary >= emp.getSalary());
            }
            prevDeptId = emp.getDepartmentId();
            prevSalary = emp.getSalary();
        }
    }

    /**
     * Tests distinct department IDs.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get distinct department IDs")
    void shouldGetDistinctDepartmentIds(DbConfig config) {
        // When
        List<Long> deptIds = config.queryEmployees()
                .selectDistinct(io.github.nextentity.core.util.Paths.get(Employee::getDepartmentId))
                .orderBy(Employee::getDepartmentId).asc()
                .getList();

        // Then
        assertNotNull(deptIds);
        assertEquals(5, deptIds.size());
    }

    /**
     * Tests employees with specific status in department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter by status and department")
    void shouldFilterByStatusAndDepartment(DbConfig config) {
        // When
        List<Employee> activeInDept1 = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertTrue(activeInDept1.size() > 0);
        for (Employee emp : activeInDept1) {
            assertTrue(emp.getActive());
            assertEquals(1L, emp.getDepartmentId());
        }
    }

    /**
     * Tests department budget query.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query department budget")
    void shouldQueryDepartmentBudget(DbConfig config) {
        // When
        List<Department> activeDepts = config.queryDepartments()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getBudget).desc()
                .getList();

        // Then
        assertTrue(activeDepts.size() > 0);
        for (Department dept : activeDepts) {
            assertTrue(dept.getActive());
            assertNotNull(dept.getBudget());
        }
    }
}
