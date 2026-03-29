package io.github.nextentity.core.expression;

import io.github.nextentity.api.TypedExpression;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify ExpressionNodes utility class
 * <p>
 * Test scenarios:
 * 1. isNullOrTrue detection
 * 2. getNode extraction
 */
class ExpressionNodesTest {

    @Nested
    class IsNullOrTrue {

        /**
         * Test objective: Verify null is detected as null or true
         * Test scenario: Check null
         * Expected result: true
         */
        @Test
        void isNullOrTrue_Null_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue((TypedExpression<?, ?>) null);

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify EmptyNode is detected as null or true
         * Test scenario: Check EmptyNode
         * Expected result: true
         */
        @Test
        void isNullOrTrue_EmptyNode_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(EmptyNode.INSTANCE);

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify TRUE literal is detected as null or true
         * Test scenario: Check TRUE literal
         * Expected result: true
         */
        @Test
        void isNullOrTrue_TrueLiteral_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(LiteralNode.TRUE);

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify FALSE literal is not null or true
         * Test scenario: Check FALSE literal
         * Expected result: false
         */
        @Test
        void isNullOrTrue_FalseLiteral_ReturnsFalse() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(LiteralNode.FALSE);

            // then
            assertThat(result).isFalse();
        }

        /**
         * Test objective: Verify non-boolean literal is not null or true
         * Test scenario: Check string literal
         * Expected result: false
         */
        @Test
        void isNullOrTrue_StringLiteral_ReturnsFalse() {
            // given
            LiteralNode stringNode = new LiteralNode("test");

            // when
            boolean result = ExpressionNodes.isNullOrTrue(stringNode);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class GetNode {

        /**
         * Test objective: Verify getNode extracts node from expression
         * Test scenario: Get node from Predicate
         * Expected result: Correct node
         */
        @Test
        void getNode_FromExpression_ReturnsNode() {
            // given
            PredicateImpl<Object> predicate = new PredicateImpl<>(LiteralNode.TRUE);

            // when
            ExpressionNode node = ExpressionNodes.getNode(predicate);

            // then
            assertThat(node).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify getNode extracts node from literal expression
         * Test scenario: Get node from literal expression
         * Expected result: Correct node
         */
        @Test
        void getNode_FromLiteralExpression_ReturnsNode() {
            // given
            TypedExpression<?, String> expression = TypedExpression.of("test");

            // when
            ExpressionNode node = ExpressionNodes.getNode(expression);

            // then
            assertThat(node).isInstanceOf(LiteralNode.class);
            assertThat(((LiteralNode) node).value()).isEqualTo("test");
        }
    }
}
