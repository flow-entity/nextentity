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

/// 测试目标：验证ImmutableList提供正确的不可变列表行为
/// <p>
/// 测试场景：
/// 1. 工厂方法创建正确的列表
/// 2. 列表操作是只读的
/// 3. 修改操作抛出UnsupportedOperationException
/// 4. 迭代器行为正确
/// 5. 子列表工作正常
/// <p>
/// 预期结果：ImmutableList确实是不可变的并且行为正确
class ImmutableListTest {

    @Nested
    class FactoryMethods {

///
         /// 测试目标: 验证y of() creates list with given elements
         /// 测试场景: Create list with multiple elements
         /// 预期结果: List contains all elements in order
        @Test
        void of_WithElements_ShouldContainAllElements() {
            // when
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThat(list).containsExactly("a", "b", "c");
            assertThat(list.size()).isEqualTo(3);
        }

///
         /// 测试目标: 验证y of() with no args returns empty list
         /// 测试场景: Call of() with no arguments
         /// 预期结果: Empty list is returned
        @Test
        void of_WithNoArgs_ShouldReturnEmptyList() {
            // when
            ImmutableList<String> list = ImmutableList.of();

            // then
            assertThat(list).isEmpty();
            assertThat(list).isSameAs(ImmutableList.empty());
        }

///
         /// 测试目标: 验证y empty() returns same instance
         /// 测试场景: Call empty() multiple times
         /// 预期结果: Same instance is returned
        @Test
        void empty_ShouldReturnSameInstance() {
            // when
            ImmutableList<String> empty1 = ImmutableList.empty();
            ImmutableList<String> empty2 = ImmutableList.empty();

            // then
            assertThat(empty1).isSameAs(empty2);
        }

///
         /// 测试目标: 验证y ofIterable with Collection works 正确
         /// 测试场景: Create ImmutableList from ArrayList
         /// 预期结果: List contains all elements from collection
        @Test
        void ofIterable_WithCollection_ShouldContainAllElements() {
            // given
            List<String> source = new ArrayList<>(List.of("x", "y", "z"));

            // when
            ImmutableList<String> list = ImmutableList.ofIterable(source);

            // then
            assertThat(list).containsExactly("x", "y", "z");
        }

///
         /// 测试目标: 验证y ofIterable with non-Collection iterable works
         /// 测试场景: Create ImmutableList from a Set iterator
         /// 预期结果: List contains all elements from iterable
        @Test
        void ofIterable_WithNonCollection_ShouldContainAllElements() {
            // given
            Iterable<String> source = () -> List.of("a", "b").iterator();

            // when
            ImmutableList<String> list = ImmutableList.ofIterable(source);

            // then
            assertThat(list).containsExactlyInAnyOrder("a", "b");
        }

///
         /// 测试目标: 验证y ofCollection with ImmutableList returns same instance
         /// 测试场景: Pass ImmutableList to ofCollection
         /// 预期结果: Same ImmutableList instance is returned
        @Test
        void ofCollection_WithImmutableList_ShouldReturnSameInstance() {
            // given
            ImmutableList<String> original = ImmutableList.of("a", "b");

            // when
            ImmutableList<String> result = ImmutableList.ofCollection(original);

            // then
            assertThat(result).isSameAs(original);
        }

///
         /// 测试目标: 验证y ofCollection with empty collection returns empty list
         /// 测试场景: Pass empty collection to ofCollection
         /// 预期结果: Empty ImmutableList is returned
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

///
         /// 测试目标: 验证y concat combines two collections
         /// 测试场景: Concat two lists
         /// 预期结果: Combined list contains all elements
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

///
         /// 测试目标: 验证y copyOf() creates defensive copy
         /// 测试场景: Modify original array after creating list
         /// 预期结果: ImmutableList is not affected
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

///
         /// 测试目标: 验证y of() does NOT create defensive copy
         /// 测试场景: Modify original array after creating list
         /// 预期结果: ImmutableList is affected (shares same array)
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

///
         /// 测试目标: 验证y copyOf() with empty array returns empty list
         /// 测试场景: Pass empty array to copyOf()
         /// 预期结果: Empty ImmutableList is returned
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

///
         /// 测试目标: 验证y get returns correct element
         /// 测试场景: Access element at each index
         /// 预期结果: Correct element returned
        @Test
        void get_WithValidIndex_ShouldReturnElement() {
            // given
            ImmutableList<Integer> list = ImmutableList.of(1, 2, 3, 4, 5);

            // then
            assertThat(list.get(0)).isEqualTo(1);
            assertThat(list.get(2)).isEqualTo(3);
            assertThat(list.get(4)).isEqualTo(5);
        }

///
         /// 测试目标: 验证y get throws IndexOutOfBoundsException for negative index
         /// 测试场景: Pass negative index
         /// 预期结果: IndexOutOfBoundsException thrown
        @Test
        void get_WithNegativeIndex_ShouldThrowException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.get(-1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

///
         /// 测试目标: 验证y get throws IndexOutOfBoundsException for out of bounds index
         /// 测试场景: Pass index >= size
         /// 预期结果: IndexOutOfBoundsException thrown
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

///
         /// 测试目标: 验证y indexOf finds element
         /// 测试场景: Search for existing element
         /// 预期结果: Correct index returned
        @Test
        void indexOf_WithExistingElement_ShouldReturnCorrectIndex() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "b");

