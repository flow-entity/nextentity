package io.github.nextentity.examples;

import io.github.nextentity.api.Select;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.examples.entity.Department;
import io.github.nextentity.examples.entity.Employee;
import io.github.nextentity.examples.entity.EmployeeStatus;
import io.github.nextentity.examples.repository.DepartmentRepository;
import io.github.nextentity.examples.repository.EmployeeRepository;

import java.util.List;

/// Projection Example demonstrating how to select specific fields from entities
public class ProjectionExample {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public ProjectionExample(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    // ==================== Single Field Selection ====================

    /// Select a single field from entity
    public List<String> findEmployeeNames() {
        return queryEmployee()
                .select(Employee::getName)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select distinct values
    public List<Long> findDistinctDepartmentIds() {
        return queryEmployee()
                .selectDistinct(Employee::getDepartmentId)
                .getList();
    }

    // ==================== Tuple Selection ====================

    /// Select two fields using Tuple2
    public List<Tuple2<String, Double>> findNameAndSalary() {
        return queryEmployee()
                .select(Employee::getName, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select three fields using Tuple3
    public List<Tuple3<String, String, Double>> findNameEmailSalary() {
        return queryEmployee()
                .select(Employee::getName, Employee::getEmail, Employee::getSalary)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select distinct tuple values
    public List<Tuple2<String, EmployeeStatus>> findDistinctNameStatus() {
        return queryEmployee()
                .selectDistinct(Employee::getName, Employee::getStatus)
                .getList();
    }

    // ==================== DTO Projection ====================

    /// Select into a DTO class
    public List<EmployeeSummary> findEmployeeSummaries() {
        return queryEmployee()
                .select(EmployeeSummary.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select with conditions into DTO
    public List<EmployeeSummary> findHighEarnerSummaries() {
        return queryEmployee()
                .select(EmployeeSummary.class)
                .where(Employee::getSalary).gt(50000.0)
                .where(Employee::getStatus).eq(EmployeeStatus.ACTIVE)
                .getList();
    }

    /// Select distinct into DTO
    public List<EmployeeInfo> findDistinctEmployeeInfo() {
        return queryEmployee()
                .selectDistinct(EmployeeInfo.class)
                .getList();
    }

    // ==================== Entity Selection ====================

    /// Select full entity (default behavior)
    public List<Employee> findActiveEmployees() {
        return queryEmployee()
                .where(Employee::getActive).eq(true)
                .getList();
    }

    /// Select distinct entities
    public List<Employee> findDistinctActiveEmployees() {
        return queryEmployee()
                .selectDistinct(Employee.class)
                .where(Employee::getActive).eq(true)
                .getList();
    }

    // ==================== Cross-Entity Selection ====================

    /// Select from different entity types
    public List<String> findActiveDepartmentNames() {
        return queryDepartment()
                .select(Department::getName)
                .where(Department::getActive).eq(true)
                .getList();
    }

    /// Select department info into DTO
    public List<DepartmentInfo> findDepartmentInfo() {
        return queryDepartment()
                .select(DepartmentInfo.class)
                .where(Department::getActive).eq(true)
                .getList();
    }

    // ==================== Helper Methods ====================

    private Select<Employee> queryEmployee() {
        return employeeRepository.query();
    }

    private Select<Department> queryDepartment() {
        return departmentRepository.query();
    }

    // ==================== DTO Classes ====================

    /// DTO for employee summary projection
    public static class EmployeeSummary {
        private String name;
        private String email;
        private Double salary;
        private EmployeeStatus status;

        public EmployeeSummary() {}

        public EmployeeSummary(String name, String email, Double salary, EmployeeStatus status) {
            this.name = name;
            this.email = email;
            this.salary = salary;
            this.status = status;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Double getSalary() { return salary; }
        public void setSalary(Double salary) { this.salary = salary; }
        public EmployeeStatus getStatus() { return status; }
        public void setStatus(EmployeeStatus status) { this.status = status; }
    }

    /// DTO for basic employee info
    public static class EmployeeInfo {
        private Long id;
        private String name;
        private String email;

        public EmployeeInfo() {}

        public EmployeeInfo(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /// DTO for department info
    public static class DepartmentInfo {
        private Long id;
        private String name;
        private String location;
        private Double budget;

        public DepartmentInfo() {}

        public DepartmentInfo(Long id, String name, String location, Double budget) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.budget = budget;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getBudget() { return budget; }
        public void setBudget(Double budget) { this.budget = budget; }
    }
}