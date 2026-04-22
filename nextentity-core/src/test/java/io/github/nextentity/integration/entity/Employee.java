package io.github.nextentity.integration.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

///
/// Employee entity for integration testing.
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    private String email;

    private Double salary;

    private Boolean active;

    private EmployeeStatus status;

    private Long departmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;

    private LocalDate hireDate;

    private LocalDateTime createdAt;

    public Employee() {
    }

    public Employee(Long id, String name, String email, Double salary, Boolean active,
                    EmployeeStatus status, Long departmentId, LocalDate hireDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.active = active;
        this.status = status;
        this.departmentId = departmentId;
        this.hireDate = hireDate;
        this.createdAt = LocalDateTime.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public EmployeeStatus getStatus() {
        return status;
    }

    public void setStatus(EmployeeStatus status) {
        this.status = status;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Employee{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", salary=" + salary +
               ", active=" + active +
               ", status=" + status +
               ", departmentId=" + departmentId +
               ", hireDate=" + hireDate +
               '}';
    }
}
