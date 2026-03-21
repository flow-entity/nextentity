package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for NumberOperatorImpl.
 */
class NumberOperatorImplTest {

    private NumberOperatorImpl<Object, Double, String> operator;
    private ExpressionNode capturedNode;

    @BeforeEach
    void setUp() {
        PathNode pathNode = new PathNode("salary");
        capturedNode = null;
        Function<ExpressionNode, String> callback = node -> {
            capturedNode = node;
            return "result";
        };
        operator = new NumberOperatorImpl<>(pathNode, callback);
    }

    @Nested
    class ArithmeticOperationsWithValues {

        /**
         * Test objective: Verify add() creates ADD operator with value.
         * Test scenario: Call add(100.0) then eq() on number operator.
         * Expected result: Creates ADD operator node.
         */
        @Test
        void add_ShouldCreateAddOperator() {
            // when
            operator.add(100.0).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be ADD operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode addNode = (OperatorNode) opNode.operands().get(0);
            assertThat(addNode.operator()).isEqualTo(Operator.ADD);
        }

        /**
         * Test objective: Verify subtract() creates SUBTRACT operator with value.
         * Test scenario: Call subtract(50.0) then eq() on number operator.
         * Expected result: Creates SUBTRACT operator node.
         */
        @Test
        void subtract_ShouldCreateSubtractOperator() {
            // when
            operator.subtract(50.0).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode subNode = (OperatorNode) opNode.operands().get(0);
            assertThat(subNode.operator()).isEqualTo(Operator.SUBTRACT);
        }

        /**
         * Test objective: Verify multiply() creates MULTIPLY operator with value.
         * Test scenario: Call multiply(2.0) then eq() on number operator.
         * Expected result: Creates MULTIPLY operator node.
         */
        @Test
        void multiply_ShouldCreateMultiplyOperator() {
            // when
            operator.multiply(2.0).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode mulNode = (OperatorNode) opNode.operands().get(0);
            assertThat(mulNode.operator()).isEqualTo(Operator.MULTIPLY);
        }

        /**
         * Test objective: Verify divide() creates DIVIDE operator with value.
         * Test scenario: Call divide(2.0) then eq() on number operator.
         * Expected result: Creates DIVIDE operator node.
         */
        @Test
        void divide_ShouldCreateDivideOperator() {
            // when
            operator.divide(2.0).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode divNode = (OperatorNode) opNode.operands().get(0);
            assertThat(divNode.operator()).isEqualTo(Operator.DIVIDE);
        }

        /**
         * Test objective: Verify mod() creates MOD operator with value.
         * Test scenario: Call mod(10.0) then eq() on number operator.
         * Expected result: Creates MOD operator node.
         */
        @Test
        void mod_ShouldCreateModOperator() {
            // when
            operator.mod(10.0).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode modNode = (OperatorNode) opNode.operands().get(0);
            assertThat(modNode.operator()).isEqualTo(Operator.MOD);
        }
    }

    @Nested
    class ArithmeticOperationsWithExpressions {

        /**
         * Test objective: Verify add() with expression creates ADD operator.
         * Test scenario: Call add(expression) then eq() on number operator.
         * Expected result: Creates ADD operator node with expression operand.
         */
        @Test
        void add_WithExpression_ShouldCreateAddOperator() {
            // given
            LiteralNode exprNode = new LiteralNode(50.0);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);

            // when
            operator.add(expression).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode addNode = (OperatorNode) opNode.operands().get(0);
            assertThat(addNode.operator()).isEqualTo(Operator.ADD);
        }

        /**
         * Test objective: Verify subtract() with expression creates SUBTRACT operator.
         * Test scenario: Call subtract(expression) then eq() on number operator.
         * Expected result: Creates SUBTRACT operator node with expression operand.
         */
        @Test
        void subtract_WithExpression_ShouldCreateSubtractOperator() {
            // given
            LiteralNode exprNode = new LiteralNode(25.0);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);

            // when
            operator.subtract(expression).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode subNode = (OperatorNode) opNode.operands().get(0);
            assertThat(subNode.operator()).isEqualTo(Operator.SUBTRACT);
        }

        /**
         * Test objective: Verify multiply() with expression creates MULTIPLY operator.
         * Test scenario: Call multiply(expression) then eq() on number operator.
         * Expected result: Creates MULTIPLY operator node with expression operand.
         */
        @Test
        void multiply_WithExpression_ShouldCreateMultiplyOperator() {
            // given
            LiteralNode exprNode = new LiteralNode(1.5);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);

            // when
            operator.multiply(expression).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode mulNode = (OperatorNode) opNode.operands().get(0);
            assertThat(mulNode.operator()).isEqualTo(Operator.MULTIPLY);
        }

        /**
         * Test objective: Verify divide() with expression creates DIVIDE operator.
         * Test scenario: Call divide(expression) then eq() on number operator.
         * Expected result: Creates DIVIDE operator node with expression operand.
         */
        @Test
        void divide_WithExpression_ShouldCreateDivideOperator() {
            // given
            LiteralNode exprNode = new LiteralNode(2.0);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);

            // when
            operator.divide(expression).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode divNode = (OperatorNode) opNode.operands().get(0);
            assertThat(divNode.operator()).isEqualTo(Operator.DIVIDE);
        }

        /**
         * Test objective: Verify mod() with expression creates MOD operator.
         * Test scenario: Call mod(expression) then eq() on number operator.
         * Expected result: Creates MOD operator node with expression operand.
         */
        @Test
        void mod_WithExpression_ShouldCreateModOperator() {
            // given
            LiteralNode exprNode = new LiteralNode(10.0);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);

            // when
            operator.mod(expression).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode modNode = (OperatorNode) opNode.operands().get(0);
            assertThat(modNode.operator()).isEqualTo(Operator.MOD);
        }
    }

    @Nested
    class ReturnType {

        /**
         * Test objective: Verify arithmetic operations return NumberOperator.
         * Test scenario: Call add() and verify return type.
         * Expected result: Returns NumberOperatorImpl instance.
         */
        @Test
        void arithmeticOperations_ShouldReturnNumberOperator() {
            // when
            var result = operator.add(100.0);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify subtract returns NumberOperator.
         * Test scenario: Call subtract().
         * Expected result: Returns NumberOperatorImpl instance.
         */
        @Test
        void subtract_ShouldReturnNumberOperator() {
            // when
            var result = operator.subtract(50.0);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify multiply returns NumberOperator.
         * Test scenario: Call multiply().
         * Expected result: Returns NumberOperatorImpl instance.
         */
        @Test
        void multiply_ShouldReturnNumberOperator() {
            // when
            var result = operator.multiply(2.0);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify divide returns NumberOperator.
         * Test scenario: Call divide().
         * Expected result: Returns NumberOperatorImpl instance.
         */
        @Test
        void divide_ShouldReturnNumberOperator() {
            // when
            var result = operator.divide(2.0);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify mod returns NumberOperator.
         * Test scenario: Call mod().
         * Expected result: Returns NumberOperatorImpl instance.
         */
        @Test
        void mod_ShouldReturnNumberOperator() {
            // when
            var result = operator.mod(10.0);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }
    }
}
