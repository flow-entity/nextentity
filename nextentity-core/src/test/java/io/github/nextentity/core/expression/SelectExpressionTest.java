package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y SelectExpression 正确 represents expression selection
/// <p>
/// 测试场景s:
/// 1. Create with expression and distinct flag
/// 2. Access properties
class SelectExpressionTest {

    @Nested
    class Creation {

        @Test
        void selectExpression_CreatesWithExpression() {
            // given
            ExpressionNode expression = LiteralNode.TRUE;

            // when
            SelectExpression selectExpression = new SelectExpression(expression, false);

            // then
            assertThat(selectExpression.expression()).isSameAs(LiteralNode.TRUE);
            assertThat(selectExpression.distinct()).isFalse();
        }

        @Test
        void selectExpression_CreatesWithDistinct() {
            // given
            ExpressionNode expression = LiteralNode.FALSE;

            // when
            SelectExpression selectExpression = new SelectExpression(expression, true);

            // then
            assertThat(selectExpression.distinct()).isTrue();
        }

        @Test
        void selectExpression_ImplementsSelected() {
            // given
            SelectExpression selectExpression = new SelectExpression(LiteralNode.TRUE, false);

            // then
            assertThat(selectExpression).isInstanceOf(Selected.class);
        }
    }
}
