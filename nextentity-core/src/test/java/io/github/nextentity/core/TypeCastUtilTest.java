package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link TypeCastUtil}.
 */
@DisplayName("TypeCastUtil Tests")
class TypeCastUtilTest {

    @Test
    @DisplayName("should cast list type")
    void shouldCastListType() {
        List<?> source = Arrays.asList("a", "b", "c");
        List<String> result = TypeCastUtil.cast(source);

        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("should cast immutable array type")
    void shouldCastImmutableArrayType() {
        ImmutableArray<?> source = ImmutableList.of("a", "b", "c");
        ImmutableArray<String> result = TypeCastUtil.cast(source);

        assertThat(result).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("should cast class type")
    void shouldCastClassType() {
        Class<?> source = String.class;
        Class<String> result = TypeCastUtil.cast(source);

        assertThat(result).isEqualTo(String.class);
    }

    @Test
    @DisplayName("should unsafe cast object")
    void shouldUnsafeCastObject() {
        Object source = "test string";
        String result = TypeCastUtil.unsafeCast(source);

        assertThat(result).isEqualTo("test string");
    }

    @Test
    @DisplayName("should unsafe cast null to null")
    void shouldUnsafeCastNullToNull() {
        String result = TypeCastUtil.unsafeCast(null);

        assertThat(result).isNull();
    }
}
