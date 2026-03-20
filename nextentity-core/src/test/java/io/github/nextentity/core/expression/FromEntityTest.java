package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify FromEntity correctly represents an entity source
 * <p>
 * Test scenarios:
 * 1. Create with entity class
 * 2. Access type property
 */
class FromEntityTest {

    @Nested
    class Creation {

        @Test
        void fromEntity_CreatesWithType() {
            // given
            Class<?> entityClass = String.class;

            // when
            FromEntity fromEntity = new FromEntity(entityClass);

            // then
            assertThat(fromEntity.type()).isEqualTo(String.class);
        }

        @Test
        void fromEntity_ImplementsFrom() {
            // given
            FromEntity fromEntity = new FromEntity(String.class);

            // then
            assertThat(fromEntity).isInstanceOf(From.class);
        }
    }
}
