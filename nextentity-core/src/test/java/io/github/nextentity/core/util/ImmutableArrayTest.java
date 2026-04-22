package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
/// 测试目标: 验证y ImmutableArray interface functionality
/// <p>
/// 测试场景s:
/// 1. get 方法 returns correct element at index
/// 2. stream 方法 returns stream of elements
/// 3. asList 方法 returns List view
/// 4. size 方法 returns element count
/// 5. isEmpty returns correct state
/// <p>
/// 预期结果: Interface default 方法 work 正确
class ImmutableArrayTest {

    ///
    /// 测试目标: 验证y ImmutableArray.of returns correct size
    /// 测试场景: Create ImmutableArray with multiple elements
    /// 预期结果: size() returns correct count
    @Test
    void of_WithMultipleElements_ShouldReturnCorrectSize() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThat(array.size()).isEqualTo(3);
        assertThat(array.isEmpty()).isFalse();
    }

    ///
    /// 测试目标: 验证y get returns correct element at valid index
    /// 测试场景: Access elements at various valid indices
    /// 预期结果: Correct element returned for each index
    @Test
    void get_WithValidIndex_ShouldReturnElement() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThat(array.get(0)).isEqualTo("a");
        assertThat(array.get(1)).isEqualTo("b");
        assertThat(array.get(2)).isEqualTo("c");
    }

    ///
    /// 测试目标: 验证y get throws IndexOutOfBoundsException for negative index
    /// 测试场景: Pass negative index to get
    /// 预期结果: IndexOutOfBoundsException is thrown
    @Test
    void get_WithNegativeIndex_ShouldThrowIndexOutOfBoundsException() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThatThrownBy(() -> array.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    ///
    /// 测试目标: 验证y get throws IndexOutOfBoundsException for index >= size
    /// 测试场景: Pass index equal to size
    /// 预期结果: IndexOutOfBoundsException is thrown
    @Test
    void get_WithIndexEqualToSize_ShouldThrowIndexOutOfBoundsException() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // then
        assertThatThrownBy(() -> array.get(3))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    ///
    /// 测试目标: 验证y stream returns stream of all elements
    /// 测试场景: Call stream() on ImmutableArray
    /// 预期结果: Stream contains all elements in correct order
    @Test
    void stream_ShouldReturnAllElements() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // when
        Stream<String> stream = array.stream();

        // then
        assertThat(stream).containsExactly("a", "b", "c");
    }

    ///
    /// 测试目标: 验证y asList returns List with all elements
    /// 测试场景: Call asList() on ImmutableArray
    /// 预期结果: List contains all elements in correct order
    @Test
    void asList_ShouldReturnListView() {
        // given
        ImmutableArray<String> array = ImmutableList.of("a", "b", "c");

        // when
        List<? extends String> list = array.asList();

        // then
        assertThat(list).isEqualTo(array);
    }

    ///
    /// 测试目标: 验证y asList returns the same instance for ImmutableList
    /// 测试场景: Call asList() on ImmutableList which overrides asList
    /// 预期结果: Returns itself (optimization)
    @Test
    void asList_OnImmutableList_ShouldReturnSelf() {
        // given
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        // when
        List<String> result = list.asList();

        // then
        assertThat(result).isSameAs(list);
    }

    ///
    /// 测试目标: 验证y empty ImmutableArray has size 0
    /// 测试场景: Create empty ImmutableArray
    /// 预期结果: size() returns 0, isEmpty() returns true
    @Test
    void empty_ShouldHaveSizeZero() {
        // given
        ImmutableArray<String> array = ImmutableList.empty();

        // then
        assertThat(array.size()).isZero();
        assertThat(array.isEmpty()).isTrue();
    }

    ///
    /// 测试目标: 验证y empty ImmutableArray stream is empty
    /// 测试场景: Call stream() on empty ImmutableArray
    /// 预期结果: Stream is empty
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
