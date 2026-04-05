package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.Predicate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证Expressions实用类
/// <p>
/// 测试场景：
/// 1. 创建字面量表达式
/// 2. 创建真谓词
/// 3. 创建假谓词
class ExpressionsTest {

    @Nested
    class OfValue {

        /// 测试目标：验证of()创建字面量表达式
        /// 测试场景：使用值创建表达式
        /// 预期结果：包含该值的TypedExpression
        @Test
        void of_WithValue_ReturnsTypedExpression() {
            // given
            String value = "test";

            // when
            Expression<?, String> expression = Expression.of(value);

            // then
            assertThat(expression).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(expression);
            assertThat(node).isInstanceOf(LiteralNode.class);
            LiteralNode literalNode = (LiteralNode) node;
            assertThat(literalNode.value()).isEqualTo(value);
        }

        /// 测试目标：验证of()使用null值
        /// 测试场景：使用null创建表达式
        /// 预期结果：带有null值的TypedExpression
        @Test
        void of_WithNull_ReturnsTypedExpressionWithNull() {
            // when
            Expression<?, Object> expression = Expression.of(null);

            // then
            assertThat(expression).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(expression);
            assertThat(node).isInstanceOf(LiteralNode.class);
            LiteralNode literalNode = (LiteralNode) node;
            assertThat(literalNode.value()).isNull();
        }
    }

    @Nested
    class OfTrueFalse {

        /// 测试目标：验证ofTrue()创建真谓词
        /// 测试场景：调用ofTrue()
        /// 预期结果：带有TRUE字面量的谓词
        @Test
        void ofTrue_ReturnsTruePredicate() {
            // when
            Predicate<Object> predicate = Predicate.ofTrue();

            // then
            assertThat(predicate).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(predicate);
            assertThat(node).isSameAs(LiteralNode.TRUE);
        }

        /// 测试目标：验证ofFalse()创建假谓词
        /// 测试场景：调用ofFalse()
        /// 预期结果：带有FALSE字面量的谓词
        @Test
        void ofFalse_ReturnsFalsePredicate() {
            // when
            Predicate<Object> predicate = Predicate.ofFalse();

            // then
            assertThat(predicate).isNotNull();
            ExpressionNode node = ExpressionNodes.getNode(predicate);
            assertThat(node).isSameAs(LiteralNode.FALSE);
        }
    }

}
