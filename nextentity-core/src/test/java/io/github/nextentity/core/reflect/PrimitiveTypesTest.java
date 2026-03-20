package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify PrimitiveTypes utility class provides correct primitive type handling
 * <p>
 * Test scenarios:
 * 1. getWrapper returns correct wrapper type for primitives
 * 2. getWrapper returns same type for non-primitives
 * 3. isBasicType returns true for primitives and wrappers
 * 4. isBasicType returns false for other types
 * <p>
 * Expected result: Primitive type mapping works correctly
 */
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

    /**
     * Test objective: Verify getWrapper returns correct wrapper for each primitive
     * Test scenario: Call getWrapper with each primitive type
     * Expected result: Corresponding wrapper type returned
     */
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void getWrapper_ForPrimitive_ShouldReturnWrapper(Class<?> primitive, Class<?> wrapper) {
        // when
        Class<?> result = PrimitiveTypes.getWrapper(primitive);

        // then
        assertThat(result).isEqualTo(wrapper);
    }

    /**
     * Test objective: Verify getWrapper returns same type for wrapper types
     * Test scenario: Call getWrapper with wrapper types
     * Expected result: Same wrapper type returned
     */
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void getWrapper_ForWrapper_ShouldReturnSameType(Class<?> primitive, Class<?> wrapper) {
        // when
        Class<?> result = PrimitiveTypes.getWrapper(wrapper);

        // then
        assertThat(result).isEqualTo(wrapper);
    }

    /**
     * Test objective: Verify getWrapper returns same type for non-primitive, non-wrapper types
     * Test scenario: Call getWrapper with String
     * Expected result: String returned unchanged
     */
    @Test
    void getWrapper_ForNonPrimitive_ShouldReturnSameType() {
        // given
        Class<?> stringType = String.class;

        // when
        Class<?> result = PrimitiveTypes.getWrapper(stringType);

        // then
        assertThat(result).isEqualTo(String.class);
    }

    /**
     * Test objective: Verify isBasicType returns true for primitive types
     * Test scenario: Check primitive types
     * Expected result: true returned
     */
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void isBasicType_ForPrimitive_ShouldReturnTrue(Class<?> primitive, Class<?> wrapper) {
        // when
        boolean result = PrimitiveTypes.isBasicType(primitive);

        // then
        assertThat(result).isTrue();
    }

    /**
     * Test objective: Verify isBasicType returns true for wrapper types
     * Test scenario: Check wrapper types
     * Expected result: true returned
     */
    @ParameterizedTest
    @MethodSource("primitiveWrapperPairs")
    void isBasicType_ForWrapper_ShouldReturnTrue(Class<?> primitive, Class<?> wrapper) {
        // when
        boolean result = PrimitiveTypes.isBasicType(wrapper);

        // then
        assertThat(result).isTrue();
    }

    /**
     * Test objective: Verify isBasicType returns false for non-basic types
     * Test scenario: Check String, Object, custom types
     * Expected result: false returned
     */
    @Test
    void isBasicType_ForNonBasicType_ShouldReturnFalse() {
        assertThat(PrimitiveTypes.isBasicType(String.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(Object.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(Number.class)).isFalse();
    }
}
