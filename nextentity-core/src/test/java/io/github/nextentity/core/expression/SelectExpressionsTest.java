package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
 /// 单元测试 SelectExpressions.
class SelectExpressionsTest {

///
     /// 测试目标: 验证y constructor sets items and distinct 正确.
     /// 测试场景: Create SelectExpressions with items and distinct flag.
     /// 预期结果: Items and distinct are accessible via getters.
    @Test
    void constructor_ShouldSetItemsAndDistinct() {
        // given
        ImmutableList<ExpressionNode> items = ImmutableList.of(new LiteralNode("a"), new LiteralNode("b"));
        boolean distinct = true;

        // when
        SelectExpressions result = new SelectExpressions(items, distinct);

        // then
        assertThat(result.items()).isSameAs(items);
        assertThat(result.distinct()).isTrue();
    }

///
     /// 测试目标: 验证y distinct defaults to false.
     /// 测试场景: Create SelectExpressions with distinct=false.
     /// 预期结果: distinct() returns false.
    @Test
    void distinct_ShouldDefaultToFalse() {
        // given
        ImmutableList<ExpressionNode> items = ImmutableList.of(new LiteralNode("a"));

        // when
        SelectExpressions result = new SelectExpressions(items, false);

        // then
        assertThat(result.distinct()).isFalse();
    }

///
     /// 测试目标: 验证y items can be empty.
     /// 测试场景: Create SelectExpressions with empty items list.
     /// 预期结果: items() returns empty array.
    @Test
    void items_CanBeEmpty() {
        // given
        ImmutableList<ExpressionNode> items = ImmutableList.empty();

        // when
        SelectExpressions result = new SelectExpressions(items, false);

        // then
        assertThat(result.items()).isEmpty();
    }
}
