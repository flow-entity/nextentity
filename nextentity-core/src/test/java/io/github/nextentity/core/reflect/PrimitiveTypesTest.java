package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// PrimitiveTypes 单元测试
///
/// 覆盖场景：
/// 1. getWrapper：9 种基本类型转换为对应包装类
/// 2. getWrapper：非基本类型原样返回
/// 3. getWrapper：null 输入返回 null
/// 4. isBasicType：9 种基本类型返回 true
/// 5. isBasicType：9 种包装类返回 true
/// 6. isBasicType：非基本类型返回 false
/// 7. isBasicType：null 输入抛出 NullPointerException
@DisplayName("PrimitiveTypes")
class PrimitiveTypesTest {

    private static Stream<Arguments> primitiveAndWrapperPairs() {
        return Stream.of(
                Arguments.of(Boolean.TYPE, Boolean.class, "boolean"),
                Arguments.of(Character.TYPE, Character.class, "char"),
                Arguments.of(Byte.TYPE, Byte.class, "byte"),
                Arguments.of(Short.TYPE, Short.class, "short"),
                Arguments.of(Integer.TYPE, Integer.class, "int"),
                Arguments.of(Long.TYPE, Long.class, "long"),
                Arguments.of(Float.TYPE, Float.class, "float"),
                Arguments.of(Double.TYPE, Double.class, "double"),
                Arguments.of(Void.TYPE, Void.class, "void")
        );
    }

    private static Stream<Arguments> primitiveTypes() {
        return Stream.of(
                Arguments.of(Boolean.TYPE, "boolean"),
                Arguments.of(Character.TYPE, "char"),
                Arguments.of(Byte.TYPE, "byte"),
                Arguments.of(Short.TYPE, "short"),
                Arguments.of(Integer.TYPE, "int"),
                Arguments.of(Long.TYPE, "long"),
                Arguments.of(Float.TYPE, "float"),
                Arguments.of(Double.TYPE, "double"),
                Arguments.of(Void.TYPE, "void")
        );
    }

    private static Stream<Arguments> wrapperTypes() {
        return Stream.of(
                Arguments.of(Boolean.class, "Boolean"),
                Arguments.of(Character.class, "Character"),
                Arguments.of(Byte.class, "Byte"),
                Arguments.of(Short.class, "Short"),
                Arguments.of(Integer.class, "Integer"),
                Arguments.of(Long.class, "Long"),
                Arguments.of(Float.class, "Float"),
                Arguments.of(Double.class, "Double"),
                Arguments.of(Void.class, "Void")
        );
    }

    @Nested
    @DisplayName("getWrapper()")
    class GetWrapperTests {

        @ParameterizedTest(name = "{2} -> {1}.class")
        @MethodSource("io.github.nextentity.core.reflect.PrimitiveTypesTest#primitiveAndWrapperPairs")
        @DisplayName("基本类型转换为对应包装类")
        void shouldReturnWrapperForPrimitiveType(Class<?> primitive, Class<?> wrapper, String name) {
            assertThat(PrimitiveTypes.getWrapper(primitive)).isSameAs(wrapper);
        }

        @ParameterizedTest
        @MethodSource("io.github.nextentity.core.reflect.PrimitiveTypesTest#wrapperTypes")
        @DisplayName("包装类原样返回")
        void shouldReturnSameForWrapperType(Class<?> wrapper) {
            assertThat(PrimitiveTypes.getWrapper(wrapper)).isSameAs(wrapper);
        }

        @Test
        @DisplayName("非基本类型原样返回")
        void shouldReturnSameForNonPrimitiveType() {
            assertThat(PrimitiveTypes.getWrapper(String.class)).isSameAs(String.class);
            assertThat(PrimitiveTypes.getWrapper(Object.class)).isSameAs(Object.class);
            assertThat(PrimitiveTypes.getWrapper(List.class)).isSameAs(List.class);
            assertThat(PrimitiveTypes.getWrapper(int[].class)).isSameAs(int[].class);
        }

        @Test
        @DisplayName("null 输入返回 null")
        void shouldReturnNullForNullInput() {
            assertThat(PrimitiveTypes.getWrapper(null)).isNull();
        }
    }

    @Nested
    @DisplayName("isBasicType()")
    class IsBasicTypeTests {

        @ParameterizedTest(name = "{1} 是基本类型")
        @MethodSource("io.github.nextentity.core.reflect.PrimitiveTypesTest#primitiveTypes")
        @DisplayName("基本类型返回 true")
        void shouldReturnTrueForPrimitiveType(Class<?> primitive, String name) {
            assertThat(PrimitiveTypes.isBasicType(primitive)).isTrue();
        }

        @ParameterizedTest(name = "{1} 是包装类")
        @MethodSource("io.github.nextentity.core.reflect.PrimitiveTypesTest#wrapperTypes")
        @DisplayName("包装类返回 true")
        void shouldReturnTrueForWrapperType(Class<?> wrapper, String name) {
            assertThat(PrimitiveTypes.isBasicType(wrapper)).isTrue();
        }

        @Test
        @DisplayName("非基本类型返回 false")
        void shouldReturnFalseForNonBasicType() {
            assertThat(PrimitiveTypes.isBasicType(String.class)).isFalse();
            assertThat(PrimitiveTypes.isBasicType(Object.class)).isFalse();
            assertThat(PrimitiveTypes.isBasicType(List.class)).isFalse();
            assertThat(PrimitiveTypes.isBasicType(int[].class)).isFalse();
            assertThat(PrimitiveTypes.isBasicType(PrimitiveTypesTest.class)).isFalse();
        }

        @Test
        @DisplayName("null 输入抛出 NullPointerException")
        void shouldThrowNPEForNullInput() {
            assertThatThrownBy(() -> PrimitiveTypes.isBasicType(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
