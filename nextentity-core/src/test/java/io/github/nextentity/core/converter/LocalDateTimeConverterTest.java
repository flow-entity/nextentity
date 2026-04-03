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

/// 测试目标：验证LocalDateTimeConverter能正确转换日期/时间类型
/// <p>
/// 测试场景：
/// 1. 将java.sql.Date转换为LocalDate
/// 2. 将Timestamp转换为LocalDateTime
/// 3. 将Time转换为LocalTime
/// 4. 将java.util.Date转换为java.time类型
/// <p>
/// 预期结果：日期/时间转换正常工作
class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = LocalDateTimeConverter.of();

    @Nested
    class SqlDateConversions {

        /// 测试目标：验证java.sql.Date转换为LocalDate
        /// 测试场景：将sql日期转换为本地日期
        /// 预期结果：正确的LocalDate
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

        /// 测试目标：验证Timestamp转换为LocalDateTime
        /// 测试场景：将时间戳转换为本地日期时间
        /// 预期结果：正确的LocalDateTime
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

        /// 测试目标：验证Time转换为LocalTime
        /// 测试场景：将sql时间转换为本地时间
        /// 预期结果：正确的LocalTime
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

        /// 测试目标：验证java.util.Date转换为LocalDate
        /// 测试场景：将util日期转换为本地日期
        /// 预期结果：正确的LocalDate
        @Test
        void convert_UtilDateToLocalDate_ShouldReturnLocalDate() {
            // given
            java.util.Date utilDate = Date.valueOf("2024-01-15");

            // when
            Object result = converter.convert(utilDate, LocalDate.class);

            // then
            assertThat(result).isInstanceOf(LocalDate.class);
        }

        /// 测试目标：验证java.util.Date转换为LocalDateTime
        /// 测试场景：将util日期转换为本地日期时间
        /// 预期结果：正确的LocalDateTime
        @Test
        void convert_UtilDateToLocalDateTime_ShouldReturnLocalDateTime() {
            // given
            java.util.Date utilDate = new java.util.Date();

            // when
            Object result = converter.convert(utilDate, LocalDateTime.class);

            // then
            assertThat(result).isInstanceOf(LocalDateTime.class);
        }

        /// 测试目标：验证java.util.Date转换为LocalTime
        /// 测试场景：将util日期转换为本地时间
        /// 预期结果：正确的LocalTime
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

        /// 测试目标：验证null返回null
        /// 测试场景：转换null
        /// 预期结果：null
        @Test
        void convert_Null_ShouldReturnNull() {
            // when
            Object result = converter.convert(null, LocalDate.class);

            // then
            assertThat(result).isNull();
        }

        /// 测试目标：验证相同类型返回相同值
        /// 测试场景：将LocalDate转换为LocalDate
        /// 预期结果：相同实例
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

        /// 测试目标：验证不支持的转换返回原始值
        /// 测试场景：转换不支持的类型
        /// 预期结果：原始值
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
