package io.github.nextentity.core.constructor;

import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchemaAttribute;
import io.github.nextentity.core.meta.ProjectionAttribute;
import io.github.nextentity.core.meta.ProjectionBasicAttribute;
import io.github.nextentity.core.meta.ProjectionJoinAttribute;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
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
        if (expressionNode instanceof PathNode pathNode) {
            return ofPath(pathNode, null, tableIndex);
        } else if (expressionNode instanceof OperatorNode operatorNode) {
            return ofOperator(operatorNode, null);
        } else if (expressionNode instanceof LiteralNode literalNode) {
            return ofLiteral(literalNode, null);
        } else {
            throw new IllegalArgumentException("Unsupported ExpressionNode type: " + expressionNode.getClass().getName());
        }
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

    /// 判断是否为常量列
    public boolean isLiteralNode() {
        return source instanceof LiteralNode;
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

    /// 从 EntityBasicAttribute 创建 Column
    ///
    /// @param attribute 实体基本属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromEntityBasicAttribute(EntityBasicAttribute attribute, int tableIndex) {
        ValueConverter<?, ?> converter = attribute.valueConvertor();
        return new Column(attribute.path(), converter, tableIndex);
    }

    /// 从 EntitySchemaAttribute 创建 Column
    ///
    /// @param attribute 实体关联属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromEntitySchemaAttribute(EntitySchemaAttribute attribute, int tableIndex) {
        return new Column(attribute.path(), null, tableIndex);
    }

    /// 从 PathNode 和实体类型创建 Column
    ///
    /// @param entityType 实体类型
    /// @param pathNode 路径节点
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromPathNode(Class<?> entityType, PathNode pathNode, int tableIndex) {
        ValueConverter<?, ?> converter = getValueConverter(entityType, pathNode);
        return new Column(pathNode, converter, tableIndex);
    }

    /// 从 SelectItem 创建 Column
    ///
    /// @param entityType 实体类型
    /// @param selectItem 选择项
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromSelectItem(Class<?> entityType, SelectItem selectItem, int tableIndex) {
        // 使用 expression() 方法统一处理所有 SelectItem 类型
        ExpressionNode expression = selectItem.expression();

        if (expression instanceof PathNode pathNode) {
            return fromPathNode(entityType, pathNode, tableIndex);
        } else if (expression instanceof OperatorNode operatorNode) {
            return new Column(operatorNode, null, -1);
        } else if (expression instanceof LiteralNode literalNode) {
            return new Column(literalNode, null, -1);
        } else if (selectItem instanceof EntityAttribute entityAttribute) {
            // EntityAttribute 的 expression() 已经处理，但我们需要它的路径元数据
            return fromEntityAttribute(entityAttribute, tableIndex);
        } else {
            throw new IllegalArgumentException("Unsupported SelectItem expression type: " + expression.getClass().getName());
        }
    }

    /// 从 ProjectionAttribute 创建 Column
    ///
    /// @param attribute 投影属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromProjectionAttribute(ProjectionAttribute attribute, int tableIndex) {
        EntityAttribute entityAttribute = attribute.getEntityAttribute();
        ValueConverter<?, ?> converter = getConverterFromAttribute(entityAttribute);
        return new Column(entityAttribute.path(), converter, tableIndex);
    }

    /// 从 ProjectionBasicAttribute 创建 Column
    ///
    /// @param attribute 投影基本属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromProjectionBasicAttribute(ProjectionBasicAttribute attribute, int tableIndex) {
        EntityBasicAttribute entityAttribute = attribute.getEntityAttribute();
        ValueConverter<?, ?> converter = entityAttribute.valueConvertor();
        return new Column(entityAttribute.path(), converter, tableIndex);
    }

    /// 从 ProjectionSchemaAttribute 创建 Column
    ///
    /// @param attribute 投影关联属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromProjectionSchemaAttribute(ProjectionSchemaAttribute attribute, int tableIndex) {
        EntitySchemaAttribute entityAttribute = attribute.getEntityAttribute();
        return new Column(entityAttribute.path(), null, tableIndex);
    }

    /// 从 ProjectionJoinAttribute 创建 Column
    ///
    /// @param attribute 投影 JOIN 属性
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static Column fromProjectionJoinAttribute(ProjectionJoinAttribute attribute, int tableIndex) {
        // JOIN 属性通常复用源实体的转换器
        EntityAttribute entityAttribute = attribute.getEntityAttribute();
        ValueConverter<?, ?> converter = getConverterFromAttribute(entityAttribute);
        return new Column(entityAttribute.path(), converter, tableIndex);
    }

    /// 从 Path 创建 Column（通过方法引用）
    ///
    /// @param entityType 实体类型
    /// @param path Path 方法引用
    /// @param tableIndex 表索引
    /// @return Column 实例
    public static <T> Column fromPath(Class<T> entityType, io.github.nextentity.api.Path<T, ?> path, int tableIndex) {
        PathNode pathNode = PathNode.of(path);
        ValueConverter<?, ?> converter = getValueConverter(entityType, pathNode);
        return new Column(pathNode, converter, tableIndex);
    }

    /// 根据实体类型和路径获取值转换器
    ///
    /// @param entityType 实体类型
    /// @param pathNode 路径节点
    /// @return 值转换器，如果无法获取则返回 null
    private static ValueConverter<?, ?> getValueConverter(Class<?> entityType, PathNode pathNode) {
        // 这里可以根据 entityType 和 pathNode 从 Metamodel 获取属性元数据
        // 从而获取对应的 ValueConverter
        // 暂时返回 null，由调用方处理
        return null;
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
        return null;
    }
}