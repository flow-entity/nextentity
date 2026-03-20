package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SelectProjection correctly represents projection selection
 * <p>
 * Test scenarios:
 * 1. Create with projection type and distinct flag
 * 2. Access properties
 */
class SelectProjectionTest {

    @Nested
    class Creation {

        @Test
        void selectProjection_CreatesWithType() {
            // given
            Class<?> projectionType = String.class;

            // when
            SelectProjection selectProjection = new SelectProjection(projectionType, false);

            // then
            assertThat(selectProjection.type()).isEqualTo(String.class);
            assertThat(selectProjection.distinct()).isFalse();
        }

        @Test
        void selectProjection_CreatesWithDistinct() {
            // given
            Class<?> projectionType = Long.class;

            // when
            SelectProjection selectProjection = new SelectProjection(projectionType, true);

            // then
            assertThat(selectProjection.distinct()).isTrue();
        }

        @Test
        void selectProjection_ImplementsSelected() {
            // given
            SelectProjection selectProjection = new SelectProjection(String.class, false);

            // then
            assertThat(selectProjection).isInstanceOf(Selected.class);
        }
    }
}
