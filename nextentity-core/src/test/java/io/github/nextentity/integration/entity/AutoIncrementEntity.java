package io.github.nextentity.integration.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * Auto-increment entity for testing ID generation.
 * This entity uses database-generated identity values.
 */
@Entity
public class AutoIncrementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Integer priority;

    private Boolean active;

    private LocalDateTime createdAt;

    public AutoIncrementEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public AutoIncrementEntity(String name, String description, Integer priority, Boolean active) {
        this.name = name;
        this.description = description;
        this.priority = priority;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
        return "AutoIncrementEntity{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", priority=" + priority +
               ", active=" + active +
               ", createdAt=" + createdAt +
               '}';
    }
}