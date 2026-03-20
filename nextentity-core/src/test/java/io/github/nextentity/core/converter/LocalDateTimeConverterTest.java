package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify LocalDateTimeConverter correctly converts date/time types
 * <p>
 * Test scenarios:
 * 1. Convert java.sql.Date to LocalDate
 * 2. Convert Timestamp to LocalDateTime
 * 3. Convert Time to LocalTime
 * 4. Convert java.util.Date to java.time types
 * <p>
 * Expected result: Date/time conversions work correctly
 */
class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = LocalDateTimeConverter.of();

    @Nested
    class SqlDateConversions {

        /**
         * Test objective: Verify java.sql.Date converts to LocalDate
         * Test scenario: Convert sql date to local date
         * Expected result: Correct LocalDate
         */
        @Test
        void convert_SqlDateToLocalDate_ShouldReturnLocalDate() {
            // given
            Date sqlDate = Date.valueOf("2024-01-15");

            // when
            Object result = converter.convert(sqlDate, LocalDate.class);

            // then
            assertThat(result).isEqualTo(LocalDate.of(2024, 1, 15));
        }
    }

    @Nested
    class TimestampConversions {

        /**
         * Test objective: Verify Timestamp converts to LocalDateTime
         * Test scenario: Convert timestamp to local datetime
         * Expected result: Correct LocalDateTime
         */
        @Test
        void convert_TimestampToLocalDateTime_ShouldReturnLocalDateTime() {
            // given
            Timestamp timestamp = Timestamp.valueOf("2024-01-15 10:30:45");

            // when
            Object result = converter.convert(timestamp, LocalDateTime.class);

            // then
            assertThat(result).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30, 45));
        }
    }

    @Nested
    class TimeConversions {

        /**
         * Test objective: Verify Time converts to LocalTime
         * Test scenario: Convert sql time to local time
         * Expected result: Correct LocalTime
         */
        @Test
        void convert_TimeToLocalTime_ShouldReturnLocalTime() {
            // given
            Time time = Time.valueOf("14:30:45");

            // when
            Object result = converter.convert(time, LocalTime.class);

            // then
            assertThat(result).isEqualTo(LocalTime.of(14, 30, 45));
        }
    }

    @Nested
    class UtilDateConversions {

        /**
         * Test objective: Verify java.util.Date converts to LocalDate
         * Test scenario: Convert util date to local date
         * Expected result: Correct LocalDate
         */
        @Test
        void convert_UtilDateToLocalDate_ShouldReturnLocalDate() {
            // given
            java.util.Date utilDate = Date.valueOf("2024-01-15");

            // when
            Object result = converter.convert(utilDate, LocalDate.class);

            // then
            assertThat(result).isInstanceOf(LocalDate.class);
        }

        /**
         * Test objective: Verify java.util.Date converts to LocalDateTime
         * Test scenario: Convert util date to local datetime
         * Expected result: Correct LocalDateTime
         */
        @Test
        void convert_UtilDateToLocalDateTime_ShouldReturnLocalDateTime() {
            // given
            java.util.Date utilDate = new java.util.Date();

            // when
            Object result = converter.convert(utilDate, LocalDateTime.class);

            // then
            assertThat(result).isInstanceOf(LocalDateTime.class);
        }

        /**
         * Test objective: Verify java.util.Date converts to LocalTime
         * Test scenario: Convert util date to local time
         * Expected result: Correct LocalTime
         */
        @Test
        void convert_UtilDateToLocalTime_ShouldReturnLocalTime() {
            // given
            java.util.Date utilDate = new java.util.Date();

            // when
            Object result = converter.convert(utilDate, LocalTime.class);

            // then
            assertThat(result).isInstanceOf(LocalTime.class);
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
            // when
            Object result = converter.convert(null, LocalDate.class);

            // then
            assertThat(result).isNull();
        }

        /**
         * Test objective: Verify same type returns same value
         * Test scenario: Convert LocalDate to LocalDate
         * Expected result: Same instance
         */
        @Test
        void convert_SameType_ShouldReturnSameValue() {
            // given
            LocalDate date = LocalDate.now();

            // when
            Object result = converter.convert(date, LocalDate.class);

            // then
            assertThat(result).isSameAs(date);
        }
    }

    @Nested
    class UnhandledConversions {

        /**
         * Test objective: Verify unsupported conversions return original
         * Test scenario: Convert unsupported type
         * Expected result: Original value
         */
        @Test
        void convert_UnsupportedType_ShouldReturnOriginal() {
            // given
            String input = "not-a-date";

            // when
            Object result = converter.convert(input, LocalDate.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }
}
