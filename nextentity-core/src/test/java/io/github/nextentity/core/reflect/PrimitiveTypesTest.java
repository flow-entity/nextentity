package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证 PrimitiveTypes 工具类提供正确的基本类型处理
/// <p>
/// 测试场景：
/// 1. getWrapper 为基本类型返回正确的包装类型
/// 2. getWrapper 为非基本类型返回相同类型
/// 3. isBasicType 对基本类型和包装类型返回 true
/// 4. isBasicType 对其他类型返回 false
/// <p>
/// 预期结果：基本类型映射正常工作
class PrimitiveTypesTest {

    static Stream<Object[]> primitiveWrapperPairs() {
        return Stream.of(
                new Object[]{boolean.class, Boolean.class},
                new Object[]{byte.class, Byte.class},
                new Object[]{char.class, Character.class},
                new Object[]{short.class, Short.class},
                new Object[]{int.class, Integer.class},
                new Object[]{long.class, Long.class},
                new Object[]{float.class, Float.class},
                new Object[]{double.class, Double.class},
                new Object[]{void.class, Void.class}
        );
    }

    /// 测试目标：验证 getWrapper 为每个基本类型返回正确的包装器
    /// 测试场景：用每个基本类型调用 getWrapper
    /// 预期结果：返回相应的包装类型
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void getWrapper_ForPrimitive_ShouldReturnWrapper(Class<?> primitive, Class<?> wrapper) {
        // when
        Class<?> result = PrimitiveTypes.getWrapper(primitive);

        // then
        assertThat(result).isEqualTo(wrapper);
    }

    /// 测试目标：验证 getWrapper 为包装类型返回相同类型
    /// 测试场景：用包装类型调用 getWrapper
    /// 预期结果：返回相同的包装类型
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void getWrapper_ForWrapper_ShouldReturnSameType(Class<?> primitive, Class<?> wrapper) {
        // when
        Class<?> result = PrimitiveTypes.getWrapper(wrapper);

        // then
        assertThat(result).isEqualTo(wrapper);
    }

    /// 测试目标：验证 getWrapper 为非基本、非包装类型返回相同类型
    /// 测试场景：用 String 调用 getWrapper
    /// 预期结果：String 不变返回
    @Test
    void getWrapper_ForNonPrimitive_ShouldReturnSameType() {
        // given
        Class<?> stringType = String.class;

        // when
        Class<?> result = PrimitiveTypes.getWrapper(stringType);

        // then
        assertThat(result).isEqualTo(String.class);
    }

    /// 测试目标：验证 isBasicType 对基本类型返回 true
    /// 测试场景：检查基本类型
    /// 预期结果：返回 true
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void isBasicType_ForPrimitive_ShouldReturnTrue(Class<?> primitive, Class<?> wrapper) {
        // when
        boolean result = PrimitiveTypes.isBasicType(primitive);

        // then
        assertThat(result).isTrue();
    }

    /// 测试目标：验证 isBasicType 对包装类型返回 true
    /// 测试场景：检查包装类型
    /// 预期结果：返回 true
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void isBasicType_ForWrapper_ShouldReturnTrue(Class<?> primitive, Class<?> wrapper) {
        // when
        boolean result = PrimitiveTypes.isBasicType(wrapper);

        // then
        assertThat(result).isTrue();
    }

    /// 测试目标：验证 isBasicType 对非基本类型返回 false
    /// 测试场景：检查 String、Object、自定义类型
    /// 预期结果：返回 false
    @Test
    void isBasicType_ForNonBasicType_ShouldReturnFalse() {
        assertThat(PrimitiveTypes.isBasicType(String.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(Object.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(Number.class)).isFalse();
    }
}