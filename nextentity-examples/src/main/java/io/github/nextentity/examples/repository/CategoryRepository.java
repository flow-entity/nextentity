package io.github.nextentity.examples.repository;

import io.github.nextentity.examples.entity.Category;
import io.github.nextentity.spring.PersistableRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Category repository extending PersistableRepository.
 * <p>
 * Demonstrates ID-based methods inherited from PersistableRepository
 * for the {@link Category} entity.
 */
@Repository
public class CategoryRepository extends PersistableRepository<Category, Long> {

    public CategoryRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    /// Find all active categories
    public List<Category> findActiveCategories() {
        return query()
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// Find root categories (no parent)
    public List<Category> findRootCategories() {
        return query()
                .where(Category::getParentId).isNull()
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// Find subcategories by parent ID
    public List<Category> findSubcategories(Long parentId) {
        return query()
                .where(Category::getParentId).eq(parentId)
                .where(Category::getActive).eq(true)
                .orderBy(Category::getName).asc()
                .list();
    }

    /// Find category by name
    public Category findByName(String name) {
        return query()
                .where(Category::getName).eq(name)
                .first();
    }
}
