package io.github.nextentity.core.expression;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// ExpressionBuilderImpl的单元测试。
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

        /// 测试目标：验证eqIfNotNull在值不为null时调用eq。
        /// 测试场景：对非空值调用eqIfNotNull。
        /// 预期结果：使用该值创建EQ操作符。
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

        /// 测试目标：验证eqIfNotNull正确处理null值。
        /// 测试场景：对null值调用eqIfNotNull。
        /// 预期结果：返回operateNull结果（EmptyNode）。
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

        /// 测试目标：验证next方法应用回调。
        /// 测试场景：对表达式节点调用next。
        /// 预期结果：应用回调并返回结果。
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

    @Nested
    class ExceptionAndEdgeCases {

        /// 测试eqIfNotNull与空字符串值创建EQ操作符。
        @Test
        void eqIfNotNull_WithEmptyString_ShouldCreateEqOperator() {
            // given
            String value = "";

            // when
            builder.eqIfNotNull(value);

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }

        /// 测试next方法处理null节点。
        @Test
        void next_WithNullNode_ShouldPassNullToCallback() {
            // when
            builder.next(null);

            // then
            assertThat(capturedNode).isNull();
        }

        /// 测试neIfNotNull与null值返回operateNull结果。
        @Test
        void neIfNotNull_WithNullValue_ShouldReturnOperateNull() {
            // given
            String value = null;

            // when
            String result = builder.neIfNotNull(value);

            // then
            assertThat(result).isEqualTo("result");
            assertThat(capturedNode).isInstanceOf(EmptyNode.class);
        }
    }
}
