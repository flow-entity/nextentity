package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify TypeConverters chain multiple converters correctly
 * <p>
 * Test scenarios:
 * 1. Chain multiple converters
 * 2. Skip converters when target type is already satisfied
 * 3. Handle primitive wrapper compatibility
 * <p>
 * Expected result: Chained converters work correctly
 */
class TypeConvertersTest {

    @Nested
    class ChainConversion {

        /**
         * Test objective: Verify chained converters can convert complex cases
         * Test scenario: Convert through multiple converters
         * Expected result: Final converted value
         */
        @Test
        void convert_WithMultipleConverters_ShouldUseAppropriateOne() {
            // given
            TypeConverter converter = TypeConverter.of(
                    NumberConverter.of(),
                    EnumConverter.of()
            );

            // when - convert string to int (via NumberConverter)
            Object result = converter.convert("42", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(42));
        }

        /**
         * Test objective: Verify ofDefault creates default converter chain
         * Test scenario: Use ofDefault factory method
         * Expected result: Default converters available
         */
        @Test
        void ofDefault_ShouldCreateDefaultConverter() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();

            // when
            Object result = converter.convert("123", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(123));
        }

        /**
         * Test objective: Verify of(List) creates converter from list
         * Test scenario: Pass list of converters
         * Expected result: Chained converter
         */
        @Test
        void of_WithList_ShouldCreateConverter() {
            // given
            List<TypeConverter> converters = List.of(NumberConverter.of());

            // when
            TypeConverter converter = TypeConverter.of(converters);

            // then - should work without error
            assertThat(converter).isNotNull();
        }
    }

    @Nested
    class PrimitiveHandling {

        /**
         * Test objective: Verify primitive wrapper compatibility
         * Test scenario: Integer value for int primitive target
         * Expected result: Integer value (wrapper)
         */
        @Test
        void convert_IntegerForIntTarget_ShouldReturnInteger() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();
            Integer value = Integer.valueOf(42);

            // when
            Object result = converter.convert(value, int.class);

            // then
            assertThat(result).isEqualTo(value);
        }
    }

    @Nested
    class NullAndSameType {

        /**
         * Test objective: Verify null returns null
         * Test scenario: Convert null
         * Expected result: null
         */
        @Test
        void convert_Null_ShouldReturnNull() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();

            // when
            Object result = converter.convert(null, String.class);

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify same type returns same value
         * Test scenario: Convert value to same type
         * Expected result: Same value
         */
        @Test
        void convert_SameType_ShouldReturnSameValue() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();
            String value = "test";

            // when
            Object result = converter.convert(value, String.class);

            // then
            assertThat(result).isSameAs(value);
        }
    }
}
