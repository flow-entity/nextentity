package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

///
 /// Query 操作s integration tests.
 /// <p>
 /// 测试s basic query 操作s including:
 /// - Select all records
 /// - Select with conditions (WHERE clause)
 /// - Ordering (ORDER BY)
 /// - Pagination (LIMIT/OFFSET)
 /// <p>
 /// These tests run against MySQL and PostgreSQL using 测试containers.
 /// 
 /// @author HuangChengwei
@DisplayName("Query Operations Integration Tests")
public class QueryOperationsIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(QueryOperationsIntegrationTest.class);

///
     /// 测试s selecting all records from a table.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select all employees")
    void shouldSelectAllEmployees(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees().list();

        // Then
        assertNotNull(employees);
        assertEquals(12, employees.size(), "Should have 12 employees");
    }

///
     /// 测试s selecting all records from a table with Department entity.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select all departments")
    void shouldSelectAllDepartments(IntegrationTestContext context) {
        // When
        List<Department> departments = context.queryDepartments().list();

        // Then
        assertNotNull(departments);
        assertEquals(5, departments.size(), "Should have 5 departments");
    }

///
     /// 测试s selecting a single record by ID.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should find employee by ID")
    void shouldFindEmployeeById(IntegrationTestContext context) {
        // When
        Employee employee = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .list().get(0);

        // Then
        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("Alice Johnson", employee.getName());
    }

///
     /// 测试s selecting with equality condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by name with eq")
    void shouldFilterEmployeesByName(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).eq("Alice Johnson")
                .list();

        // Then
        assertEquals(1, employees.size());
        assertEquals("Alice Johnson", employees.get(0).getName());
    }

///
     /// 测试s selecting with inequality condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with ne condition")
    void shouldFilterEmployeesWithNeCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).ne(1L)
                .list();

        // Then
        assertEquals(11, employees.size());
        assertTrue(employees.stream().noneMatch(e -> e.getId() == 1L));
    }

///
     /// 测试s selecting with greater than condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with gt condition")
    void shouldFilterEmployeesWithGtCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).gt(10L)
                .list();

        // Then
        assertEquals(2, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getId() > 10L));
    }

///
     /// 测试s selecting with less than condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with lt condition")
    void shouldFilterEmployeesWithLtCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).lt(4L)
                .list();

        // Then
        assertEquals(3, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getId() < 4L));
    }

///
     /// 测试s selecting with IN condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with in condition")
    void shouldFilterEmployeesWithInCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).in(1L, 3L, 5L)
                .list();

        // Then
        assertEquals(3, employees.size());
        assertTrue(employees.stream().allMatch(e ->
                e.getId() == 1L || e.getId() == 3L || e.getId() == 5L));
    }

///
     /// 测试s selecting with NOT IN condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees with notIn condition")
    void shouldFilterEmployeesWithNotInCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getId).notIn(1L, 3L, 5L)
                .list();

        // Then
        assertEquals(9, employees.size());
        assertTrue(employees.stream().allMatch(e ->
                e.getId() != 1L && e.getId() != 3L && e.getId() != 5L));
    }

///
     /// 测试s selecting with IS NULL condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with isNull condition")
    void shouldFilterWithIsNullCondition(IntegrationTestContext context) {
        // First, update an employee to have null email
        Employee employee = context.queryEmployees().where(Employee::getId).eq(1L).list().get(0);
        String email = employee.getEmail();
        employee.setEmail(null);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));

        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).isNull()
                .list();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().anyMatch(e -> e.getId() == 1L));
        employee.setEmail(email);
        context.getUpdateExecutor().update(employee, context.getEntityContext(Employee.class));
    }

///
     /// 测试s selecting with IS NOT NULL condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with isNotNull condition")
    void shouldFilterWithIsNotNullCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getEmail).isNotNull()
                .list();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().allMatch(e -> e.getEmail() != null));
    }

///
     /// 测试s selecting with boolean condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter active employees")
    void shouldFilterActiveEmployees(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .list();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().allMatch(Employee::getActive));
    }

///
     /// 测试s ordering by single field ascending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by id ascending")
    void shouldOrderEmployeesByIdAsc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertEquals(12, employees.size());
        for (int i = 0; i < employees.size() - 1; i++) {
            assertTrue(employees.get(i).getId() <= employees.get(i + 1).getId());
        }
    }

