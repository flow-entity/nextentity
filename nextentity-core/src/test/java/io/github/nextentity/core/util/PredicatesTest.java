package io.github.nextentity.core.util;

import io.github.nextentity.api.Predicate;
import io.github.nextentity.core.expression.ExpressionNodes;
import io.github.nextentity.core.expression.Expressions;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Predicates utility class provides predicate operations
 * <p>
 * Test scenarios:
 * 1. of() creates Predicate from TypedExpression
 * 2. and() combines predicates with AND operator
 * 3. or() combines predicates with OR operator
 * 4. not() negates a predicate
 * <p>
 * Expected result: Predicates can be combined and negated correctly
 */
class PredicatesTest {

    @Nested
    class OfMethod {

        /**
         * Test objective: Verify of() creates Predicate wrapping the expression
         * Test scenario: Create predicate from true expression
         * Expected result: Predicate with TRUE literal node
         */
        @Test
        void of_WithTrueExpression_ShouldCreatePredicateWithTrueNode() {
            // given
            var trueExpr = Expressions.ofTrue();

            // when
            Predicate<Object> predicate = Predicates.of(trueExpr);

            // then
            assertThat(predicate).isNotNull();
            assertThat(ExpressionNodes.getNode(predicate)).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify of() creates Predicate from false expression
         * Test scenario: Create predicate from false expression
         * Expected result: Predicate with FALSE literal node
         */
        @Test
        void of_WithFalseExpression_ShouldCreatePredicateWithFalseNode() {
            // given
            var falseExpr = Expressions.ofFalse();

            // when
            Predicate<Object> predicate = Predicates.of(falseExpr);

            // then
            assertThat(predicate).isNotNull();
            assertThat(ExpressionNodes.getNode(predicate)).isSameAs(LiteralNode.FALSE);
        }
    }

    @Nested
    class AndMethod {

        /**
         * Test objective: Verify and() combines predicates with AND operator
         * Test scenario: Combine false and true predicates
         * Expected result: FALSE (boolean algebra optimization: FALSE AND anything = FALSE)
         */
        @Test
        void and_WithFalseAndTrue_ShouldReturnFalse() {
            // given
            var falsePred = Expressions.ofFalse();
            var truePred = Expressions.ofTrue();

            // when
            Predicate<Object> result = Predicates.and(falsePred, truePred);

            // then
            assertThat(result).isNotNull();
            // AND with FALSE results in FALSE (boolean algebra optimization)
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.FALSE);
        }

        /**
         * Test objective: Verify and() with two false predicates
         * Test scenario: Combine two false predicates
         * Expected result: FALSE (optimization)
         */
        @Test
        void and_WithTwoFalsePredicates_ShouldReturnFalse() {
            // given
            var falsePred1 = Expressions.ofFalse();
            var falsePred2 = Expressions.ofFalse();

            // when
            Predicate<Object> result = Predicates.and(falsePred1, falsePred2);

            // then
            assertThat(result).isNotNull();
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.FALSE);
        }
    }

    @Nested
    class OrMethod {

        /**
         * Test objective: Verify or() combines predicates with OR operator
         * Test scenario: Combine true and false predicates
         * Expected result: TRUE (boolean algebra optimization: TRUE OR anything = TRUE)
         */
        @Test
        void or_WithTrueAndFalse_ShouldReturnTrue() {
            // given
            var truePred = Expressions.ofTrue();
            var falsePred = Expressions.ofFalse();

            // when
            Predicate<Object> result = Predicates.or(truePred, falsePred);

            // then
            assertThat(result).isNotNull();
            // OR with TRUE results in TRUE (boolean algebra optimization)
            assertThat(ExpressionNodes.getNode(result)).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify or() with two false predicates creates an operator node
         * Test scenario: Combine two false predicates
         * Expected result: OperatorNode is created (no optimization for false OR false)
         */
        @Test
        void or_WithTwoFalsePredicates_ShouldCreateOperatorNode() {
            // given
            var falsePred1 = Expressions.ofFalse();
            var falsePred2 = Expressions.ofFalse();

            // when
            Predicate<Object> result = Predicates.or(falsePred1, falsePred2);

            // then
            assertThat(result).isNotNull();
            var node = ExpressionNodes.getNode(result);
            // OR on FALSE creates an operator node with the operands
            assertThat(node).isInstanceOf(OperatorNode.class);
        }
    }

    @Nested
    class NotMethod {

        /**
         * Test objective: Verify not() creates NOT operator node
         * Test scenario: Negate a true predicate
         * Expected result: OperatorNode with NOT operator wrapping TRUE
         */
        @Test
        void not_WithTruePredicate_ShouldCreateNotOperatorNode() {
            // given
            var truePred = Expressions.ofTrue();

            // when
            Predicate<Object> result = Predicates.not(truePred);

            // then
            assertThat(result).isNotNull();
            var node = ExpressionNodes.getNode(result);
            assertThat(node).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) node;
            assertThat(opNode.operator()).isEqualTo(io.github.nextentity.core.expression.Operator.NOT);
            assertThat(opNode.firstOperand()).isSameAs(LiteralNode.TRUE);
        }

        /**
         * Test objective: Verify not() with false predicate
         * Test scenario: Negate a false predicate
         * Expected result: OperatorNode with NOT operator wrapping FALSE
         */
        @Test
        void not_WithFalsePredicate_ShouldCreateNotOperatorNode() {
            // given
            var falsePred = Expressions.ofFalse();

            // when
            Predicate<Object> result = Predicates.not(falsePred);

            // then
            assertThat(result).isNotNull();
            var node = ExpressionNodes.getNode(result);
            assertThat(node).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) node;
            assertThat(opNode.operator()).isEqualTo(io.github.nextentity.core.expression.Operator.NOT);
            assertThat(opNode.firstOperand()).isSameAs(LiteralNode.FALSE);
        }
    }
}