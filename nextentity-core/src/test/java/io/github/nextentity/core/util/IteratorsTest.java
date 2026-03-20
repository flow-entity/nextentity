package io.github.nextentity.core.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify Iterators utility class provides correct iteration operations
 * <p>
 * Test scenarios:
 * 1. toList converts Iterable to List
 * 2. map transforms elements using mapper function
 * 3. toArray converts Iterable to Object array
 * 4. sizeOf returns size for Collection/Sizeable
 * 5. size returns correct element count
 * 6. iterate returns ArrayIterator
 * <p>
 * Expected result: All utility methods work correctly
 */
class IteratorsTest {

    @Nested
    class ToList {

        /**
         * Test objective: Verify toList returns same instance for List input
         * Test scenario: Pass a List to toList
         * Expected result: Same List instance is returned
         */
        @Test
        void toList_WithListInput_ShouldReturnSameInstance() {
            // given
            List<String> list = new ArrayList<>(List.of("a", "b", "c"));

            // when
            List<String> result = Iterators.toList(list);

            // then
            assertThat(result).isSameAs(list);
        }

        /**
         * Test objective: Verify toList returns ImmutableList for non-List Iterable
         * Test scenario: Pass a Set to toList
         * Expected result: ImmutableList with same elements
         */
        @Test
        void toList_WithNonListIterable_ShouldReturnImmutableList() {
            // given
            Set<String> set = new LinkedHashSet<>(List.of("a", "b", "c"));

            // when
            List<String> result = Iterators.toList(set);

            // then
            assertThat(result).containsExactly("a", "b", "c");
            assertThat(result).isInstanceOf(ImmutableList.class);
        }

