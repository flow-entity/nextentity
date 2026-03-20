package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify TypeCastUtil correctly performs type casting
 * <p>
 * Test scenarios:
 * 1. Cast List
 * 2. Cast ImmutableArray
 * 3. Cast Class
 * 4. Unsafe cast
 */
class TypeCastUtilTest {

    @Nested
    class CastList {

        /**
         * Test objective: Verify cast() works for List
         * Test scenario: Cast List<?> to List<String>
         * Expected result: Correctly cast list
         */
        @Test
        void cast_List_ReturnsCastedList() {
            // given
            List<?> list = Arrays.asList("a", "b", "c");

            // when
            List<String> result = TypeCastUtil.cast(list);

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }
    }

    @Nested
    class CastImmutableArray {

        /**
         * Test objective: Verify cast() works for ImmutableArray
         * Test scenario: Cast ImmutableArray<?> to ImmutableArray<String>
         * Expected result: Correctly cast array
         */
        @Test
        void cast_ImmutableArray_ReturnsCastedArray() {
            // given
            ImmutableArray<?> array = ImmutableList.of("x", "y", "z");

            // when
            ImmutableArray<String> result = TypeCastUtil.cast(array);

            // then
            assertThat(result.stream()).containsExactly("x", "y", "z");
        }
    }

    @Nested
    class CastClass {

        /**
         * Test objective: Verify cast() works for Class
         * Test scenario: Cast Class<?> to Class<String>
         * Expected result: Correctly cast class
         */
        @Test
        void cast_Class_ReturnsCastedClass() {
            // given
            Class<?> clazz = String.class;

            // when
            Class<String> result = TypeCastUtil.cast(clazz);

            // then
            assertThat(result).isEqualTo(String.class);
        }
    }

    @Nested
    class UnsafeCast {

        /**
         * Test objective: Verify unsafeCast() returns same object
         * Test scenario: Cast string to string
         * Expected result: Same object
         */
        @Test
        void unsafeCast_ReturnsSameObject() {
            // given
            String value = "test";

            // when
            String result = TypeCastUtil.unsafeCast(value);

            // then
            assertThat(result).isSameAs(value);
        }

        /**
         * Test objective: Verify unsafeCast() handles null
         * Test scenario: Cast null
         * Expected result: null
         */
        @Test
        void unsafeCast_Null_ReturnsNull() {
            // when
            Object result = TypeCastUtil.unsafeCast(null);

            // then
            assertThat(result).isNull();
        }
    }
}
