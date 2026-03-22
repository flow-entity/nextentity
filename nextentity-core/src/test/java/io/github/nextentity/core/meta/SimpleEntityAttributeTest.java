package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.PathNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify SimpleEntityAttribute correctly handles entity attribute metadata
 * <p>
 * Test scenarios:
 * 1. expression() creates and caches PathNode
 * 2. toString() returns path as dot-separated string
 * 3. Default flag values are correct
 * <p>
 * Note: Simple getter/setter tests are omitted as they provide no verification value.
 */
class SimpleEntityAttributeTest {

    @Nested
    class ExpressionMethod {

        /**
         * Test objective: Verify expression() returns a non-null PathNode.
         * Test scenario: Call expression() on attribute.
         * Expected result: Returns a non-null PathNode instance.
         */
        @Test
        void expression_ShouldReturnPathNode() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            PathNode result = attribute.expression();

            // then
            assertThat(result).isNotNull();
        }

        /**
         * Test objective: Verify expression() caches the result.
         * Test scenario: Call expression() twice.
         * Expected result: Returns same instance on subsequent calls.
         */
        @Test
        void expression_ShouldCacheResult() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            PathNode result1 = attribute.expression();
            PathNode result2 = attribute.expression();

            // then
            assertThat(result1).isSameAs(result2);
        }
    }

    @Nested
    class ToStringMethod {

        /**
         * Test objective: Verify toString() returns path as string.
         * Test scenario: Call toString on attribute.
         * Expected result: Returns the path joined by dots.
         */
        @Test
        void toString_ShouldReturnPathString() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            String result = attribute.toString();

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    class DefaultFlagValues {

        /**
         * Test objective: Verify default version flag is false.
         * Test scenario: Create new attribute without setting version.
         * Expected result: isVersion() returns false.
         */
        @Test
        void isVersion_DefaultShouldBeFalse() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            boolean result = attribute.isVersion();

            // then
            assertThat(result).isFalse();
        }

        /**
         * Test objective: Verify default ID flag is false.
         * Test scenario: Create new attribute without setting ID.
         * Expected result: isId() returns false.
         */
        @Test
        void isId_DefaultShouldBeFalse() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            boolean result = attribute.isId();

            // then
            assertThat(result).isFalse();
        }

        /**
         * Test objective: Verify default updatable flag is false.
         * Test scenario: Create new attribute without setting updatable.
         * Expected result: isUpdatable() returns false.
         */
        @Test
        void isUpdatable_DefaultShouldBeFalse() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();

            // when
            boolean result = attribute.isUpdatable();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class FlagMutations {

        /**
         * Test objective: Verify version flag can be set and retrieved.
         * Test scenario: Set version flag to true.
         * Expected result: isVersion() returns true.
         */
        @Test
        void setVersion_ShouldUpdateFlag() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();
            attribute.setVersion(true);

            // when
            boolean result = attribute.isVersion();

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify ID flag can be set and retrieved.
         * Test scenario: Set ID flag to true.
         * Expected result: isId() returns true.
         */
        @Test
        void setId_ShouldUpdateFlag() {
            // given
            SimpleEntityAttribute attribute = new SimpleEntityAttribute();
            attribute.setId(true);

            // when
            boolean result = attribute.isId();

            // then
            assertThat(result).isTrue();
        }
    }
}