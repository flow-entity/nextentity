package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Department entity for integration testing.
 */
@Entity
public class Department {

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
