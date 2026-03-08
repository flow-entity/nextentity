package io.github.nextentity.core.reflect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link PrimitiveTypes}.
 */
@DisplayName("PrimitiveTypes Tests")
class PrimitiveTypesTest {

    @Test
    @DisplayName("should get wrapper for primitive types")
    void shouldGetWrapperForPrimitiveTypes() {
        assertThat(PrimitiveTypes.getWrapper(int.class)).isEqualTo(Integer.class);
        assertThat(PrimitiveTypes.getWrapper(long.class)).isEqualTo(Long.class);
        assertThat(PrimitiveTypes.getWrapper(double.class)).isEqualTo(Double.class);
        assertThat(PrimitiveTypes.getWrapper(boolean.class)).isEqualTo(Boolean.class);
        assertThat(PrimitiveTypes.getWrapper(char.class)).isEqualTo(Character.class);
        assertThat(PrimitiveTypes.getWrapper(byte.class)).isEqualTo(Byte.class);
        assertThat(PrimitiveTypes.getWrapper(short.class)).isEqualTo(Short.class);
        assertThat(PrimitiveTypes.getWrapper(float.class)).isEqualTo(Float.class);
        assertThat(PrimitiveTypes.getWrapper(void.class)).isEqualTo(Void.class);
    }

    @Test
    @DisplayName("should return same class for non-primitive types")
    void shouldReturnSameClassForNonPrimitiveTypes() {
        assertThat(PrimitiveTypes.getWrapper(String.class)).isEqualTo(String.class);
        assertThat(PrimitiveTypes.getWrapper(Integer.class)).isEqualTo(Integer.class);
        assertThat(PrimitiveTypes.getWrapper(Object.class)).isEqualTo(Object.class);
    }

    @Test
    @DisplayName("should identify basic types")
    void shouldIdentifyBasicTypes() {
        assertThat(PrimitiveTypes.isBasicType(int.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(Integer.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(long.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(Long.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(double.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(Double.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(boolean.class)).isTrue();
        assertThat(PrimitiveTypes.isBasicType(Boolean.class)).isTrue();
    }

    @Test
    @DisplayName("should return false for non-basic types")
    void shouldReturnFalseForNonBasicTypes() {
        assertThat(PrimitiveTypes.isBasicType(String.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(Object.class)).isFalse();
        assertThat(PrimitiveTypes.isBasicType(java.util.List.class)).isFalse();
    }
}
