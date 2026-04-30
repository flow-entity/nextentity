package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.*;

/// 表示查询中的列，可以是表达式或连接的属性。
///
/// Column是一个密封接口，有两个实现：
/// - {@link SelectItem.Expr}：表示表达式列，包含表达式节点和值转换器
/// - {@link SelectItem.Joined}：表示连接的属性列，包含实体属性和连接信息
///
/// 这个接口用于在查询构建过程中表示不同类型的列，支持类型安全的列操作。
///
/// @author HuangChengwei
/// @since 2.2.2
public sealed interface SelectItem permits SelectItem.Expr, SelectItem.Joined {

    static SelectItem of(ExpressionNode expression, ValueConverter<?, ?> converter) {
        return new Expr(expression, converter);
    }

    /// 根据实体基本属性创建 SelectItem。
    ///
    /// 如果属性声明方是嵌入类型（{@code @Embedded}），嵌入字段与主表共享同一张表，
    /// 因此使用 {@link Expr} 而非 {@link Joined}，避免生成错误的 JOIN 子句。
    ///
    /// @param attribute 实体基本属性
    /// @return SelectItem 实例
    static SelectItem of(EntityBasicAttribute attribute) {
        EntitySchema schema = attribute.declareBy();
        if (schema instanceof EntitySchemaAttribute target && !target.schema().isEmbedded()) {
            return new Joined(attribute, target);
        } else {
            return new Expr(attribute.path(), attribute.valueConvertor());
        }

    }

    static SelectItem of(ProjectionBasicAttribute attribute) {
        ProjectionSchema projectionSchema = attribute.declareBy();
        EntityBasicAttribute basicAttribute = attribute.getEntityAttribute();
        if (projectionSchema instanceof JoinAttribute target) {
            return new Joined(basicAttribute, target);
        } else {
            return new Expr(basicAttribute.path(), basicAttribute.valueConvertor());
        }

    }

    static SelectItem ofLazy(JoinAttribute target) {
        EntityBasicAttribute targetAttribute = target.getTargetAttribute();
        return new Joined(targetAttribute, target);
    }

    ValueConverter<?, ?> converter();

    ExpressionNode source();

    record Expr(ExpressionNode source, ValueConverter<?, ?> converter) implements SelectItem {
    }

    record Joined(EntityBasicAttribute attribute, JoinAttribute join) implements SelectItem {

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