            // then
            assertThat(list.indexOf("a")).isEqualTo(0);
            assertThat(list.indexOf("b")).isEqualTo(1);
            assertThat(list.indexOf("c")).isEqualTo(2);
        }

///
         /// 测试目标: 验证y indexOf returns -1 for non-existing element
         /// 测试场景: Search for non-existing element
         /// 预期结果: -1 returned
        @Test
        void indexOf_WithNonExistingElement_ShouldReturnMinusOne() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // then
            assertThat(list.indexOf("x")).isEqualTo(-1);
        }

///
         /// 测试目标: 验证y lastIndexOf finds last occurrence
         /// 测试场景: Search for element that appears multiple times
         /// 预期结果: Last index returned
        @Test
        void lastIndexOf_ShouldReturnLastOccurrence() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "b");

            // then
            assertThat(list.lastIndexOf("b")).isEqualTo(3);
        }

///
         /// 测试目标: 验证y contains works 正确
         /// 测试场景: Check for existing and non-existing elements
         /// 预期结果: Correct boolean returned
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

///
         /// 测试目标: 验证y containsAll works 正确
         /// 测试场景: Check for multiple elements
         /// 预期结果: Correct boolean returned
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

///
         /// 测试目标: 验证y add throws UnsupportedOperationException
         /// 测试场景: Call add() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void add_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.add("c"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y remove throws UnsupportedOperationException
         /// 测试场景: Call remove() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void remove_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.remove("a"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y remove by index throws UnsupportedOperationException
         /// 测试场景: Call remove(int) 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void removeByIndex_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.remove(0))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y set throws UnsupportedOperationException
         /// 测试场景: Call set() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void set_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.set(0, "x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y addAll throws UnsupportedOperationException
         /// 测试场景: Call addAll() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void addAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.addAll(List.of("c", "d")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y clear throws UnsupportedOperationException
         /// 测试场景: Call clear() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void clear_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(list::clear)
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y removeAll throws UnsupportedOperationException
         /// 测试场景: Call removeAll() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void removeAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.removeAll(List.of("a")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y retainAll throws UnsupportedOperationException
         /// 测试场景: Call retainAll() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void retainAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.retainAll(List.of("a")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y sort throws UnsupportedOperationException
         /// 测试场景: Call sort() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void sort_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("b", "a");

            // then
            assertThatThrownBy(() -> list.sort(Comparator.naturalOrder()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y replaceAll throws UnsupportedOperationException
         /// 测试场景: Call replaceAll() 方法
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void replaceAll_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b");

            // then
            assertThatThrownBy(() -> list.replaceAll(String::toUpperCase))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y removeIf throws UnsupportedOperationException
         /// 测试场景: Call removeIf() 方法
         /// 预期结果: UnsupportedOperationException thrown
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

///
         /// 测试目标: 验证y iterator returns elements in order
         /// 测试场景: Use iterator to iterate all elements
         /// 预期结果: Elements returned in correct order
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

///
         /// 测试目标: 验证y listIterator works 正确
         /// 测试场景: Use listIterator to iterate forward and backward
         /// 预期结果: Correct elements returned in both directions
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

///
         /// 测试目标: 验证y iterator remove throws UnsupportedOperationException
         /// 测试场景: Call remove() on iterator
         /// 预期结果: UnsupportedOperationException thrown
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

///
         /// 测试目标: 验证y listIterator add throws UnsupportedOperationException
         /// 测试场景: Call add() on listIterator
         /// 预期结果: UnsupportedOperationException thrown
        @Test
        void listIteratorAdd_ShouldThrowUnsupportedOperationException() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");
            ListIterator<String> it = list.listIterator();

            // then
            assertThatThrownBy(() -> it.add("x"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

///
         /// 测试目标: 验证y listIterator set throws UnsupportedOperationException
         /// 测试场景: Call set() on listIterator
         /// 预期结果: UnsupportedOperationException thrown
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

///
         /// 测试目标: 验证y subList returns correct portion
         /// 测试场景: Create subList with valid indices
         /// 预期结果: Correct sublist returned
        @Test
        void subList_WithValidIndices_ShouldReturnCorrectPortion() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c", "d", "e");

            // when
            ImmutableList<String> sub = list.subList(1, 4);

            // then
            assertThat(sub).containsExactly("b", "c", "d");
        }

///
         /// 测试目标: 验证y subList with same indices returns empty list
         /// 测试场景: Create subList with fromIndex == toIndex
         /// 预期结果: Empty list returned
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

///
         /// 测试目标: 验证y subList with full range returns same list
         /// 测试场景: Create subList(0, size)
         /// 预期结果: Same list instance returned
        @Test
        void subList_WithFullRange_ShouldReturnSameList() {
            // given
            ImmutableList<String> list = ImmutableList.of("a", "b", "c");

            // when
            ImmutableList<String> sub = list.subList(0, 3);

            // then
            assertThat(sub).isSameAs(list);
        }

///
         /// 测试目标: 验证y subList with invalid indices throws exception
         /// 测试场景: Create subList with invalid indices
         /// 预期结果: Exception thrown
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

///
         /// 测试目标: 验证y toArray() returns new Object array
         /// 测试场景: Call toArray()
         /// 预期结果: New Object array with all elements
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

///
         /// 测试目标: 验证y toArray(T[]) with sufficient array
         /// 测试场景: Pass array with sufficient size
         /// 预期结果: Same array filled with elements
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

///
         /// 测试目标: 验证y toArray(T[]) with larger array
         /// 测试场景: Pass array larger than list
         /// 预期结果: Same array with null after last element
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

///
         /// 测试目标: 验证y toArray(T[]) with smaller array
         /// 测试场景: Pass array smaller than list
         /// 预期结果: New array with correct size returned
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

///
         /// 测试目标: 验证y clone returns new instance
         /// 测试场景: Clone list
         /// 预期结果: New instance with same elements
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

///
         /// 测试目标: 验证y builder creates list with added elements
         /// 测试场景: Add elements via builder and build
         /// 预期结果: List with all added elements
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

///
         /// 测试目标: 验证y builder with initial capacity
         /// 测试场景: Create builder with initial capacity
         /// 预期结果: List built 正确
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

///
         /// 测试目标: 验证y builder addAll with collection
         /// 测试场景: Add all elements from collection
         /// 预期结果: List contains all elements
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

///
         /// 测试目标: 验证y builder isEmpty works 正确
         /// 测试场景: Check isEmpty before and after adding
         /// 预期结果: Correct empty state
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

///
         /// 测试目标: 验证y builder builds empty list when nothing added
         /// 测试场景: Build without adding elements
         /// 预期结果: Empty ImmutableList
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

///
         /// 测试目标: 验证y collector works with stream
         /// 测试场景: Collect stream into ImmutableList
         /// 预期结果: ImmutableList with stream elements
        @Test
        void collector_ShouldWorkWithStream() {
            // given
            Stream<String> stream = Stream.of("a", "b", "c");

            // when
            ImmutableList<String> list = stream.collect(ImmutableList.collector());

            // then
            assertThat(list).containsExactly("a", "b", "c");
        }

///
         /// 测试目标: 验证y collector with initial capacity
         /// 测试场景: Collect with specified initial capacity
         /// 预期结果: ImmutableList with correct elements
        @Test
        void collector_WithInitialCapacity_ShouldWork() {
            // given
            Stream<Integer> stream = IntStream.range(0, 100).boxed();

            // when
            ImmutableList<Integer> list = stream.collect(ImmutableList.collector(100));

            // then
            assertThat(list).hasSize(100);
        }

///
         /// 测试目标: 验证y collector works with empty stream
         /// 测试场景: Collect empty stream
         /// 预期结果: Empty ImmutableList
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
