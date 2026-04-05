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

/// 集成测试的测试配置。
/// 在每次测试运行前初始化测试数据。
@TestConfiguration
public class TestConfig {

    private static final Logger log = LoggerFactory.getLogger(TestConfig.class);

    /// 初始化测试数据。
    /// 此 Bean 根据测试需求条件性创建。
    @Bean
    public CommandLineRunner initDataInitializer(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository) {

        return args -> {
            log.info("Initializing test data...");

            // 清除现有数据
            clearAllData(employeeRepository, departmentRepository);

            // 插入部门
            List<Department> departments = TestDataFactory.createTestDepartments();
            departmentRepository.insertAll(departments);
            log.info("Inserted {} departments", departments.size());

            // 插入员工
            List<Employee> employees = TestDataFactory.createTestEmployees();
            employeeRepository.insertAll(employees);
            log.info("Inserted {} employees", employees.size());
        };
    }

    private void clearAllData(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        // 删除所有员工
        List<Employee> employees = employeeRepository.findAllEmployees();
        if (!employees.isEmpty()) {
            employeeRepository.deleteAll(employees);
        }

        // 删除所有部门
        List<Department> departments = departmentRepository.query().list();
        if (!departments.isEmpty()) {
            departmentRepository.deleteAll(departments);
        }
    }
}