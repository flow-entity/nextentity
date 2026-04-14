package io.github.nextentity.core.annotation;

import java.lang.annotation.*;

/// 标记 EntityReference 字段的 ID 来源。
///
/// 用于指定 ID 值从哪个字段获取：
/// - 默认自动推断：字段名 + "Id"（如 user -> userId）
/// - 显式指定：通过 value 或 path 配置
///
/// ## ID 来源场景
///
/// | 场景 | 配置 | 说明 |
/// |------|------|------|
/// | 直接关联（自动推断） | 无需注解 | userId 字段自动匹配 |
/// | 未关联但有 ID 字段 | `@ReferenceId("managerId")` | 指定 ID 字段名 |
/// | 嵌套关联 | `@ReferenceId(path = "user.id")` | 从关联实体获取 ID |
///
/// ## 使用示例
/// ```java
/// public class OrderRef {
///     // 自动推断：从 order.userId 获取
///     private UserRef user;
///
///     // 显式指定：从 order.managerId 获取（非关联字段）
///     @ReferenceId("managerId")
///     private EmployeeRef manager;
///
///     // 嵌套路径：从 order.user.department.id 获取
///     @ReferenceId(path = "user.department.id")
///     private DepartmentRef department;
/// }
/// ```
///
/// @author HuangChengwei
/// @since 2.2.0
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReferenceId {

    /// ID 字段名。
    ///
    /// 指定存储 ID 的字段名，如 "managerId"。
    /// 如果为空，使用自动推断或 path 配置。
    ///
    /// @return ID 字段名
    String value() default "";

    /// 嵌套路径表达式。
    ///
    /// 用于从嵌套关联获取 ID，如 "user.department.id"。
    /// 点分隔的路径，从源实体开始遍历。
    ///
    /// @return 嵌套路径
    String path() default "";

    /// 计算表达式（预留）。
    ///
    /// 用于复杂 ID 计算场景，如表达式语言。
    /// 当前版本未实现，后续扩展。
    ///
    /// @return 表达式
    String expression() default "";
}