package io.github.nextentity.integration.dto;

///
/// JavaBean projection for Employee summary statistics.
/// 测试s projection with partial fields from entity.
public class EmployeeSummary {

    private String name;
    private Double salary;
    private Boolean active;

    // Default constructor required for JavaBean projection
    public EmployeeSummary() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "EmployeeSummary{" +
               "name='" + name + '\'' +
               ", salary=" + salary +
               ", active=" + active +
               '}';
    }
}
