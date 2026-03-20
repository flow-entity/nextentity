package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test objective: Verify Maps utility class provides fluent Map building
 * <p>
 * Test scenarios:
 * 1. hashmap() creates builder for HashMap
 * 2. put() adds key-value pairs
 * 3. put with collections adds multiple entries
 * 4. build() returns the built map
 * 5. Collection size mismatch throws exception
 * <p>
 * Expected result: Map can be built fluently with expected entries
 */
class MapsTest {

    /**
     * Test objective: Verify hashmap() returns builder instance
     * Test scenario: Call Maps.hashmap()
     * Expected result: Builder is returned
     */
    @Test
    void hashmap_ShouldReturnBuilder() {
        // when
        Maps.Builder<HashMap<String, Integer>, String, Integer> builder = Maps.hashmap();

        // then
        assertThat(builder).isNotNull();
    }

    /**
     * Test objective: Verify put() adds single key-value pair
     * Test scenario: Use put to add entries
     * Expected result: Map contains added entries
     */
    @Test
    void put_ShouldAddKeyValuePair() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put("a", 1)
                .put("b", 2)
                .put("c", 3)
                .build();

        // then
        assertThat(map).containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3)
                .hasSize(3);
    }

    /**
     * Test objective: Verify put() returns builder for chaining
     * Test scenario: Chain multiple put calls
     * Expected result: All entries added
     */
    @Test
    void put_ShouldReturnBuilderForChaining() {
        // when
        Map<String, String> map = Maps.<String, String>hashmap()
                .put("key1", "value1")
                .put("key2", "value2")
                .build();

        // then
        assertThat(map).containsEntry("key1", "value1")
                .containsEntry("key2", "value2");
    }

    /**
     * Test objective: Verify put with collections adds multiple entries
     * Test scenario: Use put(keys, values) with matching sizes
     * Expected result: All key-value pairs added
     */
    @Test
    void put_WithMatchingCollections_ShouldAddAllEntries() {
        // given
        List<String> keys = List.of("a", "b", "c");
        List<Integer> values = List.of(1, 2, 3);

        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put(keys, values)
                .build();

        // then
        assertThat(map).containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3)
                .hasSize(3);
    }

    /**
     * Test objective: Verify put with collections throws on size mismatch
     * Test scenario: Use put(keys, values) with different sizes
     * Expected result: IllegalArgumentException thrown
     */
    @Test
    void put_WithMismatchedCollections_ShouldThrowException() {
        // given
        List<String> keys = List.of("a", "b", "c");
        List<Integer> values = List.of(1, 2); // different size

        // then
        assertThatThrownBy(() -> Maps.<String, Integer>hashmap()
                        .put(keys, values)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Test objective: Verify put with empty collections works
     * Test scenario: Use put with empty lists
     * Expected result: Empty map returned
     */
    @Test
    void put_WithEmptyCollections_ShouldReturnEmptyMap() {
        // given
        List<String> keys = Collections.emptyList();
        List<Integer> values = Collections.emptyList();

        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put(keys, values)
                .build();

        // then
        assertThat(map).isEmpty();
    }

    /**
     * Test objective: Verify build() returns the HashMap
     * Test scenario: Call build()
     * Expected result: HashMap instance returned
     */
    @Test
    void build_ShouldReturnHashMap() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put("a", 1)
                .build();

        // then
        assertThat(map).isInstanceOf(HashMap.class);
    }

    /**
     * Test objective: Verify can overwrite values for same key
     * Test scenario: Put same key multiple times
     * Expected result: Last value wins
     */
    @Test
    void put_WithSameKey_ShouldOverwriteValue() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put("a", 1)
                .put("a", 2)
                .put("a", 3)
                .build();

        // then
        assertThat(map).hasSize(1);
        assertThat(map).containsEntry("a", 3);
    }

    /**
     * Test objective: Verify mixed put and putAll usage
     * Test scenario: Use both single and collection put
     * Expected result: All entries added
     */
    @Test
    void put_MixedWithPutAll_ShouldWork() {
        // given
        List<String> keys = List.of("c", "d");
        List<Integer> values = List.of(3, 4);

        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put("a", 1)
                .put(keys, values)
                .put("b", 2)
                .build();

        // then
        assertThat(map).hasSize(4)
                .containsEntry("a", 1)
                .containsEntry("b", 2)
                .containsEntry("c", 3)
                .containsEntry("d", 4);
    }

    /**
     * Test objective: Verify builder accepts null keys and values
     * Test scenario: Put null key and value
     * Expected result: Null entries added
     */
    @Test
    void put_ShouldAllowNullKeysAndValues() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put(null, 1)
                .put("key", null)
                .build();

        // then
        assertThat(map).hasSize(2);
        assertThat(map).containsEntry(null, 1)
                .containsEntry("key", null);
    }

    /**
     * Test objective: Verify can build empty map
     * Test scenario: Build without any puts
     * Expected result: Empty HashMap returned
     */
    @Test
    void build_WithoutPuts_ShouldReturnEmptyMap() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .build();

        // then
        assertThat(map).isEmpty();
    }
}
