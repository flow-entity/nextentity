package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.ValueConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("值转换器测试")
class ValueConvertersTest {

    @Nested
    @DisplayName("IdentityValueConverter")
    class IdentityValueConverterTest {

        @Test
        @DisplayName("of() 无参返回单例")
        void of_noArgs_returnsSingleton() {
            ValueConverter<?, ?> instance1 = IdentityValueConverter.of();
            ValueConverter<?, ?> instance2 = IdentityValueConverter.of();

            assertThat(instance1).isSameAs(instance2);
        }

        @Test
        @DisplayName("of(Class) 返回指定类型的转换器")
        void of_withType_returnsTypedConverter() {
            IdentityValueConverter<String> converter = IdentityValueConverter.of(String.class);

            assertThat(converter.getDatabaseColumnType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("convertToDatabaseColumn 原样返回")
        void convertToDatabaseColumn_returnsSameValue() {
            IdentityValueConverter<String> converter = IdentityValueConverter.of(String.class);
            String value = "hello";

            assertThat(converter.convertToDatabaseColumn(value)).isSameAs(value);
        }

        @Test
        @DisplayName("convertToEntityAttribute 原样返回")
        void convertToEntityAttribute_returnsSameValue() {
            IdentityValueConverter<Integer> converter = IdentityValueConverter.of(Integer.class);
            Integer value = 42;

            assertThat(converter.convertToEntityAttribute(value)).isSameAs(value);
        }

        @Test
        @DisplayName("null 值原样返回")
        void convert_null_returnsNull() {
            IdentityValueConverter<String> converter = IdentityValueConverter.of(String.class);

            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("getDatabaseColumnType 返回传入的类型")
        void getDatabaseColumnType_returnsType() {
            IdentityValueConverter<Long> converter = IdentityValueConverter.of(Long.class);

            assertThat(converter.getDatabaseColumnType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("双向转换一致")
        void roundTrip_isConsistent() {
            IdentityValueConverter<String> converter = IdentityValueConverter.of(String.class);
            String original = "test-value";

            String dbValue = converter.convertToDatabaseColumn(original);
            String entityValue = converter.convertToEntityAttribute(dbValue);

            assertThat(entityValue).isEqualTo(original);
        }
    }

    @Nested
    @DisplayName("EnumValueConverter")
    class EnumValueConverterTest {

        enum TestStatus {
            ACTIVE, INACTIVE, PENDING
        }

        @Test
        @DisplayName("枚举转 ordinal")
        void convertToDatabaseColumn_returnsOrdinal() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThat(converter.convertToDatabaseColumn(TestStatus.ACTIVE)).isEqualTo(0);
            assertThat(converter.convertToDatabaseColumn(TestStatus.INACTIVE)).isEqualTo(1);
            assertThat(converter.convertToDatabaseColumn(TestStatus.PENDING)).isEqualTo(2);
        }

        @Test
        @DisplayName("null 枚举返回 null")
        void convertToDatabaseColumn_null_returnsNull() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        @DisplayName("ordinal 转回枚举")
        void convertToEntityAttribute_returnsEnum() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThat(converter.convertToEntityAttribute(0)).isEqualTo(TestStatus.ACTIVE);
            assertThat(converter.convertToEntityAttribute(1)).isEqualTo(TestStatus.INACTIVE);
            assertThat(converter.convertToEntityAttribute(2)).isEqualTo(TestStatus.PENDING);
        }

        @Test
        @DisplayName("null ordinal 返回 null")
        void convertToEntityAttribute_null_returnsNull() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("getDatabaseColumnType 返回 Integer.class")
        void getDatabaseColumnType_returnsInteger() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThat(converter.getDatabaseColumnType()).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("of() 静态工厂方法")
        void of_returnsConverter() {
            EnumValueConverter<? extends Enum<?>> converter = EnumValueConverter.of(TestStatus.class);

            assertThat(converter).isNotNull();
            assertThat(converter.getDatabaseColumnType()).isEqualTo(Integer.class);
        }

        @Test
        @DisplayName("越界 ordinal 抛出异常")
        void convertToEntityAttribute_outOfBounds_throwsException() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            assertThatThrownBy(() -> converter.convertToEntityAttribute(99))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("双向转换一致")
        void roundTrip_isConsistent() {
            EnumValueConverter<TestStatus> converter = new EnumValueConverter<>(TestStatus.class);

            for (TestStatus status : TestStatus.values()) {
                Integer ordinal = converter.convertToDatabaseColumn(status);
                TestStatus result = converter.convertToEntityAttribute(ordinal);
                assertThat(result).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("LocalDateTimeValueConverter")
    class LocalDateTimeValueConverterTest {

        private final LocalDateTimeValueConverter converter = LocalDateTimeValueConverter.of();

        @Test
        @DisplayName("单例验证")
        void of_returnsSingleton() {
            assertThat(LocalDateTimeValueConverter.of()).isSameAs(converter);
        }

        @Test
        @DisplayName("LocalDateTime 转 Timestamp")
        void convertToDatabaseColumn_returnsTimestamp() {
            LocalDateTime localDateTime = LocalDateTime.of(2024, 6, 15, 10, 30, 45);
            Timestamp timestamp = converter.convertToDatabaseColumn(localDateTime);

            assertThat(timestamp).isNotNull();
            assertThat(timestamp.toLocalDateTime()).isEqualTo(localDateTime);
        }

        @Test
        @DisplayName("null LocalDateTime 返回 null")
        void convertToDatabaseColumn_null_returnsNull() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        @DisplayName("Timestamp 转 LocalDateTime")
        void convertToEntityAttribute_returnsLocalDateTime() {
            Timestamp timestamp = Timestamp.valueOf("2024-06-15 10:30:45");
            LocalDateTime result = converter.convertToEntityAttribute(timestamp);

            assertThat(result).isEqualTo(LocalDateTime.of(2024, 6, 15, 10, 30, 45));
        }

        @Test
        @DisplayName("null Timestamp 返回 null")
        void convertToEntityAttribute_null_returnsNull() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("双向转换一致")
        void roundTrip_isConsistent() {
            LocalDateTime original = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
            Timestamp dbValue = converter.convertToDatabaseColumn(original);
            LocalDateTime result = converter.convertToEntityAttribute(dbValue);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("纳秒精度的双向转换一致")
        void roundTrip_withNanos_isConsistent() {
            LocalDateTime original = LocalDateTime.of(2024, 6, 15, 10, 30, 45, 123_456_789);
            Timestamp dbValue = converter.convertToDatabaseColumn(original);
            LocalDateTime result = converter.convertToEntityAttribute(dbValue);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("getDatabaseColumnType 返回 Timestamp.class")
        void getDatabaseColumnType_returnsTimestamp() {
            assertThat(converter.getDatabaseColumnType()).isEqualTo(Timestamp.class);
        }
    }

    @Nested
    @DisplayName("LocalTimeValueConverter")
    class LocalTimeValueConverterTest {

        private final LocalTimeValueConverter converter = LocalTimeValueConverter.of();

        @Test
        @DisplayName("单例验证")
        void of_returnsSingleton() {
            assertThat(LocalTimeValueConverter.of()).isSameAs(converter);
        }

        @Test
        @DisplayName("LocalTime 转 Time")
        void convertToDatabaseColumn_returnsTime() {
            LocalTime localTime = LocalTime.of(14, 30, 45);
            Time time = converter.convertToDatabaseColumn(localTime);

            assertThat(time).isNotNull();
            assertThat(time.toLocalTime()).isEqualTo(localTime);
        }

        @Test
        @DisplayName("null LocalTime 返回 null")
        void convertToDatabaseColumn_null_returnsNull() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        @DisplayName("Time 转 LocalTime")
        void convertToEntityAttribute_returnsLocalTime() {
            Time time = Time.valueOf("14:30:45");
            LocalTime result = converter.convertToEntityAttribute(time);

            assertThat(result).isEqualTo(LocalTime.of(14, 30, 45));
        }

        @Test
        @DisplayName("null Time 返回 null")
        void convertToEntityAttribute_null_returnsNull() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("双向转换一致")
        void roundTrip_isConsistent() {
            LocalTime original = LocalTime.of(8, 0, 0);
            Time dbValue = converter.convertToDatabaseColumn(original);
            LocalTime result = converter.convertToEntityAttribute(dbValue);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("纳秒部分在转换中丢失（java.sql.Time 仅保留到秒）")
        void roundTrip_withNanos_losesNanos() {
            LocalTime withNanos = LocalTime.of(14, 30, 45, 123_456_789);
            Time dbValue = converter.convertToDatabaseColumn(withNanos);
            LocalTime result = converter.convertToEntityAttribute(dbValue);

            assertThat(result).isEqualTo(LocalTime.of(14, 30, 45));
        }

        @Test
        @DisplayName("getDatabaseColumnType 返回 Time.class")
        void getDatabaseColumnType_returnsTime() {
            assertThat(converter.getDatabaseColumnType()).isEqualTo(Time.class);
        }
    }

    @Nested
    @DisplayName("LocalDateValueConverter")
    class LocalDateValueConverterTest {

        private final LocalDateValueConverter converter = LocalDateValueConverter.of();

        @Test
        @DisplayName("单例验证")
        void of_returnsSingleton() {
            assertThat(LocalDateValueConverter.of()).isSameAs(converter);
        }

        @Test
        @DisplayName("LocalDate 转 Date")
        void convertToDatabaseColumn_returnsDate() {
            LocalDate localDate = LocalDate.of(2024, 6, 15);
            Date date = converter.convertToDatabaseColumn(localDate);

            assertThat(date).isNotNull();
            assertThat(date.toLocalDate()).isEqualTo(localDate);
        }

        @Test
        @DisplayName("null LocalDate 返回 null")
        void convertToDatabaseColumn_null_returnsNull() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
        }

        @Test
        @DisplayName("Date 转 LocalDate")
        void convertToEntityAttribute_returnsLocalDate() {
            Date date = Date.valueOf("2024-06-15");
            LocalDate result = converter.convertToEntityAttribute(date);

            assertThat(result).isEqualTo(LocalDate.of(2024, 6, 15));
        }

        @Test
        @DisplayName("null Date 返回 null")
        void convertToEntityAttribute_null_returnsNull() {
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @Test
        @DisplayName("双向转换一致")
        void roundTrip_isConsistent() {
            LocalDate original = LocalDate.of(2024, 1, 1);
            Date dbValue = converter.convertToDatabaseColumn(original);
            LocalDate result = converter.convertToEntityAttribute(dbValue);

            assertThat(result).isEqualTo(original);
        }

        @Test
        @DisplayName("getDatabaseColumnType 返回 Date.class")
        void getDatabaseColumnType_returnsDate() {
            assertThat(converter.getDatabaseColumnType()).isEqualTo(Date.class);
        }
    }
}
