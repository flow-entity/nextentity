package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.*;

/// 表示查询中的列，可以是表达式或连接的属性。
///
/// Column是一个密封接口，有两个实现：
/// - {@link Column.Expr}：表示表达式列，包含表达式节点和值转换器
/// - {@link Column.Joined}：表示连接的属性列，包含实体属性和连接信息
///
/// 这个接口用于在查询构建过程中表示不同类型的列，支持类型安全的列操作。
///
/// @author HuangChengwei
/// @since 2.2.2
public sealed interface Column permits Column.Expr, Column.Joined {

    static Column of(ExpressionNode expression, ValueConverter<?, ?> converter) {
        return new Expr(expression, converter);
    }

    static Column of(EntityBasicAttribute attribute) {
        EntitySchema schema = attribute.declareBy();
        if (schema instanceof EntitySchemaAttribute target) {
            return new Joined(attribute, target);
        } else {
            return new Expr(attribute.path(), attribute.valueConvertor());
        }

    }

    static Column of(ProjectionBasicAttribute attribute) {
        ProjectionSchema projectionSchema = attribute.declareBy();
        EntityBasicAttribute basicAttribute = attribute.getEntityAttribute();
        if (projectionSchema instanceof JoinAttribute target) {
            return new Joined(basicAttribute, target);
        } else {
            return new Expr(basicAttribute.path(), basicAttribute.valueConvertor());
        }

    }

    static Column ofLazy(JoinAttribute target) {
        EntityBasicAttribute targetAttribute = target.getTargetAttribute();
        return new Joined(targetAttribute, target);
    }

    ValueConverter<?, ?> converter();

    ExpressionNode source();

    record Expr(ExpressionNode source, ValueConverter<?, ?> converter) implements Column {
    }

    record Joined(EntityBasicAttribute attribute, JoinAttribute join) implements Column {

        @Override
        public ValueConverter<?, ?> converter() {
            return attribute.valueConvertor();
        }

        @Override
        public ExpressionNode source() {
            return attribute.path();
        }

    }

}
