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

/**
 * Base class for integration tests.
 * Provides common setup and cleanup for test data.
 */
@SpringBootTest
@Import(TestConfig.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected DepartmentRepository departmentRepository;

    protected List<Department> testDepartments;
    protected List<Employee> testEmployees;

    /**
     * Setup test data before each test.
     */
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

    /**
     * Cleanup after each test.
     */
    @AfterEach
    void tearDownBase() {
        clearAllData();
    }

    /**
     * Clear all test data from database.
     */
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

    /**
     * Create an employee for testing.
     */
    protected Employee createTestEmployee(Long id, String name, String email, BigDecimal salary, Long departmentId) {
        return TestDataFactory.createSimpleEmployee(id, name, email, salary, departmentId);
    }

    /**
     * Create a department for testing.
     */
    protected Department createTestDepartment(Long id, String name, String location, Double budget) {
        return TestDataFactory.createDepartment(id, name, location, budget, true);
    }

    /**
     * Get the first department ID from test data.
     */
    protected Long getFirstDepartmentId() {
        return testDepartments.isEmpty() ? TestDataFactory.getDeptIdStart() + 1 : testDepartments.get(0).getId();
    }

    /**
     * Get the first employee ID from test data.
     */
    protected Long getFirstEmployeeId() {
        return testEmployees.isEmpty() ? TestDataFactory.getEmpIdStart() : testEmployees.get(0).getId();
    }

    /**
     * Count employees in test data.
     */
    protected int getTestEmployeeCount() {
        return testEmployees.size();
    }

    /**
     * Count departments in test data.
     */
    protected int getTestDepartmentCount() {
        return testDepartments.size();
    }
}
