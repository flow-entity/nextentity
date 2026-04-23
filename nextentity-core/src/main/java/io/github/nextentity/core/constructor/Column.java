package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.ProjectionBasicAttribute;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import io.github.nextentity.jdbc.Arguments;

/// SQL SELECT 列的抽象
///
/// Column 统一使用 ExpressionNode 作为列来源，支持：
/// - PathNode: 实体属性路径 → alias.columnName
/// - OperatorNode: 函数表达式 → COUNT(*), SUM(salary)
/// - LiteralNode: 常量 → 1, 'hello'
///
/// @param source     列来源（统一使用 ExpressionNode 接口）
/// @param converter  值转换器（从 ResultSet 获取值时使用）
/// @param tableIndex 所属表索引（仅对 PathNode 有效）
/// 0 = 主表，>0 = join 表索引
/// 对于 OperatorNode/LiteralNode，tableIndex = -1（无意义）
/// @author HuangChengwei
/// @since 2.2.2
public record Column(ExpressionNode source, ValueConverter<?, ?> converter, int tableIndex) {

    /// 创建 PathNode 列
    ///
    /// @param source     属性路径
    /// @param converter  值转换器
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column ofPath(PathNode source,
                                ValueConverter<?, ?> converter,
                                int tableIndex) {
        return new Column(source, converter, tableIndex);
    }

    /// 创建 OperatorNode 列（无表索引）
    ///
    /// @param source    运算符表达式
    /// @param converter 值转换器
    /// @return Column 实例
    public static Column ofOperator(OperatorNode source,
                                    ValueConverter<?, ?> converter) {
        return new Column(source, converter, -1);
    }

    /// 创建 LiteralNode 列（无表索引）
    ///
    /// @param source    常量值
    /// @param converter 值转换器
    /// @return Column 实例
    public static Column ofLiteral(LiteralNode source,
                                   ValueConverter<?, ?> converter) {
        return new Column(source, converter, -1);
    }

    /// 从 ExpressionNode 创建 Column（根据节点类型分发）
    ///
    /// @param expressionNode 表达式节点
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column ofExpressionNode(ExpressionNode expressionNode, int tableIndex) {
        return switch (expressionNode) {
            case PathNode pathNode -> ofPath(pathNode, null, tableIndex);
            case OperatorNode operatorNode -> ofOperator(operatorNode, null);
            case LiteralNode literalNode -> ofLiteral(literalNode, null);
            default -> throw new IllegalArgumentException(
                    "Unsupported ExpressionNode type: " + expressionNode.getClass().getName());
        };
    }

    /// 获取列来源
    @Override
    public ExpressionNode source() {
        return source;
    }

    /// 获取值转换器
    @Override
    public ValueConverter<?, ?> converter() {
        return converter;
    }

    /// 获取表索引
    @Override
    public int tableIndex() {
        return tableIndex;
    }

    /// 判断是否为属性路径列
    public boolean isPathNode() {
        return source instanceof PathNode;
    }

    /// 判断是否为运算符列
    public boolean isOperatorNode() {
        return source instanceof OperatorNode;
    }

    /// 从 Arguments 获取列值
    ///
    /// @param arguments 参数供应器
    /// @return 列值
    public Object getValue(Arguments arguments) {
        if (converter == null) {
            return arguments.next(IdentityValueConverter.of());
        }
        return arguments.next(converter);
    }

    // ==================== 静态工厂方法 ====================

    /// 从 EntityAttribute 创建 Column
    ///
    /// @param attribute 实体属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromEntityAttribute(EntityAttribute attribute, int tableIndex) {
        ValueConverter<?, ?> converter = getConverterFromAttribute(attribute);
        return new Column(attribute.path(), converter, tableIndex);
    }

    /// 从 ProjectionBasicAttribute 创建 Column
    ///
    /// @param attribute 投影基本属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromProjectionBasicAttribute(ProjectionBasicAttribute attribute, int tableIndex) {
        EntityBasicAttribute entityAttribute = attribute.getEntityAttribute();
        return fromEntityAttribute(entityAttribute, tableIndex);
    }

    /// 从实体属性获取值转换器
    ///
    /// @param attribute 实体属性
    /// @return 值转换器
    private static ValueConverter<?, ?> getConverterFromAttribute(EntityAttribute attribute) {
        if (attribute instanceof EntityBasicAttribute basicAttribute) {
            return basicAttribute.valueConvertor();
        }
        // EntitySchemaAttribute 没有值转换器
        return IdentityValueConverter.of(attribute.type());
    }
}