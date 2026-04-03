package io.github.nextentity.core.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

///
 /// 测试目标: 验证y Maps utility class provides fluent Map building
 /// <p>
 /// 测试场景s:
 /// 1. hashmap() creates builder for HashMap
 /// 2. put() adds key-value pairs
 /// 3. put with collections adds multiple entries
 /// 4. build() returns the built map
 /// 5. Collection size mismatch throws exception
 /// <p>
 /// 预期结果: Map can be built fluently with expected entries
class MapsTest {

///
     /// 测试目标: 验证y hashmap() returns builder instance
     /// 测试场景: Call Maps.hashmap()
     /// 预期结果: Builder is returned
    @Test
    void hashmap_ShouldReturnBuilder() {
        // when
        Maps.Builder<HashMap<String, Integer>, String, Integer> builder = Maps.hashmap();

        // then
        assertThat(builder).isNotNull();
    }

///
     /// 测试目标: 验证y put() adds single key-value pair
     /// 测试场景: Use put to add entries
     /// 预期结果: Map contains added entries
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

///
     /// 测试目标: 验证y put() returns builder for chaining
     /// 测试场景: Chain multiple put calls
     /// 预期结果: All entries added
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

///
     /// 测试目标: 验证y put with collections adds multiple entries
     /// 测试场景: Use put(keys, values) with matching sizes
     /// 预期结果: All key-value pairs added
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

///
     /// 测试目标: 验证y put with collections throws on size mismatch
     /// 测试场景: Use put(keys, values) with different sizes
     /// 预期结果: IllegalArgumentException thrown
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

///
     /// 测试目标: 验证y put with empty collections works
     /// 测试场景: Use put with empty lists
     /// 预期结果: Empty map returned
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

///
     /// 测试目标: 验证y build() returns the HashMap
     /// 测试场景: Call build()
     /// 预期结果: HashMap instance returned
    @Test
    void build_ShouldReturnHashMap() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .put("a", 1)
                .build();

        // then
        assertThat(map).isInstanceOf(HashMap.class);
    }

///
     /// 测试目标: 验证y can overwrite values for same key
     /// 测试场景: Put same key multiple times
     /// 预期结果: Last value wins
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

///
     /// 测试目标: 验证y mixed put and putAll usage
     /// 测试场景: Use both single and collection put
     /// 预期结果: All entries added
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

///
     /// 测试目标: 验证y builder accepts null keys and values
     /// 测试场景: Put null key and value
     /// 预期结果: Null entries added
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

///
     /// 测试目标: 验证y can build empty map
     /// 测试场景: Build without any puts
     /// 预期结果: Empty HashMap returned
    @Test
    void build_WithoutPuts_ShouldReturnEmptyMap() {
        // when
        Map<String, Integer> map = Maps.<String, Integer>hashmap()
                .build();

        // then
        assertThat(map).isEmpty();
    }
}
