package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y SelectEntity 正确 represents entity selection
/// <p>
/// 测试场景s:
/// 1. Create with fetch paths and distinct flag
/// 2. Access properties
class SelectEntityTest {

    @Nested
    class Creation {

        @Test
        void selectEntity_CreatesWithEmptyFetch() {
            // given
            var fetch = io.github.nextentity.core.util.ImmutableList.<PathNode>of();

            // when
            SelectEntity selectEntity = new SelectEntity(fetch, false);

            // then
            assertThat(selectEntity.fetch()).isEmpty();
            assertThat(selectEntity.distinct()).isFalse();
        }

        @Test
        void selectEntity_CreatesWithDistinct() {
            // given
            var fetch = io.github.nextentity.core.util.ImmutableList.<PathNode>of();

            // when
            SelectEntity selectEntity = new SelectEntity(fetch, true);

            // then
            assertThat(selectEntity.distinct()).isTrue();
        }

        @Test
        void selectEntity_ImplementsSelected() {
            // given
            SelectEntity selectEntity = new SelectEntity(
                    io.github.nextentity.core.util.ImmutableList.of(), false);

            // then
            assertThat(selectEntity).isInstanceOf(Selected.class);
        }
    }
}
