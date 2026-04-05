package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标：验证EmptyNode作为空表达式的正确行为
/// <p>
/// 测试场景：
/// 1. 单例实例
/// 2. 与空节点操作返回自身
/// 3. 与节点操作返回OperatorNode
///
class EmptyNodeTest {

    @Nested
    class SingletonInstance {

        ///
        /// 测试目标：验证INSTANCE是单例
        /// 测试场景：多次获取INSTANCE
        /// 预期结果：相同实例
        ///
        @Test
        void instance_IsSingleton() {
            // when
            EmptyNode instance1 = EmptyNode.INSTANCE;
            EmptyNode instance2 = EmptyNode.INSTANCE;

            // then
            assertThat(instance1).isSameAs(instance2);
        }
    }

    @Nested
    class OperateMethod {

        ///
        /// 测试目标：验证与空节点操作返回EmptyNode
        /// 测试场景：用空集合调用操作
        /// 预期结果：返回EmptyNode.INSTANCE
        ///
        @Test
        void operate_EmptyNodes_ReturnsEmptyNode() {
            // given
            EmptyNode emptyNode = EmptyNode.INSTANCE;

            // when
            ExpressionNode result = emptyNode.operate(Operator.EQ, Collections.emptyList());

            // then
            assertThat(result).isSameAs(EmptyNode.INSTANCE);
        }

        ///
        /// 测试目标：验证与节点操作返回OperatorNode
        /// 测试场景：用非空集合调用操作
        /// 预期结果：返回OperatorNode
        ///
        @Test
        void operate_WithNodes_ReturnsOperatorNode() {
            // given
            EmptyNode emptyNode = EmptyNode.INSTANCE;
            LiteralNode literal = new LiteralNode("test");

            // when
            ExpressionNode result = emptyNode.operate(Operator.EQ, List.of(literal));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode operatorNode = (OperatorNode) result;
            assertThat(operatorNode.operator()).isEqualTo(Operator.EQ);
            assertThat(operatorNode.operands()).hasSize(1);
            assertThat(operatorNode.firstOperand()).isEqualTo(literal);
        }

        ///
        /// 测试目标：验证与多个节点操作
        /// 测试场景：用多个节点调用操作
        /// 预期结果：返回包含所有节点的OperatorNode
        ///
        @Test
        void operate_WithMultipleNodes_ReturnsOperatorNodeWithAllNodes() {
            // given
            EmptyNode emptyNode = EmptyNode.INSTANCE;
            LiteralNode literal1 = new LiteralNode("a");
            LiteralNode literal2 = new LiteralNode("b");

            // when
            ExpressionNode result = emptyNode.operate(Operator.AND, List.of(literal1, literal2));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode operatorNode = (OperatorNode) result;
            assertThat(operatorNode.operands()).hasSize(2);
        }
    }
}
