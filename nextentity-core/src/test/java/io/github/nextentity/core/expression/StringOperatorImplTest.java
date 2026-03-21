package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for StringOperatorImpl.
 */
class StringOperatorImplTest {

    private StringOperatorImpl<Object, String> operator;
    private ExpressionNode capturedNode;

    @BeforeEach
    void setUp() {
        PathNode pathNode = new PathNode("name");
        capturedNode = null;
        Function<ExpressionNode, String> callback = node -> {
            capturedNode = node;
            return "result";
        };
        operator = new StringOperatorImpl<>(pathNode, callback);
    }

    @Nested
    class LowerOperation {

        /**
         * Test objective: Verify lower() creates LOWER operator.
         * Test scenario: Call lower() then eq() on string operator.
         * Expected result: Creates LOWER operator node.
         */
        @Test
        void lower_ShouldCreateLowerOperator() {
            // when
            operator.lower().eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be LOWER operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode lowerNode = (OperatorNode) opNode.operands().get(0);
            assertThat(lowerNode.operator()).isEqualTo(Operator.LOWER);
        }
    }

    @Nested
    class UpperOperation {

        /**
         * Test objective: Verify upper() creates UPPER operator.
         * Test scenario: Call upper() then eq() on string operator.
         * Expected result: Creates UPPER operator node.
         */
        @Test
        void upper_ShouldCreateUpperOperator() {
            // when
            operator.upper().eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be UPPER operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode upperNode = (OperatorNode) opNode.operands().get(0);
            assertThat(upperNode.operator()).isEqualTo(Operator.UPPER);
        }
    }

    @Nested
    class SubstringOperation {

        /**
         * Test objective: Verify substring() creates SUBSTRING operator with parameters.
         * Test scenario: Call substring(0, 5) then eq() on string operator.
         * Expected result: Creates SUBSTRING operator node with offset and length.
         */
        @Test
        void substring_ShouldCreateSubstringOperator() {
            // when
            operator.substring(0, 5).eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be SUBSTRING operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode substringNode = (OperatorNode) opNode.operands().get(0);
            assertThat(substringNode.operator()).isEqualTo(Operator.SUBSTRING);
        }
    }

    @Nested
    class TrimOperation {

        /**
         * Test objective: Verify trim() creates TRIM operator.
         * Test scenario: Call trim() then eq() on string operator.
         * Expected result: Creates TRIM operator node.
         */
        @Test
        void trim_ShouldCreateTrimOperator() {
            // when
            operator.trim().eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be TRIM operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode trimNode = (OperatorNode) opNode.operands().get(0);
            assertThat(trimNode.operator()).isEqualTo(Operator.TRIM);
        }
    }

    @Nested
    class LengthOperation {

        /**
         * Test objective: Verify length() creates LENGTH operator and returns NumberOperator.
         * Test scenario: Call length() on string operator.
         * Expected result: Creates LENGTH operator node and returns NumberOperator.
         */
        @Test
        void length_ShouldCreateLengthOperator() {
            // when
            var result = operator.length();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify length() followed by eq creates correct expression.
         * Test scenario: Call length() then eq() on string operator.
         * Expected result: Creates LENGTH operator node with comparison.
         */
        @Test
        void length_WithComparison_ShouldCreateLengthOperator() {
            // when
            operator.length().eq(5);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
            // The first operand should be LENGTH operator
            assertThat(opNode.operands().get(0)).isInstanceOf(OperatorNode.class);
            OperatorNode lengthNode = (OperatorNode) opNode.operands().get(0);
            assertThat(lengthNode.operator()).isEqualTo(Operator.LENGTH);
        }
    }

    @Nested
    class ReturnType {

        /**
         * Test objective: Verify lower() returns StringOperator.
         * Test scenario: Call lower() on string operator.
         * Expected result: Returns StringOperatorImpl instance.
         */
        @Test
        void lower_ShouldReturnStringOperator() {
            // when
            var result = operator.lower();

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }

        /**
         * Test objective: Verify upper() returns StringOperator.
         * Test scenario: Call upper() on string operator.
         * Expected result: Returns StringOperatorImpl instance.
         */
        @Test
        void upper_ShouldReturnStringOperator() {
            // when
            var result = operator.upper();

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }

        /**
         * Test objective: Verify trim() returns StringOperator.
         * Test scenario: Call trim() on string operator.
         * Expected result: Returns StringOperatorImpl instance.
         */
        @Test
        void trim_ShouldReturnStringOperator() {
            // when
            var result = operator.trim();

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }

        /**
         * Test objective: Verify substring() returns StringOperator.
         * Test scenario: Call substring() on string operator.
         * Expected result: Returns StringOperatorImpl instance.
         */
        @Test
        void substring_ShouldReturnStringOperator() {
            // when
            var result = operator.substring(0, 5);

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }
    }
}
