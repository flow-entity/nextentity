package io.github.nextentity.core.meta;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify InstantConverter correctly converts between Instant and Timestamp
 * <p>
 * Test scenarios:
 * 1. Singleton instance
 * 2. convertToDatabaseColumn converts Instant to Timestamp
 * 3. convertToEntityAttribute converts Timestamp to Instant
 * 4. getDatabaseColumnType returns Timestamp.class
 */
class InstantConverterTest {

    @Nested
    class SingletonInstance {

        /**
         * Test objective: Verify of() returns singleton
         * Test scenario: Call of() multiple times
         * Expected result: Same instance
         */
        @Test
        void of_ReturnsSingleton() {
            // when
            InstantConverter instance1 = InstantConverter.of();
            InstantConverter instance2 = InstantConverter.of();

            // then
            assertThat(instance1).isSameAs(instance2);
        }
    }

    @Nested
    class ConvertToDatabaseColumn {

        /**
         * Test objective: Verify Instant converts to Timestamp
         * Test scenario: Convert specific Instant
         * Expected result: Equivalent Timestamp
         */
        @Test
        void convertToDatabaseColumn_ConvertsToTimestamp() {
            // given
            InstantConverter converter = InstantConverter.of();
            Instant instant = Instant.parse("2024-01-15T10:30:00Z");

            // when
            Timestamp result = converter.convertToDatabaseColumn(instant);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toInstant()).isEqualTo(instant);
        }

        /**
         * Test objective: Verify null converts to null
         * Test scenario: Convert null Instant
         * Expected result: null
         */
        @Test
        void convertToDatabaseColumn_Null_ReturnsNull() {
            // given
            InstantConverter converter = InstantConverter.of();

            // when
            Timestamp result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ConvertToEntityAttribute {

        /**
         * Test objective: Verify Timestamp converts to Instant
         * Test scenario: Convert specific Timestamp
         * Expected result: Equivalent Instant
         */
        @Test
        void convertToEntityAttribute_ConvertsToInstant() {
            // given
            InstantConverter converter = InstantConverter.of();
            Instant instant = Instant.parse("2024-01-15T10:30:00Z");
            Timestamp timestamp = Timestamp.from(instant);

            // when
            Instant result = converter.convertToEntityAttribute(timestamp);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(instant);
        }

        /**
         * Test objective: Verify null Timestamp converts to null
         * Test scenario: Convert null Timestamp
         * Expected result: null
         */
        @Test
        void convertToEntityAttribute_Null_ReturnsNull() {
            // given
            InstantConverter converter = InstantConverter.of();

            // when
            Instant result = converter.convertToEntityAttribute(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class GetDatabaseColumnType {

        /**
         * Test objective: Verify getDatabaseColumnType returns Timestamp.class
         * Test scenario: Get database column type
         * Expected result: Timestamp.class
         */
        @Test
        void getDatabaseColumnType_ReturnsTimestamp() {
            // given
            InstantConverter converter = InstantConverter.of();

            // when
            Class<? extends Timestamp> result = converter.getDatabaseColumnType();

            // then
            assertThat(result).isEqualTo(Timestamp.class);
        }
    }

    @Nested
    class RoundTrip {

        /**
         * Test objective: Verify round-trip conversion preserves value
         * Test scenario: Convert Instant to Timestamp and back
         * Expected result: Same Instant value
         */
        @Test
        void roundTrip_PreservesValue() {
            // given
            InstantConverter converter = InstantConverter.of();
            Instant original = Instant.parse("2024-06-15T14:45:30Z");

            // when
            Timestamp timestamp = converter.convertToDatabaseColumn(original);
            Instant result = converter.convertToEntityAttribute(timestamp);

            // then
            assertThat(result).isEqualTo(original);
        }
    }
}
