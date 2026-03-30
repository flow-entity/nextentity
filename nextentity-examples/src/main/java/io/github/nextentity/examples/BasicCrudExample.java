package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;

/// Basic CRUD Operations Example
public class BasicCrudExample {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public BasicCrudExample(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== INSERT Operations ====================

    /// Insert a single employee
    public void insertSingleEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john@example.com");
        employee.setSalary(50000.0);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartmentId(1L);
        employee.setHireDate(LocalDate.now());

        employeeRepository.insert(employee);
    }

    /// Batch insert multiple employees
    public void insertMultipleEmployees() {
        List<Employee> employees = List.of(
                createEmployee(1L, "Alice", "alice@example.com", 50000.0),
                createEmployee(2L, "Bob", "bob@example.com", 55000.0),
                createEmployee(3L, "Charlie", "charlie@example.com", 60000.0)
        );

        employeeRepository.insertAll(employees);
    }

    // ==================== SELECT Operations ====================

    /// Query all employees
    public List<Employee> findAllEmployees() {
        return queryEmployee().getList();
    }

    /// Query employee by ID
    public Employee findEmployeeById(Long id) {
        return queryEmployee()
                .where(Employee::getId).eq(id)
                .getList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    /// Query employees by multiple IDs using IN clause
    public List<Employee> findEmployeesByIds(List<Long> ids) {
        return queryEmployee()
                .where(Employee::getId).in(ids)
                .getList();
    }

    /// Query employees by department with active status
    public List<Employee> findActiveEmployeesByDepartment(Long departmentId) {
        return queryEmployee()
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Get single employee by email
    public Employee findEmployeeByEmail(String email) {
        return queryEmployee()
                .where(Employee::getEmail).eq(email)
                .getFirst();
    }

    // ==================== UPDATE Operations ====================

    /// Update an existing employee's salary
    public void updateEmployeeSalary(Long id, Double newSalary) {
        Employee employee = findEmployeeById(id);
        if (employee != null) {
            employee.setSalary(newSalary);
            employeeRepository.update(employee);
        }
    }

    /// Give raise to all employees in a department
    public List<Employee> giveRaiseToDepartment(Long departmentId, double percentage) {
        List<Employee> employees = queryEmployee()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();

        employees.forEach(e -> e.setSalary(e.getSalary() * (1 + percentage)));
        employeeRepository.updateAll(employees);
        return employees;
    }

    // ==================== DELETE Operations ====================

    /// Delete a single employee
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        if (employee != null) {
            employeeRepository.delete(employee);
        }
    }

    /// Delete all employees in a department
    public void deleteEmployeesByDepartment(Long departmentId) {
        List<Employee> employees = queryEmployee()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();

        employeeRepository.deleteAll(employees);
    }

    // ==================== Transaction Operations ====================

    /// Execute operations within a transaction
    public void createDepartmentWithEmployees() {
        employeeRepository.doInTransaction(() -> {
            Department dept = new Department(10L, "Engineering", "Building A", 100000.0, true);
            departmentRepository.insert(dept);

            Employee emp = createEmployee(100L, "Engineer", "eng@example.com", 80000.0);
            emp.setDepartmentId(10L);
            employeeRepository.insert(emp);
        });
    }

    // ==================== Helper Methods ====================

    private Employee createEmployee(Long id, String name, String email, Double salary) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        employee.setEmail(email);
        employee.setSalary(salary);
        employee.setActive(true);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setHireDate(LocalDate.now());
        return employee;
    }

    private Select<Employee> queryEmployee() {
        return employeeRepository.query();
    }
}