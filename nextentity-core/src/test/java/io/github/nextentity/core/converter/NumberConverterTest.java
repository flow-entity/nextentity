package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify NumberConverter correctly converts between number types
 * <p>
 * Test scenarios:
 * 1. Convert between different number primitives
 * 2. Convert between wrappers
 * 3. Convert from String to numbers
 * 4. Handle null and already-correct type
 * 5. Return original value when conversion fails
 * <p>
 * Expected result: Numbers are converted correctly between types
 */
class NumberConverterTest {

    private final NumberConverter converter = NumberConverter.of();

    @Nested
    class BasicConversions {

        /**
         * Test objective: Verify integer to long conversion
         * Test scenario: Convert Integer to Long
         * Expected result: Long value
         */
        @Test
        void convert_IntegerToLong_ShouldReturnLong() {
            // when
            Object result = converter.convert(Integer.valueOf(42), long.class);

            // then
            assertThat(result).isEqualTo(Long.valueOf(42L));
        }

        /**
         * Test objective: Verify double to int conversion with truncation
         * Test scenario: Convert Double(3.14) to Integer
         * Expected result: Integer(3) - truncated value
         *
         * Bug #2: NumberConverter returns original value when precision is lost
         */
        @Test
        void convert_DoubleToInt_ShouldTruncate() {
            // given
            Double value = Double.valueOf(3.14);

            // when
            Object result = converter.convert(value, int.class);

            // then - should truncate to integer
            assertThat(result).isEqualTo(Integer.valueOf(3));
        }

        /**
         * Test objective: Verify long to double conversion
         * Test scenario: Convert Long to Double
         * Expected result: Double value
         */
        @Test
        void convert_LongToDouble_ShouldReturnDouble() {
            // when
            Object result = converter.convert(Long.valueOf(100L), double.class);

            // then
            assertThat(result).isEqualTo(Double.valueOf(100.0));
        }
    }

    @Nested
    class SpecialCases {

        /**
         * Test objective: Verify null returns null
         * Test scenario: Convert null value
         * Expected result: null returned
         */
        @Test
        void convert_Null_ShouldReturnNull() {
            // when
            Object result = converter.convert(null, int.class);

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify same type returns same value
         * Test scenario: Convert Integer to Integer
         * Expected result: Same value returned
         */
        @Test
        void convert_SameType_ShouldReturnSameValue() {
            // given
            Integer value = Integer.valueOf(42);

            // when
            Object result = converter.convert(value, Integer.class);

            // then
            assertThat(result).isSameAs(value);
        }

        /**
         * Test objective: Verify BigDecimal conversion
         * Test scenario: Convert to BigDecimal
         * Expected result: BigDecimal value
         */
        @Test
        void convert_ToBigDecimal_ShouldReturnBigDecimal() {
            // when
            Object result = converter.convert(Integer.valueOf(42), BigDecimal.class);

            // then
            assertThat(result).isEqualTo(BigDecimal.valueOf(42));
        }

        /**
         * Test objective: Verify BigInteger conversion
         * Test scenario: Convert to BigInteger
         * Expected result: BigInteger value
         */
        @Test
        void convert_ToBigInteger_ShouldReturnBigInteger() {
            // when
            Object result = converter.convert(Integer.valueOf(42), BigInteger.class);

            // then
            assertThat(result).isEqualTo(BigInteger.valueOf(42));
        }
    }

    @Nested
    class StringConversions {

        /**
         * Test objective: Verify string "42" to int conversion
         * Test scenario: Convert string to int
         * Expected result: Integer 42
         */
        @Test
        void convert_StringToInt_ShouldReturnInt() {
            // when
            Object result = converter.convert("42", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(42));
        }

        /**
         * Test objective: Verify invalid string returns unchanged
         * Test scenario: Convert non-numeric string to int
         * Expected result: Original string returned
         */
        @Test
        void convert_InvalidString_ShouldReturnOriginal() {
            // given
            String input = "not-a-number";

            // when
            Object result = converter.convert(input, int.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

}
