package io.github.nextentity.integration.config.fixtures;

import io.github.nextentity.integration.entity.Department;
import io.github.nextentity.integration.entity.Employee;
import io.github.nextentity.integration.entity.EmployeeStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Test data factory for integration tests.
 */
public class TestDataFactory {

    public static List<Department> createDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department(1L, "Engineering", "Building A", 500000.0, true));
        departments.add(new Department(2L, "Marketing", "Building B", 300000.0, true));
        departments.add(new Department(3L, "Sales", "Building C", 400000.0, true));
        departments.add(new Department(4L, "HR", "Building A", 200000.0, true));
        departments.add(new Department(5L, "Finance", "Building D", 250000.0, false));
        return departments;
    }

    public static List<Employee> createEmployees() {
        List<Employee> employees = new ArrayList<>();

        // Engineering department employees (deptId = 1)
        employees.add(new Employee(1L, "Alice Johnson", "alice@example.com", 75000.0, true,
                EmployeeStatus.ACTIVE, 1L, LocalDate.of(2020, 1, 15)));
        employees.add(new Employee(2L, "Bob Smith", "bob@example.com", 80000.0, true,
                EmployeeStatus.ACTIVE, 1L, LocalDate.of(2019, 3, 20)));
        employees.add(new Employee(3L, "Charlie Brown", "charlie@example.com", 70000.0, true,
                EmployeeStatus.ON_LEAVE, 1L, LocalDate.of(2021, 6, 10)));
        employees.add(new Employee(4L, "Diana Prince", "diana@example.com", 85000.0, true,
                EmployeeStatus.ACTIVE, 1L, LocalDate.of(2018, 9, 5)));
        employees.add(new Employee(5L, "Eve Adams", "eve@example.com", 65000.0, false,
                EmployeeStatus.TERMINATED, 1L, LocalDate.of(2022, 2, 28)));

        // Marketing department employees (deptId = 2)
        employees.add(new Employee(6L, "Frank Miller", "frank@example.com", 60000.0, true,
                EmployeeStatus.ACTIVE, 2L, LocalDate.of(2021, 4, 1)));
        employees.add(new Employee(7L, "Grace Lee", "grace@example.com", 55000.0, true,
                EmployeeStatus.ACTIVE, 2L, LocalDate.of(2022, 7, 15)));
        employees.add(new Employee(8L, "Henry Wilson", "henry@example.com", 58000.0, true,
                EmployeeStatus.INACTIVE, 2L, LocalDate.of(2020, 11, 20)));

        // Sales department employees (deptId = 3)
        employees.add(new Employee(9L, "Iris Taylor", "iris@example.com", 50000.0, true,
                EmployeeStatus.ACTIVE, 3L, LocalDate.of(2023, 1, 10)));
        employees.add(new Employee(10L, "Jack Davis", "jack@example.com", 52000.0, true,
                EmployeeStatus.ACTIVE, 3L, LocalDate.of(2022, 5, 25)));

        // HR department employees (deptId = 4)
        employees.add(new Employee(11L, "Karen White", "karen@example.com", 48000.0, true,
                EmployeeStatus.ACTIVE, 4L, LocalDate.of(2021, 8, 8)));

        // Finance department employees (deptId = 5)
        employees.add(new Employee(12L, "Larry Martin", "larry@example.com", 62000.0, true,
                EmployeeStatus.ACTIVE, 5L, LocalDate.of(2020, 3, 12)));

        return employees;
    }

    public static Department createDepartment(Long id, String name, String location, Double budget, Boolean active) {
        return new Department(id, name, location, budget, active);
    }

    public static Employee createEmployee(Long id, String name, String email, Double salary,
                                          Boolean active, EmployeeStatus status, Long departmentId, LocalDate hireDate) {
        return new Employee(id, name, email, salary, active, status, departmentId, hireDate);
    }

    public static Employee createEmployee(Long id, String name, String email, Double salary) {
        return createEmployee(id, name, email, salary, true, EmployeeStatus.ACTIVE, 1L, LocalDate.now());
    }
}
