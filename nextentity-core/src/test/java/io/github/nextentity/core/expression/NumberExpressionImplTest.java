package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify NumberExpressionImpl correctly implements numeric operations
 * <p>
 * Test scenarios:
 * 1. Arithmetic operations (add, subtract, multiply, divide, mod)
 * 2. Aggregate functions (sum, avg, max, min)
 */
class NumberExpressionImplTest {

    @Nested
    class ArithmeticOperations {

        /**
         * Test objective: Verify add() creates ADD operator
         * Test scenario: Call add() on expression
         * Expected result: Expression with ADD operator
         */
        @Test
        void add_CreatesAddOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expressions.of(10);

            // when
            var result = expr.add(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.ADD);
        }

        /**
         * Test objective: Verify subtract() creates SUBTRACT operator
         * Test scenario: Call subtract() on expression
         * Expected result: Expression with SUBTRACT operator
         */
        @Test
        void subtract_CreatesSubtractOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expressions.of(5);

            // when
            var result = expr.subtract(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUBTRACT);
        }

        /**
         * Test objective: Verify multiply() creates MULTIPLY operator
         * Test scenario: Call multiply() on expression
         * Expected result: Expression with MULTIPLY operator
         */
        @Test
        void multiply_CreatesMultiplyOperator() {
            // given
            PathNode pathNode = new PathNode("price");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expressions.of(2);

            // when
            var result = expr.multiply(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MULTIPLY);
        }

        /**
         * Test objective: Verify divide() creates DIVIDE operator
         * Test scenario: Call divide() on expression
         * Expected result: Expression with DIVIDE operator
         */
        @Test
        void divide_CreatesDivideOperator() {
            // given
            PathNode pathNode = new PathNode("total");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expressions.of(10);

            // when
            var result = expr.divide(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.DIVIDE);
        }

        /**
         * Test objective: Verify mod() creates MOD operator
         * Test scenario: Call mod() on expression
         * Expected result: Expression with MOD operator
         */
        @Test
        void mod_CreatesModOperator() {
            // given
            PathNode pathNode = new PathNode("value");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expressions.of(3);

            // when
            var result = expr.mod(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MOD);
        }
    }

    @Nested
    class AggregateFunctions {

        /**
         * Test objective: Verify sum() creates SUM operator
         * Test scenario: Call sum() on expression
         * Expected result: Expression with SUM operator
         */
        @Test
        void sum_CreatesSumOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.sum();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUM);
        }

        /**
         * Test objective: Verify avg() creates AVG operator
         * Test scenario: Call avg() on expression
         * Expected result: Expression with AVG operator
         */
        @Test
        void avg_CreatesAvgOperator() {
            // given
            PathNode pathNode = new PathNode("price");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.avg();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.AVG);
        }

        /**
         * Test objective: Verify max() creates MAX operator
         * Test scenario: Call max() on expression
         * Expected result: Expression with MAX operator
         */
        @Test
        void max_CreatesMaxOperator() {
            // given
            PathNode pathNode = new PathNode("score");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.max();

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
            PathNode pathNode = new PathNode("score");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.min();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MIN);
        }
    }
}
