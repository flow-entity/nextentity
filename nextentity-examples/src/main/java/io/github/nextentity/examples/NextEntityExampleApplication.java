package io.github.nextentity.examples;

import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/// NextEntity 示例应用程序。
/// <p>
/// 一个演示 NextEntity 用法的 Spring Boot 应用程序。
/// 运行此应用程序可以查看各种 API 示例的实际效果。
///
/// <h2>运行示例：</h2>
/// <pre>{@code
/// mvn spring-boot:run -pl nextentity-examples
/// }</pre>
@SpringBootApplication
public class NextEntityExampleApplication {

    static void main(String[] args) {
        SpringApplication.run(NextEntityExampleApplication.class, args);
    }

    /// 演示运行器，执行各种示例。
    @Bean
    public CommandLineRunner demo(EmployeeRepository employeeRepository,
                                  DepartmentRepository departmentRepository) {
        return args -> {
            System.out.println("\n=== NextEntity Examples ===\n");

            // Create sample data
            createSampleData(employeeRepository, departmentRepository);

            // Run examples
            demonstrateBasicQueries(employeeRepository, departmentRepository);
            demonstrateConditions(employeeRepository, departmentRepository);
            demonstrateProjections(employeeRepository, departmentRepository);
            demonstratePagination(employeeRepository, departmentRepository);

            System.out.println("\n=== Examples Complete ===\n");
        };
    }

    private void createSampleData(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        System.out.println("--- Creating Sample Data ---");

        // Create departments
        Department engineering = new Department(1L, "Engineering", "Building A", 500000.0, true);
        Department sales = new Department(2L, "Sales", "Building B", 300000.0, true);
        Department hr = new Department(3L, "HR", "Building C", 200000.0, true);

        departmentRepository.insertAll(List.of(engineering, sales, hr));

        // Create employees
        List<Employee> employees = List.of(
                createEmployee(1L, "John Doe", "john@example.com", BigDecimal.valueOf(75000.0), EmployeeStatus.ACTIVE, 1L),
                createEmployee(2L, "Jane Smith", "jane@example.com", BigDecimal.valueOf(85000.0), EmployeeStatus.ACTIVE, 1L),
                createEmployee(3L, "Bob Johnson", "bob@example.com", BigDecimal.valueOf(65000.0), EmployeeStatus.ACTIVE, 2L),
                createEmployee(4L, "Alice Brown", "alice@example.com", BigDecimal.valueOf(55000.0), EmployeeStatus.ON_LEAVE, 2L),
                createEmployee(5L, "Charlie Wilson", "charlie@example.com", BigDecimal.valueOf(95000.0), EmployeeStatus.ACTIVE, 1L),
                createEmployee(6L, "Diana Lee", "diana@example.com", BigDecimal.valueOf(45000.0), EmployeeStatus.INACTIVE, 3L)
        );

        employeeRepository.insertAll(employees);
        System.out.println("Created 3 departments and 6 employees\n");
    }

    private void demonstrateBasicQueries(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        System.out.println("--- Basic Queries ---");

        // Find all employees
        List<Employee> allEmployees = employeeRepository.findAllEmployees();
        System.out.println("All employees: " + allEmployees.size());

        // Find by ID
        Employee employee = employeeRepository.findEmployeeById(1L);
        System.out.println("Employee with ID 1: " + (employee != null ? employee.getName() : "not found"));

        // Find by department
        List<Employee> engineering = employeeRepository.findByDepartmentId(1L);
        System.out.println("Engineering employees: " + engineering.size());

        System.out.println();
    }

    private void demonstrateConditions(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        System.out.println("--- Query Conditions ---");

        // Active high earners
        List<Employee> activeHighEarners = employeeRepository.findActiveHighEarners();
        System.out.println("Active employees earning > 50k: " + activeHighEarners.size());

        // IN clause
        List<Employee> specificStatuses = employeeRepository.findByStatuses(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE);
        System.out.println("Active or on leave: " + specificStatuses.size());

        // Between
        List<Employee> salaryRange = employeeRepository.findBySalaryBetween(BigDecimal.valueOf(50000.0), BigDecimal.valueOf(80000.0));
        System.out.println("Salary between 50k-80k: " + salaryRange.size());

        // String contains
        List<Employee> nameContains = employeeRepository.findByNameContaining("John");
        System.out.println("Name contains 'John': " + nameContains.size());

        System.out.println();
    }

    private void demonstrateProjections(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        System.out.println("--- Projections ---");

        // Single field
        List<String> names = employeeRepository.findEmployeeNames();
        System.out.println("Employee names: " + names);

        // Tuple projection
        var nameSalaries = employeeRepository.findNameAndSalary();
        System.out.println("Name-Salary pairs: " + nameSalaries.size() + " records");

        System.out.println();
    }

    private void demonstratePagination(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        System.out.println("--- Pagination ---");

        // Page 1
        List<Employee> page1 = employeeRepository.findPage(0, 3);
        System.out.println("Page 1 (first 3): " + page1.stream().map(Employee::getName).toList());

        // Page 2
        List<Employee> page2 = employeeRepository.findPage(1, 3);
        System.out.println("Page 2 (next 3): " + page2.stream().map(Employee::getName).toList());

        // Top earners
        List<Employee> topEarners = employeeRepository.findTopEarners(3);
        System.out.println("Top 3 earners: " + topEarners.stream().map(Employee::getName).toList());

        System.out.println();
    }

    private Employee createEmployee(Long id, String name, String email, BigDecimal salary,
                                    EmployeeStatus status, Long departmentId) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(salary);
        employee.setActive(status == EmployeeStatus.ACTIVE || status == EmployeeStatus.ON_LEAVE);
        employee.setStatus(status);
        employee.setDepartmentId(departmentId);
        employee.setHireDate(LocalDate.now().minusYears(1));
        return employee;
    }
}