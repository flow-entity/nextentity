package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/// 集成测试的基类。
/// 提供测试数据的通用设置和清理。
@SpringBootTest
@Import(TestConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected DepartmentRepository departmentRepository;

    protected List<Department> testDepartments;
    protected List<Employee> testEmployees;

    /// 每个测试前设置测试数据。
    @BeforeEach
    void setUpBase() {
        // Ensure repositories are injected
        assert employeeRepository != null : "EmployeeRepository not injected";
        assert departmentRepository != null : "DepartmentRepository not injected";

        // Clear existing data
        clearAllData();

        // Create fresh test data
        testDepartments = TestDataFactory.createTestDepartments();
        testEmployees = TestDataFactory.createTestEmployees();

        // Insert test data
        departmentRepository.insertAll(testDepartments);
        employeeRepository.insertAll(testEmployees);
    }

    /// 每个测试后清理。
    @AfterEach
    void tearDownBase() {
        clearAllData();
    }

    /// 清除数据库中的所有测试数据。
    protected void clearAllData() {
        // Delete all employees first (foreign key constraint)
        List<Employee> employees = employeeRepository.findAllEmployees();
        if (!employees.isEmpty()) {
            employeeRepository.deleteAll(employees);
        }

        // Delete all departments
        List<Department> departments = departmentRepository.query().list();
        if (!departments.isEmpty()) {
            departmentRepository.deleteAll(departments);
        }
    }

    /// 创建用于测试的员工。
    protected Employee createTestEmployee(Long id, String name, String email, BigDecimal salary, Long departmentId) {
        return TestDataFactory.createSimpleEmployee(id, name, email, salary, departmentId);
    }

    /// 创建用于测试的部门。
    protected Department createTestDepartment(Long id, String name, String location, Double budget) {
        return TestDataFactory.createDepartment(id, name, location, budget, true);
    }

    /// 从测试数据中获取第一个部门 ID。
    protected Long getFirstDepartmentId() {
        return testDepartments.isEmpty() ? TestDataFactory.getDeptIdStart() + 1 : testDepartments.get(0).getId();
    }

    /// 从测试数据中获取第一个员工 ID。
    protected Long getFirstEmployeeId() {
        return testEmployees.isEmpty() ? TestDataFactory.getEmpIdStart() : testEmployees.get(0).getId();
    }

    /// 计算测试数据中的员工数量。
    protected int getTestEmployeeCount() {
        return testEmployees.size();
    }

    /// 计算测试数据中的部门数量。
    protected int getTestDepartmentCount() {
        return testDepartments.size();
    }
}
