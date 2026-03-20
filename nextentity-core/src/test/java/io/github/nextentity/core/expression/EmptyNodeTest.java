package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify EmptyNode behaves correctly as empty expression
 * <p>
 * Test scenarios:
 * 1. Singleton instance
 * 2. operate with empty nodes returns itself
 * 3. operate with nodes returns OperatorNode
 */
class EmptyNodeTest {

    @Nested
    class SingletonInstance {

        /**
         * Test objective: Verify INSTANCE is singleton
         * Test scenario: Get INSTANCE multiple times
         * Expected result: Same instance
         */
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

        /**
         * Test objective: Verify operate with empty nodes returns EmptyNode
         * Test scenario: Call operate with empty collection
         * Expected result: Returns EmptyNode.INSTANCE
         */
        @Test
        void operate_EmptyNodes_ReturnsEmptyNode() {
            // given
            EmptyNode emptyNode = EmptyNode.INSTANCE;

            // when
            ExpressionNode result = emptyNode.operate(Operator.EQ, Collections.emptyList());

            // then
            assertThat(result).isSameAs(EmptyNode.INSTANCE);
        }

        /**
         * Test objective: Verify operate with nodes returns OperatorNode
         * Test scenario: Call operate with non-empty collection
         * Expected result: Returns OperatorNode
         */
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

        /**
         * Test objective: Verify operate with multiple nodes
         * Test scenario: Call operate with multiple nodes
         * Expected result: Returns OperatorNode with all nodes
         */
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
