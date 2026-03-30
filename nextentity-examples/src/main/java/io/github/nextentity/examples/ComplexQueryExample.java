package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/// Complex Query Example demonstrating multi-condition queries, dynamic query building, and batch operations
public class ComplexQueryExample {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public ComplexQueryExample(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== Complex Condition Queries ====================

    /// Multi-condition employee search combining multiple operators
    public List<Employee> findEmployeesByMultipleConditions() {
        return queryEmployee()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).in(EmployeeStatus.ACTIVE, EmployeeStatus.ON_LEAVE)
                .where(Employee::getSalary).between(40000.0, 80000.0)
                .where(Employee::getDepartmentId).isNotNull()
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Employee search with optional filters using conditional operators
    public List<Employee> searchEmployees(String name, Long departmentId, Double minSalary) {
        return queryEmployee()
                .where(Employee::getName).containsIfNotEmpty(name)
                .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
                .where(Employee::getSalary).geIfNotNull(minSalary)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Advanced search with many optional parameters
    public List<Employee> advancedSearch(
            String name, String email, Long departmentId, EmployeeStatus status,
            Boolean active, Double minSalary, Double maxSalary, LocalDate hireAfter) {

        return queryEmployee()
                .where(Employee::getName).containsIfNotEmpty(name)
                .where(Employee::getEmail).endsWithIfNotNull(email)
                .where(Employee::getDepartmentId).eqIfNotNull(departmentId)
                .where(Employee::getStatus).eqIfNotNull(status)
                .where(Employee::getActive).eqIfNotNull(active)
                .where(Employee::getSalary).geIfNotNull(minSalary)
                .where(Employee::getSalary).leIfNotNull(maxSalary)
                .where(Employee::getHireDate).geIfNotNull(hireAfter)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    // ==================== Report Queries ====================

    /// Generate salary report grouped by department
    public List<Map.Entry<Long, List<Employee>>> generateDepartmentReport() {
        return queryEmployee()
                .where(Employee::getActive).eq(true)
                .getList()
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(Employee::getDepartmentId))
                .entrySet().stream().toList();
    }

    /// Find employees due for review (hired > 1 year ago, active, below-average salary)
    public List<Employee> findEmployeesDueForReview() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return queryEmployee()
                .where(Employee::getActive).eq(true)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .where(Employee::getHireDate).le(oneYearAgo)
                .where(Employee::getSalary).lt(50000.0)
                .orderBy(Employee::getHireDate).asc()
                .getList();
    }

    // ==================== Batch Operations ====================

    /// Give a raise to all employees in a department within a transaction
    public void giveDepartmentRaise(Long departmentId, double percentage) {
        employeeRepository.doInTransaction(() -> {
            List<Employee> employees = queryEmployee()
                    .where(Employee::getDepartmentId).eq(departmentId)
                    .where(Employee::getActive).eq(true)
                    .getList();

            employees.forEach(e -> e.setSalary(e.getSalary() * (1 + percentage)));
            employeeRepository.updateAll(employees);
        });
    }

    /// Transfer employees between departments within a transaction
    public void transferEmployees(List<Long> employeeIds, Long newDepartmentId) {
        employeeRepository.doInTransaction(() -> {
            List<Employee> employees = queryEmployee()
                    .where(Employee::getId).in(employeeIds)
                    .getList();

            employees.forEach(e -> e.setDepartmentId(newDepartmentId));
            employeeRepository.updateAll(employees);
        });
    }

    /// Deactivate terminated employees within a transaction
    public void deactivateTerminatedEmployees() {
        employeeRepository.doInTransaction(() -> {
            List<Employee> employees = queryEmployee()
                    .where(Employee::getStatus).eq(EmployeeStatus.TERMINATED)
                    .getList();

            employees.forEach(e -> e.setActive(false));
            employeeRepository.updateAll(employees);
        });
    }

    // ==================== Cross-Entity Queries ====================

    /// Find employees with department eagerly loaded
    public List<Employee> findEmployeesWithDepartment() {
        return queryEmployee()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    /// Find all active departments
    public List<Department> findActiveDepartments() {
        return queryDepartment()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getName).asc()
                .getList();
    }

    // ==================== Audit Queries ====================

    /// Find employees hired within recent days
    public List<Employee> findRecentlyHired(int days) {
        LocalDate recentDate = LocalDate.now().minusDays(days);

        return queryEmployee()
                .where(Employee::getHireDate).ge(recentDate)
                .orderBy(Employee::getHireDate).desc()
                .getList();
    }

    /// Find employees without department
    public List<Employee> findEmployeesWithoutDepartment() {
        return queryEmployee()
                .where(Employee::getDepartmentId).isNull()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Find employees with missing email
    public List<Employee> findEmployeesWithMissingEmail() {
        return queryEmployee()
                .where(Employee::getEmail).isNull()
                .getList();
    }

    // ==================== Helper Methods ====================

    private Select<Employee> queryEmployee() {
        return employeeRepository.query();
    }

    private Select<Department> queryDepartment() {
        return departmentRepository.query();
    }
}