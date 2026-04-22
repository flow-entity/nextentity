package io.github.nextentity.integration.dto;

///
/// JavaBean projection for Employee with department info.
/// 测试s nested projection with association.
public class EmployeeWithDepartment {

    private Long id;
    private String name;
    private String departmentName;
    private Double salary;

    // Default constructor required for JavaBean projection
    public EmployeeWithDepartment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "EmployeeWithDepartment{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", departmentName='" + departmentName + '\'' +
               ", salary=" + salary +
               '}';
    }
}
