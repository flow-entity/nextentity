package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.meta.*;

/// TODO 添加注释
///
/// @author HuangChengwei
/// @since 2.2.2
public interface Column {

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

    record Joined(
            EntityBasicAttribute attribute,
            JoinAttribute join
    ) implements Column {

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
