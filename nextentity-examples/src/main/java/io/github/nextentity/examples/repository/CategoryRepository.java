package io.github.nextentity.examples.repository;

import io.github.nextentity.api.EntityContext;
import io.github.nextentity.core.EntityOperationsFactory;
import io.github.nextentity.examples.entity.Category;
import io.github.nextentity.spring.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/// 分类 Repository，继承 {@link AbstractRepository}。
///
/// 用于 {@link Category} 实体的数据访问。
///
/// ## 继承的 ID 相关方法
///
/// 从 AbstractRepository 继承以下方法：
/// - `findById(Long id)` - 按 ID 查找，返回 Optional
/// - `getById(Long id)` - 按 ID 获取，不存在返回 null
/// - `findAllById(Collection)` - 按多个 ID 查找
/// - `findAllAsMapById(Collection)` - 按 ID 查找并返回 Map
/// - `existsById(Long id)` - 检查 ID 是否存在
/// - `deleteById(Long id)` - 按 ID 删除
@Repository
public class CategoryRepository extends AbstractRepository<Category, Long> {

    /// 创建 Repository 实例。
    ///
    /// 通过构造器注入 EntityContext，自动检测实体类型和主键类型，
    /// 并初始化查询构建器和更新执行器。
    ///
    /// @param context NextEntity 上下文
    protected CategoryRepository(EntityOperationsFactory context) {
        super(context);
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