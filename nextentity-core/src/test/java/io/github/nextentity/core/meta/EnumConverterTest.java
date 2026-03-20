package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify EnumConverter correctly converts between enum and ordinal
 * <p>
 * Test scenarios:
 * 1. convertToDatabaseColumn returns correct ordinal
 * 2. convertToEntityAttribute returns correct enum
 * 3. null values are handled correctly
 * 4. getDatabaseColumnType returns Integer.class
 * <p>
 * Expected result: Enum conversion works bidirectionally
 */
class EnumConverterTest {

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    private EnumConverter<TestStatus> converter;

    @BeforeEach
    void setUp() {
        Attribute attribute = new TestAttribute(TestStatus.class);
        converter = new EnumConverter<>(attribute);
    }

    @Nested
    class ConvertToDatabaseColumn {

        /**
         * Test objective: Verify ordinal is returned for each enum value
         * Test scenario: Convert each enum constant
         * Expected result: Correct ordinal returned
         */
        @ParameterizedTest
        @EnumSource(TestStatus.class)
        void convertToDatabaseColumn_ShouldReturnOrdinal(TestStatus status) {
            // when
            Integer result = converter.convertToDatabaseColumn(status);

            // then
            assertThat(result).isEqualTo(status.ordinal());
        }

        /**
         * Test objective: Verify null returns null
         * Test scenario: Convert null enum
         * Expected result: null returned
         */
        @Test
        void convertToDatabaseColumn_WithNull_ShouldReturnNull() {
            // when
            Integer result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ConvertToEntityAttribute {

        /**
         * Test objective: Verify enum is returned for each ordinal
         * Test scenario: Convert ordinal back to enum
         * Expected result: Correct enum constant returned
         */
        @Test
        void convertToEntityAttribute_ShouldReturnEnum() {
            assertThat(converter.convertToEntityAttribute(0)).isEqualTo(TestStatus.ACTIVE);
            assertThat(converter.convertToEntityAttribute(1)).isEqualTo(TestStatus.INACTIVE);
            assertThat(converter.convertToEntityAttribute(2)).isEqualTo(TestStatus.PENDING);
        }

        /**
         * Test objective: Verify null returns null
         * Test scenario: Convert null ordinal
         * Expected result: null returned
         */
        @Test
        void convertToEntityAttribute_WithNull_ShouldReturnNull() {
            // when
            TestStatus result = converter.convertToEntityAttribute(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class RoundTrip {

        /**
         * Test objective: Verify round-trip conversion works
         * Test scenario: Convert enum to ordinal and back
         * Expected result: Same enum returned
         */
        @ParameterizedTest
        @EnumSource(TestStatus.class)
        void roundTrip_ShouldReturnSameEnum(TestStatus status) {
            // when
            Integer ordinal = converter.convertToDatabaseColumn(status);
            TestStatus result = converter.convertToEntityAttribute(ordinal);

            // then
            assertThat(result).isEqualTo(status);
        }
    }

    @Nested
    class GetDatabaseColumnType {

        /**
         * Test objective: Verify getDatabaseColumnType returns Integer
         * Test scenario: Call getDatabaseColumnType
         * Expected result: Integer.class returned
         */
        @Test
        void getDatabaseColumnType_ShouldReturnInteger() {
            // when
            Class<Integer> result = converter.getDatabaseColumnType();

            // then
            assertThat(result).isEqualTo(Integer.class);
        }
    }

    // Test helper class
    static class TestAttribute implements Attribute {
        private final Class<?> type;

        TestAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public Class<?> type() {
            return type;
        }

        @Override
        public String name() {
            return "test";
        }

        @Override
        public java.lang.reflect.Method getter() {
            return null;
        }

        @Override
        public java.lang.reflect.Method setter() {
            return null;
        }

        @Override
        public java.lang.reflect.Field field() {
            return null;
        }

        @Override
        public Schema declareBy() {
            return null;
        }

        @Override
        public int ordinal() {
            return 0;
        }

        @Override
        public io.github.nextentity.core.util.ImmutableList<String> path() {
            return io.github.nextentity.core.util.ImmutableList.of("test");
        }
    }
}
