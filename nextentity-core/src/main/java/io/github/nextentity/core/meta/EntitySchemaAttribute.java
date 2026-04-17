package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;

public non-sealed interface EntitySchemaAttribute extends EntityAttribute, SchemaAttribute {

    /// 获取此关联的连接表名。
    ///
    /// 对于多对多关系，返回中间连接表。
    /// 对于其他关系，返回目标实体的表名。
    ///
    /// @return 连接表名
    EntitySchema target();

    /// 获取声明实体中此连接属性的名称。
    ///
    /// @return 连接属性名
    EntityBasicAttribute sourceAttribute();

    /// 获取引用目标实体外键列名。
    ///
    /// @return 引用列名
    EntityBasicAttribute targetAttribute();

}
