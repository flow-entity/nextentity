package io.github.nextentity.core.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify ImmutableList provides correct immutable list behavior
 * <p>
 * Test scenarios:
 * 1. Factory methods create correct lists
 * 2. List operations are read-only
 * 3. Modification operations throw UnsupportedOperationException
 * 4. Iterator behavior is correct
 * 5. SubList works correctly
 * <p>
 * Expected result: ImmutableList is truly immutable and behaves correctly
 */
class ImmutableListTest {

    @Nested
    class FactoryMethods {

        /**
         * Test objective: Verify of() creates list with given elements
         * Test scenario: Create list with multiple elements
         * Expected result: List contains all elements in order
         */
        @Test
        void of_WithElements_ShouldContainAllElements() {
            // when
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThat(list).containsExactly("a", "b", "c");
            assertThat(list.size()).isEqualTo(3);
        }

        /**
         * Test objective: Verify of() with no args returns empty list
         * Test scenario: Call of() with no arguments
         * Expected result: Empty list is returned
         */
        @Test
        void of_WithNoArgs_ShouldReturnEmptyList() {
            // when
            ImmutableList<String> list = ImmutableList.of();

            // then
            assertThat(list).isEmpty();
            assertThat(list).isSameAs(ImmutableList.empty());
        }

        /**
         * Test objective: Verify empty() returns same instance
         * Test scenario: Call empty() multiple times
         * Expected result: Same instance is returned
         */
        @Test
        void empty_ShouldReturnSameInstance() {
            // when
            ImmutableList<String> empty1 = ImmutableList.empty();
            ImmutableList<String> empty2 = ImmutableList.empty();

            // then
            assertThat(empty1).isSameAs(empty2);
        }

        /**
         * Test objective: Verify ofIterable with Collection works correctly
         * Test scenario: Create ImmutableList from ArrayList
         * Expected result: List contains all elements from collection
         */
        @Test
        void ofIterable_WithCollection_ShouldContainAllElements() {
            // given
            List<String> source = new ArrayList<>(List.of("x", "y", "z"));

            // when
            ImmutableList<String> list = ImmutableList.ofIterable(source);

            // then
            assertThat(list).containsExactly("x", "y", "z");
        }

        /**
         * Test objective: Verify ofIterable with non-Collection iterable works
         * Test scenario: Create ImmutableList from a Set iterator
         * Expected result: List contains all elements from iterable
         */
        @Test
        void ofIterable_WithNonCollection_ShouldContainAllElements() {
            // given
            Iterable<String> source = () -> List.of("a", "b").iterator();

            // when
            ImmutableList<String> list = ImmutableList.ofIterable(source);

            // then
            assertThat(list).containsExactlyInAnyOrder("a", "b");
        }

        /**
         * Test objective: Verify ofCollection with ImmutableList returns same instance
         * Test scenario: Pass ImmutableList to ofCollection
         * Expected result: Same ImmutableList instance is returned
         */
        @Test
        void ofCollection_WithImmutableList_ShouldReturnSameInstance() {
            // given
            ImmutableList<String> original = ImmutableList.of("a", "b");

            // when
            ImmutableList<String> result = ImmutableList.ofCollection(original);

            // then
            assertThat(result).isSameAs(original);
        }

        /**
         * Test objective: Verify ofCollection with empty collection returns empty list
         * Test scenario: Pass empty collection to ofCollection
         * Expected result: Empty ImmutableList is returned
         */
        @Test
        void ofCollection_WithEmptyCollection_ShouldReturnEmptyList() {
            // given
            List<String> empty = Collections.emptyList();

            // when
            ImmutableList<String> result = ImmutableList.ofCollection(empty);

            // then
            assertThat(result).isEmpty();
            assertThat(result).isSameAs(ImmutableList.empty());
        }

