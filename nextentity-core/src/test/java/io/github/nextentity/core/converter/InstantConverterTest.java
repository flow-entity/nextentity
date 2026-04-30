package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// 测试目标：验证 InstantConverter 在 Instant 与 Timestamp 之间的转换逻辑
/// <p>
/// 测试场景：
/// 1. of() 单例验证
/// 2. Instant → Timestamp 正向转换
/// 3. Timestamp → Instant 反向转换
/// 4. null 输入处理
/// 5. 双向转换一致性
/// 6. 数据库列类型元数据
/// 7. 纪元时间和极值边界
class InstantConverterTest {

    private final InstantConverter converter = InstantConverter.of();

    @Nested
    class Singleton {

        /// 测试目标：验证 of() 多次调用返回同一实例
        @Test
        void of_ShouldReturnSameInstance() {
            // when
            InstantConverter first = InstantConverter.of();
            InstantConverter second = InstantConverter.of();

            // then
            assertThat(first).isSameAs(second);
        }
    }

    @Nested
    class ConvertToDatabaseColumn {

        /// 测试目标：验证 Instant 正确转换为 Timestamp
        @Test
        void shouldConvertInstantToTimestamp() {
            // given
            Instant instant = Instant.parse("2024-06-15T10:30:45.123456789Z");

            // when
            Timestamp result = converter.convertToDatabaseColumn(instant);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toInstant()).isEqualTo(instant);
        }

        /// 测试目标：验证 null 输入返回 null
        @Test
        void shouldReturnNullWhenInputIsNull() {
            // when
            Timestamp result = converter.convertToDatabaseColumn(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ConvertToEntityAttribute {

        /// 测试目标：验证 Timestamp 正确转换为 Instant
        @Test
        void shouldConvertTimestampToInstant() {
            // given
            Timestamp timestamp = Timestamp.valueOf("2024-06-15 10:30:45.123456789");

            // when
            Instant result = converter.convertToEntityAttribute(timestamp);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(timestamp.toInstant());
        }

        /// 测试目标：验证 null 输入返回 null
        @Test
        void shouldReturnNullWhenInputIsNull() {
            // when
            Instant result = converter.convertToEntityAttribute(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class RoundTripConsistency {

        /// 测试目标：验证 Instant → Timestamp → Instant 后值相等
        @Test
        void shouldPreserveValueAfterRoundTrip() {
            // given
            Instant original = Instant.parse("2024-06-15T10:30:45.123456789Z");

            // when
            Timestamp toDb = converter.convertToDatabaseColumn(original);
            Instant backToEntity = converter.convertToEntityAttribute(toDb);

            // then
            assertThat(backToEntity).isEqualTo(original);
        }
    }

    @Nested
    class DatabaseColumnType {

        /// 测试目标：验证 getDatabaseColumnType 返回 Timestamp.class
        @Test
        void shouldReturnTimestampClass() {
            // when
            Class<? extends Timestamp> type = converter.getDatabaseColumnType();

            // then
            assertThat(type).isEqualTo(Timestamp.class);
        }
    }

    @Nested
    class BoundaryValues {

        /// 测试目标：验证纪元时间（Epoch）的转换
        @Test
        void shouldConvertEpochInstant() {
            // given
            Instant epoch = Instant.EPOCH;

            // when
            Timestamp toDb = converter.convertToDatabaseColumn(epoch);
            Instant backToEntity = converter.convertToEntityAttribute(toDb);

            // then
            assertThat(backToEntity).isEqualTo(epoch);
        }

        /// 测试目标：验证 Instant.MAX 转换时抛出异常
        /// Timestamp 无法表示 Instant.MAX（超出年份范围），抛出 IllegalArgumentException
        @Test
        void shouldThrowWhenConvertingInstantMax() {
            // when / then
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Instant.MAX))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        /// 测试目标：验证 Instant.MIN 转换时抛出异常
        /// Timestamp 无法表示 Instant.MIN（超出年份范围），抛出 IllegalArgumentException
        @Test
        void shouldThrowWhenConvertingInstantMin() {
            // when / then
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Instant.MIN))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
