package io.github.nextentity.examples.integration;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Test configuration for integration tests.
 * Initializes test data before each test run.
 */
@TestConfiguration
public class TestConfig {

    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

    /**
     * Initialize test data.
     * This bean is conditionally created based on test needs.
     */
    @Bean
    public CommandLineRunner initDataInitializer(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {

        return args -> {
            log.info("Initializing test data...");

            // Clear existing data
            clearAllData(employeeRepository, departmentRepository);

            // Insert departments
            List<Department> departments = TestDataFactory.createTestDepartments();
            departmentRepository.insertAll(departments);
            log.info("Inserted {} departments", departments.size());

            // Insert employees
            List<Employee> employees = TestDataFactory.createTestEmployees();
            employeeRepository.insertAll(employees);
            log.info("Inserted {} employees", employees.size());
        };
    }

    private void clearAllData(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        // Delete all employees
        List<Employee> employees = employeeRepository.findAllEmployees();
        if (!employees.isEmpty()) {
            employeeRepository.deleteAll(employees);
        }

        // Delete all departments
        List<Department> departments = departmentRepository.query().getList();
        if (!departments.isEmpty()) {
            departmentRepository.deleteAll(departments);
        }
    }
}