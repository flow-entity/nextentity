package io.github.nextentity.examples.entity;

import io.github.nextentity.api.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Category entity implementing Persistable interface.
 * <p>
 * This entity represents a product category and demonstrates
 * the relationship with {@link Product} entity.
 */
@Entity
public class Category implements Persistable<Long> {

    /**
     * Category ID (primary key).
     */
    @Id
    private Long id;

    /**
     * Category name.
     */
    private String name;

    /**
     * Category description.
     */
    private String description;

    /**
     * Parent category ID (for hierarchical categories).
     */
    private Long parentId;

    /**
     * Whether the category is active.
     */
    private Boolean active;

    /**
     * Products in this category (lazy loaded).
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private List<Product> products;

    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;

    public Category() {
    }

    public Category(Long id, String name, String description, Long parentId, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    // Persistable interface implementation
    @Override
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}