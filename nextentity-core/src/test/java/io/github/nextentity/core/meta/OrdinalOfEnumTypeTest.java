package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify OrdinalOfEnumType correctly converts enum to ordinal
 * <p>
 * Test scenarios:
 * 1. Constructor creates converter for enum type
 * 2. toDatabaseType converts enum to ordinal
 * 3. toAttributeType converts ordinal to enum
 * 4. databaseType returns Integer.class
 */
class OrdinalOfEnumTypeTest {

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    @Nested
    class Constructor {

        /**
         * Test objective: Verify constructor creates converter
         * Test scenario: Create converter for enum type
         * Expected result: Converter is created
         */
        @Test
        void constructor_CreatesConverter() {
            // when
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // then
            assertThat(converter).isNotNull();
        }
    }

    @Nested
    class DatabaseType {

        /**
         * Test objective: Verify databaseType returns Integer.class
         * Test scenario: Call databaseType()
         * Expected result: Integer.class
         */
        @Test
        void databaseType_ReturnsInteger() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Class<?> result = converter.databaseType();

            // then
            assertThat(result).isEqualTo(Integer.class);
        }
    }

    @Nested
    class ToDatabaseType {

        /**
         * Test objective: Verify toDatabaseType converts enum to ordinal
         * Test scenario: Convert ACTIVE enum
         * Expected result: 0 (ordinal of ACTIVE)
         */
        @Test
        void toDatabaseType_ConvertsToOrdinal() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(TestStatus.ACTIVE);

            // then
            assertThat(result).isEqualTo(0);
        }

        /**
         * Test objective: Verify toDatabaseType converts INACTIVE to ordinal 1
         * Test scenario: Convert INACTIVE enum
         * Expected result: 1 (ordinal of INACTIVE)
         */
        @Test
        void toDatabaseType_ConvertsInActiveToOrdinalOne() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(TestStatus.INACTIVE);

            // then
            assertThat(result).isEqualTo(1);
        }

        /**
         * Test objective: Verify toDatabaseType handles null
         * Test scenario: Convert null
         * Expected result: null
         */
        @Test
        void toDatabaseType_Null_ReturnsNull() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toDatabaseType(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ToAttributeType {

        /**
         * Test objective: Verify toAttributeType converts ordinal to enum
         * Test scenario: Convert ordinal 0
         * Expected result: ACTIVE enum
         */
        @Test
        void toAttributeType_ConvertsToEnum() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toAttributeType(0);

            // then
            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

        /**
         * Test objective: Verify toAttributeType converts ordinal 1
         * Test scenario: Convert ordinal 1
         * Expected result: INACTIVE enum
         */
        @Test
        void toAttributeType_ConvertsOrdinalOneToEnum() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);

            // when
            Object result = converter.toAttributeType(1);

            // then
            assertThat(result).isEqualTo(TestStatus.INACTIVE);
        }

        /**
         * Test objective: Verify toAttributeType returns non-Integer unchanged
         * Test scenario: Convert string
         * Expected result: Same string
         */
        @Test
        void toAttributeType_NonInteger_ReturnsValue() {
            // given
            OrdinalOfEnumType converter = new OrdinalOfEnumType(TestStatus.class);
            String value = "test";

            // when
            Object result = converter.toAttributeType(value);

            // then
            assertThat(result).isEqualTo(value);
        }
    }
}
