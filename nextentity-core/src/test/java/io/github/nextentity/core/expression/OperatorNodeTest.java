package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify OperatorNode correctly represents operations
 * <p>
 * Test scenarios:
 * 1. Creation with operands and operator
 * 2. NOT operation optimization (double negation)
 * 3. Multivalued operator optimization
 * 4. Operand access methods
 */
class OperatorNodeTest {

    @Nested
    class Creation {

        /**
         * Test objective: Verify operands and operator are stored
         * Test scenario: Create OperatorNode with operands and operator
         * Expected result: All values are retrievable
         */
        @Test
        void constructor_StoresOperandsAndOperator() {
            // given
            ImmutableList<ExpressionNode> operands = ImmutableList.of(
                    new LiteralNode("a"),
                    new LiteralNode("b")
            );

            // when
            OperatorNode node = new OperatorNode(operands, Operator.EQ);

            // then
            assertThat(node.operands()).isEqualTo(operands);
            assertThat(node.operator()).isEqualTo(Operator.EQ);
        }
    }

    @Nested
    class NotOperationOptimization {

        /**
         * Test objective: Verify double NOT cancels out
         * Test scenario: Apply NOT to NOT operator node
         * Expected result: Returns inner operand
         */
        @Test
        void operate_NotOnNot_ReturnsInnerOperand() {
            // given
            LiteralNode inner = new LiteralNode("test");
            OperatorNode notNode = new OperatorNode(ImmutableList.of(inner), Operator.NOT);

            // when
            ExpressionNode result = notNode.operate(Operator.NOT);

            // then
            assertThat(result).isEqualTo(inner);
        }
    }

    @Nested
    class MultivaluedOperatorOptimization {

        /**
         * Test objective: Verify AND on AND merges operands
         * Test scenario: Apply AND to AND operator node
         * Expected result: Combined operands
         */
        @Test
        void operate_AndOnAnd_MergesOperands() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode andNode = new OperatorNode(ImmutableList.of(a, b), Operator.AND);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = andNode.operate(Operator.AND, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            assertThat(resultNode.operands()).hasSize(3);
            assertThat(resultNode.operator()).isEqualTo(Operator.AND);
        }

        /**
         * Test objective: Verify OR on OR merges operands
         * Test scenario: Apply OR to OR operator node
         * Expected result: Combined operands
         */
        @Test
        void operate_OrOnOr_MergesOperands() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode orNode = new OperatorNode(ImmutableList.of(a, b), Operator.OR);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = orNode.operate(Operator.OR, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            assertThat(resultNode.operands()).hasSize(3);
            assertThat(resultNode.operator()).isEqualTo(Operator.OR);
        }

        /**
         * Test objective: Verify non-multivalued operator does not merge
         * Test scenario: Apply EQ to EQ operator node
         * Expected result: Nested operator node
         */
        @Test
        void operate_EqOnEq_CreatesNestedNode() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode eqNode = new OperatorNode(ImmutableList.of(a, b), Operator.EQ);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = eqNode.operate(Operator.EQ, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            // Should create new node with original as first operand
            assertThat(resultNode.operands()).hasSize(2);
        }
    }

    @Nested
    class OperandAccess {

        /**
         * Test objective: Verify firstOperand() returns first operand
         * Test scenario: Call firstOperand() on node with multiple operands
         * Expected result: First operand
         */
        @Test
        void firstOperand_ReturnsFirstOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.firstOperand();

            // then
            assertThat(result).isEqualTo(first);
        }

        /**
         * Test objective: Verify secondOperand() returns second operand
         * Test scenario: Call secondOperand() on node with two operands
         * Expected result: Second operand
         */
        @Test
        void secondOperand_ReturnsSecondOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.secondOperand();

            // then
            assertThat(result).isEqualTo(second);
        }

        /**
         * Test objective: Verify secondOperand() returns null for single operand
         * Test scenario: Call secondOperand() on node with one operand
         * Expected result: null
         */
        @Test
        void secondOperand_SingleOperand_ReturnsNull() {
            // given
            LiteralNode only = new LiteralNode("only");
            OperatorNode node = new OperatorNode(ImmutableList.of(only), Operator.NOT);

            // when
            ExpressionNode result = node.secondOperand();

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify thirdOperand() returns third operand
         * Test scenario: Call thirdOperand() on node with three operands
         * Expected result: Third operand
         */
        @Test
        void thirdOperand_ReturnsThirdOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            LiteralNode third = new LiteralNode("third");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second, third), Operator.BETWEEN);

            // when
            ExpressionNode result = node.thirdOperand();

            // then
            assertThat(result).isEqualTo(third);
        }

        /**
         * Test objective: Verify thirdOperand() returns null for two operands
         * Test scenario: Call thirdOperand() on node with two operands
         * Expected result: null
         */
        @Test
        void thirdOperand_TwoOperands_ReturnsNull() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.thirdOperand();

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ExpressionMethod {

        /**
         * Test objective: Verify expression() returns self
         * Test scenario: Call expression() on OperatorNode
         * Expected result: Returns same instance
         */
        @Test
        void expression_ReturnsSelf() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode("a")),
                    Operator.NOT
            );

            // when
            ExpressionNode result = node.expression();

            // then
            assertThat(result).isSameAs(node);
        }
    }
}
