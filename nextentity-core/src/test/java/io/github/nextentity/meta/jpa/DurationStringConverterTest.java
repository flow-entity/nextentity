package io.github.nextentity.meta.jpa;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y DurationStringConverter 正确 converts Duration to/from String
/// <p>
/// 测试场景s:
/// 1. Convert Duration to String
/// 2. Convert String to Duration
/// 3. Handle null values
class DurationStringConverterTest {

    private final DurationStringConverter converter = new DurationStringConverter();

    @Nested
    class ConvertToDatabaseColumn {

        @Test
        void convertToDatabaseColumn_ValidDuration_ReturnsIsoString() {
            // given
            Duration duration = Duration.ofHours(2).plusMinutes(30);

            // when
            String result = converter.convertToDatabaseColumn(duration);

            // then
            assertThat(result).isEqualTo("PT2H30M");
        }

        @Test
        void convertToDatabaseColumn_ZeroDuration_ReturnsZeroString() {
            // given
            Duration duration = Duration.ZERO;

            // when
            String result = converter.convertToDatabaseColumn(duration);

            // then
            assertThat(result).isEqualTo("PT0S");
        }

        @Test
        void convertToDatabaseColumn_Null_ReturnsNull() {
            // when
            String result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ConvertToEntityAttribute {

        @Test
        void convertToEntityAttribute_ValidString_ReturnsDuration() {
            // given
            String dbValue = "PT2H30M";

            // when
            Duration result = converter.convertToEntityAttribute(dbValue);

            // then
            assertThat(result).isEqualTo(Duration.ofHours(2).plusMinutes(30));
        }

        @Test
        void convertToEntityAttribute_ZeroString_ReturnsZeroDuration() {
            // given
            String dbValue = "PT0S";

            // when
            Duration result = converter.convertToEntityAttribute(dbValue);

            // then
            assertThat(result).isEqualTo(Duration.ZERO);
        }

        @Test
        void convertToEntityAttribute_Null_ReturnsNull() {
            // when
            Duration result = converter.convertToEntityAttribute(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class RoundTrip {

        @Test
        void roundTrip_PreservesValue() {
            // given
            Duration original = Duration.ofDays(1).plusHours(5).plusMinutes(30).plusSeconds(15);

            // when
            String dbValue = converter.convertToDatabaseColumn(original);
            Duration result = converter.convertToEntityAttribute(dbValue);

            // then
            assertThat(result).isEqualTo(original);
        }
    }
}
