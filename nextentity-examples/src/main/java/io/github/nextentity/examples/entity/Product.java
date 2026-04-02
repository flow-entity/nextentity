package io.github.nextentity.examples.entity;

import io.github.nextentity.api.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/// 产品实体类，实现了 {@link Persistable} 接口。
///
/// 本实体演示：
/// - {@link Persistable} 接口的使用（提供 ID 相关便捷方法）
/// - 与 {@link Category} 实体的关系
/// - {@code @Transient} 字段的使用
///
/// ## Persistable 接口优势
///
/// 与 Employee 和 Department 不同，Product 实现了 {@link Persistable}，
/// 可以使用 Repository 的便捷方法：
/// ```java
/// Product product = productRepository.findById(1L);
/// List<Product> products = productRepository.findAllById(List.of(1L, 2L));
/// Map<Long, Product> productMap = productRepository.findMapById(List.of(1L, 2L));
/// ```
@jakarta.persistence.Entity
public class Product implements Persistable<Long> {

    /// 产品 ID（主键）。
    @Id
    private Long id;

    /// 产品名称。
    private String name;

    /// 产品 SKU 编码。
    private String sku;

    /// 产品价格。
    private BigDecimal price;

    /// 库存数量。
    private Integer stock;

    /// 分类 ID（外键）。
    private Long categoryId;

    /// 分类关联（懒加载）。
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private Category category;

    /// 产品是否上架。
    private Boolean active;

    /// 创建时间戳。
    private LocalDateTime createdAt;

    /// 分类名称（瞬态字段，用于投影）。
    ///
    /// 用于包含关联数据的 DTO 投影。
    @Transient
    private String categoryName;

    public Product() {
    }

    public Product(Long id, String name, String sku, BigDecimal price, Integer stock, Long categoryId, Boolean active) {
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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