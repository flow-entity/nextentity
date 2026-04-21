package io.github.nextentity.core.meta;

/// 实体 Schema 接口，描述一个实体类映射到数据库表的完整元数据。
///
/// 提供表名、主键、乐观锁版本字段等核心信息，
/// 并通过继承 {@link MetamodelSchema} 支持属性的按名称查找和嵌套路径访问。
///
/// @see MetamodelSchema
/// @see EntityAttribute
public interface EntitySchema extends MetamodelSchema<EntityAttribute> {
    /// 获取此实体的主键（标识）属性。
    ///
    /// @return 标识属性
    EntityBasicAttribute id();

    /// 获取乐观锁的版本属性。
    ///
    /// 如果此实体不使用乐观锁，返回 null。
    ///
    /// @return 版本属性，如果不适用则返回 null
    EntityBasicAttribute version();

    /// 获取此实体的数据库表名。
    ///
    /// @return 表名
    String tableName();

    /// 获取此实体的实体名称。
    ///
    /// 通常与 JPA {@code @Entity(name="...")} 注解的 name 属性一致。
    ///
    /// @return 实体名称
    String entityName();

}
