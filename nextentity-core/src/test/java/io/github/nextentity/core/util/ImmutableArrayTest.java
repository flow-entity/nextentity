package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify ImmutableArray interface functionality
 * <p>
 * Test scenarios:
 * 1. get method returns correct element at index
 * 2. stream method returns stream of elements
 * 3. asList method returns List view
 * 4. size method returns element count
 * 5. isEmpty returns correct state
 * <p>
 * Expected result: Interface default methods work correctly
 */
class ImmutableArrayTest {

    /**
     * Test objective: Verify ImmutableArray.of returns correct size
     * Test scenario: Create ImmutableArray with multiple elements
     * Expected result: size() returns correct count
     */
    @Test
    void of_WithMultipleElements_ShouldReturnCorrectSize() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThat(array.size()).isEqualTo(3);
        assertThat(array.isEmpty()).isFalse();
    }

    /**
     * Test objective: Verify get returns correct element at valid index
     * Test scenario: Access elements at various valid indices
     * Expected result: Correct element returned for each index
     */
    @Test
    void get_WithValidIndex_ShouldReturnElement() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThat(array.get(0)).isEqualTo("a");
        assertThat(array.get(1)).isEqualTo("b");
        assertThat(array.get(2)).isEqualTo("c");
    }

    /**
     * Test objective: Verify get throws IndexOutOfBoundsException for negative index
     * Test scenario: Pass negative index to get
     * Expected result: IndexOutOfBoundsException is thrown
     */
    @Test
    void get_WithNegativeIndex_ShouldThrowIndexOutOfBoundsException() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThatThrownBy(() -> array.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    /**
     * Test objective: Verify get throws IndexOutOfBoundsException for index >= size
     * Test scenario: Pass index equal to size
     * Expected result: IndexOutOfBoundsException is thrown
     */
    @Test
    void get_WithIndexEqualToSize_ShouldThrowIndexOutOfBoundsException() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThatThrownBy(() -> array.get(3))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    /**
     * Test objective: Verify stream returns stream of all elements
     * Test scenario: Call stream() on ImmutableArray
     * Expected result: Stream contains all elements in correct order
     */
    @Test
    void stream_ShouldReturnAllElements() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // when
        Stream<String> stream = array.stream();

        // then
        assertThat(stream).containsExactly("a", "b", "c");
    }

    /**
     * Test objective: Verify asList returns List with all elements
     * Test scenario: Call asList() on ImmutableArray
     * Expected result: List contains all elements in correct order
     */
    @Test
    void asList_ShouldReturnListView() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // when
        List<String> list = array.asList();

        // then
        assertThat(list).containsExactly("a", "b", "c");
    }

    /**
     * Test objective: Verify asList returns the same instance for ImmutableList
     * Test scenario: Call asList() on ImmutableList which overrides asList
     * Expected result: Returns itself (optimization)
     */
    @Test
    void asList_OnImmutableList_ShouldReturnSelf() {
        // given
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        // when
        List<String> result = list.asList();

        // then
        assertThat(result).isSameAs(list);
    }

    /**
     * Test objective: Verify empty ImmutableArray has size 0
     * Test scenario: Create empty ImmutableArray
     * Expected result: size() returns 0, isEmpty() returns true
     */
    @Test
    void empty_ShouldHaveSizeZero() {
        // given
        ImmutableArray<String> array = ImmutableList.empty();

        // then
        assertThat(array.size()).isZero();
        assertThat(array.isEmpty()).isTrue();
    }

    /**
     * Test objective: Verify empty ImmutableArray stream is empty
     * Test scenario: Call stream() on empty ImmutableArray
     * Expected result: Stream is empty
     */
    @Test
    void stream_OnEmptyArray_ShouldReturnEmptyStream() {
        // given
        ImmutableArray<String> array = ImmutableList.empty();

        // when
        Stream<String> stream = array.stream();

        // then
        assertThat(stream).isEmpty();
    }
}
