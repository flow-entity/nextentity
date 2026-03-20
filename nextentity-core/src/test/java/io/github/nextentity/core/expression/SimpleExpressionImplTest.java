package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SimpleExpressionImpl correctly implements expression operations
 * <p>
 * Test scenarios:
 * 1. Root method
 * 2. Count operations
 * 3. Min/Max operations
 * 4. getRoot returns correct node
 */
class SimpleExpressionImplTest {

    @Nested
    class GetRoot {

        /**
         * Test objective: Verify getRoot returns the expression node
         * Test scenario: Create SimpleExpressionImpl and get root
         * Expected result: Returns the node passed in constructor
         */
        @Test
        void getRoot_ReturnsNode() {
            // given
            LiteralNode node = new LiteralNode("test");
            SimpleExpressionImpl<Object, String> expression = new SimpleExpressionImpl<>(node);

            // when
            ExpressionNode root = expression.getRoot();

            // then
            assertThat(root).isSameAs(node);
        }
    }

    @Nested
    class CountOperations {

        /**
         * Test objective: Verify count() creates COUNT operator
         * Test scenario: Call count() on expression
         * Expected result: Expression with COUNT operator
         */
        @Test
        void count_CreatesCountOperator() {
            // given
            PathNode pathNode = new PathNode("field");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.count();

            // then - cast to ExpressionTree to get root
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.COUNT);
        }
    }

    @Nested
    class MinMaxOperations {

        /**
         * Test objective: Verify max() creates MAX operator
         * Test scenario: Call max() on expression
         * Expected result: Expression with MAX operator
         */
        @Test
        void max_CreatesMaxOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.max();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MAX);
        }

        /**
         * Test objective: Verify min() creates MIN operator
         * Test scenario: Call min() on expression
         * Expected result: Expression with MIN operator
         */
        @Test
        void min_CreatesMinOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            SimpleExpressionImpl<Object, Object> expression = new SimpleExpressionImpl<>(pathNode);

            // when
            var result = expression.min();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MIN);
        }
    }

    @Nested
    class RootMethod {

        /**
         * Test objective: Verify root() returns EntityRoot
         * Test scenario: Call root() on expression
         * Expected result: Non-null EntityRoot
         */
        @Test
        void root_ReturnsEntityRoot() {
            // given
            LiteralNode node = new LiteralNode("test");
            SimpleExpressionImpl<Object, String> expression = new SimpleExpressionImpl<>(node);

            // when
            var result = expression.root();

            // then
            assertThat(result).isNotNull();
        }
    }
}
