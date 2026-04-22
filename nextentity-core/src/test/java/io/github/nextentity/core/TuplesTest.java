package io.github.nextentity.core;

import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y Tuples utility class creates tuples 正确
/// <p>
/// 测试场景s:
/// 1. Create tuple from array
/// 2. Create tuple2
/// 3. Create tuple3
/// 4. Tuple get 方法
/// 5. Tuple size 方法
/// 6. Tuple equals and hashCode
class TuplesTest {

    @Nested
    class OfArray {

        ///
        /// 测试目标: 验证y of(Object[]) creates tuple
        /// 测试场景: Create tuple from array
        /// 预期结果: Tuple with array elements
        @Test
        void of_Array_CreatesTuple() {
            // given
            Object[] data = {"a", "b", "c"};

            // when
            Tuple tuple = Tuples.of(data);

            // then
            assertThat(tuple.size()).isEqualTo(3);
            assertThat((Object) tuple.get(0)).isEqualTo("a");
            assertThat((Object) tuple.get(1)).isEqualTo("b");
            assertThat((Object) tuple.get(2)).isEqualTo("c");
        }
    }

    @Nested
    class OfTuple2 {

        ///
        /// 测试目标: 验证y of(a, b) creates Tuple2
        /// 测试场景: Create tuple with two elements
        /// 预期结果: Tuple2 with both elements
        @Test
        void of_TwoElements_CreatesTuple2() {
            // given
            String first = "hello";
            Integer second = 42;

            // when
            Tuple2<String, Integer> tuple = Tuples.of(first, second);

            // then
            assertThat(tuple.size()).isEqualTo(2);
            assertThat((Object) tuple.get(0)).isEqualTo(first);
            assertThat((Object) tuple.get(1)).isEqualTo(second);
        }
    }

    @Nested
    class OfTuple3 {

        ///
        /// 测试目标: 验证y of(a, b, c) creates Tuple3
        /// 测试场景: Create tuple with three elements
        /// 预期结果: Tuple3 with all elements
        @Test
        void of_ThreeElements_CreatesTuple3() {
            // given
            String first = "a";
            String second = "b";
            String third = "c";

            // when
            Tuple3<String, String, String> tuple = Tuples.of(first, second, third);

            // then
            assertThat(tuple.size()).isEqualTo(3);
            assertThat((Object) tuple.get(0)).isEqualTo(first);
            assertThat((Object) tuple.get(1)).isEqualTo(second);
            assertThat((Object) tuple.get(2)).isEqualTo(third);
        }
    }

    @Nested
    class TupleMethods {

        ///
        /// 测试目标: 验证y toList() returns list
        /// 测试场景: Call toList() on tuple
        /// 预期结果: List with all elements
        @Test
        void toList_ReturnsElementsList() {
            // given
            Tuple tuple = Tuples.of("a", "b", "c");

            // when
            List<Object> list = tuple.toList();

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

        ///
        /// 测试目标: 验证y toArray() returns copy
        /// 测试场景: Call toArray() on tuple
        /// 预期结果: Array copy of elements
        @Test
        void toArray_ReturnsArrayCopy() {
            // given
            Tuple tuple = Tuples.of("a", "b", "c");

            // when
            Object[] array = tuple.toArray();

            // then
            assertThat(array).containsExactly("a", "b", "c");
        }

        ///
        /// 测试目标: 验证y equals compares content
        /// 测试场景: Compare two equal tuples
        /// 预期结果: Are equal
        @Test
        void equals_SameContent_AreEqual() {
            // given
            Tuple tuple1 = Tuples.of("a", "b");
            Tuple tuple2 = Tuples.of("a", "b");

            // when & then
            assertThat(tuple1).isEqualTo(tuple2);
            assertThat(tuple1.hashCode()).isEqualTo(tuple2.hashCode());
        }

        ///
        /// 测试目标: 验证y equals with different content
        /// 测试场景: Compare two different tuples
        /// 预期结果: Are not equal
        @Test
        void equals_DifferentContent_AreNotEqual() {
            // given
            Tuple tuple1 = Tuples.of("a", "b");
            Tuple tuple2 = Tuples.of("a", "c");

            // when & then
            assertThat(tuple1).isNotEqualTo(tuple2);
        }
    }

    @Nested
    class TupleIterator {

        ///
        /// 测试目标: 验证y iterator iterates all elements
        /// 测试场景: Iterate over tuple
        /// 预期结果: All elements in order
        @Test
        void iterator_ReturnsAllElements() {
            // given
            Tuple tuple = Tuples.of("a", "b", "c");
            java.util.Iterator<Object> iter = tuple.iterator();

            // when & then
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo("a");
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo("b");
            assertThat(iter.hasNext()).isTrue();
            assertThat(iter.next()).isEqualTo("c");
            assertThat(iter.hasNext()).isFalse();
        }
    }
}
