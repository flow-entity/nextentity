package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/// 表示表达式树中节点的密封接口。
///
/// ExpressionNode 是表达式系统的基础，表示查询构建中的所有类型表达式：
/// 路径、字面量、运算符和完整的查询结构。
///
/// 允许的子类型：
/// - {@link LiteralNode}：字面量值
/// - {@link PathNode}：实体属性路径
/// - {@link OperatorNode}：运算符表达式
/// - {@link EmptyNode}：空/无操作表达式
/// - {@link QueryStructure}：完整的查询结构
///
/// @author HuangChengwei
/// @since 1.0.0
public sealed interface ExpressionNode permits LiteralNode, PathNode, OperatorNode, EmptyNode, QueryStructure {

    /// 表达式节点操作的日志记录器。
    Logger log = LoggerFactory.getLogger(ExpressionNode.class);

    /// 对此表达式应用一元运算符。
    ///
    /// @param operator 要应用的运算符
    /// @return 应用运算符后的新表达式节点
    default ExpressionNode operate(Operator operator) {
        return operate(operator, Collections.emptyList());
    }

    /// 对此表达式和另一个节点应用二元运算符。
    ///
    /// @param operator 要应用的运算符
    /// @param node 右操作数
    /// @return 应用运算符后的新表达式节点
    default ExpressionNode operate(Operator operator, ExpressionNode node) {
        if(node instanceof EmptyNode) {
            return this;
        }
        return operate(operator, Collections.singleton(node));
    }

    /// 对此表达式和另外两个节点应用三元运算符。
    ///
    /// @param operator 要应用的运算符
    /// @param node0 第一个右操作数
    /// @param node1 第二个右操作数
    /// @return 应用运算符后的新表达式节点
    default ExpressionNode operate(Operator operator, ExpressionNode node0, ExpressionNode node1) {
        return operate(operator, List.of(node0, node1));
    }

    /// 对此表达式和节点数组应用运算符。
    ///
    /// @param operator 要应用的运算符
    /// @param nodes 操作数数组
    /// @return 应用运算符后的新表达式节点
    default ExpressionNode operate(Operator operator, ExpressionNode[] nodes) {
        return nodes.length == 0 ? operate(operator, Collections.emptyList()) : operate(operator, Arrays.asList(nodes));
    }

    /// 对此表达式和节点集合应用运算符。
    ///
    /// 创建包含此表达式和所有操作数的新 OperatorNode。
    /// 对 IN 运算符的空操作数进行特殊处理，返回 FALSE 表达式。
    ///
    /// @param operator 要应用的运算符
    /// @param nodes 操作数集合
    /// @return 应用运算符后的新表达式节点
    default ExpressionNode operate(Operator operator, Collection<ExpressionNode> nodes) {
        if (operator == Operator.IN && nodes.isEmpty()) {
            log.warn("Operator 'IN' with empty operands detected, returning FALSE expression");
            return LiteralNode.FALSE;
        }
        ImmutableList.Builder<ExpressionNode> builder;
        builder = new ImmutableList.Builder<>(nodes.size() + 1);
        builder.add(this);
        builder.addAll(nodes);
        return new OperatorNode(builder.build(), operator);
    }

}