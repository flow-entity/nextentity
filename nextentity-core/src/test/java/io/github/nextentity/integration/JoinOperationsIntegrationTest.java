package io.github.nextentity.integration;

import io.github.nextentity.api.Path;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.fast.FastIntegrationTestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

///
/// JOIN and fetch 操作s integration tests.
/// <p>
/// 测试s association queries including:
/// - Fetch associated entities
/// - Left join queries
/// - Many-to-one associations
/// <p>
/// These tests run against MySQL and PostgreSQL using 测试containers.
///
/// @author HuangChengwei
@DisplayName("JOIN Operations Integration Tests")
public class JoinOperationsIntegrationTest {

    /// 测试s fetching associated department for employees.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fetch employee with department")
    void shouldFetchEmployeeWithDepartment(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertNotNull(employees);
        assertEquals(12, employees.size());

        // Verify departments are loaded
        Employee emp = employees.getFirst();
        assertNotNull(emp.getDepartment());
        assertNotNull(emp.getDepartment().getId());
    }

    ///
    /// 测试s querying employees with department filter.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by department name")
    void shouldFilterEmployeesByDepartmentName(IntegrationTestContext context) {
        // When - query employees in department 1
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertEquals(5, employees.size());
        for (Employee emp : employees) {
            assertEquals(1L, emp.getDepartmentId());
            assertNotNull(emp.getDepartment());
            assertEquals(1L, emp.getDepartment().getId());
        }
    }

    ///
    /// 测试s querying departments and their employees.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query departments")
    void shouldQueryDepartments(IntegrationTestContext context) {
        // When
        List<Department> departments = context.queryDepartments()
                .orderBy(Department::getId).asc()
                .list();

        // Then
        assertEquals(5, departments.size());

        // Verify first department
        Department dept = departments.getFirst();
        assertEquals("Engineering", dept.getName());
        assertEquals("Building A", dept.getLocation());
    }

    ///
    /// 测试s employee count per department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees per department")
    void shouldCountEmployeesPerDepartment(IntegrationTestContext context) {
        // When - get employees for department 1
        List<Employee> dept1Employees = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list();

        // Then
        assertEquals(5, dept1Employees.size());

        // When - get employees for department 2
        List<Employee> dept2Employees = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(2L)
                .list();

        // Then
        assertEquals(3, dept2Employees.size());
    }

    ///
    /// 测试s fetching with multiple associations.
    @ParameterizedTest
    @ArgumentsSource(FastIntegrationTestProvider.class) // TODO use IntegrationTestProvider.class
    @DisplayName("Should fetch with multiple conditions")
    void shouldFetchWithMultipleConditions(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertNotNull(employees);
        assertFalse(employees.isEmpty());

        // Verify all are active and in department 1
        for (Employee emp : employees) {
            assertTrue(emp.getActive());
            assertEquals(1L, emp.getDepartmentId());
        }
    }

    ///
    /// 测试s salary statistics per department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should calculate salary statistics per department")
    void shouldCalculateSalaryStatsPerDepartment(IntegrationTestContext context) {
        // When - get max salary in department 1
        Number maxSalary = context.queryEmployees()
                .select(Path.of(Employee::getSalary).max())
                .where(Employee::getDepartmentId).eq(1L)
                .single();

        // Then
        assertNotNull(maxSalary);

        // Verify max salary in department 1
        double expectedMax = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .list().stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0);
        assertEquals(expectedMax, maxSalary.doubleValue(), 0.01);
    }

    ///
    /// 测试s ordering employees by department and salary.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order by department and salary")
    void shouldOrderByDepartmentAndSalary(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getSalary).desc()
                .list();

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

    ///
    /// 测试s distinct department IDs.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get distinct department IDs")
    void shouldGetDistinctDepartmentIds(IntegrationTestContext context) {
        // When
        List<Long> deptIds = context.queryEmployees()
                .selectDistinct(Employee::getDepartmentId)
                .orderBy(Employee::getDepartmentId).asc()
                .list();

        // Then
        assertNotNull(deptIds);
        assertEquals(5, deptIds.size());
    }

    ///
    /// 测试s employees with specific status in department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter by status and department")
    void shouldFilterByStatusAndDepartment(IntegrationTestContext context) {
        // When
        List<Employee> activeInDept1 = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertFalse(activeInDept1.isEmpty());
        for (Employee emp : activeInDept1) {
            assertTrue(emp.getActive());
            assertEquals(1L, emp.getDepartmentId());
        }
    }

    ///
    /// 测试s department budget query.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query department budget")
    void shouldQueryDepartmentBudget(IntegrationTestContext context) {
        // When
        List<Department> activeDepts = context.queryDepartments()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getBudget).desc()
                .list();

        // Then
        assertFalse(activeDepts.isEmpty());
        for (Department dept : activeDepts) {
            assertTrue(dept.getActive());
            assertNotNull(dept.getBudget());
        }
    }
}

