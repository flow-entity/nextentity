package io.github.nextentity.examples.entity;

import io.github.nextentity.api.Persistable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;
import java.util.List;

/// 产品分类实体类，实现了 {@link Persistable} 接口。
///
/// 本实体演示：
/// - {@link Persistable} 接口的使用（提供标准 ID 访问方法）
/// - 与 {@link Product} 实体的关系
/// - 分层分类结构（parentId）
///
/// ## Persistable 接口
///
/// 实现 {@link Persistable} 接口提供标准的 `getId()` 方法，
/// 这是良好的设计实践，同时支持类型安全的嵌套路径访问。
@jakarta.persistence.Entity
public class Category implements Persistable<Long> {

    /// 分类 ID（主键）。
    @Id
    private Long id;

    /// 分类名称。
    private String name;

    /// 分类描述。
    private String description;

    /// 父分类 ID（用于分层分类结构）。
    private Long parentId;

    /// 分类是否活跃。
    private Boolean active;

    /// 分类下的产品列表（懒加载）。
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    private List<Product> products;

    /// 创建时间戳。
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