package io.github.nextentity.examples.repository;

import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.examples.entity.Category;
import io.github.nextentity.examples.entity.Product;
import io.github.nextentity.spring.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/// 产品 Repository，继承 {@link AbstractRepository}。
///
/// 演示以下功能：
///
/// - ID 相关便捷方法（继承自 AbstractRepository）
/// - 与 {@link Category} 的关联查询
/// - 懒加载与急加载
/// - DTO 投影与嵌套属性
@Repository
public class ProductRepository extends AbstractRepository<Product, Long> {

    // ==================== 继承的 ID 相关方法 ====================
    //
    // 以下方法从 AbstractRepository 继承：
    //
    // - findById(Long id) -> Optional<Product>
    // - getById(Long id) -> Product (可空)
    // - findAllById(Collection<Long> ids) -> List<Product>
    // - getAllById(Collection<Long> ids) -> List<Product>
    // - findMapById(Collection<Long> ids) -> Map<Long, Product>
    // - findMapAll() -> Map<Long, Product>
    // - existsById(Long id) -> boolean
    // - countById(Collection<Long> ids) -> long
    // - deleteById(Long id) -> void
    // - deleteAllById(Collection<Long> ids) -> void

    // ==================== 关联查询方法 ====================

    /// 根据分类 ID 查询产品（最简单的关联查询方式）。
    public List<Product> findByCategoryId(Long categoryId) {
        return query()
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 根据多个分类 ID 查询产品。
    public List<Product> findByCategoryIds(Collection<Long> categoryIds) {
        return query()
                .where(Product::getCategoryId).in(categoryIds)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getCategoryId).asc()
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 查询无分类的产品。
    public List<Product> findWithoutCategory() {
        return query()
                .where(Product::getCategoryId).isNull()
                .where(Product::getActive).eq(true)
                .list();
    }

    /// 懒加载（默认）- 分类在首次访问时加载。
    /// 如果遍历并访问分类，可能产生 N+1 查询问题。
    public List<Product> findWithLazyCategory() {
        return query()
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 急加载 - 一次查询加载产品和分类。
    /// 需要关联数据时使用此方法避免 N+1 问题。
    public List<Product> findWithCategoryFetch() {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 带条件的急加载。
    public List<Product> findActiveInCategoryWithFetch(Long categoryId) {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 根据分类查询（简化方式）。
    public List<Product> findByCategoryNameSimple(Long categoryId, String categoryName) {
        return query()
                .fetch(Product::getCategory)
                .where(Product::getCategoryId).eq(categoryId)
                .where(Product::getActive).eq(true)
                .list();
    }

    /// DTO 投影与关联数据。
    /// 一次查询获取产品名称、价格和分类名称。
    public List<ProductWithCategory> findProductWithCategoryInfo() {
        return query()
                .select(ProductWithCategory.class)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 使用 Stream 手动关联 - 用于复杂转换。
    public List<ProductCategoryInfo> findProductCategoryInfo() {
        List<Product> products = query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .list();

        return products.stream()
                .map(p -> new ProductCategoryInfo(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getCategory() != null ? p.getCategory().getName() : "未分类"
                ))
                .toList();
    }

    /// 三字段投影与嵌套路径 select。
    /// 一次查询获取产品名称、价格和分类名称。
    public List<Tuple3<String, BigDecimal, String>> findProductNamePriceCategoryName() {
        return query()
                .select(
                        Product::getName,
                        Product::getPrice,
                        path(Product::getCategory).get(Category::getName)
                )
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 两字段投影与嵌套路径 select。
    public List<Tuple2<String, String>> findProductNameAndCategoryName() {
        return query()
                .select(
                        Product::getName,
                        path(Product::getCategory).get(Category::getName)
                )
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 使用 Stream 按分类分组产品。
    public Map<Long, List<Product>> groupProductsByCategory() {
        List<Product> products = query()
                .fetch(Product::getCategory)
                .where(Product::getActive).eq(true)
                .list();

        return products.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getCategoryId() != null ? p.getCategoryId() : -1L
                ));
    }

    /// 将产品转移到其他分类。
    @Transactional
    public void transferToCategory(List<Long> productIds, Long newCategoryId) {
        List<Product> products = findAllById(productIds);
        products.forEach(p -> p.setCategoryId(newCategoryId));
        updateAll(products);
    }

    // ==================== 基本查询方法 ====================

    /// 根据 SKU 编码查找产品。
    public Product findBySku(String sku) {
        return query().where(Product::getSku).eq(sku).first();
    }

    /// 根据名称包含文本查找产品。
    public List<Product> findByNameContaining(String name) {
        return query()
                .where(Product::getName).contains(name)
                .where(Product::getActive).eq(true)
                .list();
    }

    /// 根据价格区间查找产品。
    public List<Product> findByPriceBetween(BigDecimal min, BigDecimal max) {
        return query()
                .where(Product::getPrice).between(min, max)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getPrice).asc()
                .list();
    }

    /// 查找低库存产品。
    public List<Product> findLowStock(int threshold) {
        return query()
                .where(Product::getStock).lt(threshold)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getStock).asc()
                .list();
    }

    /// 分页查询活跃产品。
    public Slice<Product> findActiveProductsPaged(int page, int size) {
        return query()
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .slice(page * size, size);
    }

    /// 获取产品名称和价格对。
    public List<Tuple2<String, BigDecimal>> findProductNamePrices() {
        return query()
                .select(Product::getName, Product::getPrice)
                .where(Product::getActive).eq(true)
                .orderBy(Product::getName).asc()
                .list();
    }

    /// 计算库存总价值。
    public BigDecimal calculateTotalStockValue() {
        List<Product> products = query()
                .where(Product::getActive).eq(true)
                .where(Product::getPrice).isNotNull()
                .list();
        return products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /// 更新产品价格。
    @Transactional
    public void updatePrice(Long id, BigDecimal newPrice) {
        Product product = getById(id);
        if (product != null) {
            product.setPrice(newPrice);
            update(product);
        }
    }

    /// 更新库存数量。
    @Transactional
    public void updateStock(Long id, Integer newStock) {
        Product product = getById(id);
        if (product != null) {
            product.setStock(newStock);
            update(product);
        }
    }

    /// 根据 ID 下架产品（使用继承的 getById）。
    @Transactional
    public void deactivateProduct(Long id) {
        Product product = getById(id);
        if (product != null) {
            product.setActive(false);
            update(product);
        }
    }

    /// 根据多个 ID 上架产品（使用继承的 findAllById）。
    @Transactional
    public void activateProducts(Collection<Long> ids) {
        List<Product> products = findAllById(ids);
        products.forEach(p -> p.setActive(true));
        updateAll(products);
    }

    /// 按多个 ID 查询并返回 Map（使用继承的 findMapById）。
    public Map<Long, Product> findProductsAsMap(Collection<Long> ids) {
        return findMapById(ids);
    }

    /// 检查产品是否存在且活跃。
    public boolean existsAndActive(Long id) {
        Product product = getById(id);
        return product != null && Boolean.TRUE.equals(product.getActive());
    }

    // ==================== DTO 类 ====================

    /// 产品与分类名称 DTO。
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

    /// 产品分类信息 DTO（更多字段）。
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