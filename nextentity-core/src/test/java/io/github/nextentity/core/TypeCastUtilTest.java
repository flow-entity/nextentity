package io.github.nextentity.core;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y TypeCastUtil 正确 performs type casting
/// <p>
/// 测试场景s:
/// 1. Cast List
/// 2. Cast ImmutableArray
/// 3. Cast Class
/// 4. Unsafe cast
class TypeCastUtilTest {

    @Nested
    class CastList {

        ///
        /// 测试目标: 验证y cast() works for List
        /// 测试场景: Cast List<?> to List<String>
        /// 预期结果: Correctly cast list
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

        ///
        /// 测试目标: 验证y cast() works for ImmutableArray
        /// 测试场景: Cast ImmutableArray<?> to ImmutableArray<String>
        /// 预期结果: Correctly cast array
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

        ///
        /// 测试目标: 验证y cast() works for Class
        /// 测试场景: Cast Class<?> to Class<String>
        /// 预期结果: Correctly cast class
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

        ///
        /// 测试目标: 验证y unsafeCast() returns same object
        /// 测试场景: Cast string to string
        /// 预期结果: Same object
        @Test
        void unsafeCast_ReturnsSameObject() {
            // given
            String value = "test";

            // when
            String result = TypeCastUtil.unsafeCast(value);

            // then
            assertThat(result).isSameAs(value);
        }

        ///
        /// 测试目标: 验证y unsafeCast() 处理 null
        /// 测试场景: Cast null
        /// 预期结果: null
        @Test
        void unsafeCast_Null_ReturnsNull() {
            // when
            Object result = TypeCastUtil.unsafeCast(null);

            // then
            assertThat(result).isNull();
        }
    }
}
