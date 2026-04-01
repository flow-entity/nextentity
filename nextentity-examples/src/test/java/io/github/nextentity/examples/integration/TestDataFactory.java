package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data factory for integration tests.
 * Creates consistent test data for Employee and Department entities.
 */
public class TestDataFactory {

    // ID ranges to avoid conflicts
    private static final long DEPT_ID_START = 100L;
    private static final long EMP_ID_START = 1000L;

    /**
     * Create test departments.
     */
    public static List<Department> createTestDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(createDepartment(DEPT_ID_START + 1, "Engineering", "Building A", 500000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 2, "Marketing", "Building B", 300000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 3, "Sales", "Building C", 400000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 4, "HR", "Building D", 200000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 5, "Finance", "Building E", 250000.0, false));
        return departments;
    }

    /**
     * Create test employees distributed across departments.
     */
    public static List<Employee> createTestEmployees() {
        List<Employee> employees = new ArrayList<>();
        long id = EMP_ID_START;

        // Engineering department (5 employees)
        employees.add(createEmployee(id++, "Alice Johnson", "alice@example.com",
                new BigDecimal("75000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 1,
                LocalDate.of(2020, 1, 15)));
        employees.add(createEmployee(id++, "Bob Smith", "bob@example.com",
                new BigDecimal("80000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 1,
                LocalDate.of(2019, 3, 20)));
        employees.add(createEmployee(id++, "Charlie Brown", "charlie@example.com",
                new BigDecimal("70000.00"), true, EmployeeStatus.ON_LEAVE, DEPT_ID_START + 1,
                LocalDate.of(2021, 6, 10)));
        employees.add(createEmployee(id++, "Diana Prince", "diana@example.com",
                new BigDecimal("85000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 1,
                LocalDate.of(2018, 9, 5)));
        employees.add(createEmployee(id++, "Eve Adams", "eve@example.com",
                new BigDecimal("65000.00"), false, EmployeeStatus.TERMINATED, DEPT_ID_START + 1,
                LocalDate.of(2022, 2, 28)));

        // Marketing department (3 employees)
        employees.add(createEmployee(id++, "Frank Miller", "frank@example.com",
                new BigDecimal("60000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2021, 4, 1)));
        employees.add(createEmployee(id++, "Grace Lee", "grace@example.com",
                new BigDecimal("55000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2022, 7, 15)));
        employees.add(createEmployee(id++, "Henry Wilson", "henry@example.com",
                new BigDecimal("58000.00"), true, EmployeeStatus.INACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2020, 11, 20)));

        // Sales department (2 employees)
        employees.add(createEmployee(id++, "Iris Taylor", "iris@example.com",
                new BigDecimal("50000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 3,
                LocalDate.of(2023, 1, 10)));
        employees.add(createEmployee(id++, "Jack Davis", "jack@example.com",
                new BigDecimal("52000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 3,
                LocalDate.of(2022, 5, 25)));

        // HR department (1 employee)
        employees.add(createEmployee(id++, "Karen White", "karen@example.com",
                new BigDecimal("48000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 4,
                LocalDate.of(2021, 8, 8)));

        // Finance department (1 employee)
        employees.add(createEmployee(id++, "Larry Martin", "larry@example.com",
                new BigDecimal("62000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 5,
                LocalDate.of(2020, 3, 12)));

        return employees;
    }

    /**
     * Create a single department.
     */
    public static Department createDepartment(Long id, String name, String location, Double budget, Boolean active) {
        return new Department(id, name, location, budget, active);
    }

    /**
     * Create a single employee with all fields.
     */
    public static Employee createEmployee(Long id, String name, String email, BigDecimal salary,
                                          Boolean active, EmployeeStatus status, Long departmentId, LocalDate hireDate) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(salary);
        employee.setActive(active);
        employee.setStatus(status);
        employee.setDepartmentId(departmentId);
        employee.setHireDate(hireDate);
        return employee;
    }

    /**
     * Create a simple employee with default values.
     */
    public static Employee createSimpleEmployee(Long id, String name, String email, BigDecimal salary, Long departmentId) {
        return createEmployee(id, name, email, salary, true, EmployeeStatus.ACTIVE, departmentId, LocalDate.now());
    }

    /**
     * Create an employee for insertion tests.
     */
    public static Employee createEmployeeForInsert(Long id, Long departmentId) {
        return createSimpleEmployee(id, "Test Employee " + id, "test" + id + "@example.com",
                new BigDecimal("50000.00"), departmentId);
    }

    // Getter methods for test constants
    public static long getDeptIdStart() {
        return DEPT_ID_START;
    }

    public static long getEmpIdStart() {
        return EMP_ID_START;
    }
}