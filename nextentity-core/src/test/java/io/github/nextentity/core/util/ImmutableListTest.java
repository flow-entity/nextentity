package io.github.nextentity.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link ImmutableList}.
 */
@DisplayName("ImmutableList Tests")
class ImmutableListTest {

    @Test
    @DisplayName("should create empty list")
    void shouldCreateEmptyList() {
        ImmutableList<String> list = ImmutableList.empty();

        assertThat(list).isEmpty();
        assertThat(list.size()).isZero();
    }

    @Test
    @DisplayName("should create list with single element")
    void shouldCreateListWithSingleElement() {
        ImmutableList<String> list = ImmutableList.of("a");

        assertThat(list).containsExactly("a");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("should create list with multiple elements")
    void shouldCreateListWithMultipleElements() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThat(list).containsExactly("a", "b", "c");
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("should create list from collection")
    void shouldCreateListFromCollection() {
        List<String> source = Arrays.asList("a", "b", "c");
        ImmutableList<String> list = ImmutableList.ofCollection(source);

        assertThat(list).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("should return empty list for empty collection")
    void shouldReturnEmptyListForEmptyCollection() {
        ImmutableList<String> list = ImmutableList.ofCollection(Collections.emptyList());

        assertThat(list).isEmpty();
    }

    @Test
    @DisplayName("should concat two lists")
    void shouldConcatTwoLists() {
        ImmutableList<String> list1 = ImmutableList.of("a", "b");
        ImmutableList<String> list2 = ImmutableList.of("c", "d");

        ImmutableList<String> result = ImmutableList.concat(list1, list2);

        assertThat(result).containsExactly("a", "b", "c", "d");
    }

    @Test
    @DisplayName("should concat with empty list")
    void shouldConcatWithEmptyList() {
        ImmutableList<String> list = ImmutableList.of("a", "b");
        ImmutableList<String> empty = ImmutableList.empty();

        ImmutableList<String> result = ImmutableList.concat(list, empty);

        assertThat(result).containsExactly("a", "b");
        // Note: concat always creates a new list, so we don't check reference equality
    }

    @Test
    @DisplayName("should not allow modification operations")
    void shouldNotAllowModificationOperations() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThatThrownBy(() -> list.add("d"))
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> list.remove("a"))
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(list::clear)
                .isInstanceOf(UnsupportedOperationException.class);

        assertThatThrownBy(() -> list.set(0, "x"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("should get element by index")
    void shouldGetElementByIndex() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.get(1)).isEqualTo("b");
        assertThat(list.get(2)).isEqualTo("c");
    }

    @Test
    @DisplayName("should throw IndexOutOfBounds for invalid index")
    void shouldThrowIndexOutOfBoundsForInvalidIndex() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThatThrownBy(() -> list.get(3))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> list.get(-1))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("should create sub list")
    void shouldCreateSubList() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c", "d", "e");

        ImmutableList<String> subList = list.subList(1, 4);

        assertThat(subList).containsExactly("b", "c", "d");
    }

    @Test
    @DisplayName("should return empty for zero-length sub list")
    void shouldReturnEmptyForZeroLengthSubList() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        ImmutableList<String> subList = list.subList(1, 1);

        assertThat(subList).isEmpty();
    }

    @Test
    @DisplayName("should return same list for full range sub list")
    void shouldReturnSameListForFullRangeSubList() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        ImmutableList<String> subList = list.subList(0, 3);

        assertThat(subList).isSameAs(list);
    }

    @Test
    @DisplayName("should throw for invalid sub list range")
    void shouldThrowForInvalidSubListRange() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThatThrownBy(() -> list.subList(2, 1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> list.subList(-1, 2))
                .isInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> list.subList(0, 4))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    @DisplayName("should build list using builder")
    void shouldBuildListUsingBuilder() {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        builder.add("a");
        builder.add("b");
        builder.add("c");

        ImmutableList<String> list = builder.build();

        assertThat(list).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("should build list from collection using builder")
    void shouldBuildListFromCollectionUsingBuilder() {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        builder.addAll(Arrays.asList("a", "b", "c"));

        ImmutableList<String> list = builder.build();

        assertThat(list).containsExactly("a", "b", "c");
    }

    @Test
    @DisplayName("should handle large list")
    void shouldHandleLargeList() {
        ImmutableList.Builder<Integer> builder = new ImmutableList.Builder<>(1000);
        for (int i = 0; i < 1000; i++) {
            builder.add(i);
        }

        ImmutableList<Integer> list = builder.build();

        assertThat(list.size()).isEqualTo(1000);
        assertThat(list.get(0)).isEqualTo(0);
        assertThat(list.get(999)).isEqualTo(999);
    }

    @Test
    @DisplayName("should support iteration")
    void shouldSupportIteration() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThat(list).isInstanceOf(Iterable.class);

        int count = 0;
        for (String item : list) {
            count++;
        }

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should convert to array")
    void shouldConvertToArray() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        Object[] array = list.toArray();

        assertThat(array).isEqualTo(new String[]{"a", "b", "c"});
    }

    @Test
    @DisplayName("should convert to typed array")
    void shouldConvertToTypedArray() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        String[] array = list.toArray(new String[0]);

        assertThat(array).isEqualTo(new String[]{"a", "b", "c"});
    }

    @Test
    @DisplayName("should check contains")
    void shouldCheckContains() {
        ImmutableList<String> list = ImmutableList.of("a", "b", "c");

        assertThat(list).contains("a");
        assertThat(list).doesNotContain("d");
    }

    @Test
    @DisplayName("should check isEmpty builder")
    void shouldCheckIsEmptyBuilder() {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
        assertThat(builder.isEmpty()).isTrue();

        builder.add("a");
        assertThat(builder.isEmpty()).isFalse();
    }
}