        /**
         * Test objective: Verify concat combines two collections
         * Test scenario: Concat two lists
         * Expected result: Combined list contains all elements
         */
        @Test
        void concat_ShouldCombineLists() {
            // given
            List<String> a = List.of("a", "b");
            List<String> b = List.of("c", "d");

            // when
            ImmutableList<String> result = ImmutableList.concat(a, b);

            // then
            assertThat(result).containsExactly("a", "b", "c", "d");
        }

        /**
         * Test objective: Verify copyOf() creates defensive copy
         * Test scenario: Modify original array after creating list
         * Expected result: ImmutableList is not affected
         */
        @Test
        void copyOf_ShouldCreateDefensiveCopy() {
            // given
            String[] array = {"a", "b", "c"};

            // when
            ImmutableList<String> list = ImmutableList.copyOf(array);
            array[0] = "modified";

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify of() does NOT create defensive copy
         * Test scenario: Modify original array after creating list
         * Expected result: ImmutableList is affected (shares same array)
         */
        @Test
        void of_DoesNotCreateDefensiveCopy() {
            // given
            String[] array = {"a", "b", "c"};

            // when
            ImmutableList<String> list = ImmutableList.of(array);
            array[0] = "modified";

            // then - of() shares the array, so modification is visible
            assertThat(list).containsExactly("modified", "b", "c");
        }

        /**
         * Test objective: Verify copyOf() with empty array returns empty list
         * Test scenario: Pass empty array to copyOf()
         * Expected result: Empty ImmutableList is returned
         */
        @Test
        void copyOf_WithEmptyArray_ShouldReturnEmptyList() {
            // given
            String[] array = {};

            // when
            ImmutableList<String> result = ImmutableList.copyOf(array);

            // then
            assertThat(result).isEmpty();
            assertThat(result).isSameAs(ImmutableList.empty());
        }
    }

    @Nested
    class ElementAccess {

        /**
         * Test objective: Verify get returns correct element
         * Test scenario: Access element at each index
         * Expected result: Correct element returned
         */
        @Test
        void get_WithValidIndex_ShouldReturnElement() {
            // given
            ImmutableList<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);

            // then
            assertThat(list.get(0)).isEqualTo(1);
            assertThat(list.get(2)).isEqualTo(3);
            assertThat(list.get(4)).isEqualTo(5);
        }

