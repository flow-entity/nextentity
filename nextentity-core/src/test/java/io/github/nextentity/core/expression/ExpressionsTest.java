package io.github.nextentity.core.expression;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.api.TypedExpression;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Expressions utility class
 * <p>
 * Test scenarios:
 * 1. Create literal expression
 * 2. Create true predicate
 * 3. Create false predicate
 */
class ExpressionsTest {

    @Nested
    class OfValue {

        /**
         * Test objective: Verify of() creates literal expression
         * Test scenario: Create expression with value
         * Expected result: TypedExpression containing the value
         */
        @Test
        void of_WithValue_ReturnsTypedExpression() {
            // given
            String value = "test";

            // when
            TypedExpression<?, String> expression = TypedExpression.of(value);

            // then
            assertThat(expression).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(expression);
            assertThat(node).isInstanceOf(LiteralNode.class);
            LiteralNode literalNode = (LiteralNode) node;
            assertThat(literalNode.value()).isEqualTo(value);
        }

        /**
         * Test objective: Verify of() with null value
         * Test scenario: Create expression with null
         * Expected result: TypedExpression with null value
         */
        @Test
        void of_WithNull_ReturnsTypedExpressionWithNull() {
            // when
            TypedExpression<?, Object> expression = TypedExpression.of(null);

            // then
            assertThat(expression).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(expression);
            assertThat(node).isInstanceOf(LiteralNode.class);
            LiteralNode literalNode = (LiteralNode) node;
            assertThat(literalNode.value()).isNull();
        }
    }

    @Nested
    class OfTrueFalse {

        /**
         * Test objective: Verify ofTrue() creates true predicate
         * Test scenario: Call ofTrue()
         * Expected result: Predicate with TRUE literal
         */
        @Test
        void ofTrue_ReturnsTruePredicate() {
            // when
            Predicate<Object> predicate = Predicate.ofTrue();

            // then
            assertThat(predicate).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(predicate);
            assertThat(node).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify ofFalse() creates false predicate
         * Test scenario: Call ofFalse()
         * Expected result: Predicate with FALSE literal
         */
        @Test
        void ofFalse_ReturnsFalsePredicate() {
            // when
            Predicate<Object> predicate = Predicate.ofFalse();

            // then
            assertThat(predicate).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(predicate);
            assertThat(node).isSameAs(LiteralNode.FALSE);
        }
    }

}
