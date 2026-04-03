package io.github.nextentity.core.expression;

import io.github.nextentity.api.ExpressionBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/// NumberOperatorImpl的单元测试。
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

///
         /// 验证ies that arithmetic 操作s create correct operator nodes.
         /// 测试s add, subtract, multiply, divide, and mod 操作s with values.
        @ParameterizedTest
        @CsvSource({
            "ADD, 100.0",
            "SUBTRACT, 50.0",
            "MULTIPLY, 2.0",
            "DIVIDE, 2.0",
            "MOD, 10.0"
        })
        void shouldCreateCorrectOperator(String expectedOperator, double operandValue) {
            // given
            Function<NumberOperatorImpl<Object, Double, String>, ExpressionBuilder.NumberOperator<Object, Double, String>> operation =
                    getOperationForOperator(expectedOperator);

            // when
            operation.apply(operator).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode operationNode = (OperatorNode) opNode.operands().get(0);
            assertThat(operationNode.operator()).isEqualTo(Operator.valueOf(expectedOperator));
        }

        private Function<NumberOperatorImpl<Object, Double, String>, ExpressionBuilder.NumberOperator<Object, Double, String>>
                getOperationForOperator(String operator) {
            return switch (operator) {
                case "ADD" -> op -> op.add(100.0);
                case "SUBTRACT" -> op -> op.subtract(50.0);
                case "MULTIPLY" -> op -> op.multiply(2.0);
                case "DIVIDE" -> op -> op.divide(2.0);
                case "MOD" -> op -> op.mod(10.0);
                default -> throw new IllegalArgumentException("Unknown operator: " + operator);
            };
        }
    }

    @Nested
    class ArithmeticOperationsWithExpressions {

///
         /// 验证ies that arithmetic 操作s with expressions create correct operator nodes.
        @ParameterizedTest
        @CsvSource({
            "ADD, 50.0",
            "SUBTRACT, 25.0",
            "MULTIPLY, 1.5",
            "DIVIDE, 2.0",
            "MOD, 10.0"
        })
        void shouldCreateCorrectOperatorWithExpression(String expectedOperator, double expressionValue) {
            // given
            LiteralNode exprNode = new LiteralNode(expressionValue);
            SimpleExpressionImpl<Object, Double> expression = new SimpleExpressionImpl<>(exprNode);
            Function<NumberOperatorImpl<Object, Double, String>, ExpressionBuilder.NumberOperator<Object, Double, String>> operation =
                    getExpressionOperationForOperator(expectedOperator, expression);

            // when
            operation.apply(operator).eq(1000.0);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            OperatorNode operationNode = (OperatorNode) opNode.operands().get(0);
            assertThat(operationNode.operator()).isEqualTo(Operator.valueOf(expectedOperator));
        }

        private Function<NumberOperatorImpl<Object, Double, String>, ExpressionBuilder.NumberOperator<Object, Double, String>>
                getExpressionOperationForOperator(String operator, SimpleExpressionImpl<Object, Double> expression) {
            return switch (operator) {
                case "ADD" -> op -> op.add(expression);
                case "SUBTRACT" -> op -> op.subtract(expression);
                case "MULTIPLY" -> op -> op.multiply(expression);
                case "DIVIDE" -> op -> op.divide(expression);
                case "MOD" -> op -> op.mod(expression);
                default -> throw new IllegalArgumentException("Unknown operator: " + operator);
            };
        }
    }

    @Nested
    class ReturnType {

///
         /// 验证ies that all arithmetic 操作s return NumberOperatorImpl.
        @ParameterizedTest
        @CsvSource({
            "ADD, 100.0",
            "SUBTRACT, 50.0",
            "MULTIPLY, 2.0",
            "DIVIDE, 2.0",
            "MOD, 10.0"
        })
        void arithmeticOperations_ShouldReturnNumberOperator(String operationName, double operandValue) {
            // when
            var result = applyOperation(operationName, operandValue);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        private ExpressionBuilder.NumberOperator<Object, Double, String> applyOperation(String operation, double value) {
            return switch (operation) {
                case "ADD" -> operator.add(value);
                case "SUBTRACT" -> operator.subtract(value);
                case "MULTIPLY" -> operator.multiply(value);
                case "DIVIDE" -> operator.divide(value);
                case "MOD" -> operator.mod(value);
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };
        }
    }
}