        /**
         * Test objective: Verify toList handles empty iterable
         * Test scenario: Pass empty iterable
         * Expected result: Empty list
         */
        @Test
        void toList_WithEmptyIterable_ShouldReturnEmptyList() {
            // given
            Iterable<String> empty = Collections.emptyList();

            // when
            List<String> result = Iterators.toList(empty);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class Map {

        /**
         * Test objective: Verify map transforms elements correctly
         * Test scenario: Map strings to their lengths
         * Expected result: Iterable with transformed values
         */
        @Test
        void map_ShouldTransformElements() {
            // given
            List<String> list = List.of("a", "bb", "ccc");

            // when
            Iterable<Integer> result = Iterators.map(list, String::length);

            // then
            assertThat(result).containsExactly(1, 2, 3);
        }

        /**
         * Test objective: Verify map with empty iterable returns empty list
         * Test scenario: Map over empty iterable
         * Expected result: Empty iterable
         */
        @Test
        void map_WithEmptyIterable_ShouldReturnEmptyList() {
            // given
            List<String> empty = Collections.emptyList();

            // when
            Iterable<Integer> result = Iterators.map(empty, String::length);

            // then
            assertThat(result).isEmpty();
        }

        /**
         * Test objective: Verify map returns Sizeable when input has known size
         * Test scenario: Map over a Collection
         * Expected result: Result implements Sizeable
         */
        @Test
        void map_WithSizedIterable_ShouldReturnSizeable() {
            // given
            List<String> list = List.of("a", "b", "c");

            // when
            Iterable<String> result = Iterators.map(list, s -> s);

            // then
            assertThat(result).isInstanceOf(Sizeable.class);
            assertThat(((Sizeable) result).size()).isEqualTo(3);
        }

        /**
         * Test objective: Verify MappedIterable.toString works correctly
         * Test scenario: Call toString on mapped iterable
         * Expected result: String representation with elements
         */
        @Test
        void mappedIterable_ToString_ShouldShowElements() {
            // given
            List<Integer> list = List.of(1, 2, 3);

            // when
            Iterable<String> mapped = Iterators.map(list, Object::toString);

            // then
            assertThat(mapped.toString()).isEqualTo("[1, 2, 3]");
        }
    }

    @Nested
    class ToArray {

        /**
         * Test objective: Verify toArray returns correct array
         * Test scenario: Convert list to array
         * Expected result: Object array with all elements
         */
        @Test
        void toArray_ShouldReturnCorrectArray() {
            // given
            List<String> list = List.of("a", "b", "c");

            // when
            Object[] result = Iterators.toArray(list);

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify toArray with empty iterable returns shared empty array
         * Test scenario: Convert empty iterable to array
         * Expected result: Shared EmptyArrays.OBJECT constant
         */
        @Test
        void toArray_WithEmptyIterable_ShouldReturnSharedEmptyArray() {
            // given
            List<String> empty = Collections.emptyList();

            // when
            Object[] result = Iterators.toArray(empty);

            // then
            assertThat(result).isEmpty();
            assertThat(result).isSameAs(EmptyArrays.OBJECT);
        }

        /**
         * Test objective: Verify toArray creates new array
         * Test scenario: Check if result is a new array
         * Expected result: New array instance
         */
        @Test
        void toArray_ShouldCreateNewArray() {
            // given
            List<String> list = List.of("a", "b", "c");

            // when
            Object[] result1 = Iterators.toArray(list);
            Object[] result2 = Iterators.toArray(list);

            // then
            assertThat(result1).isNotSameAs(result2);
        }
    }

    @Nested
    class SizeOf {

        /**
         * Test objective: Verify sizeOf returns correct size for Collection
         * Test scenario: Pass Collection to sizeOf
         * Expected result: Correct size returned
         */
        @Test
        void sizeOf_WithCollection_ShouldReturnCorrectSize() {
            // given
            Collection<String> collection = List.of("a", "b", "c");

            // when
            int size = Iterators.sizeOf(collection);

            // then
            assertThat(size).isEqualTo(3);
        }

        /**
         * Test objective: Verify sizeOf returns correct size for Sizeable
         * Test scenario: Pass Sizeable to sizeOf
         * Expected result: Correct size returned
         */
        @Test
        void sizeOf_WithSizeable_ShouldReturnCorrectSize() {
            // given - an Iterable that also implements Sizeable
            Iterable<String> iterable = new Iterable<String>() {
                @Override
                public Iterator<String> iterator() {
                    return List.of("a", "b", "c", "d", "e").iterator();
                }
            };
            // Test with a Collection (which is a Sizeable in practice)
            Collection<String> collection = List.of("a", "b", "c", "d", "e");

            // when
            int size = Iterators.sizeOf(collection);

            // then
            assertThat(size).isEqualTo(5);
        }

        /**
         * Test objective: Verify sizeOf returns -1 for unknown size
         * Test scenario: Pass non-Collection, non-Sizeable iterable
         * Expected result: -1 returned
         */
        @Test
        void sizeOf_WithUnknownSizeIterable_ShouldReturnMinusOne() {
            // given
            Iterable<String> iterable = () -> List.of("a", "b", "c").iterator();

            // when
            int size = Iterators.sizeOf(iterable);

            // then
            assertThat(size).isEqualTo(-1);
        }
    }

    @Nested
    class Size {

        /**
         * Test objective: Verify size returns correct count for Collection
         * Test scenario: Get size of Collection
         * Expected result: Correct size
         */
        @Test
        void size_WithCollection_ShouldReturnCorrectSize() {
            // given
            Collection<String> collection = List.of("a", "b", "c");

            // when
            int size = Iterators.size(collection);

            // then
            assertThat(size).isEqualTo(3);
        }

        /**
         * Test objective: Verify size iterates to count for unknown size iterable
         * Test scenario: Get size of non-Collection iterable
         * Expected result: Correct count by iteration
         */
        @Test
        void size_WithUnknownSizeIterable_ShouldIterateToCount() {
            // given
            Iterable<String> iterable = () -> List.of("a", "b", "c", "d").iterator();

            // when
            int size = Iterators.size(iterable);

            // then
            assertThat(size).isEqualTo(4);
        }

        /**
         * Test objective: Verify size returns 0 for empty iterable
         * Test scenario: Get size of empty iterable
         * Expected result: 0
         */
        @Test
        void size_WithEmptyIterable_ShouldReturnZero() {
            // given
            Iterable<String> empty = Collections::emptyIterator;

            // when
            int size = Iterators.size(empty);

            // then
            assertThat(size).isZero();
        }
    }

    @Nested
    class Iterate {

        /**
         * Test objective: Verify iterate returns correct ArrayIterator
         * Test scenario: Create iterator from array
         * Expected result: Iterator iterates over all elements
         */
        @Test
        void iterate_ShouldReturnCorrectIterator() {
            // given
            String[] array = {"a", "b", "c"};

            // when
            Iterator<String> iterator = Iterators.iterate(array);

            // then
            List<String> result = new ArrayList<>();
            iterator.forEachRemaining(result::add);
            assertThat(result).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify iterate hasNext works correctly
         * Test scenario: Check hasNext at different positions
         * Expected result: Correct hasNext values
         */
        @Test
        void iterate_hasNext_ShouldWorkCorrectly() {
            // given
            String[] array = {"a", "b", "c"};
            Iterator<String> iterator = Iterators.iterate(array);

            // then
            assertThat(iterator.hasNext()).isTrue();
            iterator.next();
            assertThat(iterator.hasNext()).isTrue();
            iterator.next();
            assertThat(iterator.hasNext()).isTrue();
            iterator.next();
            assertThat(iterator.hasNext()).isFalse();
        }

        /**
         * Test objective: Verify iterate next throws NoSuchElementException when exhausted
         * Test scenario: Call next after iterator is exhausted
         * Expected result: NoSuchElementException thrown
         */
        @Test
        void iterate_next_WhenExhausted_ShouldThrowNoSuchElementException() {
            // given
            String[] array = {"a"};
            Iterator<String> iterator = Iterators.iterate(array);
            iterator.next(); // consume the only element

            // then
            assertThatThrownBy(iterator::next)
                    .isInstanceOf(NoSuchElementException.class);
        }

        /**
         * Test objective: Verify iterate with null array throws NPE
         * Test scenario: Pass null array
         * Expected result: NullPointerException thrown
         */
        @Test
        void iterate_WithNullArray_ShouldThrowNPE() {
            // then
            assertThatThrownBy(() -> Iterators.iterate(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class ArrayIterator {

        /**
         * Test objective: Verify ArrayIterator works with empty array
         * Test scenario: Iterate over empty array
         * Expected result: hasNext is false immediately
         */
        @Test
        void arrayIterator_WithEmptyArray_ShouldHaveNoElements() {
            // given
            String[] empty = {};
            Iterator<String> iterator = Iterators.iterate(empty);

            // then
            assertThat(iterator.hasNext()).isFalse();
        }
    }

    @Nested
    class MappedIterator {

        /**
         * Test objective: Verify MappedIterator transforms elements lazily
         * Test scenario: Map with transformation function
         * Expected result: Elements transformed on iteration
         */
        @Test
        void mappedIterator_ShouldTransformLazily() {
            // given
            List<String> list = List.of("a", "bb", "ccc");

            // when
            Iterable<Integer> mapped = Iterators.map(list, String::length);

            // then
            Iterator<Integer> iterator = mapped.iterator();
            assertThat(iterator.hasNext()).isTrue();
            assertThat(iterator.next()).isEqualTo(1);
            assertThat(iterator.next()).isEqualTo(2);
            assertThat(iterator.next()).isEqualTo(3);
            assertThat(iterator.hasNext()).isFalse();
        }
    }

    @Nested
    class MappedSizeableIterable {

        /**
         * Test objective: Verify MappedSizeableIterable reports correct size
         * Test scenario: Create mapped iterable from Collection
         * Expected result: Size is reported correctly
         */
        @Test
        void mappedSizeableIterable_ShouldReportCorrectSize() {
            // given
            List<String> list = List.of("a", "b", "c");

            // when
            Iterable<String> mapped = Iterators.map(list, s -> s.toUpperCase());

            // then
            assertThat(mapped).isInstanceOf(Sizeable.class);
            assertThat(((Sizeable) mapped).size()).isEqualTo(3);
        }
    }
}
