package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.util.List;

/// Association and Fetch Example demonstrating entity associations and fetch strategies
public class AssociationFetchExample {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public AssociationFetchExample(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== Fetch Associations ====================

    /// Lazy loading (default behavior) - department loaded on first access
    public List<Employee> findWithLazyLoading() {
        return queryEmployee()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Eager fetch association - load employees with departments in one query
    public List<Employee> findWithDepartmentFetch() {
        return queryEmployee()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Fetch multiple associations
    public List<Employee> findWithMultipleFetches() {
        return queryEmployee()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    // ==================== Query with Association ====================

    /// Query by association ID
    public List<Employee> findByDepartmentId(Long departmentId) {
        return queryEmployee()
                .where(Employee::getDepartmentId).eq(departmentId)
                .getList();
    }

    /// Query with fetch and conditions
    public List<Employee> findActiveInDepartmentWithFetch(Long departmentId) {
        return queryEmployee()
                .fetch(Employee::getDepartment)
                .where(Employee::getDepartmentId).eq(departmentId)
                .where(Employee::getActive).eq(true)
                .orderBy(Employee::getName).asc()
                .getList();
    }

    // ==================== Projection with Association ====================

    /// Select with association data using DTO
    public List<EmployeeWithDept> findEmployeeWithDepartmentInfo() {
        return queryEmployee()
                .select(EmployeeWithDept.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Join data from two entities manually
    public List<EmployeeWithDept> findWithManualJoin() {
        List<Employee> employees = queryEmployee()
                .fetch(Employee::getDepartment)
                .where(Employee::getActive).eq(true)
                .getList();

        return employees.stream()
                .map(emp -> new EmployeeWithDept(
                        emp.getName(),
                        emp.getDepartment() != null ? emp.getDepartment().getName() : null
                ))
                .toList();
    }

    // ==================== Department Queries ====================

    /// Query all active departments
    public List<Department> findActiveDepartments() {
        return queryDepartment()
                .where(Department::getActive).eq(true)
                .orderBy(Department::getName).asc()
                .getList();
    }

    /// Find department by ID
    public Department findDepartmentById(Long id) {
        return queryDepartment()
                .where(Department::getId).eq(id)
                .getFirst();
    }

    // ==================== Helper Methods ====================

    private Select<Employee> queryEmployee() {
        return employeeRepository.query();
    }

    private Select<Department> queryDepartment() {
        return departmentRepository.query();
    }

    // ==================== DTO Classes ====================

    /// DTO combining employee and department info
    public static class EmployeeWithDept {
        private String employeeName;
        private String departmentName;

        public EmployeeWithDept() {}

        public EmployeeWithDept(String employeeName, String departmentName) {
            this.employeeName = employeeName;
            this.departmentName = departmentName;
        }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getDepartmentName() { return departmentName; }
        public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    }
}