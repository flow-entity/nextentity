package io.github.nextentity.core.util;

import io.github.nextentity.core.expression.PathNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link ImmutableArray}.
 */
@DisplayName("ImmutableArray Tests")
class ImmutableArrayTest {

    @Test
    @DisplayName("should create empty array via ImmutableList")
    void shouldCreateEmptyArray() {
        ImmutableArray<String> array = ImmutableList.empty();

        assertThat(array).isEmpty();
        assertThat(array.size()).isZero();
    }

    @Test
    @DisplayName("should get element by index")
    void shouldGetElementByIndex() {
        ImmutableArray<String> array = new PathNode(new String[]{"a", "b", "c"});

        assertThat(array.get(0)).isEqualTo("a");
        assertThat(array.get(1)).isEqualTo("b");
        assertThat(array.get(2)).isEqualTo("c");
    }

    @Test
    @DisplayName("should return correct size")
    void shouldReturnCorrectSize() {
        ImmutableArray<String> array = new PathNode(new String[]{"a", "b", "c", "d", "e"});

        assertThat(array.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("should support iteration")
    void shouldSupportIteration() {
        ImmutableArray<String> array = new PathNode(new String[]{"a", "b", "c"});

        assertThat(array).isInstanceOf(Iterable.class);

        int count = 0;
        for (String item : array) {
            count++;
        }

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should stream elements")
    void shouldStreamElements() {
        ImmutableArray<String> array = new PathNode(new String[]{"a", "b", "c"});

        long count = array.stream().count();

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("should check isEmpty")
    void shouldCheckIsEmpty() {
        ImmutableArray<String> empty = ImmutableList.empty();
        ImmutableArray<String> nonEmpty = new PathNode(new String[]{"a"});

        assertThat(empty.isEmpty()).isTrue();
        assertThat(nonEmpty.isEmpty()).isFalse();
    }
}
