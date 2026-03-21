package io.github.nextentity.core.meta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleProjectionAttribute.
 */
class SimpleProjectionAttributeTest {

    private SimpleProjectionAttribute attribute;
    private SimpleEntityAttribute sourceAttribute;

    @BeforeEach
    void setUp() {
        sourceAttribute = new SimpleEntityAttribute();
        sourceAttribute.setColumnName("test_column");
        sourceAttribute.setUpdatable(true);
        attribute = new SimpleProjectionAttribute(sourceAttribute);
    }

    @Nested
    class Source {

        /**
         * Test objective: Verify source() returns the source attribute.
         * Test scenario: Create SimpleProjectionAttribute with source and call source().
         * Expected result: Returns the source attribute.
         */
        @Test
        void source_ShouldReturnSourceAttribute() {
            // when
            EntityAttribute result = attribute.source();

            // then
            assertThat(result).isSameAs(sourceAttribute);
        }
    }

    @Nested
    class IsUpdatable {

        /**
         * Test objective: Verify isUpdatable() delegates to source.
         * Test scenario: Check isUpdatable when source is updatable.
         * Expected result: Returns the same value as source.
         */
        @Test
        void isUpdatable_ShouldDelegateToSource() {
            // when
            boolean result = attribute.isUpdatable();

            // then
            assertThat(result).isTrue();
            assertThat(result).isEqualTo(sourceAttribute.isUpdatable());
        }

        /**
         * Test objective: Verify isUpdatable() returns false when source is not updatable.
         * Test scenario: Set source as not updatable.
         * Expected result: Returns false.
         */
        @Test
        void isUpdatable_WhenSourceNotUpdatable_ShouldReturnFalse() {
            // given
            sourceAttribute.setUpdatable(false);

            // when
            boolean result = attribute.isUpdatable();

            // then
            assertThat(result).isFalse();
        }
    }
}
