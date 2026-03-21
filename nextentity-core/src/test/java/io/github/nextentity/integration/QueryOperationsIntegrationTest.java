package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Query operations integration tests.
 * <p>
 * Tests basic query operations including:
 * - Select all records
 * - Select with conditions (WHERE clause)
 * - Ordering (ORDER BY)
 * - Pagination (LIMIT/OFFSET)
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Query Operations Integration Tests")
public class QueryOperationsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(QueryOperationsIntegrationTest.class);

    /**
     * Tests selecting all records from a table.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select all employees")
    void shouldSelectAllEmployees(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees().getList();

        // Then
        assertNotNull(employees);
        assertEquals(12, employees.size(), "Should have 12 employees");
    }

    /**
     * Tests selecting all records from a table with Department entity.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select all departments")
    void shouldSelectAllDepartments(DbConfig config) {
        // When
        List<Department> departments = config.queryDepartments().getList();

        // Then
        assertNotNull(departments);
        assertEquals(5, departments.size(), "Should have 5 departments");
    }

    /**
     * Tests selecting a single record by ID.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find employee by ID")
    void shouldFindEmployeeById(DbConfig config) {
        // When
        Employee employee = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .getList().get(0);

        // Then
        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("Alice Johnson", employee.getName());
    }

    /**
     * Tests selecting with equality condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by name with eq")
    void shouldFilterEmployeesByName(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getName).eq("Alice Johnson")
                .getList();

        // Then
        assertEquals(1, employees.size());
        assertEquals("Alice Johnson", employees.get(0).getName());
    }

    /**
     * Tests selecting with inequality condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with ne condition")
    void shouldFilterEmployeesWithNeCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).ne(1L)
                .getList();

        // Then
        assertEquals(11, employees.size());
        assertTrue(employees.stream().noneMatch(e -> e.getId() == 1L));
    }

    /**
     * Tests selecting with greater than condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with gt condition")
    void shouldFilterEmployeesWithGtCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).gt(10L)
                .getList();

        // Then
        assertEquals(2, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getId() > 10L));
    }

    /**
     * Tests selecting with less than condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with lt condition")
    void shouldFilterEmployeesWithLtCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).lt(4L)
                .getList();

        // Then
        assertEquals(3, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getId() < 4L));
    }

    /**
     * Tests selecting with IN condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with in condition")
    void shouldFilterEmployeesWithInCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).in(1L, 3L, 5L)
                .getList();

        // Then
        assertEquals(3, employees.size());
        assertTrue(employees.stream().allMatch(e ->
                e.getId() == 1L || e.getId() == 3L || e.getId() == 5L));
    }

    /**
     * Tests selecting with NOT IN condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with notIn condition")
    void shouldFilterEmployeesWithNotInCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getId).notIn(1L, 3L, 5L)
                .getList();

        // Then
        assertEquals(9, employees.size());
        assertTrue(employees.stream().allMatch(e ->
                e.getId() != 1L && e.getId() != 3L && e.getId() != 5L));
    }

    /**
     * Tests selecting with IS NULL condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with isNull condition")
    void shouldFilterWithIsNullCondition(DbConfig config) {
        // First, update an employee to have null email
        Employee employee = config.queryEmployees().where(Employee::getId).eq(1L).getList().get(0);
        String email = employee.getEmail();
        employee.setEmail(null);
        config.getUpdateExecutor().update(employee, Employee.class);

        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getEmail).isNull()
                .getList();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().anyMatch(e -> e.getId() == 1L));
        employee.setEmail(email);
        config.getUpdateExecutor().update(employee, Employee.class);
    }

    /**
     * Tests selecting with IS NOT NULL condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with isNotNull condition")
    void shouldFilterWithIsNotNullCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getEmail).isNotNull()
                .getList();

        // Then
        assertTrue(employees.size() > 0);
        assertTrue(employees.stream().allMatch(e -> e.getEmail() != null));
    }

    /**
     * Tests selecting with boolean condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter active employees")
    void shouldFilterActiveEmployees(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .getList();

        // Then
        assertTrue(employees.size() > 0);
        assertTrue(employees.stream().allMatch(Employee::getActive));
    }

    /**
     * Tests ordering by single field ascending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by id ascending")
    void shouldOrderEmployeesByIdAsc(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertEquals(12, employees.size());
        for (int i = 0; i < employees.size() - 1; i++) {
            assertTrue(employees.get(i).getId() <= employees.get(i + 1).getId());
        }
    }

    /**
     * Tests ordering by single field descending.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by id descending")
    void shouldOrderEmployeesByIdDesc(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).desc()
                .getList();

        // Then
        assertEquals(12, employees.size());
        for (int i = 0; i < employees.size() - 1; i++) {
            assertTrue(employees.get(i).getId() >= employees.get(i + 1).getId());
        }
    }

    /**
     * Tests ordering by multiple fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by department and name")
    void shouldOrderEmployeesByMultipleFields(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getName).asc()
                .getList();

        // Then
        assertEquals(12, employees.size());
        // Verify ordering by department
        Long prevDeptId = null;
        for (Employee emp : employees) {
            if (prevDeptId != null) {
                assertTrue(emp.getDepartmentId() >= prevDeptId);
            }
            prevDeptId = emp.getDepartmentId();
        }
    }

    /**
     * Tests pagination with limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit results")
    void shouldLimitResults(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(0, 5);

        // Then
        assertEquals(5, employees.size());
        assertEquals(1L, employees.get(0).getId());
        assertEquals(5L, employees.get(4).getId());
    }

    /**
     * Tests pagination with offset and limit.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should paginate with offset and limit")
    void shouldPaginateWithOffsetAndLimit(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getList(5, 3);

        // Then
        assertEquals(3, employees.size());
        assertEquals(6L, employees.get(0).getId());
        assertEquals(8L, employees.get(2).getId());
    }

    /**
     * Tests counting records.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees")
    void shouldCountEmployees(DbConfig config) {
        // When
        long count = config.queryEmployees().count();

        // Then
        assertEquals(12, count);
    }

    /**
     * Tests checking existence.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check if employees exist")
    void shouldCheckExistence(DbConfig config) {
        // When
        boolean exists = config.queryEmployees().exist();

        // Then
        assertTrue(exists);
    }

    /**
     * Tests checking existence with condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with condition")
    void shouldCheckExistenceWithCondition(DbConfig config) {
        // When
        boolean exists = config.queryEmployees()
                .where(Employee::getId).eq(1L)
                .exist();

        // Then
        assertTrue(exists);

        boolean notExists = config.queryEmployees()
                .where(Employee::getId).eq(999L)
                .exist();

        assertFalse(notExists);
    }

    /**
     * Tests selecting first record.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first employee")
    void shouldGetFirstEmployee(DbConfig config) {
        // When
        var firstOpt = config.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertTrue(firstOpt.isPresent());
        assertEquals(1L, firstOpt.get().getId());
    }

    /**
     * Tests selecting with AND condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with AND condition")
    void shouldFilterWithAndCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .getList();

        // Then
        assertTrue(employees.size() > 0);
        assertTrue(employees.stream().allMatch(e -> e.getActive() && e.getDepartmentId() == 1L));
    }

    /**
     * Tests selecting with LIKE condition.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE condition")
    void shouldFilterWithLikeCondition(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getName).like("A%")
                .getList();

        // Then
        assertTrue(employees.size() > 0);
        assertTrue(employees.stream().allMatch(e -> e.getName().startsWith("A")));
    }

    /**
     * Tests selecting projected fields.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select projected fields")
    void shouldSelectProjectedFields(DbConfig config) {
        // When
        List<Tuple2<String, String>> tuples = config.queryEmployees()
                .select(Employee::getName, Employee::getEmail)
                .where(Employee::getId).eq(1L)
                .getList();
        Employee employee = config.queryEmployees().where(Employee::getId).eq(1L).getSingle();
        System.out.println(employee);
        // Then
        assertEquals(1, tuples.size());
        Tuple2<String, String> tuple = tuples.get(0);
        assertEquals("Alice Johnson", tuple.get0());
        assertEquals("alice@example.com", tuple.get1());
    }

    /**
     * Tests selecting departments by active status.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter departments by active status")
    void shouldFilterDepartmentsByActiveStatus(DbConfig config) {
        // When
        List<Department> departments = config.queryDepartments()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getId).asc()
                .getList();

        // Then
        assertEquals(4, departments.size());
        assertTrue(departments.stream().allMatch(Department::getActive));
    }

    /**
     * Tests selecting employees by department.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by department")
    void shouldFilterEmployeesByDepartment(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .getList();

        // Then
        assertEquals(5, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getDepartmentId() == 1L));
    }

    /**
     * Tests selecting employees by salary range.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by salary range")
    void shouldFilterEmployeesBySalaryRange(DbConfig config) {
        // When
        List<Employee> employees = config.queryEmployees()
                .where(Employee::getSalary).ge(60000.0)
                .where(Employee::getSalary).le(80000.0)
                .orderBy(Employee::getSalary).desc()
                .getList();

        // Then
        assertTrue(employees.size() > 0);
        for (Employee emp : employees) {
            assertTrue(emp.getSalary() >= 60000.0);
            assertTrue(emp.getSalary() <= 80000.0);
        }
    }
}
