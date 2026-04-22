//package io.github.nextentity.core.meta;
//
//import io.github.nextentity.core.reflect.schema.Attribute;
//import io.github.nextentity.core.reflect.schema.Schema;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.EnumSource;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
/// // 测试目标：验证EnumConverter在枚举和序数之间正确转换
/// // <p>
/// // 测试场景：
/// // 1. convertToDatabaseColumn返回正确的序数
/// // 2. convertToEntityAttribute返回正确的枚举
/// // 3. 正确处理空值
/// // 4. getDatabaseColumnType返回Integer.class
/// // <p>
/// // 预期结果：枚举转换双向工作正常
//class EnumConverterTest {
//
//    enum TestStatus {
//        ACTIVE, INACTIVE, PENDING
//    }
//
//    private EnumConverter<TestStatus> converter;
//
//    @BeforeEach
//    void setUp() {
//        Attribute attribute = new TestAttribute(TestStatus.class);
//        converter = new EnumConverter<>(attribute);
//    }
//
//    @Nested
//    class ConvertToDatabaseColumn {
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @ParameterizedTest
//        @EnumSource(TestStatus.class)
//        void convertToDatabaseColumn_ShouldReturnOrdinal(TestStatus status) {
//            // when
//            Integer result = converter.convertToDatabaseColumn(status);
//
//            // then
//            assertThat(result).isEqualTo(status.ordinal());
//        }
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @Test
//        void convertToDatabaseColumn_WithNull_ShouldReturnNull() {
//            // when
//            Integer result = converter.convertToDatabaseColumn(null);
//
//            // then
//            assertThat(result).isNull();
//        }
//    }
//
//    @Nested
//    class ConvertToEntityAttribute {
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @Test
//        void convertToEntityAttribute_ShouldReturnEnum() {
//            assertThat(converter.convertToEntityAttribute(0)).isEqualTo(TestStatus.ACTIVE);
//            assertThat(converter.convertToEntityAttribute(1)).isEqualTo(TestStatus.INACTIVE);
//            assertThat(converter.convertToEntityAttribute(2)).isEqualTo(TestStatus.PENDING);
//        }
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @Test
//        void convertToEntityAttribute_WithNull_ShouldReturnNull() {
//            // when
//            TestStatus result = converter.convertToEntityAttribute(null);
//
//            // then
//            assertThat(result).isNull();
//        }
//    }
//
//    @Nested
//    class RoundTrip {
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @ParameterizedTest
//        @EnumSource(TestStatus.class)
//        void roundTrip_ShouldReturnSameEnum(TestStatus status) {
//            // when
//            Integer ordinal = converter.convertToDatabaseColumn(status);
//            TestStatus result = converter.convertToEntityAttribute(ordinal);
//
//            // then
//            assertThat(result).isEqualTo(status);
//        }
//    }
//
//    @Nested
//    class GetDatabaseColumnType {
//
//        /// 测试目标：验证EnumConverter在枚举和序数之间正确转换
///// <p>
///// 测试场景：
///// 1. convertToDatabaseColumn返回正确的序数
///// 2. convertToEntityAttribute返回正确的枚举
///// 3. 正确处理空值
///// 4. getDatabaseColumnType返回Integer.class
///// <p>
///// 预期结果：枚举转换双向工作正常
//        @Test
//        void getDatabaseColumnType_ShouldReturnInteger() {
//            // when
//            Class<Integer> result = converter.getDatabaseColumnType();
//
//            // then
//            assertThat(result).isEqualTo(Integer.class);
//        }
//    }
//
//    // Test helper class
//    static class TestAttribute implements Attribute {
//        private final Class<?> type;
//
//        TestAttribute(Class<?> type) {
//            this.type = type;
//        }
//
//        @Override
//        public Class<?> type() {
//            return type;
//        }
//
//        @Override
//        public String name() {
//            return "test";
//        }
//
//        @Override
//        public java.lang.reflect.Method getter() {
//            return null;
//        }
//
//        @Override
//        public java.lang.reflect.Method setter() {
//            return null;
//        }
//
//        @Override
//        public java.lang.reflect.Field field() {
//            return null;
//        }
//
//        @Override
//        public Schema declareBy() {
//            return null;
//        }
//
//        @Override
//        public int ordinal() {
//            return 0;
//        }
//
//        @Override
//        public io.github.nextentity.core.util.ImmutableList<String> path() {
//            return io.github.nextentity.core.util.ImmutableList.of("test");
//        }
//    }
//}
