package io.github.nextentity.core.annotation;

import java.lang.annotation.*;

/// 标记直接引用字段（非延迟加载）。
///
/// 用于 EntityReference 字段但希望立即加载的场景：
/// - 查询时直接 JOIN 加载完整实体
/// - 不需要延迟加载，减少后续查询
///
/// ## 与 EntityReference 的区别
///
/// | 特性 | EntityReference（默认） | @DirectReference |
/// |------|------------------------|------------------|
/// | 加载时机 | 延迟（调用 get()） | 立即（查询时） |
/// | 查询方式 | 只查 ID | JOIN 查完整实体 |
/// | 适用场景 | 可能不使用关联 | 大概率会使用关联 |
///
/// ## 使用示例
/// ```java
/// public class OrderRef {
///     // 延迟加载用户（可能不使用）
///     private UserRef user;
///
///     // 立即加载订单详情（大概率会使用）
///     @DirectReference
///     private OrderDetailRef detail;
///
///     // 指定 JOIN 路径
///     @DirectReference("items.product")
///     private ProductRef product;
/// }
/// ```
///
/// @author HuangChengwei
/// @since 2.2.0
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DirectReference {

    /// 关联路径。
    ///
    /// 指定 JOIN 的路径，如 "items.product"。
    /// 如果为空，使用字段名作为路径。
    ///
    /// @return 关联路径
    String value() default "";

    /// 是否强制立即加载。
    ///
    /// 即使路径可能为 null，仍然尝试 JOIN 加载。
    ///
    /// @return true 强制加载，false 按需加载
    boolean eager() default true;
}