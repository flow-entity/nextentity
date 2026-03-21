package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ExpressionBuilderImpl.
 */
class ExpressionBuilderImplTest {

    private ExpressionBuilderImpl<Object, String, String> builder;
    private ExpressionNode capturedNode;

    @BeforeEach
    void setUp() {
        PathNode pathNode = new PathNode("name");
        capturedNode = null;
        Function<ExpressionNode, String> callback = node -> {
            capturedNode = node;
            return "result";
        };
        builder = new ExpressionBuilderImpl<>(pathNode, callback);
    }

    @Nested
    class EqIfNotNull {

        /**
         * Test objective: Verify eqIfNotNull calls eq when value is not null.
         * Test scenario: Call eqIfNotNull with non-null value.
         * Expected result: Creates EQ operator with the value.
         */
        @Test
        void eqIfNotNull_WithNonNullValue_ShouldCallEq() {
            // given
            String value = "test";

            // when
            String result = builder.eqIfNotNull(value);

            // then
            assertThat(result).isEqualTo("result");
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        /**
         * Test objective: Verify eqIfNotNull handles null value correctly.
         * Test scenario: Call eqIfNotNull with null value.
         * Expected result: Returns operateNull result (EmptyNode).
         */
        @Test
        void eqIfNotNull_WithNullValue_ShouldReturnOperateNull() {
            // given
            String value = null;

            // when
            String result = builder.eqIfNotNull(value);

            // then
            assertThat(result).isEqualTo("result");
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }
    }

    @Nested
    class NextMethod {

        /**
         * Test objective: Verify next method applies the callback.
         * Test scenario: Call next with an expression node.
         * Expected result: Callback is applied and result returned.
         */
        @Test
        void next_ShouldApplyCallback() {
            // given
            LiteralNode node = new LiteralNode("value");

            // when
            String result = builder.next(node);

            // then
            assertThat(result).isEqualTo("result");
            assertThat(capturedNode).isSameAs(node);
        }
    }
}
