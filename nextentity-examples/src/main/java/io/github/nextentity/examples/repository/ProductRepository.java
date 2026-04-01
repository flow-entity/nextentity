package io.github.nextentity.examples.repository;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.examples.entity.Category;
import io.github.nextentity.examples.entity.Product;
import io.github.nextentity.spring.PersistableRepository;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/// Product repository extending PersistableRepository.
///
/// This repository demonstrates the benefits of using {@link io.github.nextentity.api.Persistable}
/// interface. Unlike EmployeeRepository which needs manual ID-based queries,
/// this repository inherits convenient ID-based methods automatically:
///
/// - {@link #findById(Long)} - Find by ID returning Optional
/// - {@link #getById(Long)} - Get by ID returning null if not found
/// - {@link #findAllById(Collection)} - Find all by IDs
/// - {@link #findMapById(Collection)} - Find by IDs as Map
/// - {@link #existsById(Long)} - Check existence by ID
/// - {@link #deleteById(Long)} - Delete by ID
///
/// This repository also demonstrates association queries with {@link Category}:
///
/// - Lazy loading (default behavior)
/// - Eager fetching with fetch()
/// - Query by association ID
/// - Nested property access
/// - DTO projection with association data
@Repository
public class ProductRepository extends PersistableRepository<Product, Long> {

