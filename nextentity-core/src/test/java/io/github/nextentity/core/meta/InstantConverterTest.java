//package io.github.nextentity.core.meta;
//
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Timestamp;
//import java.time.Instant;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/// //
// /// 测试目标: 验证y InstantConverter 正确 converts between Instant and Timestamp
// /// <p>
// /// 测试场景s:
// /// 1. Singleton instance
// /// 2. convertToDatabaseColumn converts Instant to Timestamp
// /// 3. convertToEntityAttribute converts Timestamp to Instant
// /// 4. getDatabaseColumnType returns Timestamp.class
//class InstantConverterTest {
//
//    @Nested
//    class SingletonInstance {
//
/////
//         /// 测试目标: 验证y of() returns singleton
//         /// 测试场景: Call of() multiple times
//         /// 预期结果: Same instance
//        @Test
//        void of_ReturnsSingleton() {
//            // when
//            InstantConverter instance1 = InstantConverter.of();
//            InstantConverter instance2 = InstantConverter.of();
//
//            // then
//            assertThat(instance1).isSameAs(instance2);
//        }
//    }
//
//    @Nested
//    class ConvertToDatabaseColumn {
//
/////
//         /// 测试目标: 验证y Instant converts to Timestamp
//         /// 测试场景: Convert specific Instant
//         /// 预期结果: Equivalent Timestamp
//        @Test
//        void convertToDatabaseColumn_ConvertsToTimestamp() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//            Instant instant = Instant.parse("2024-01-15T10:30:00Z");
//
//            // when
//            Timestamp result = converter.convertToDatabaseColumn(instant);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.toInstant()).isEqualTo(instant);
//        }
//
/////
//         /// 测试目标: 验证y null converts to null
//         /// 测试场景: Convert null Instant
//         /// 预期结果: null
//        @Test
//        void convertToDatabaseColumn_Null_ReturnsNull() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//
//            // when
//            Timestamp result = converter.convertToDatabaseColumn(null);
//
//            // then
//            assertThat(result).isNull();
//        }
//    }
//
//    @Nested
//    class ConvertToEntityAttribute {
//
/////
//         /// 测试目标: 验证y Timestamp converts to Instant
//         /// 测试场景: Convert specific Timestamp
//         /// 预期结果: Equivalent Instant
//        @Test
//        void convertToEntityAttribute_ConvertsToInstant() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//            Instant instant = Instant.parse("2024-01-15T10:30:00Z");
//            Timestamp timestamp = Timestamp.from(instant);
//
//            // when
//            Instant result = converter.convertToEntityAttribute(timestamp);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result).isEqualTo(instant);
//        }
//
/////
//         /// 测试目标: 验证y null Timestamp converts to null
//         /// 测试场景: Convert null Timestamp
//         /// 预期结果: null
//        @Test
//        void convertToEntityAttribute_Null_ReturnsNull() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//
//            // when
//            Instant result = converter.convertToEntityAttribute(null);
//
//            // then
//            assertThat(result).isNull();
//        }
//    }
//
//    @Nested
//    class GetDatabaseColumnType {
//
/////
//         /// 测试目标: 验证y getDatabaseColumnType returns Timestamp.class
//         /// 测试场景: Get database column type
//         /// 预期结果: Timestamp.class
//        @Test
//        void getDatabaseColumnType_ReturnsTimestamp() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//
//            // when
//            Class<? extends Timestamp> result = converter.getDatabaseColumnType();
//
//            // then
//            assertThat(result).isEqualTo(Timestamp.class);
//        }
//    }
//
//    @Nested
//    class RoundTrip {
//
/////
//         /// 测试目标: 验证y round-trip conversion preserves value
//         /// 测试场景: Convert Instant to Timestamp and back
//         /// 预期结果: Same Instant value
//        @Test
//        void roundTrip_PreservesValue() {
//            // given
//            InstantConverter converter = InstantConverter.of();
//            Instant original = Instant.parse("2024-06-15T14:45:30Z");
//
//            // when
//            Timestamp timestamp = converter.convertToDatabaseColumn(original);
//            Instant result = converter.convertToEntityAttribute(timestamp);
//
//            // then
//            assertThat(result).isEqualTo(original);
//        }
//    }
//}