///
     /// 测试s ordering by single field descending.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by id descending")
    void shouldOrderEmployeesByIdDesc(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).desc()
                .list();

        // Then
        assertEquals(12, employees.size());
        for (int i = 0; i < employees.size() - 1; i++) {
            assertTrue(employees.get(i).getId() >= employees.get(i + 1).getId());
        }
    }

///
     /// 测试s ordering by multiple fields.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should order employees by department and name")
    void shouldOrderEmployeesByMultipleFields(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getDepartmentId).asc()
                .orderBy(Employee::getName).asc()
                .list();

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

///
     /// 测试s pagination with limit.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit results")
    void shouldLimitResults(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(5);

        // Then
        assertEquals(5, employees.size());
        assertEquals(1L, employees.get(0).getId());
        assertEquals(5L, employees.get(4).getId());
    }

///
     /// 测试s pagination with offset and limit.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should paginate with offset and limit")
    void shouldPaginateWithOffsetAndLimit(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .list(5, 3);

        // Then
        assertEquals(3, employees.size());
        assertEquals(6L, employees.get(0).getId());
        assertEquals(8L, employees.get(2).getId());
    }

///
     /// 测试s counting records.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should count employees")
    void shouldCountEmployees(IntegrationTestContext context) {
        // When
        long count = context.queryEmployees().count();

        // Then
        assertEquals(12, count);
    }

///
     /// 测试s checking existence.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check if employees exist")
    void shouldCheckExistence(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees().exists();

        // Then
        assertTrue(exists);
    }

///
     /// 测试s checking existence with condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should check existence with condition")
    void shouldCheckExistenceWithCondition(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .where(Employee::getId).eq(1L)
                .exists();

        // Then
        assertTrue(exists);

        boolean notExists = context.queryEmployees()
                .where(Employee::getId).eq(999L)
                .exists();

        assertFalse(notExists);
    }

///
     /// 测试s selecting first record.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first employee")
    void shouldGetFirstEmployee(IntegrationTestContext context) {
        // When
        var first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertNotNull(first);
        assertEquals(1L, first.getId());
    }

///
     /// 测试s selecting with AND condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with AND condition")
    void shouldFilterWithAndCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getActive).eq(true)
                .where(Employee::getDepartmentId).eq(1L)
                .list();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().allMatch(e -> e.getActive() && e.getDepartmentId() == 1L));
    }

///
     /// 测试s selecting with LIKE condition.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter with LIKE condition")
    void shouldFilterWithLikeCondition(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getName).like("A%")
                .list();

        // Then
        assertFalse(employees.isEmpty());
        assertTrue(employees.stream().allMatch(e -> e.getName().startsWith("A")));
    }

///
     /// 测试s selecting projected fields.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should select projected fields")
    void shouldSelectProjectedFields(IntegrationTestContext context) {
        // When
        List<Tuple2<String, String>> tuples = context.queryEmployees()
                .select(Employee::getName, Employee::getEmail)
                .where(Employee::getId).eq(1L)
                .list();
        Employee employee = context.queryEmployees().where(Employee::getId).eq(1L).single();
        System.out.println(employee);
        // Then
        assertEquals(1, tuples.size());
        Tuple2<String, String> tuple = tuples.get(0);
        assertEquals("Alice Johnson", tuple.get0());
        assertEquals("alice@example.com", tuple.get1());
    }

///
     /// 测试s selecting departments by active status.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter departments by active status")
    void shouldFilterDepartmentsByActiveStatus(IntegrationTestContext context) {
        // When
        List<Department> departments = context.queryDepartments()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getId).asc()
                .list();

        // Then
        assertEquals(4, departments.size());
        assertTrue(departments.stream().allMatch(Department::getActive));
    }

///
     /// 测试s selecting employees by department.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by department")
    void shouldFilterEmployeesByDepartment(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getDepartmentId).eq(1L)
                .orderBy(Employee::getId).asc()
                .list();

        // Then
        assertEquals(5, employees.size());
        assertTrue(employees.stream().allMatch(e -> e.getDepartmentId() == 1L));
    }

///
     /// 测试s selecting employees by salary range.
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter employees by salary range")
    void shouldFilterEmployeesBySalaryRange(IntegrationTestContext context) {
        // When
        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).ge(60000.0)
                .where(Employee::getSalary).le(80000.0)
                .orderBy(Employee::getSalary).desc()
                .list();

        // Then
        assertFalse(employees.isEmpty());
        for (Employee emp : employees) {
            assertTrue(emp.getSalary() >= 60000.0);
            assertTrue(emp.getSalary() <= 80000.0);
        }
    }

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }
}


