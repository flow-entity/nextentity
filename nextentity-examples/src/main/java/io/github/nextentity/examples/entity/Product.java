package io.github.nextentity.examples.entity;

import io.github.nextentity.api.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;

import java.time.LocalDateTime;

/**
 * Product entity implementing Persistable interface.
 * <p>
 * This entity demonstrates the use of {@link Persistable} interface
 * which provides ID-based convenience methods in the repository.
 * <p>
 * Unlike Employee and Department which don't implement Persistable,
 * Product can benefit from methods like findById, findAllById, findMapById, etc.
 * <p>
 * This entity also demonstrates the relationship with {@link Category} entity.
 */
@Entity
public class Product implements Persistable<Long> {

    /**
     * Product ID (primary key).
     */
    @Id
    private Long id;

    /**
     * Product name.
     */
    private String name;

    /**
     * Product SKU code.
     */
    private String sku;

    /**
     * Product price.
     */
    private Double price;

    /**
     * Stock quantity.
     */
    private Integer stock;

    /**
     * Category ID (foreign key).
     */
    private Long categoryId;

    /**
     * Category association (lazy loaded).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;

    /**
     * Whether the product is active.
     */
    private Boolean active;

    /**
     * Creation timestamp.
     */
    private LocalDateTime createdAt;

    /**
     * Category name (transient field for projection).
     * Used for DTO projections with association data.
     */
    @Transient
    private String categoryName;

    public Product() {
    }

    public Product(Long id, String name, String sku, Double price, Integer stock, Long categoryId, Boolean active) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}