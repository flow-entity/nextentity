package io.github.nextentity.core.expression;

import io.github.nextentity.api.SortOrder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y SortExpression 正确 represents sort specification
/// <p>
/// 测试场景s:
/// 1. Create with expression and order
/// 2. Access properties
class SortExpressionTest {

    @Nested
    class Creation {

        @Test
        void sortExpression_CreatesWithExpressionAndOrder() {
            // given
            ExpressionNode expression = LiteralNode.TRUE;
            SortOrder order = SortOrder.ASC;

            // when
            SortExpression sortExpression = new SortExpression(expression, order);

            // then
            assertThat(sortExpression.expression()).isSameAs(LiteralNode.TRUE);
            assertThat(sortExpression.order()).isEqualTo(SortOrder.ASC);
        }

        @Test
        void sortExpression_CreatesWithDescOrder() {
            // given
            ExpressionNode expression = new LiteralNode("test");
            SortOrder order = SortOrder.DESC;

            // when
            SortExpression sortExpression = new SortExpression(expression, order);

            // then
            assertThat(sortExpression.order()).isEqualTo(SortOrder.DESC);
        }

        @Test
        void sortExpression_ExpressionNotNull() {
            // given
            ExpressionNode expression = new LiteralNode(42);

            // when
            SortExpression sortExpression = new SortExpression(expression, SortOrder.ASC);

            // then
            assertThat(sortExpression.expression()).isNotNull();
        }
    }
}