    public ProductRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        super(entityManager, jdbcTemplate);
    }

    // ==================== Inherited ID-based Methods ====================
    //
    // The following methods are inherited from PersistableRepository
    // and work automatically because Product implements Persistable<Long>:
    //
    // - findById(Long id) -> Optional<Product>
    // - getById(Long id) -> Product (nullable)
    // - findAllById(Collection<Long> ids) -> List<Product>
    // - getAllById(Collection<Long> ids) -> List<Product>
    // - findMapById(Collection<Long> ids) -> Map<Long, Product>
    // - findMapAll() -> Map<Long, Product>
    // - existsById(Long id) -> boolean
    // - countById(Collection<Long> ids) -> long
    // - deleteById(Long id) -> void
    // - deleteAllById(Collection<Long> ids) -> void
    //
    // No implementation needed! Compare with EmployeeRepository where
    // similar methods require manual query building.

    // ==================== Association Query Methods ====================

    /// Find products by category ID.
    /// This is the simplest form of association query - querying by foreign key.
    public List<Product> findByCategoryId(Long categoryId) {
        return query()
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Find products by multiple category IDs.
    public List<Product> findByCategoryIds(Collection<Long> categoryIds) {
        return query()
                .where(Product::getCategoryId).in(categoryIds)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getCategoryId).asc()
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Find products without category (categoryId is null).
    public List<Product> findWithoutCategory() {
        return query()
                .where(Product::getCategoryId).isNull()
                .where(Product::getActive).eq(true)
                .getList();
    }

    /// Lazy loading (default) - category is loaded on first access.
    /// This may cause N+1 query problem if you iterate and access category.
    public List<Product> findWithLazyCategory() {
        return query()
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Eager fetch - load products with categories in a single query.
    /// Use this to avoid N+1 query problem when you need the association.
    public List<Product> findWithCategoryFetch() {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Fetch with conditions - load products with categories filtered by category.
    public List<Product> findActiveInCategoryWithFetch(Long categoryId) {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Query by category using categoryId (simpler approach).
    public List<Product> findByCategoryNameSimple(Long categoryId, String categoryName) {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .getList();
    }

    /// DTO projection with association data.
    /// Select product name, price, and category name in one query.
    public List<ProductWithCategory> findProductWithCategoryInfo() {
        return query()
                .select(ProductWithCategory.class)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Manual join using stream - for complex transformations.
    public List<ProductCategoryInfo> findProductCategoryInfo() {
        List<Product> products = query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .getList();

        return products.stream()
                .map(p -> new ProductCategoryInfo(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getCategory() != null ? p.getCategory().getName() : "Uncategorized"
                ))
                .toList();
    }

    /// Three-field projection with association using nested path select.
    /// Select product name, price, and category name in one query.
    public List<Tuple3<String, BigDecimal, String>> findProductNamePriceCategoryName() {
        return query()
                .select(
                        Product::getName,
                        Product::getPrice,
                        path(Product::getCategory).get(Category::getName)
                )
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Two-field projection with association using nested path select.
    /// Select product name and category name in one query.
    public List<Tuple2<String, String>> findProductNameAndCategoryName() {
        return query()
                .select(
                        Product::getName,
                        path(Product::getCategory).get(Category::getName)
                )
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Group products by category using stream.
    /// First fetch all with category, then group in memory.
    public Map<Long, List<Product>> groupProductsByCategory() {
        List<Product> products = query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .getList();

        return products.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getCategoryId() != null ? p.getCategoryId() : -1L
                ));
    }

    /// Transfer products to another category.
    @Transactional
    public void transferToCategory(List<Long> productIds, Long newCategoryId) {
        List<Product> products = findAllById(productIds);
        products.forEach(p -> p.setCategoryId(newCategoryId));
        updateAll(products);
    }

    // ==================== Basic Query Methods ====================

    /// Find product by SKU code
    public Product findBySku(String sku) {
        return query().where(Product::getSku).eq(sku).getFirst();
    }

    /// Find products by name containing text
    public List<Product> findByNameContaining(String name) {
        return query()
                .where(Product::getName).contains(name)
                .where(Product::getActive).eq(true)
                .getList();
    }

    /// Find products in price range
    public List<Product> findByPriceBetween(BigDecimal min, BigDecimal max) {
        return query()
                .where(Product::getPrice).between(min, max)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getPrice).asc()
                .getList();
    }

    /// Find products with low stock
    public List<Product> findLowStock(int threshold) {
        return query()
                .where(Product::getStock).lt(threshold)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getStock).asc()
                .getList();
    }

    /// Find active products with pagination
    public Slice<Product> findActiveProductsPaged(int page, int size) {
        return query()
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .slice(page * size, size);
    }

    /// Get product name and price pairs
    public List<Tuple2<String, BigDecimal>> findProductNamePrices() {
        return query()
                .select(Product::getName, Product::getPrice)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .getList();
    }

    /// Calculate total stock value
    public BigDecimal calculateTotalStockValue() {
        List<Product> products = query()
                .where(Product::getActive).eq(true)
                .where(Product::getPrice).isNotNull()
                .getList();
        return products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /// Update product price
    @Transactional
    public void updatePrice(Long id, BigDecimal newPrice) {
        Product product = getById(id);
        if (product != null) {
            product.setPrice(newPrice);
            update(product);
        }
    }

    /// Update stock quantity
    @Transactional
    public void updateStock(Long id, Integer newStock) {
        Product product = getById(id);
        if (product != null) {
            product.setStock(newStock);
            update(product);
        }
    }

    /// Deactivate a product by ID (uses inherited getById)
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = getById(id);
        if (product != null) {
            product.setActive(false);
            update(product);
        }
    }

    /// Activate products by IDs (uses inherited findAllById)
    @Transactional
    public void activateProducts(Collection<Long> ids) {
        List<Product> products = findAllById(ids);
        products.forEach(p -> p.setActive(true));
        updateAll(products);
    }

    /// Find products by IDs and return as map (uses inherited findMapById)
    public Map<Long, Product> findProductsAsMap(Collection<Long> ids) {
        return findMapById(ids);
    }

    /// Check if product exists and is active
    public boolean existsAndActive(Long id) {
        Product product = getById(id);
        return product != null && Boolean.TRUE.equals(product.getActive());
    }

    // ==================== DTO Classes ====================

    /// DTO for product with category name
    public static class ProductWithCategory {
        private String name;
        private BigDecimal price;
        private String categoryName;

        public ProductWithCategory() {}

        public ProductWithCategory(String name, BigDecimal price, String categoryName) {
            this.name = name;
            this.price = price;
            this.categoryName = categoryName;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    }

    /// DTO for product category info with more fields
    public static class ProductCategoryInfo {
        private Long productId;
        private String productName;
        private BigDecimal price;
        private String categoryName;

        public ProductCategoryInfo() {}

        public ProductCategoryInfo(Long productId, String productName, BigDecimal price, String categoryName) {
            this.productId = productId;
            this.productName = productName;
            this.price = price;
            this.categoryName = categoryName;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    }
}