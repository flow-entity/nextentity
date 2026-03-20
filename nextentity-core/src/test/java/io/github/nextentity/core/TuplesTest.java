package io.github.nextentity.core;

import io.github.nextentity.api.model.Tuple;
import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test objective: Verify Tuples utility class creates tuples correctly
 * <p>
 * Test scenarios:
 * 1. Create tuple from array
 * 2. Create tuple2
 * 3. Create tuple3
 * 4. Tuple get method
 * 5. Tuple size method
 * 6. Tuple equals and hashCode
 */
class TuplesTest {

    @Nested
    class OfArray {

        /**
         * Test objective: Verify of(Object[]) creates tuple
         * Test scenario: Create tuple from array
         * Expected result: Tuple with array elements
         */
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

        /**
         * Test objective: Verify of(a, b) creates Tuple2
         * Test scenario: Create tuple with two elements
         * Expected result: Tuple2 with both elements
         */
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

        /**
         * Test objective: Verify of(a, b, c) creates Tuple3
         * Test scenario: Create tuple with three elements
         * Expected result: Tuple3 with all elements
         */
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

        /**
         * Test objective: Verify toList() returns list
         * Test scenario: Call toList() on tuple
         * Expected result: List with all elements
         */
        @Test
        void toList_ReturnsElementsList() {
            // given
            Tuple tuple = Tuples.of("a", "b", "c");

            // when
            List<Object> list = tuple.toList();

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify toArray() returns copy
         * Test scenario: Call toArray() on tuple
         * Expected result: Array copy of elements
         */
        @Test
        void toArray_ReturnsArrayCopy() {
            // given
            Tuple tuple = Tuples.of("a", "b", "c");

            // when
            Object[] array = tuple.toArray();

            // then
            assertThat(array).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify equals compares content
         * Test scenario: Compare two equal tuples
         * Expected result: Are equal
         */
        @Test
        void equals_SameContent_AreEqual() {
            // given
            Tuple tuple1 = Tuples.of("a", "b");
            Tuple tuple2 = Tuples.of("a", "b");

            // when & then
            assertThat(tuple1).isEqualTo(tuple2);
            assertThat(tuple1.hashCode()).isEqualTo(tuple2.hashCode());
        }

        /**
         * Test objective: Verify equals with different content
         * Test scenario: Compare two different tuples
         * Expected result: Are not equal
         */
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

        /**
         * Test objective: Verify iterator iterates all elements
         * Test scenario: Iterate over tuple
         * Expected result: All elements in order
         */
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
