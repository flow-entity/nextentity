package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;

/// 表示实体之间关联的连接属性接口。
///
/// 此接口同时扩展 {@link SchemaAttribute} 和 {@link EntitySchema}，
/// 提供关于实体关系的元数据，包括连接表详情和外键引用。
///
/// 连接属性用于构建 JOIN 查询和理解元模型中的实体关系。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface JoinAttribute extends SchemaAttribute, Attribute, EntitySchema {

    /// 获取此关联的连接表名。
    ///
    /// 对于多对多关系，返回中间连接表。
    /// 对于其他关系，返回目标实体的表名。
    ///
    /// @return 连接表名
    String tableName();

    /// 获取声明实体中此连接属性的名称。
    ///
    /// @return 连接属性名
    String joinName();

    /// 获取引用目标实体外键列名。
    ///
    /// @return 引用列名
    String referencedColumnName();

}
