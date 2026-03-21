package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify EnumConverter correctly converts to enum values
 * <p>
 * Test scenarios:
 * 1. Convert string to enum
 * 2. Convert integer (ordinal) to enum
 * 3. Convert already correct enum value
 * 4. Return original when conversion fails
 * <p>
 * Expected result: Enum values are converted correctly
 */
class EnumConverterTest {

    private final EnumConverter converter = EnumConverter.of();

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    @Nested
    class StringConversions {

        /**
         * Test objective: Verify string converts to enum
         * Test scenario: Convert "ACTIVE" to TestStatus.ACTIVE
         * Expected result: TestStatus.ACTIVE
         *
         * Bug #3: ReflectUtil.getEnum(String) method invocation error
         */
        @Test
        void convert_StringToEnum_ShouldReturnEnum() {
            // given
            String input = "ACTIVE";

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

        /**
         * Test objective: Verify invalid string returns original
         * Test scenario: Convert invalid string to enum
         * Expected result: Original string returned
         */
        @Test
        void convert_InvalidString_ShouldReturnOriginal() {
            // given
            String input = "INVALID_STATUS";

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    class OrdinalConversions {

        /**
         * Test objective: Verify integer converts to enum
         * Test scenario: Convert ordinal 1 to enum
         * Expected result: Enum with ordinal 1
         */
        @Test
        void convert_IntegerToEnum_ShouldReturnEnum() {
            // when
            Object result = converter.convert(Integer.valueOf(1), TestStatus.class);

            // then
            assertThat(result).isEqualTo(TestStatus.INACTIVE);
        }

        /**
         * Test objective: Verify out-of-range ordinal returns original
         * Test scenario: Convert invalid ordinal to enum
         * Expected result: Original value returned
         */
        @Test
        void convert_InvalidOrdinal_ShouldReturnOriginal() {
            // given
            Integer input = Integer.valueOf(100);

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    class SameTypeConversion {

        /**
         * Test objective: Verify same enum type returns unchanged
         * Test scenario: Convert enum to same type
         * Expected result: Same enum returned
         */
        @ParameterizedTest
        @EnumSource(TestStatus.class)
        void convert_SameEnumType_ShouldReturnSameValue(TestStatus status) {
            // when
            Object result = converter.convert(status, TestStatus.class);

            // then
            assertThat(result).isSameAs(status);
        }
    }

    @Nested
    class NullHandling {

        /**
         * Test objective: Verify null returns null
         * Test scenario: Convert null to enum
         * Expected result: null returned
         */
        @Test
        void convert_Null_ShouldReturnNull() {
            // when
            Object result = converter.convert(null, TestStatus.class);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class NonEnumTarget {

        /**
         * Test objective: Verify non-enum target returns original
         * Test scenario: Convert to non-enum type
         * Expected result: Original value returned
         */
        @Test
        void convert_NonEnumTarget_ShouldReturnOriginal() {
            // given
            String input = "test";

            // when
            Object result = converter.convert(input, String.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }
}
