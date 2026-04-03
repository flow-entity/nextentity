package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/// 集成测试的测试数据工厂。
/// 为 Employee 和 Department 实体创建一致的测试数据。
public class TestDataFactory {

    // ID 起始范围，避免冲突
    private static final long DEPT_ID_START = 100L;
    private static final long EMP_ID_START = 1000L;

    /// 创建测试部门。
    public static List<Department> createTestDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(createDepartment(DEPT_ID_START + 1, "Engineering", "Building A", 500000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 2, "Marketing", "Building B", 300000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 3, "Sales", "Building C", 400000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 4, "HR", "Building D", 200000.0, true));
        departments.add(createDepartment(DEPT_ID_START + 5, "Finance", "Building E", 250000.0, false));
        return departments;
    }

    /// 创建分布在不同部门的测试员工。
    public static List<Employee> createTestEmployees() {
        List<Employee> employees = new ArrayList<>();
        long id = EMP_ID_START;

        // 工程部门（5名员工）
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

        // 市场部门（3名员工）
        employees.add(createEmployee(id++, "Frank Miller", "frank@example.com",
                new BigDecimal("60000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2021, 4, 1)));
        employees.add(createEmployee(id++, "Grace Lee", "grace@example.com",
                new BigDecimal("55000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2022, 7, 15)));
        employees.add(createEmployee(id++, "Henry Wilson", "henry@example.com",
                new BigDecimal("58000.00"), true, EmployeeStatus.INACTIVE, DEPT_ID_START + 2,
                LocalDate.of(2020, 11, 20)));

        // 销售部门（2名员工）
        employees.add(createEmployee(id++, "Iris Taylor", "iris@example.com",
                new BigDecimal("50000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 3,
                LocalDate.of(2023, 1, 10)));
        employees.add(createEmployee(id++, "Jack Davis", "jack@example.com",
                new BigDecimal("52000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 3,
                LocalDate.of(2022, 5, 25)));

        // 人力资源部门（1名员工）
        employees.add(createEmployee(id++, "Karen White", "karen@example.com",
                new BigDecimal("48000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 4,
                LocalDate.of(2021, 8, 8)));

        // 财务部门（1名员工）
        employees.add(createEmployee(id++, "Larry Martin", "larry@example.com",
                new BigDecimal("62000.00"), true, EmployeeStatus.ACTIVE, DEPT_ID_START + 5,
                LocalDate.of(2020, 3, 12)));

        return employees;
    }

    /// 创建单个部门。
    public static Department createDepartment(Long id, String name, String location, Double budget, Boolean active) {
        return new Department(id, name, location, budget, active);
    }

    /// 创建包含所有字段的单个员工。
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

    /// 创建使用默认值的简单员工。
    public static Employee createSimpleEmployee(Long id, String name, String email, BigDecimal salary, Long departmentId) {
        return createEmployee(id, name, email, salary, true, EmployeeStatus.ACTIVE, departmentId, LocalDate.now());
    }

    /// 创建用于插入测试的员工。
    public static Employee createEmployeeForInsert(Long id, Long departmentId) {
        return createSimpleEmployee(id, "Test Employee " + id, "test" + id + "@example.com",
                new BigDecimal("50000.00"), departmentId);
    }

    // 获取测试常量的方法
    public static long getDeptIdStart() {
        return DEPT_ID_START;
    }

    public static long getEmpIdStart() {
        return EMP_ID_START;
    }
}