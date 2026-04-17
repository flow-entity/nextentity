package io.github.nextentity.core.annotation;

import jakarta.persistence.FetchType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 指定实体或投影属性的加载策略。
///
/// 支持两种加载策略：
/// - `LAZY`: 延迟加载，属性值在首次访问时通过代理加载
/// - `EAGER`: 立即加载，属性值在查询时直接获取
///
/// 加载策略优先级规则：
/// ```
/// 投影级 @Fetch > 实体级 @Fetch > 全局默认配置
/// ```
///
/// 示例用法：
/// ```java
/// @Entity
/// public class Employee {
///     @ManyToOne
///     @Fetch(LAZY)      // 延迟加载部门
///     private Department department;
///
///     @ManyToOne
///     @Fetch(EAGER)     // 立即加载公司
///     private Company company;
///
///     @ManyToOne        // 使用全局默认配置
///     private User user;
/// }
///
/// // 投影中可覆盖实体配置
/// public class EmployeeProjection {
///     @EntityPath("department")
///     @Fetch(EAGER)     // 覆盖实体的 LAZY 配置
///     private Department department;
/// }
/// ```
///
/// @see jakarta.persistence.FetchType
/// @see io.github.nextentity.core.meta.EntitySchemaAttribute
/// @see io.github.nextentity.core.meta.ProjectionSchemaAttribute
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Fetch {

    /// 加载策略类型。
    ///
    /// 必须明确指定 LAZY 或 EAGER，无默认值。
    /// 未标注 @Fetch 时，使用实体级配置或全局默认配置。
    ///
    /// @return 加载策略（LAZY 或 EAGER）
    FetchType value();

}