        /**
         * Test objective: Verify get throws IndexOutOfBoundsException for negative index
         * Test scenario: Pass negative index
         * Expected result: IndexOutOfBoundsException thrown
         */
        @Test
        void get_WithNegativeIndex_ShouldThrowException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.get(-1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        /**
         * Test objective: Verify get throws IndexOutOfBoundsException for out of bounds index
         * Test scenario: Pass index >= size
         * Expected result: IndexOutOfBoundsException thrown
         */
        @Test
        void get_WithOutOfBoundsIndex_ShouldThrowException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.get(2))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> list.get(100))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        /**
         * Test objective: Verify indexOf finds element
         * Test scenario: Search for existing element
         * Expected result: Correct index returned
         */
        @Test
        void indexOf_WithExistingElement_ShouldReturnCorrectIndex() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "b");

            // then
            assertThat(list.indexOf("a")).isEqualTo(0);
            assertThat(list.indexOf("b")).isEqualTo(1);
            assertThat(list.indexOf("c")).isEqualTo(2);
        }

        /**
         * Test objective: Verify indexOf returns -1 for non-existing element
         * Test scenario: Search for non-existing element
         * Expected result: -1 returned
         */
        @Test
        void indexOf_WithNonExistingElement_ShouldReturnMinusOne() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThat(list.indexOf("x")).isEqualTo(-1);
        }

        /**
         * Test objective: Verify lastIndexOf finds last occurrence
         * Test scenario: Search for element that appears multiple times
         * Expected result: Last index returned
         */
        @Test
        void lastIndexOf_ShouldReturnLastOccurrence() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "b");

            // then
            assertThat(list.lastIndexOf("b")).isEqualTo(3);
        }

        /**
         * Test objective: Verify contains works correctly
         * Test scenario: Check for existing and non-existing elements
         * Expected result: Correct boolean returned
         */
        @Test
        void contains_ShouldWorkCorrectly() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThat(list.contains("a")).isTrue();
            assertThat(list.contains("b")).isTrue();
            assertThat(list.contains("x")).isFalse();
            assertThat(list.contains(null)).isFalse();
        }

        /**
         * Test objective: Verify containsAll works correctly
         * Test scenario: Check for multiple elements
         * Expected result: Correct boolean returned
         */
        @Test
        void containsAll_ShouldWorkCorrectly() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "d");

            // then
            assertThat(list.containsAll(List.of("a", "b"))).isTrue();
            assertThat(list.containsAll(List.of("a", "x"))).isFalse();
        }
    }

    @Nested
    class Immutability {

        /**
         * Test objective: Verify add throws UnsupportedOperationException
         * Test scenario: Call add() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void add_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.add("c"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify remove throws UnsupportedOperationException
         * Test scenario: Call remove() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void remove_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.remove("a"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify remove by index throws UnsupportedOperationException
         * Test scenario: Call remove(int) method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void removeByIndex_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.remove(0))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify set throws UnsupportedOperationException
         * Test scenario: Call set() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void set_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.set(0, "x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify addAll throws UnsupportedOperationException
         * Test scenario: Call addAll() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void addAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.addAll(List.of("c", "d")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify clear throws UnsupportedOperationException
         * Test scenario: Call clear() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void clear_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(list::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify removeAll throws UnsupportedOperationException
         * Test scenario: Call removeAll() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void removeAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.removeAll(List.of("a")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify retainAll throws UnsupportedOperationException
         * Test scenario: Call retainAll() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void retainAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.retainAll(List.of("a")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify sort throws UnsupportedOperationException
         * Test scenario: Call sort() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void sort_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("b", "a");

            // then
            assertThatThrownBy(() -> list.sort(Comparator.naturalOrder()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify replaceAll throws UnsupportedOperationException
         * Test scenario: Call replaceAll() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void replaceAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.replaceAll(String::toUpperCase))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify removeIf throws UnsupportedOperationException
         * Test scenario: Call removeIf() method
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void removeIf_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.removeIf(s -> s.equals("a")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Iteration {

        /**
         * Test objective: Verify iterator returns elements in order
         * Test scenario: Use iterator to iterate all elements
         * Expected result: Elements returned in correct order
         */
        @Test
        void iterator_ShouldReturnElementsInOrder() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // when
            List<String> result = new ArrayList<>();
            for (String s : list) {
                result.add(s);
            }

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify listIterator works correctly
         * Test scenario: Use listIterator to iterate forward and backward
         * Expected result: Correct elements returned in both directions
         */
        @Test
        void listIterator_ShouldIterateForwardAndBackward() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            ListIterator<String> it = list.listIterator();

            // forward
            assertThat(it.hasNext()).isTrue();
            assertThat(it.nextIndex()).isEqualTo(0);
            assertThat(it.next()).isEqualTo("a");
            assertThat(it.next()).isEqualTo("b");
            assertThat(it.next()).isEqualTo("c");
            assertThat(it.hasNext()).isFalse();

            // backward
            assertThat(it.hasPrevious()).isTrue();
            assertThat(it.previousIndex()).isEqualTo(2);
            assertThat(it.previous()).isEqualTo("c");
            assertThat(it.previous()).isEqualTo("b");
            assertThat(it.previous()).isEqualTo("a");
            assertThat(it.hasPrevious()).isFalse();
        }

        /**
         * Test objective: Verify iterator remove throws UnsupportedOperationException
         * Test scenario: Call remove() on iterator
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void iteratorRemove_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            Iterator<String> it = list.iterator();
            it.next();

            // then
            assertThatThrownBy(it::remove)
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify listIterator add throws UnsupportedOperationException
         * Test scenario: Call add() on listIterator
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void listIteratorAdd_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            ListIterator<String> it = list.listIterator();

            // then
            assertThatThrownBy(() -> it.add("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        /**
         * Test objective: Verify listIterator set throws UnsupportedOperationException
         * Test scenario: Call set() on listIterator
         * Expected result: UnsupportedOperationException thrown
         */
        @Test
        void listIteratorSet_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            ListIterator<String> it = list.listIterator();
            it.next();

            // then
            assertThatThrownBy(() -> it.set("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class SubList {

        /**
         * Test objective: Verify subList returns correct portion
         * Test scenario: Create subList with valid indices
         * Expected result: Correct sublist returned
         */
        @Test
        void subList_WithValidIndices_ShouldReturnCorrectPortion() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "d", "e");

            // when
            ImmutableList<String> sub = list.subList(1, 4);

            // then
            assertThat(sub).containsExactly("b", "c", "d");
        }

        /**
         * Test objective: Verify subList with same indices returns empty list
         * Test scenario: Create subList with fromIndex == toIndex
         * Expected result: Empty list returned
         */
        @Test
        void subList_WithSameIndices_ShouldReturnEmptyList() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // when
            ImmutableList<String> sub = list.subList(1, 1);

            // then
            assertThat(sub).isEmpty();
            assertThat(sub).isSameAs(ImmutableList.empty());
        }

        /**
         * Test objective: Verify subList with full range returns same list
         * Test scenario: Create subList(0, size)
         * Expected result: Same list instance returned
         */
        @Test
        void subList_WithFullRange_ShouldReturnSameList() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // when
            ImmutableList<String> sub = list.subList(0, 3);

            // then
            assertThat(sub).isSameAs(list);
        }

        /**
         * Test objective: Verify subList with invalid indices throws exception
         * Test scenario: Create subList with invalid indices
         * Expected result: Exception thrown
         */
        @Test
        void subList_WithInvalidIndices_ShouldThrowException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThatThrownBy(() -> list.subList(-1, 2))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> list.subList(0, 10))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> list.subList(2, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ToArray {

        /**
         * Test objective: Verify toArray() returns new Object array
         * Test scenario: Call toArray()
         * Expected result: New Object array with all elements
         */
        @Test
        void toArray_ShouldReturnNewObjectArray() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // when
            Object[] array = list.toArray();

            // then
            assertThat(array).containsExactly("a", "b", "c");
            // Verify it's a new array
            array[0] = "modified";
            assertThat(list.get(0)).isEqualTo("a");
        }

        /**
         * Test objective: Verify toArray(T[]) with sufficient array
         * Test scenario: Pass array with sufficient size
         * Expected result: Same array filled with elements
         */
        @Test
        void toArray_WithSufficientArray_ShouldFillArray() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            String[] array = new String[3];

            // when
            String[] result = list.toArray(array);

            // then
            assertThat(result).isSameAs(array);
            assertThat(result).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify toArray(T[]) with larger array
         * Test scenario: Pass array larger than list
         * Expected result: Same array with null after last element
         */
        @Test
        void toArray_WithLargerArray_ShouldSetNullAfterLastElement() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            String[] array = new String[5];
            Arrays.fill(array, "initial");

            // when
            String[] result = list.toArray(array);

            // then
            assertThat(result).isSameAs(array);
            assertThat(result[0]).isEqualTo("a");
            assertThat(result[1]).isEqualTo("b");
            assertThat(result[2]).isEqualTo("c");
            assertThat(result[3]).isNull();
        }

        /**
         * Test objective: Verify toArray(T[]) with smaller array
         * Test scenario: Pass array smaller than list
         * Expected result: New array with correct size returned
         */
        @Test
        void toArray_WithSmallerArray_ShouldReturnNewArray() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            String[] array = new String[1];

            // when
            String[] result = list.toArray(array);

            // then
            assertThat(result).isNotSameAs(array);
            assertThat(result).containsExactly("a", "b", "c");
        }
    }

    @Nested
    class Clone {

        /**
         * Test objective: Verify clone returns new instance
         * Test scenario: Clone list
         * Expected result: New instance with same elements
         */
        @Test
        void clone_ShouldReturnNewInstance() {
            // given
            ImmutableList<String> original = ImmutableList.of("a", "b", "c");

            // when
            ImmutableList<String> cloned = original.clone();

            // then
            assertThat(cloned).isNotSameAs(original);
            assertThat(cloned).isEqualTo(original);
        }
    }

    @Nested
    class Builder {

        /**
         * Test objective: Verify builder creates list with added elements
         * Test scenario: Add elements via builder and build
         * Expected result: List with all added elements
         */
        @Test
        void builder_ShouldBuildListCorrectly() {
            // given
            ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

            // when
            builder.add("a");
            builder.add("b");
            builder.add("c");
            ImmutableList<String> list = builder.build();

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify builder with initial capacity
         * Test scenario: Create builder with initial capacity
         * Expected result: List built correctly
         */
        @Test
        void builder_WithInitialCapacity_ShouldWork() {
            // given
            ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<>(10);

            // when
            for (int i = 0; i < 10; i++) {
                builder.add(i);
            }
            ImmutableList<Integer> list = builder.build();

            // then
            assertThat(list).hasSize(10);
        }

        /**
         * Test objective: Verify builder addAll with collection
         * Test scenario: Add all elements from collection
         * Expected result: List contains all elements
         */
        @Test
        void builder_addAll_WithCollection_ShouldAddAllElements() {
            // given
            ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
            List<String> source = List.of("x", "y", "z");

            // when
            builder.addAll(source);
            ImmutableList<String> list = builder.build();

            // then
            assertThat(list).containsExactly("x", "y", "z");
        }

        /**
         * Test objective: Verify builder isEmpty works correctly
         * Test scenario: Check isEmpty before and after adding
         * Expected result: Correct empty state
         */
        @Test
        void builder_isEmpty_ShouldWorkCorrectly() {
            // given
            ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

            // then
            assertThat(builder.isEmpty()).isTrue();

            // when
            builder.add("a");

            // then
            assertThat(builder.isEmpty()).isFalse();
        }

        /**
         * Test objective: Verify builder builds empty list when nothing added
         * Test scenario: Build without adding elements
         * Expected result: Empty ImmutableList
         */
        @Test
        void builder_WithNoElements_ShouldBuildEmptyList() {
            // given
            ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();

            // when
            ImmutableList<String> list = builder.build();

            // then
            assertThat(list).isEmpty();
        }
    }

    @Nested
    class CollectorSupport {

        /**
         * Test objective: Verify collector works with stream
         * Test scenario: Collect stream into ImmutableList
         * Expected result: ImmutableList with stream elements
         */
        @Test
        void collector_ShouldWorkWithStream() {
            // given
            Stream<String> stream = Stream.of("a", "b", "c");

            // when
            ImmutableList<String> list = stream.collect(ImmutableList.collector());

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

        /**
         * Test objective: Verify collector with initial capacity
         * Test scenario: Collect with specified initial capacity
         * Expected result: ImmutableList with correct elements
         */
        @Test
        void collector_WithInitialCapacity_ShouldWork() {
            // given
            Stream<Integer> stream = IntStream.range(0, 100).boxed();

            // when
            ImmutableList<Integer> list = stream.collect(ImmutableList.collector(100));

            // then
            assertThat(list).hasSize(100);
        }

        /**
         * Test objective: Verify collector works with empty stream
         * Test scenario: Collect empty stream
         * Expected result: Empty ImmutableList
         */
        @Test
        void collector_WithEmptyStream_ShouldReturnEmptyList() {
            // given
            Stream<String> stream = Stream.empty();

            // when
            ImmutableList<String> list = stream.collect(ImmutableList.collector());

            // then
            assertThat(list).isEmpty();
        }
    }
}
