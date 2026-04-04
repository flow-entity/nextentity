package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

import java.util.Collection;

/// 表示空/无操作表达式的表达式节点。
///
/// 用作查询结构中空值或缺失表达式的占位符。
/// 当与其他表达式组合时，EmptyNode 通常被忽略或
/// 被组合表达式替换。
///
/// 这是一个单例，通过 {@link #INSTANCE} 提供单个实例。
///
/// @author HuangChengwei
/// @since 1.0.0
public final class EmptyNode implements ExpressionNode {

    /// EmptyNode 的单例实例。
    public static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode() {
    }

    /// 将运算符应用于此空节点和额外操作数。
    ///
    /// 如果没有提供额外操作数，返回此空节点。
    /// 否则，使用提供的操作数创建新的 OperatorNode。
    ///
    /// @param operator 要应用的运算符
    /// @param nodes 额外操作数节点
    /// @return 如果没有操作数则返回此空节点，否则返回新的 OperatorNode
    @Override
    public ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (nodes.isEmpty()) {
            return this;
        }
        return new OperatorNode(ImmutableList.ofCollection(nodes), operator);
    }
}
