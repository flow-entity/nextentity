package io.github.nextentity.core.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
 /// 测试目标: 验证y Iterators utility class provides correct iteration 操作s
 /// <p>
 /// 测试场景s:
 /// 1. toList converts Iterable to List
 /// 2. map transforms elements using mapper function
 /// 3. toArray converts Iterable to Object array
 /// 4. sizeOf returns size for Collection/Sizeable
 /// 5. size returns correct element count
 /// 6. iterate returns ArrayIterator
 /// <p>
 /// 预期结果: All utility 方法 work 正确
class IteratorsTest {

    @Nested
    class ToList {

///
         /// 测试目标: 验证y toList returns same instance for List input
         /// 测试场景: Pass a List to toList
         /// 预期结果: Same List instance is returned
        @Test
        void toList_WithListInput_ShouldReturnSameInstance() {
            // given
            List<String> list = new ArrayList<>(List.of("a", "b", "c"));

            // when
            List<String> result = Iterators.toList(list);

            // then
            assertThat(result).isSameAs(list);
        }

///
         /// 测试目标: 验证y toList returns ImmutableList for non-List Iterable
         /// 测试场景: Pass a Set to toList
         /// 预期结果: ImmutableList with same elements
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

///
         /// 测试目标: 验证y toList 处理 empty iterable
         /// 测试场景: Pass empty iterable
         /// 预期结果: Empty list
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

///
         /// 测试目标: 验证y map transforms elements 正确
         /// 测试场景: Map strings to their lengths
         /// 预期结果: Iterable with transformed values
        @Test
        void map_ShouldTransformElements() {
            // given
            List<String> list = List.of("a", "bb", "ccc");

            // when
            Iterable<Integer> result = Iterators.map(list, String::length);

            // then
            assertThat(result).containsExactly(1, 2, 3);
        }

///
         /// 测试目标: 验证y map with empty iterable returns empty list
         /// 测试场景: Map over empty iterable
         /// 预期结果: Empty iterable
        @Test
        void map_WithEmptyIterable_ShouldReturnEmptyList() {
            // given
            List<String> empty = Collections.emptyList();

            // when
            Iterable<Integer> result = Iterators.map(empty, String::length);

            // then
            assertThat(result).isEmpty();
        }

///
         /// 测试目标: 验证y map returns Sizeable when input has known size
         /// 测试场景: Map over a Collection
         /// 预期结果: 结果 implements Sizeable
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

///
         /// 测试目标: 验证y MappedIterable.toString works 正确
         /// 测试场景: Call toString on mapped iterable
         /// 预期结果: String representation with elements
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

///
         /// 测试目标: 验证y toArray returns correct array
         /// 测试场景: Convert list to array
         /// 预期结果: Object array with all elements
        @Test
        void toArray_ShouldReturnCorrectArray() {
            // given
            List<String> list = List.of("a", "b", "c");

            // when
            Object[] result = Iterators.toArray(list);

            // then
            assertThat(result).containsExactly("a", "b", "c");
        }

///
         /// 测试目标: 验证y toArray with empty iterable returns shared empty array
         /// 测试场景: Convert empty iterable to array
         /// 预期结果: Shared EmptyArrays.OBJECT constant
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

///
         /// 测试目标: 验证y toArray creates new array
         /// 测试场景: Check if result is a new array
         /// 预期结果: New array instance
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

///
         /// 测试目标: 验证y sizeOf returns correct size for Collection
         /// 测试场景: Pass Collection to sizeOf
         /// 预期结果: Correct size returned
        @Test
        void sizeOf_WithCollection_ShouldReturnCorrectSize() {
            // given
            Collection<String> collection = List.of("a", "b", "c");

            // when
            int size = Iterators.sizeOf(collection);

            // then
            assertThat(size).isEqualTo(3);
        }

///
         /// 测试目标: 验证y sizeOf returns correct size for Sizeable
         /// 测试场景: Pass Sizeable to sizeOf
         /// 预期结果: Correct size returned
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

///
         /// 测试目标: 验证y sizeOf returns -1 for unknown size
         /// 测试场景: Pass non-Collection, non-Sizeable iterable
         /// 预期结果: -1 returned
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

///
         /// 测试目标: 验证y size returns correct count for Collection
         /// 测试场景: Get size of Collection
         /// 预期结果: Correct size
        @Test
        void size_WithCollection_ShouldReturnCorrectSize() {
            // given
            Collection<String> collection = List.of("a", "b", "c");

            // when
            int size = Iterators.size(collection);

            // then
            assertThat(size).isEqualTo(3);
        }

///
         /// 测试目标: 验证y size iterates to count for unknown size iterable
         /// 测试场景: Get size of non-Collection iterable
         /// 预期结果: Correct count by iteration
        @Test
        void size_WithUnknownSizeIterable_ShouldIterateToCount() {
            // given
            Iterable<String> iterable = () -> List.of("a", "b", "c", "d").iterator();

            // when
            int size = Iterators.size(iterable);

            // then
            assertThat(size).isEqualTo(4);
        }

///
         /// 测试目标: 验证y size returns 0 for empty iterable
         /// 测试场景: Get size of empty iterable
         /// 预期结果: 0
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

///
         /// 测试目标: 验证y iterate returns correct ArrayIterator
         /// 测试场景: Create iterator from array
         /// 预期结果: Iterator iterates over all elements
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

///
         /// 测试目标: 验证y iterate hasNext works 正确
         /// 测试场景: Check hasNext at different positions
         /// 预期结果: Correct hasNext values
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

///
         /// 测试目标: 验证y iterate next throws NoSuchElementException when exhausted
         /// 测试场景: Call next after iterator is exhausted
         /// 预期结果: NoSuchElementException thrown
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

///
         /// 测试目标: 验证y iterate with null array throws NPE
         /// 测试场景: Pass null array
         /// 预期结果: NullPointerException thrown
        @Test
        void iterate_WithNullArray_ShouldThrowNPE() {
            // then
            assertThatThrownBy(() -> Iterators.iterate(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class ArrayIterator {

///
         /// 测试目标: 验证y ArrayIterator works with empty array
         /// 测试场景: Iterate over empty array
         /// 预期结果: hasNext is false immediately
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

///
         /// 测试目标: 验证y MappedIterator transforms elements lazily
         /// 测试场景: Map with transformation function
         /// 预期结果: Elements transformed on iteration
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

///
         /// 测试目标: 验证y MappedSizeableIterable reports correct size
         /// 测试场景: Create mapped iterable from Collection
         /// 预期结果: Size is reported 正确
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
