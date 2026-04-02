package io.github.nextentity.examples.repository;

import io.github.nextentity.examples.entity.Category;
import io.github.nextentity.spring.PersistableRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/// 分类 Repository，继承 {@link PersistableRepository}。
///
/// 演示 {@link io.github.nextentity.api.Persistable} 接口带来的 ID 相关便捷方法，
/// 用于 {@link Category} 实体。
///
/// ## 继承的便捷方法
///
/// 因为 Category 实现了 {@link io.github.nextentity.api.Persistable}，
/// 以下方法自动可用：
/// - `findById(Long id)` - 按 ID 查找，返回 Optional
/// - `getById(Long id)` - 按 ID 获取，不存在返回 null
/// - `findAllById(Collection)` - 按多个 ID 查找
/// - `findMapById(Collection)` - 按 ID 查找并返回 Map
/// - `existsById(Long id)` - 检查 ID 是否存在
/// - `deleteById(Long id)` - 按 ID 删除
@Repository
public class CategoryRepository extends PersistableRepository<Category, Long> {

    public CategoryRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    /// 查询所有活跃分类。
    public List<Category> findActiveCategories() {
        return query()
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// 查询根分类（无父分类）。
    public List<Category> findRootCategories() {
        return query()
                .where(Category::getParentId).isNull()
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// 根据父分类 ID 查询子分类。
    public List<Category> findSubcategories(Long parentId) {
        return query()
                .where(Category::getParentId).eq(parentId)
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// 根据名称查找分类。
    public Category findByName(String name) {
        return query()
                .where(Category::getName).eq(name)
                .first();
    }
}