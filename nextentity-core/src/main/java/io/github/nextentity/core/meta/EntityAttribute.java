package io.github.nextentity.core.meta;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.reflect.schema.Attribute;

/// 实体属性元数据接口，表示实体类中映射到数据库的字段或关联。
///
/// 这是元模型属性体系的核心接口，采用 sealed hierarchy 设计：
/// - {@link EntityBasicAttribute}：基本属性，直接映射到数据库列
/// - {@link EntitySchemaAttribute}：关联属性，引用另一个实体类型
///
/// 同时实现了 {@link Attribute}（反射属性）和 {@link SelectItem}（查询选择项），
/// 使实体属性既能参与反射操作，也能作为 SQL 查询的投影列。
///
/// @see EntityBasicAttribute
/// @see EntitySchemaAttribute
public sealed interface EntityAttribute extends Attribute, SelectItem
        permits EntityBasicAttribute, EntitySchemaAttribute {

    /// 获取此属性在查询表达式中的路径节点。
    ///
    /// 路径节点用于构建 WHERE 条件、ORDER BY 子句等查询表达式，
    /// 例如 `Employee::getName` 会被解析为对应的列路径。
    ///
    /// @return 属性路径节点
    @Override
    PathNode path();

    /// 获取此属性对应的查询表达式节点。
    ///
    /// 默认直接返回 {@link #path()} 的结果。
    ///
    /// @return 表达式节点
    @Override
    default ExpressionNode expression() {
        return path();
    }

    /// 获取声明此属性的实体类型元数据。
    ///
    /// @return 声明实体的 schema
    @Override
    EntitySchema declareBy();
}
