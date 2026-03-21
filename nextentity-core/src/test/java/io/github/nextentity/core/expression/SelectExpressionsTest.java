package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SelectExpressions.
 */
class SelectExpressionsTest {

    /**
     * Test objective: Verify constructor sets items and distinct correctly.
     * Test scenario: Create SelectExpressions with items and distinct flag.
     * Expected result: Items and distinct are accessible via getters.
     */
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

    /**
     * Test objective: Verify distinct defaults to false.
     * Test scenario: Create SelectExpressions with distinct=false.
     * Expected result: distinct() returns false.
     */
    @Test
    void distinct_ShouldDefaultToFalse() {
        // given
        ImmutableList<ExpressionNode> items = ImmutableList.of(new LiteralNode("a"));

        // when
        SelectExpressions result = new SelectExpressions(items, false);

        // then
        assertThat(result.distinct()).isFalse();
    }

    /**
     * Test objective: Verify items can be empty.
     * Test scenario: Create SelectExpressions with empty items list.
     * Expected result: items() returns empty array.
     */
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
