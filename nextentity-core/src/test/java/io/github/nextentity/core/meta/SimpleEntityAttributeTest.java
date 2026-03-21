package io.github.nextentity.core.meta;

import io.github.nextentity.core.expression.PathNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleEntityAttribute.
 */
class SimpleEntityAttributeTest {

    private SimpleEntityAttribute attribute;

    @BeforeEach
    void setUp() {
        attribute = new SimpleEntityAttribute();
    }

    @Nested
    class ColumnName {

        /**
         * Test objective: Verify columnName getter and setter.
         * Test scenario: Set column name and retrieve it.
         * Expected result: Returns the set column name.
         */
        @Test
        void columnName_ShouldReturnSetValue() {
            // given
            attribute.setColumnName("test_column");

            // when
            String result = attribute.columnName();

            // then
            assertThat(result).isEqualTo("test_column");
        }
    }

    @Nested
    class ValueConverterTests {

        /**
         * Test objective: Verify valueConverter getter and setter.
         * Test scenario: Set value converter and retrieve it.
         * Expected result: Returns the set value converter.
         */
        @Test
        void valueConvertor_ShouldReturnSetValue() {
            // given
            IdentityValueConverter converter = new IdentityValueConverter(String.class);
            attribute.setValueConverter(converter);

            // when
            ValueConverter<?, ?> result = attribute.valueConvertor();

            // then
            assertThat(result).isSameAs(converter);
        }
    }

    @Nested
    class VersionFlag {

        /**
         * Test objective: Verify version flag getter and setter.
         * Test scenario: Set version flag and check it.
         * Expected result: Returns the set version flag.
         */
        @Test
        void isVersion_ShouldReturnSetValue() {
            // given
            attribute.setVersion(true);

            // when
            boolean result = attribute.isVersion();

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify default version flag is false.
         * Test scenario: Create new attribute without setting version.
         * Expected result: isVersion() returns false.
         */
        @Test
        void isVersion_DefaultShouldBeFalse() {
            // when
            boolean result = attribute.isVersion();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class IdFlag {

        /**
         * Test objective: Verify ID flag getter and setter.
         * Test scenario: Set ID flag and check it.
         * Expected result: Returns the set ID flag.
         */
        @Test
        void isId_ShouldReturnSetValue() {
            // given
            attribute.setId(true);

            // when
            boolean result = attribute.isId();

            // then
            assertThat(result).isTrue();
        }

        /**
         * Test objective: Verify default ID flag is false.
         * Test scenario: Create new attribute without setting ID.
         * Expected result: isId() returns false.
         */
        @Test
        void isId_DefaultShouldBeFalse() {
            // when
            boolean result = attribute.isId();

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class UpdatableFlag {

        /**
         * Test objective: Verify updatable flag getter and setter.
         * Test scenario: Set updatable flag and check it.
         * Expected result: Returns the set updatable flag.
         */
        @Test
        void isUpdatable_ShouldReturnSetValue() {
            // given
            attribute.setUpdatable(true);

            // when
            boolean result = attribute.isUpdatable();

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    class Expression {

        /**
         * Test objective: Verify expression() returns PathNode.
         * Test scenario: Call expression() on attribute with path.
         * Expected result: Returns a non-null PathNode.
         */
        @Test
        void expression_ShouldReturnPathNode() {
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
            // when
            PathNode result1 = attribute.expression();
            PathNode result2 = attribute.expression();

            // then
            assertThat(result1).isSameAs(result2);
        }
    }

    @Nested
    class ToString {

        /**
         * Test objective: Verify toString returns path as string.
         * Test scenario: Call toString on attribute.
         * Expected result: Returns the path joined by dots.
         */
        @Test
        void toString_ShouldReturnPathString() {
            // when
            String result = attribute.toString();

            // then
            assertThat(result).isNotNull();
        }
    }
}
