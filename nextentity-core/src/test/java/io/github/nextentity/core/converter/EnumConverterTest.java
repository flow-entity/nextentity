package io.github.nextentity.core.converter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证EnumConverter能正确转换为枚举值
/// <p>
/// 测试场景：
/// 1. 将字符串转换为枚举
/// 2. 将整数（序数）转换为枚举
/// 3. 转换已正确的枚举值
/// 4. 转换失败时返回原始值
/// <p>
/// 预期结果：枚举值被正确转换
class EnumConverterTest {

    private final EnumConverter converter = EnumConverter.of();

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    @Nested
    class StringConversions {

        /// 测试目标：验证字符串转换为枚举
        /// 测试场景：将"ACTIVE"转换为TestStatus.ACTIVE
        /// 预期结果：TestStatus.ACTIVE
        ///
        /// Bug #3: ReflectUtil.getEnum(String)方法调用错误
        @Test
        void convert_StringToEnum_ShouldReturnEnum() {
            // given
            String input = "ACTIVE";

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(TestStatus.ACTIVE);
        }

        /// 测试目标：验证无效字符串返回原始值
        /// 测试场景：将无效字符串转换为枚举
        /// 预期结果：返回原始字符串
        @Test
        void convert_InvalidString_ShouldReturnOriginal() {
            // given
            String input = "INVALID_STATUS";

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    class OrdinalConversions {

        /// 测试目标：验证整数转换为枚举
        /// 测试场景：将序数1转换为枚举
        /// 预期结果：序数为1的枚举
        @Test
        void convert_IntegerToEnum_ShouldReturnEnum() {
            // when
            Object result = converter.convert(Integer.valueOf(1), TestStatus.class);

            // then
            assertThat(result).isEqualTo(TestStatus.INACTIVE);
        }

        /// 测试目标：验证超出范围的序数返回原始值
        /// 测试场景：将无效序数转换为枚举
        /// 预期结果：返回原始值
        @Test
        void convert_InvalidOrdinal_ShouldReturnOriginal() {
            // given
            Integer input = Integer.valueOf(100);

            // when
            Object result = converter.convert(input, TestStatus.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }

    @Nested
    class SameTypeConversion {

        /// 测试目标：验证相同枚举类型返回不变
        /// 测试场景：将枚举转换为相同类型
        /// 预期结果：返回相同枚举
        @ParameterizedTest
        @EnumSource(TestStatus.class)
        void convert_SameEnumType_ShouldReturnSameValue(TestStatus status) {
            // when
            Object result = converter.convert(status, TestStatus.class);

            // then
            assertThat(result).isSameAs(status);
        }
    }

    @Nested
    class NullHandling {

        /// 测试目标：验证null返回null
        /// 测试场景：将null转换为枚举
        /// 预期结果：返回null
        @Test
        void convert_Null_ShouldReturnNull() {
            // when
            Object result = converter.convert(null, TestStatus.class);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class NonEnumTarget {

        /// 测试目标：验证非枚举目标返回原始值
        /// 测试场景：转换为非枚举类型
        /// 预期结果：返回原始值
        @Test
        void convert_NonEnumTarget_ShouldReturnOriginal() {
            // given
            String input = "test";

            // when
            Object result = converter.convert(input, String.class);

            // then
            assertThat(result).isEqualTo(input);
        }
    }
}
