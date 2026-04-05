package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证TypeConverters能正确链接多个转换器
/// <p>
/// 测试场景：
/// 1. 链接多个转换器
/// 2. 当目标类型已满足时跳过转换器
/// 3. 处理基本类型包装器兼容性
/// <p>
/// 预期结果：链接的转换器正常工作
class TypeConvertersTest {

    @Nested
    class ChainConversion {

        /// 测试目标：验证链接的转换器可以转换复杂情况
        /// 测试场景：通过多个转换器进行转换
        /// 预期结果：最终转换后的值
        @Test
        void convert_WithMultipleConverters_ShouldUseAppropriateOne() {
            // given
            TypeConverter converter = TypeConverter.of(
                    NumberConverter.of(),
                    EnumConverter.of()
            );

            // when - convert string to int (via NumberConverter)
            Object result = converter.convert("42", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(42));
        }

        /// 测试目标：验证ofDefault创建默认转换器链
        /// 测试场景：使用ofDefault工厂方法
        /// 预期结果：可用的默认转换器
        @Test
        void ofDefault_ShouldCreateDefaultConverter() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();

            // when
            Object result = converter.convert("123", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(123));
        }

        /// 测试目标：验证of(List)从列表创建转换器
        /// 测试场景：传递转换器列表
        /// 预期结果：链接的转换器
        @Test
        void of_WithList_ShouldCreateConverter() {
            // given
            List<TypeConverter> converters = List.of(NumberConverter.of());

            // when
            TypeConverter converter = TypeConverter.of(converters);

            // then - should work without error
            assertThat(converter).isNotNull();
        }
    }

    @Nested
    class PrimitiveHandling {

        /// 测试目标：验证基本类型包装器兼容性
        /// 测试场景：int基本类型目标的Integer值
        /// 预期结果：Integer值（包装器）
        @Test
        void convert_IntegerForIntTarget_ShouldReturnInteger() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();
            Integer value = Integer.valueOf(42);

            // when
            Object result = converter.convert(value, int.class);

            // then
            assertThat(result).isEqualTo(value);
        }
    }

    @Nested
    class NullAndSameType {

        /// 测试目标：验证null返回null
        /// 测试场景：转换null
        /// 预期结果：null
        @Test
        void convert_Null_ShouldReturnNull() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();

            // when
            Object result = converter.convert(null, String.class);

            // then
            assertThat(result).isNull();
        }

        /// 测试目标：验证相同类型返回相同值
        /// 测试场景：将值转换为相同类型
        /// 预期结果：相同值
        @Test
        void convert_SameType_ShouldReturnSameValue() {
            // given
            TypeConverter converter = TypeConverter.ofDefault();
            String value = "test";

            // when
            Object result = converter.convert(value, String.class);

            // then
            assertThat(result).isSameAs(value);
        }
    }
}
