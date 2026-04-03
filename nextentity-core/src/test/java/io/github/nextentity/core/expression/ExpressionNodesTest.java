package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证ExpressionNodes工具类
/// <p>
/// 测试场景：
/// 1. isNullOrTrue检测
/// 2. getNode提取
class ExpressionNodesTest {

    @Nested
    class IsNullOrTrue {

        /// 测试目标：验证空值被识别为空或真
        /// 测试场景：检查空值
        /// 预期结果：true
        @Test
        void isNullOrTrue_Null_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue((Expression<?, ?>) null);

            // then
            assertThat(result).isTrue();
        }

        /// 测试目标：验证EmptyNode被识别为空或真
        /// 测试场景：检查EmptyNode
        /// 预期结果：true
        @Test
        void isNullOrTrue_EmptyNode_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(EmptyNode.INSTANCE);

            // then
            assertThat(result).isTrue();
        }

        /// 测试目标：验证TRUE字面量被识别为空或真
        /// 测试场景：检查TRUE字面量
        /// 预期结果：true
        @Test
        void isNullOrTrue_TrueLiteral_ReturnsTrue() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(LiteralNode.TRUE);

            // then
            assertThat(result).isTrue();
        }

        /// 测试目标：验证FALSE字面量不是空或真
        /// 测试场景：检查FALSE字面量
        /// 预期结果：false
        @Test
        void isNullOrTrue_FalseLiteral_ReturnsFalse() {
            // when
            boolean result = ExpressionNodes.isNullOrTrue(LiteralNode.FALSE);

            // then
            assertThat(result).isFalse();
        }

        /// 测试目标：验证非布尔字面量不是空或真
        /// 测试场景：检查字符串字面量
        /// 预期结果：false
        @Test
        void isNullOrTrue_StringLiteral_ReturnsFalse() {
            // given
            LiteralNode stringNode = new LiteralNode("test");

            // when
            boolean result = ExpressionNodes.isNullOrTrue(stringNode);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class GetNode {

        /// 测试目标：验证getNode从表达式中提取节点
        /// 测试场景：从Predicate获取节点
        /// 预期结果：正确的节点
        @Test
        void getNode_FromExpression_ReturnsNode() {
            // given
            PredicateImpl<Object> predicate = new PredicateImpl<>(LiteralNode.TRUE);

            // when
            ExpressionNode node = ExpressionNodes.getNode(predicate);

            // then
            assertThat(node).isSameAs(LiteralNode.TRUE);
        }

        /// 测试目标：验证getNode从字面量表达式中提取节点
        /// 测试场景：从字面量表达式获取节点
        /// 预期结果：正确的节点
        @Test
        void getNode_FromLiteralExpression_ReturnsNode() {
            // given
            Expression<?, String> expression = Expression.of("test");

            // when
            ExpressionNode node = ExpressionNodes.getNode(expression);

            // then
            assertThat(node).isInstanceOf(LiteralNode.class);
            assertThat(((LiteralNode) node).value()).isEqualTo("test");
        }
    }
}
