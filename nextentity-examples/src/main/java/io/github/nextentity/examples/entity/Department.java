package io.github.nextentity.examples.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Department entity for examples.
 * <p>
 * This entity represents a department in an organization.
 * Used to demonstrate CRUD operations, queries, and relationships.
 */
@Entity
public class Department {

    /**
     * Department ID (primary key).
     */
    @Id
    private Long id;

    /**
     * Department name.
     */
    private String name;

    /**
     * Department location (building, floor, etc.).
     */
    private String location;

    /**
     * Department budget.
     */
    private Double budget;

    /**
     * Whether the department is active.
     */
    private Boolean active;

    /**
     * Creation timestamp.
     */
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}