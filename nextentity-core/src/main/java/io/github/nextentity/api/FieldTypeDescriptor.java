package io.github.nextentity.api;

/// 字段类型描述符，描述投影字段的元数据信息。
///
/// 用于在 ProjectionFieldHandler 中传递字段的类型信息，
/// 包括引用的实体类型、ID 类型、ID 来源路径等。
///
/// @author HuangChengwei
/// @since 2.2.0
public interface FieldTypeDescriptor {

    /// 获取投影字段的 Java 类型。
    ///
    /// @return 字段类型（如 EntityReference<User>）
    Class<?> fieldType();

    /// 获取引用的目标实体类型。
    ///
    /// 对于 EntityReference<User>，返回 User.class。
    ///
    /// @return 目标实体类型
    Class<?> targetType();

    /// 获取实体 ID 的类型。
    ///
    /// @return ID 类型（如 Long.class）
    Class<?> idType();

    /// 获取 ID 来源路径。
    ///
    /// 路径格式：
    /// - 空字符串：自动推断（默认使用字段名 + "Id"，如 userId）
    /// - "managerId"：指定 ID 字段名
    /// - "user.id"：嵌套路径（从关联实体获取 ID）
    ///
    /// @return ID 来源路径表达式
    String idSourcePath();

    /// 是否是嵌套 EntityReference。
    ///
    /// 嵌套指投影字段本身是 EntityReference，
    /// 且其目标实体中还有 EntityReference 字段。
    ///
    /// @return 如果是嵌套返回 true
    boolean isNested();

    /// 是否需要 JOIN 查询。
    ///
    /// 当 ID 来源需要通过关联实体获取时返回 true。
    ///
    /// @return 如果需要 JOIN 返回 true
    boolean requiresJoin();

    /// 创建字段类型描述符的构建器。
    ///
    /// @return 构建器实例
    static FieldTypeDescriptorBuilder builder() {
        return new FieldTypeDescriptorBuilder();
    }
}