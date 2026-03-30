package io.github.nextentity.examples.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Employee entity for examples.
 * <p>
 * This entity represents an employee in an organization.
 * Used to demonstrate CRUD operations, queries, projections, and complex queries.
 */
@Entity
public class Employee {

    /**
     * Employee ID (primary key).
     */
    @Id
    private Long id;

    /**
     * Employee name.
     */
    private String name;

    /**
     * Employee email address.
     */
    private String email;

    /**
     * Employee salary.
     */
    private Double salary;

    /**
     * Whether the employee is active.
     */
    private Boolean active;

    /**
     * Employee status.
     */
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;

    /**
     * Department ID (foreign key).
     */
    private Long departmentId;

    /**
     * Department association (lazy loaded).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departmentId", insertable = false, updatable = false)
    private Department department;

    /**
     * Employee hire date.
     */
    private LocalDate hireDate;

    /**
     * Creation timestamp.
     */
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}