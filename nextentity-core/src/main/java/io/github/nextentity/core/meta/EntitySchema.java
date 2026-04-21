package io.github.nextentity.core.meta;

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

    String entityName();

}
