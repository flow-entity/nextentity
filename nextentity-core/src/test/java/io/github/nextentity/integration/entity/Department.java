package io.github.nextentity.integration.entity;

import io.github.nextentity.api.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/// 部门实体类，用于集成测试。
///
/// 实现了 {@link Entity} 接口，支持类型安全的嵌套路径表达式。
///
/// ## 嵌套路径表达式示例
///
/// ```java
/// // 因为 Department 实现了 Entity 接口
/// // 可以直接在查询中访问嵌套属性
/// List<Employee> employees = repository.query()
///     .where(Employee::getDepartment).get(Department::getName).eq("技术部")
///     .list();
/// ```
@jakarta.persistence.Entity
public class Department implements Entity {

    @Id
    private Long id;

    private String name;

    private String location;

    private Double budget;

    private Boolean active;

    private LocalDateTime createdAt;

    public Department() {
    }

    public Department(Long id, String name, String location, Double budget, Boolean active) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.budget = budget;
        this.active = active;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", budget=" + budget +
                ", active=" + active +
                '}';
    }
}