package io.github.nextentity.core;

import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify ExpressionTypeResolver correctly resolves expression types
 * <p>
 * Test scenarios:
 * 1. Resolve literal types
 * 2. Resolve operator types
 */
class ExpressionTypeResolverTest {

    @Nested
    class LiteralType {

        @Test
        void getLiteralType_String_ReturnsStringClass() {
            // given
            LiteralNode node = new LiteralNode("test");

            // when
            Class<?> result = ExpressionTypeResolver.getLiteralType(node);

            // then
            assertThat(result).isEqualTo(String.class);
        }

        @Test
        void getLiteralType_Integer_ReturnsIntegerClass() {
            // given
            LiteralNode node = new LiteralNode(42);

            // when
            Class<?> result = ExpressionTypeResolver.getLiteralType(node);

            // then
            assertThat(result).isEqualTo(Integer.class);
        }
    }

    @Nested
    class OperationType {

        @Test
        void getOperationType_Not_ReturnsBoolean() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode(true)),
                    Operator.NOT
            );

            // when
            Class<?> result = ExpressionTypeResolver.getOperationType(node, null);

            // then
            assertThat(result).isEqualTo(Boolean.class);
        }

        @Test
        void getOperationType_Lower_ReturnsString() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode("test")),
                    Operator.LOWER
            );

            // when
            Class<?> result = ExpressionTypeResolver.getOperationType(node, null);

            // then
            assertThat(result).isEqualTo(String.class);
        }

        @Test
        void getOperationType_Count_ReturnsLong() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(LiteralNode.TRUE),
                    Operator.COUNT
            );

            // when
            Class<?> result = ExpressionTypeResolver.getOperationType(node, null);

            // then
            assertThat(result).isEqualTo(Long.class);
        }

        @Test
        void getOperationType_Divide_ReturnsDouble() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode(10), new LiteralNode(2)),
                    Operator.DIVIDE
            );

            // when
            Class<?> result = ExpressionTypeResolver.getOperationType(node, null);

            // then
            assertThat(result).isEqualTo(Double.class);
        }

        @Test
        void getOperationType_Eq_ReturnsBoolean() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode("a"), new LiteralNode("b")),
                    Operator.EQ
            );

            // when
            Class<?> result = ExpressionTypeResolver.getOperationType(node, null);

            // then
            assertThat(result).isEqualTo(Boolean.class);
        }
    }

    @Nested
    class ExpressionType {

        @Test
        void getExpressionType_LiteralNode_ReturnsValueType() {
            // given
            LiteralNode node = new LiteralNode("test");

            // when
            Class<?> result = ExpressionTypeResolver.getExpressionType(node, null);

            // then
            assertThat(result).isEqualTo(String.class);
        }

        @Test
        void getExpressionType_EmptyNode_ReturnsObject() {
            // given
            EmptyNode node = EmptyNode.INSTANCE;

            // when
            Class<?> result = ExpressionTypeResolver.getExpressionType(node, null);

            // then
            assertThat(result).isEqualTo(Object.class);
        }
    }
}
