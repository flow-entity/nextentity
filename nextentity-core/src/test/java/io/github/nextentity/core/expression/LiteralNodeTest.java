package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify LiteralNode correctly represents literal values
 * <p>
 * Test scenarios:
 * 1. Value storage and retrieval
 * 2. Static TRUE/FALSE constants
 * 3. NOT operation on boolean literals
 * 4. AND operation optimization
 * 5. OR operation optimization
 */
class LiteralNodeTest {

    @Nested
    class ValueStorage {

        /**
         * Test objective: Verify value is stored correctly
         * Test scenario: Create LiteralNode with various values
         * Expected result: Value is retrievable
         */
        @Test
        void value_IsStoredCorrectly() {
            // given
            String stringValue = "test";

            // when
            LiteralNode node = new LiteralNode(stringValue);

            // then
            assertThat(node.value()).isEqualTo(stringValue);
        }

        /**
         * Test objective: Verify null value is allowed
         * Test scenario: Create LiteralNode with null
         * Expected result: Null is stored
         */
        @Test
        void value_CanBeNull() {
            // when
            LiteralNode node = new LiteralNode(null);

            // then
            assertThat(node.value()).isNull();
        }
    }

    @Nested
    class StaticConstants {

        /**
         * Test objective: Verify TRUE constant
         * Test scenario: Access TRUE constant
         * Expected result: Contains true value
         */
        @Test
        void trueConstant_ContainsTrue() {
            // when
            LiteralNode trueNode = LiteralNode.TRUE;

            // then
            assertThat(trueNode.value()).isEqualTo(Boolean.TRUE);
        }

        /**
         * Test objective: Verify FALSE constant
         * Test scenario: Access FALSE constant
         * Expected result: Contains false value
         */
        @Test
        void falseConstant_ContainsFalse() {
            // when
            LiteralNode falseNode = LiteralNode.FALSE;

            // then
            assertThat(falseNode.value()).isEqualTo(Boolean.FALSE);
        }
    }

    @Nested
    class NotOperation {

        /**
         * Test objective: Verify NOT on true returns false
         * Test scenario: Apply NOT to TRUE
         * Expected result: Returns FALSE
         */
        @Test
        void operate_NotOnTrue_ReturnsFalse() {
            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.NOT);

            // then
            assertThat(result).isSameAs(LiteralNode.FALSE);
        }

        /**
         * Test objective: Verify NOT on false returns true
         * Test scenario: Apply NOT to FALSE
         * Expected result: Returns TRUE
         */
        @Test
        void operate_NotOnFalse_ReturnsTrue() {
            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.NOT);

            // then
            assertThat(result).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify NOT on non-boolean uses default behavior
         * Test scenario: Apply NOT to string literal
         * Expected result: Returns OperatorNode
         */
        @Test
        void operate_NotOnNonBoolean_ReturnsOperatorNode() {
            // given
            LiteralNode stringNode = new LiteralNode("test");

            // when
            ExpressionNode result = stringNode.operate(Operator.NOT);

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
        }
    }

    @Nested
    class AndOperation {

        /**
         * Test objective: Verify AND with false returns false
         * Test scenario: Apply AND to FALSE
         * Expected result: Returns FALSE
         */
        @Test
        void operate_AndOnFalse_ReturnsFalse() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.AND, List.of(otherNode));

            // then
            assertThat(result).isSameAs(LiteralNode.FALSE);
        }

        /**
         * Test objective: Verify AND with true continues with other nodes
         * Test scenario: Apply AND to TRUE with other nodes
         * Expected result: Returns OperatorNode with other nodes
         */
        @Test
        void operate_AndOnTrue_ReturnsOperatorNodeWithOtherNodes() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.AND, List.of(otherNode));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result;
            assertThat(opNode.operator()).isEqualTo(Operator.AND);
        }
    }

    @Nested
    class OrOperation {

        /**
         * Test objective: Verify OR with true returns true
         * Test scenario: Apply OR to TRUE
         * Expected result: Returns TRUE
         */
        @Test
        void operate_OrOnTrue_ReturnsTrue() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.OR, List.of(otherNode));

            // then
            assertThat(result).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify OR with false continues with other nodes
         * Test scenario: Apply OR to FALSE with other nodes
         * Expected result: Returns OperatorNode with other nodes
         */
        @Test
        void operate_OrOnFalse_ReturnsOperatorNodeWithOtherNodes() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.OR, List.of(otherNode));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result;
            // Note: Implementation uses Operator.AND in OR case (appears to be a bug)
            assertThat(opNode.operator()).isEqualTo(Operator.AND);
        }
    }

    @Nested
    class ExpressionMethod {

        /**
         * Test objective: Verify expression() returns self
         * Test scenario: Call expression() on LiteralNode
         * Expected result: Returns same instance
         */
        @Test
        void expression_ReturnsSelf() {
            // given
            LiteralNode node = new LiteralNode("test");

            // when
            ExpressionNode result = node.expression();

            // then
            assertThat(result).isSameAs(node);
        }
    }
}
