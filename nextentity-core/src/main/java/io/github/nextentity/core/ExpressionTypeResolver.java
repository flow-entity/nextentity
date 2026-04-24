package io.github.nextentity.core;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.reflect.PrimitiveTypes;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.util.ImmutableList;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/// 解析表达式类型的工具类。
///
/// @author HuangChengwei
/// @since 1.0.0
public class ExpressionTypeResolver {

    private static final List<Class<? extends Number>> NUMBER_TYPES = ImmutableList.of(
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            BigInteger.class,
            Float.class,
            Double.class,
            BigDecimal.class
    );


    public static Class<?> getExpressionType(ExpressionNode expression, EntityType entityType) {
        if (expression instanceof PathNode) {
            return getColumnType((PathNode) expression, entityType);
        } else if (expression instanceof LiteralNode) {
            return getLiteralType((LiteralNode) expression);
        } else if (expression instanceof OperatorNode) {
            return getOperationType((OperatorNode) expression, entityType);
        } else if (expression instanceof QueryStructure) {
            return getSubQueryType((QueryStructure) expression, entityType);
        }
        return Object.class;
    }

    public static Class<?> getSubQueryType(QueryStructure subQuery, EntityType entityType) {
        Selected select = subQuery.select();
        if (select instanceof SelectEntity) {
            return ((FromEntity) subQuery.from()).type();
        } else if (select instanceof SelectProjection selectProjection) {
            return selectProjection.type();
        } else if (select instanceof SelectExpression selectExpression) {
            return ExpressionTypeResolver.getExpressionType(selectExpression.expression(), entityType);
        } else if (select instanceof SelectExpressions) {
            return Tuples.class;
        }
        throw new UnsupportedOperationException();
    }

    public static Class<?> getOperationType(OperatorNode expression, EntityType entityType) {
        Operator operator = expression.operator();
        // noinspection EnhancedSwitchMigration
        switch (operator) {
            case NOT:
            case AND:
            case OR:
            case GT:
            case EQ:
            case NE:
            case GE:
            case LT:
            case LE:
            case LIKE:
            case IS_NULL:
            case IS_NOT_NULL:
            case IN:
            case BETWEEN:
                return Boolean.class;
            case LOWER:
            case UPPER:
            case SUBSTRING:
            case TRIM:
                return String.class;
            case LENGTH:
            case COUNT:
                return Long.class;
            case ADD:
            case SUBTRACT:
            case MULTIPLY:
            case MOD:
            case SUM:
                return getNumberType(expression, entityType);
            case DIVIDE:
            case AVG:
                return Double.class;
            case NULLIF:
            case IF_NULL:
            case MIN:
            case MAX:
                return getFirstOperandType(expression, entityType);
        }
        return Object.class;
    }

    private static Class<?> getFirstOperandType(OperatorNode expression, EntityType entityType) {
        if (!expression.operands().isEmpty()) {
            return getExpressionType(expression.operands().getFirst(), entityType);
        }
        return Object.class;
    }

    private static Class<?> getNumberType(OperatorNode expression, EntityType entityType) {
        int index = -1;
        for (ExpressionNode operand : expression.operands()) {
            Class<?> type = getExpressionType(operand, entityType);
            if (type.isPrimitive()) {
                type = PrimitiveTypes.getWrapper(type);
            }
            int i = NUMBER_TYPES.indexOf(type);
            if (i < 0 || i == NUMBER_TYPES.size() - 1) {
                index = i;
                break;
            }
            index = Math.max(index, i);
        }
        if (index >= 0 && index < NUMBER_TYPES.size()) {
            return NUMBER_TYPES.get(index);
        }
        return Object.class;
    }

    public static Class<?> getLiteralType(LiteralNode expression) {
        return expression.value().getClass();
    }

    public static Class<?> getColumnType(PathNode column, EntityType entityType) {
        Attribute attribute = entityType.getAttribute(column);
        return attribute.type();
    }

}
