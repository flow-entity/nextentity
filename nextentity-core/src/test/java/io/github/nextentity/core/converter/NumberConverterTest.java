package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证NumberConverter能正确在数字类型之间转换
/// <p>
/// 测试场景：
/// 1. 在不同数字原语之间转换
/// 2. 在包装器之间转换
/// 3. 从字符串到数字的转换
/// 4. 处理null和已正确的类型
/// 5. 转换失败时返回原始值
/// <p>
/// 预期结果：数字在类型之间正确转换
class NumberConverterTest {

    private final NumberConverter converter = NumberConverter.of();

    @Nested
    class BasicConversions {

        /// 测试目标：验证整数到长整型转换
        /// 测试场景：将Integer转换为Long
        /// 预期结果：Long值
        @Test
        void convert_IntegerToLong_ShouldReturnLong() {
            // when
            Object result = converter.convert(Integer.valueOf(42), long.class);

            // then
            assertThat(result).isEqualTo(Long.valueOf(42L));
        }

        /// 测试目标：验证带截断的双精度到整数转换
        /// 测试场景：将Double(3.14)转换为Integer
        /// 预期结果：Integer(3) - 截断值
        ///
        /// Bug #2: NumberConverter在精度丢失时返回原始值
        @Test
        void convert_DoubleToInt_ShouldTruncate() {
            // given
            Double value = Double.valueOf(3.14);

            // when
            Object result = converter.convert(value, int.class);

            // then - should truncate to integer
            assertThat(result).isEqualTo(Integer.valueOf(3));
        }

        /// 测试目标：验证长整型到双精度转换
        /// 测试场景：将Long转换为Double
        /// 预期结果：Double值
        @Test
        void convert_LongToDouble_ShouldReturnDouble() {
            // when
            Object result = converter.convert(Long.valueOf(100L), double.class);

            // then
            assertThat(result).isEqualTo(Double.valueOf(100.0));
        }
    }

    @Nested
    class SpecialCases {

        /// 测试目标：验证null返回null
        /// 测试场景：转换null值
        /// 预期结果：返回null
        @Test
        void convert_Null_ShouldReturnNull() {
            // when
            Object result = converter.convert(null, int.class);

            // then
            assertThat(result).isNull();
        }

        /// 测试目标：验证相同类型返回相同值
        /// 测试场景：将Integer转换为Integer
        /// 预期结果：返回相同值
        @Test
        void convert_SameType_ShouldReturnSameValue() {
            // given
            Integer value = Integer.valueOf(42);

            // when
            Object result = converter.convert(value, Integer.class);

            // then
            assertThat(result).isSameAs(value);
        }

        /// 测试目标：验证BigDecimal转换
        /// 测试场景：转换为BigDecimal
        /// 预期结果：BigDecimal值
        @Test
        void convert_ToBigDecimal_ShouldReturnBigDecimal() {
            // when
            Object result = converter.convert(Integer.valueOf(42), BigDecimal.class);

            // then
            assertThat(result).isEqualTo(BigDecimal.valueOf(42));
        }

        /// 测试目标：验证BigInteger转换
        /// 测试场景：转换为BigInteger
        /// 预期结果：BigInteger值
        @Test
        void convert_ToBigInteger_ShouldReturnBigInteger() {
            // when
            Object result = converter.convert(Integer.valueOf(42), BigInteger.class);

            // then
            assertThat(result).isEqualTo(BigInteger.valueOf(42));
        }
    }

    @Nested
    class StringConversions {

        /// 测试目标：验证字符串"42"到整数转换
        /// 测试场景：将字符串转换为整数
        /// 预期结果：整数42
        @Test
        void convert_StringToInt_ShouldReturnInt() {
            // when
            Object result = converter.convert("42", int.class);

            // then
            assertThat(result).isEqualTo(Integer.valueOf(42));
        }

        /// 测试目标：验证无效字符串返回不变
        /// 测试场景：将非数字字符串转换为整数
        /// 预期结果：返回原始字符串
        @Test
        void convert_InvalidString_ShouldReturnOriginal() {
            // given
            String input = "not-a-number";

            // when
            Object result = converter.convert(input, int.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

}
