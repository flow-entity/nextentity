package io.github.nextentity.core.constructor;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.SelectExpression;
import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 单值构造器
///
/// 包含一个列，直接返回该列值。
/// 列来源可以是 PathNode、OperatorNode 或 LiteralNode。
///
/// @author HuangChengwei
/// @since 2.2.2
public record SingleValueConstructor(List<Column> columns) implements ValueConstructor {

    public SingleValueConstructor(Column columns) {
        this(List.of(columns));
    }

    @Override
    public Object construct(Arguments arguments) {
        return columns.getFirst().getValue(arguments);
    }

    /// 从 SelectExpression 创建单值构造器
    ///
    /// @param selectExpression 选择表达式
    /// @param tableIndex 表索引
    /// @return SingleValueConstructor 实例
    public static SingleValueConstructor fromSelectExpression(SelectExpression selectExpression, int tableIndex) {
        return fromExpressionNode(selectExpression.expression(), tableIndex);
    }

    /// 从 ExpressionNode 创建单值构造器
    ///
    /// @param expressionNode 表达式节点
    /// @param tableIndex 表索引
    /// @return SingleValueConstructor 实例
    public static SingleValueConstructor fromExpressionNode(ExpressionNode expressionNode, int tableIndex) {
        return new SingleValueConstructor(Column.ofExpressionNode(expressionNode, tableIndex));
    }
}