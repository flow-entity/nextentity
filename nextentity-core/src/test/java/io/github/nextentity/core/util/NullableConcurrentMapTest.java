package io.github.nextentity.core.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证 NullableConcurrentMap 提供线程安全且支持 null 键/值的 Map 实现
///
/// 测试场景:
/// 1. 构造函数（默认、指定容量、从已有 Map）
/// 2. 基本 CRUD（put/get/remove/containsKey/containsValue）
/// 3. null 键支持
/// 4. null 值支持
/// 5. null 键 + null 值的极端场景
/// 6. size/isEmpty
/// 7. 批量操作（putAll/clear）
/// 8. 集合视图（keySet/values/entrySet）
/// 9. 并发原子操作（putIfAbsent/remove(K,V)/replace）
/// 10. compute 系列（computeIfAbsent/computeIfPresent/compute/merge）
/// 11. 默认方法（getOrDefault/forEach/replaceAll）
/// 12. equals/hashCode
/// 13. containsKey + get 区分“缺失键”和“映射到 null”
/// 14. 线程安全性
class NullableConcurrentMapTest {

    private NullableConcurrentMap<String, String> map;

    @BeforeEach
    void setUp() {
        map = new NullableConcurrentMap<>();
    }

    // ─── 构造函数 ───────────────────────────────────────

    @Nested
    class ConstructorTests {

        @Test
        void defaultConstructor_ShouldCreateEmptyMap() {
            NullableConcurrentMap<String, String> m = new NullableConcurrentMap<>();
            assertThat(m).isEmpty();
        }

        @Test
        void withInitialCapacity_ShouldCreateEmptyMap() {
            NullableConcurrentMap<String, String> m = new NullableConcurrentMap<>(16);
            assertThat(m).isEmpty();
        }

        @Test
        void fromExistingMap_ShouldCopyEntries() {
            Map<String, String> source = Map.of("a", "1", "b", "2");
            NullableConcurrentMap<String, String> m = new NullableConcurrentMap<>(source);
            assertThat(m).hasSize(2);
            assertThat(m.get("a")).isEqualTo("1");
            assertThat(m.get("b")).isEqualTo("2");
        }

        @Test
        void fromExistingMapWithNulls_ShouldCopyNullEntries() {
            Map<String, String> source = new HashMap<>();
            source.put(null, "nullKey");
            source.put("nullValue", null);
            source.put(null, null);
            NullableConcurrentMap<String, String> m = new NullableConcurrentMap<>(source);
            assertThat(m).hasSize(2);
            assertThat(m.get(null)).isNull();
            assertThat(m.get("nullValue")).isNull();
        }
    }

    // ─── 基本 CRUD ───────────────────────────────────────

    @Nested
    class BasicCrudTests {

        @Test
        void putAndGet_ShouldWorkWithNormalEntries() {
            map.put("key", "value");
            assertThat(map.get("key")).isEqualTo("value");
        }

        @Test
        void put_ShouldReturnPreviousValue() {
            assertThat(map.put("key", "v1")).isNull();
            assertThat(map.put("key", "v2")).isEqualTo("v1");
        }

        @Test
        void remove_ShouldReturnPreviousValue() {
            map.put("key", "value");
            assertThat(map.remove("key")).isEqualTo("value");
            assertThat(map.get("key")).isNull();
        }

        @Test
        void remove_NonExistentKey_ShouldReturnNull() {
            assertThat(map.remove("missing")).isNull();
        }

        @Test
        void containsKey_ShouldReturnTrueForExistingKey() {
            map.put("key", "value");
            assertThat(map.containsKey("key")).isTrue();
            assertThat(map.containsKey("missing")).isFalse();
        }

        @Test
        void containsValue_ShouldReturnTrueForExistingValue() {
            map.put("key", "value");
            assertThat(map.containsValue("value")).isTrue();
            assertThat(map.containsValue("missing")).isFalse();
        }
    }

    // ─── null 键支持 ───────────────────────────────────────

    @Nested
    class NullKeyTests {

        @Test
        void put_WithNullKey_ShouldStore() {
            map.put(null, "nullKeyValue");
            assertThat(map.get(null)).isEqualTo("nullKeyValue");
        }

        @Test
        void put_WithNullKey_ShouldReturnPreviousValue() {
            assertThat(map.put(null, "v1")).isNull();
            assertThat(map.put(null, "v2")).isEqualTo("v1");
        }

        @Test
        void remove_WithNullKey_ShouldReturnPreviousValue() {
            map.put(null, "value");
            assertThat(map.remove(null)).isEqualTo("value");
            assertThat(map.get(null)).isNull();
        }

        @Test
        void containsKey_WithNullKey_ShouldWork() {
            assertThat(map.containsKey(null)).isFalse();
            map.put(null, "value");
            assertThat(map.containsKey(null)).isTrue();
        }

        @Test
        void containsValue_WithNullKeyValue_ShouldWork() {
            map.put(null, "value");
            assertThat(map.containsValue("value")).isTrue();
        }

        @Test
        void size_WithNullKey_ShouldCountIt() {
            map.put("a", "1");
            map.put(null, "nullKeyValue");
            assertThat(map).hasSize(2);
        }

        @Test
        void clear_WithNullKey_ShouldRemoveIt() {
            map.put(null, "value");
            map.clear();
            assertThat(map).isEmpty();
            assertThat(map.get(null)).isNull();
        }
    }

    // ─── null 值支持 ───────────────────────────────────────

    @Nested
    class NullValueTests {

        @Test
        void put_WithNullValue_ShouldStore() {
            map.put("key", null);
            assertThat(map.get("key")).isNull();
            assertThat(map.containsKey("key")).isTrue();
        }

        @Test
        void put_WithNullValue_ShouldReturnPreviousValue() {
            map.put("key", "old");
            assertThat(map.put("key", null)).isEqualTo("old");
        }

        @Test
        void containsValue_WithNullValue_ShouldReturnTrue() {
            map.put("key", null);
            assertThat(map.containsValue(null)).isTrue();
        }

        @Test
        void remove_WithNullValue_ShouldWork() {
            map.put("key", null);
            assertThat(map.remove("key")).isNull();
            assertThat(map.containsKey("key")).isFalse();
        }
    }

    // ─── null 键 + null 值 ───────────────────────────────────────

    @Nested
    class NullKeyNullValueTests {

        @Test
        void put_WithNullKeyAndNullValue_ShouldStore() {
            map.put(null, null);
            assertThat(map.get(null)).isNull();
            assertThat(map.containsKey(null)).isTrue();
            assertThat(map).hasSize(1);
        }

        @Test
        void containsValue_WithBothNull_ShouldReturnTrue() {
            map.put(null, null);
            assertThat(map.containsValue(null)).isTrue();
        }

        @Test
        void replace_WithNullKeyAndNullOldValue_ShouldWork() {
            map.put(null, null);
            assertThat(map.replace(null, null, "newValue")).isTrue();
            assertThat(map.get(null)).isEqualTo("newValue");
        }

        @Test
        void replace_NullKeyNullOldValueMismatch_ShouldReturnFalse() {
            map.put(null, "existing");
            assertThat(map.replace(null, null, "newValue")).isFalse();
            assertThat(map.get(null)).isEqualTo("existing");
        }

        @Test
        void remove_WithNullKeyAndNullValue_ShouldMatch() {
            map.put(null, null);
            assertThat(map.remove(null, null)).isTrue();
            assertThat(map.containsKey(null)).isFalse();
        }

        @Test
        void remove_WithNullKeyNullValueMismatch_ShouldNotRemove() {
            map.put(null, "existing");
            assertThat(map.remove(null, null)).isFalse();
            assertThat(map.containsKey(null)).isTrue();
        }
    }

    // ─── size / isEmpty ───────────────────────────────────────

    @Nested
    class SizeTests {

        @Test
        void emptyMap_ShouldHaveZeroSize() {
            assertThat(map).isEmpty();
            assertThat(map).hasSize(0);
        }

        @Test
        void size_ShouldReflectAllEntriesIncludingNullKey() {
            map.put("a", "1");
            map.put("b", "2");
            map.put(null, "3");
            assertThat(map).hasSize(3);
        }

        @Test
        void size_AfterRemovingNullKey_ShouldDecrease() {
            map.put("a", "1");
            map.put(null, "2");
            map.remove(null);
            assertThat(map).hasSize(1);
        }
    }

    // ─── 批量操作 ───────────────────────────────────────

    @Nested
    class BulkOperationTests {

        @Test
        void putAll_ShouldCopyAllEntries() {
            Map<String, String> source = new HashMap<>();
            source.put("a", "1");
            source.put(null, "2");
            map.putAll(source);
            assertThat(map).hasSize(2);
            assertThat(map.get("a")).isEqualTo("1");
            assertThat(map.get(null)).isEqualTo("2");
        }

        @Test
        void clear_ShouldRemoveAllEntriesIncludingNullKey() {
            map.put("a", "1");
            map.put(null, "2");
            map.clear();
            assertThat(map).isEmpty();
            assertThat(map.get("a")).isNull();
            assertThat(map.get(null)).isNull();
        }
    }

    // ─── 集合视图 ───────────────────────────────────────

    @Nested
    class ViewTests {

        @Test
        void keySet_ShouldIncludeNullKey() {
            map.put("a", "1");
            map.put(null, "2");
            Set<String> keys = map.keySet();
            assertThat(keys).containsExactlyInAnyOrder("a", null);
        }

        @Test
        void keySet_WithoutNullKey_ShouldNotIncludeNull() {
            map.put("a", "1");
            Set<String> keys = map.keySet();
            assertThat(keys).containsExactly("a");
        }

        @Test
        void values_ShouldIncludeNullValue() {
            map.put("a", null);
            map.put("b", "2");
            Collection<String> values = map.values();
            assertThat(values).containsExactlyInAnyOrder(null, "2");
        }

        @Test
        void entrySet_ShouldIncludeNullKeyEntry() {
            map.put("a", "1");
            map.put(null, "2");
            Set<Map.Entry<String, String>> entries = map.entrySet();
            assertThat(entries).hasSize(2);
            Map<String, String> asMap = entries.stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            assertThat(asMap).containsEntry("a", "1");
            assertThat(asMap).containsEntry(null, "2");
        }

        @Test
        void values_WithNullKeyNullValue_ShouldIncludeNull() {
            map.put(null, null);
            Collection<String> values = map.values();
            assertThat(values).containsExactly((String) null);
        }

        @Test
        void keySet_ShouldBeLiveView() {
            Set<String> keys = map.keySet();
            map.put("a", "1");
            map.put(null, "2");
            assertThat(keys).containsExactlyInAnyOrder("a", null);

            map.remove("a");
            assertThat(keys).containsExactly((String) null);
        }

        @Test
        void keySet_RemoveShouldAffectMap_ForNullKey() {
            map.put(null, "value");
            Set<String> keys = map.keySet();
            assertThat(keys.remove(null)).isTrue();
            assertThat(map.containsKey(null)).isFalse();
        }

        @Test
        void values_ShouldBeLiveView() {
            Collection<String> values = map.values();
            map.put("a", "1");
            map.put("b", null);
            assertThat(values).containsExactlyInAnyOrder("1", null);

            map.remove("a");
            assertThat(values).containsExactly((String) null);
        }

        @Test
        void values_RemoveNullShouldAffectMap() {
            map.put("a", null);
            map.put("b", "2");
            Collection<String> values = map.values();
            assertThat(values.remove(null)).isTrue();
            assertThat(map.containsKey("a")).isFalse();
            assertThat(map.containsKey("b")).isTrue();
        }

        @Test
        void entrySet_ShouldBeLiveView() {
            Set<Map.Entry<String, String>> entries = map.entrySet();
            map.put("a", "1");
            map.put(null, "2");
            assertThat(entries).hasSize(2);

            map.remove("a");
            assertThat(entries).hasSize(1);
            assertThat(entries.iterator().next().getKey()).isNull();
        }

        @Test
        void entrySet_IteratorRemoveShouldAffectMap() {
            map.put("a", "1");
            map.put("b", "2");
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            Map.Entry<String, String> first = it.next();
            it.remove();

            assertThat(map.containsKey(first.getKey())).isFalse();
            assertThat(map).hasSize(1);
        }

        @Test
        void entrySet_SetValueShouldAffectMap() {
            map.put(null, "old");
            Map.Entry<String, String> nullEntry = map.entrySet().stream()
                    .filter(e -> e.getKey() == null)
                    .findFirst()
                    .orElseThrow();
            assertThat(nullEntry.setValue("new")).isEqualTo("old");
            assertThat(map.get(null)).isEqualTo("new");
        }
    }

    // ─── 并发原子操作 ───────────────────────────────────────

    @Nested
    class AtomicOperationTests {

        @Test
        void putIfAbsent_ShouldInsertWhenMissing() {
            assertThat(map.putIfAbsent("key", "value")).isNull();
            assertThat(map.get("key")).isEqualTo("value");
        }

        @Test
        void putIfAbsent_ShouldNotOverwriteExisting() {
            map.put("key", "existing");
            assertThat(map.putIfAbsent("key", "new")).isEqualTo("existing");
            assertThat(map.get("key")).isEqualTo("existing");
        }

        @Test
        void putIfAbsent_WithNullKey_ShouldWork() {
            assertThat(map.putIfAbsent(null, "value")).isNull();
            assertThat(map.get(null)).isEqualTo("value");
        }

        @Test
        void putIfAbsent_WithNullKeyExisting_ShouldReturnExisting() {
            map.put(null, "existing");
            assertThat(map.putIfAbsent(null, "new")).isEqualTo("existing");
        }

        @Test
        void putIfAbsent_WithNullValue_ShouldWork() {
            assertThat(map.putIfAbsent("key", null)).isNull();
            assertThat(map.get("key")).isNull();
            assertThat(map.containsKey("key")).isTrue();
        }

        @Test
        void putIfAbsent_WhenExistingValueIsNull_ShouldOverwritePerMapSemantics() {
            map.put("key", null);
            assertThat(map.putIfAbsent("key", "new")).isNull();
            assertThat(map.get("key")).isEqualTo("new");
        }

        @Test
        void putIfAbsent_WhenNullKeyExistingValueIsNull_ShouldOverwritePerMapSemantics() {
            map.put(null, null);
            assertThat(map.putIfAbsent(null, "new")).isNull();
            assertThat(map.get(null)).isEqualTo("new");
        }

        @Test
        void remove_KeyValue_ShouldRemoveWhenMatching() {
            map.put("key", "value");
            assertThat(map.remove("key", "value")).isTrue();
            assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        void remove_KeyValue_ShouldNotRemoveWhenValueMismatch() {
            map.put("key", "value");
            assertThat(map.remove("key", "wrong")).isFalse();
            assertThat(map.containsKey("key")).isTrue();
        }

        @Test
        void remove_KeyValue_WithNullKeyAndNullValue_ShouldWork() {
            map.put(null, null);
            assertThat(map.remove(null, null)).isTrue();
            assertThat(map.containsKey(null)).isFalse();
        }

        @Test
        void replace_OldNew_ShouldReplaceWhenMatching() {
            map.put("key", "old");
            assertThat(map.replace("key", "old", "new")).isTrue();
            assertThat(map.get("key")).isEqualTo("new");
        }

        @Test
        void replace_OldNew_ShouldNotReplaceWhenMismatch() {
            map.put("key", "old");
            assertThat(map.replace("key", "wrong", "new")).isFalse();
            assertThat(map.get("key")).isEqualTo("old");
        }

        @Test
        void replace_OldNew_WithNullKey_ShouldWork() {
            map.put(null, "old");
            assertThat(map.replace(null, "old", "new")).isTrue();
            assertThat(map.get(null)).isEqualTo("new");
        }

        @Test
        void replace_KeyValue_ShouldReturnOldValue() {
            map.put("key", "old");
            assertThat(map.replace("key", "new")).isEqualTo("old");
            assertThat(map.get("key")).isEqualTo("new");
        }

        @Test
        void replace_KeyValue_WithNullKey_ShouldWork() {
            map.put(null, "old");
            assertThat(map.replace(null, "new")).isEqualTo("old");
            assertThat(map.get(null)).isEqualTo("new");
        }

        @Test
        void replace_KeyValue_WhenKeyNotExists_ShouldReturnNull() {
            assertThat(map.replace("missing", "value")).isNull();
            assertThat(map.containsKey("missing")).isFalse();
        }

        @Test
        void replace_KeyValue_WhenNullKeyNotExists_ShouldReturnNull() {
            assertThat(map.replace(null, "value")).isNull();
            assertThat(map.containsKey(null)).isFalse();
        }
    }

    // ─── compute 系列 ───────────────────────────────────────

    @Nested
    class ComputeTests {

        @Test
        void computeIfAbsent_ShouldComputeWhenMissing() {
            String result = map.computeIfAbsent("key", k -> "computed_" + k);
            assertThat(result).isEqualTo("computed_key");
            assertThat(map.get("key")).isEqualTo("computed_key");
        }

        @Test
        void computeIfAbsent_ShouldNotComputeWhenPresent() {
            map.put("key", "existing");
            AtomicInteger callCount = new AtomicInteger(0);
            String result = map.computeIfAbsent("key", k -> {
                callCount.incrementAndGet();
                return "computed";
            });
            assertThat(result).isEqualTo("existing");
            assertThat(callCount.get()).isEqualTo(0);
        }

        @Test
        void computeIfAbsent_WithNullKey_ShouldWork() {
            String result = map.computeIfAbsent(null, k -> "computed_for_null");
            assertThat(result).isEqualTo("computed_for_null");
            assertThat(map.get(null)).isEqualTo("computed_for_null");
        }

        @Test
        void computeIfAbsent_WhenMappingReturnsNull_ShouldStoreNull() {
            String result = map.computeIfAbsent("key", k -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        void computeIfAbsent_WhenExistingValueIsNull_ShouldComputeAndReplace() {
            map.put("key", null);
            String result = map.computeIfAbsent("key", k -> "computed");
            assertThat(result).isEqualTo("computed");
            assertThat(map.get("key")).isEqualTo("computed");
        }

        @Test
        void computeIfAbsent_WithNullKeyAndNullValue_ShouldComputeAndReplace() {
            map.put(null, null);
            String result = map.computeIfAbsent(null, k -> "computed");
            assertThat(result).isEqualTo("computed");
            assertThat(map.get(null)).isEqualTo("computed");
        }

        @Test
        void computeIfAbsent_WithNullKeyWhenMappingReturnsNull_ShouldNotStore() {
            String result = map.computeIfAbsent(null, k -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey(null)).isFalse();
        }

        @Test
        void computeIfPresent_ShouldRemapWhenPresent() {
            map.put("key", "old");
            String result = map.computeIfPresent("key", (k, v) -> k + "_" + v);
            assertThat(result).isEqualTo("key_old");
            assertThat(map.get("key")).isEqualTo("key_old");
        }

        @Test
        void computeIfPresent_ShouldRemoveWhenRemapReturnsNull() {
            map.put("key", "old");
            String result = map.computeIfPresent("key", (k, v) -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        void computeIfPresent_ShouldReturnNullWhenKeyMissing() {
            String result = map.computeIfPresent("missing", (k, v) -> "computed");
            assertThat(result).isNull();
            assertThat(map.containsKey("missing")).isFalse();
        }

        @Test
        void computeIfPresent_WithNormalKeyNullValue_ShouldNotRemapPerMapSemantics() {
            map.put("key", null);
            AtomicInteger callCount = new AtomicInteger(0);
            String result = map.computeIfPresent("key", (k, v) -> {
                callCount.incrementAndGet();
                return "remapped";
            });
            assertThat(result).isNull();
            assertThat(callCount.get()).isEqualTo(0);
            assertThat(map.containsKey("key")).isTrue();
            assertThat(map.get("key")).isNull();
        }

        @Test
        void computeIfPresent_WithNullKey_ShouldWork() {
            map.put(null, "old");
            String result = map.computeIfPresent(null, (k, v) -> "new_" + v);
            assertThat(result).isEqualTo("new_old");
            assertThat(map.get(null)).isEqualTo("new_old");
        }

        @Test
        void computeIfPresent_WithNullKeyNullValue_ShouldNotRemapPerMapSemantics() {
            map.put(null, null);
            AtomicInteger callCount = new AtomicInteger(0);
            String result = map.computeIfPresent(null, (k, v) -> {
                callCount.incrementAndGet();
                return "remapped";
            });
            assertThat(result).isNull();
            assertThat(callCount.get()).isEqualTo(0);
            assertThat(map.containsKey(null)).isTrue();
            assertThat(map.get(null)).isNull();
        }

        @Test
        void compute_ShouldComputeWhenMissing() {
            String result = map.compute("key", (k, v) -> {
                assertThat(v).isNull();
                return "computed";
            });
            assertThat(result).isEqualTo("computed");
            assertThat(map.get("key")).isEqualTo("computed");
        }

        @Test
        void compute_ShouldRemapWhenPresent() {
            map.put("key", "old");
            String result = map.compute("key", (k, v) -> k + "_" + v);
            assertThat(result).isEqualTo("key_old");
        }

        @Test
        void compute_WhenRemapReturnsNull_ShouldRemove() {
            map.put("key", "old");
            String result = map.compute("key", (k, v) -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        void compute_WithNullKey_ShouldWork() {
            String result = map.compute(null, (k, v) -> "computed");
            assertThat(result).isEqualTo("computed");
            assertThat(map.get(null)).isEqualTo("computed");
        }

        @Test
        void compute_WithNullKeyNullValueBothNull_ShouldNotInsert() {
            String result = map.compute(null, (k, v) -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey(null)).isFalse();
        }

        @Test
        void merge_ShouldInsertWhenMissing() {
            String result = map.merge("key", "value", (old, newV) -> old + newV);
            assertThat(result).isEqualTo("value");
            assertThat(map.get("key")).isEqualTo("value");
        }

        @Test
        void merge_ShouldMergeWhenPresent() {
            map.put("key", "old_");
            String result = map.merge("key", "new", (old, newV) -> old + newV);
            assertThat(result).isEqualTo("old_new");
            assertThat(map.get("key")).isEqualTo("old_new");
        }

        @Test
        void merge_WhenRemapReturnsNull_ShouldRemove() {
            map.put("key", "old");
            String result = map.merge("key", "new", (old, newV) -> null);
            assertThat(result).isNull();
            assertThat(map.containsKey("key")).isFalse();
        }

        @Test
        void merge_WithNullKey_ShouldWork() {
            String result = map.merge(null, "value", (old, newV) -> old + newV);
            assertThat(result).isEqualTo("value");
            assertThat(map.get(null)).isEqualTo("value");
        }

        @Test
        void merge_WithNullKeyExisting_ShouldMerge() {
            map.put(null, "old_");
            String result = map.merge(null, "new", (old, newV) -> old + newV);
            assertThat(result).isEqualTo("old_new");
        }

        @Test
        void merge_WhenExistingValueIsNull_ShouldUseGivenValueWithoutRemap() {
            map.put("key", null);
            AtomicInteger callCount = new AtomicInteger(0);
            String result = map.merge("key", "value", (old, newV) -> {
                callCount.incrementAndGet();
                return old + newV;
            });
            assertThat(result).isEqualTo("value");
            assertThat(callCount.get()).isEqualTo(0);
            assertThat(map.get("key")).isEqualTo("value");
        }

        @Test
        void merge_WithNullKeyExistingValueIsNull_ShouldUseGivenValueWithoutRemap() {
            map.put(null, null);
            AtomicInteger callCount = new AtomicInteger(0);
            String result = map.merge(null, "value", (old, newV) -> {
                callCount.incrementAndGet();
                return old + newV;
            });
            assertThat(result).isEqualTo("value");
            assertThat(callCount.get()).isEqualTo(0);
            assertThat(map.get(null)).isEqualTo("value");
        }

        @Test
        void merge_WithNullValue_ShouldThrowLikeHashMap() {
            assertThatThrownBy(() -> map.merge("key", null, (old, newV) -> old + newV))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void merge_WithNullKeyAndNullValue_ShouldThrowLikeHashMap() {
            assertThatThrownBy(() -> map.merge(null, null, (old, newV) -> old + newV))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    // ─── 默认方法 ───────────────────────────────────────

    @Nested
    class DefaultMethodTests {

        @Test
        void getOrDefault_WhenKeyExists_ShouldReturnValue() {
            map.put("key", "value");
            assertThat(map.getOrDefault("key", "default")).isEqualTo("value");
        }

        @Test
        void getOrDefault_WhenKeyMissing_ShouldReturnDefault() {
            assertThat(map.getOrDefault("missing", "default")).isEqualTo("default");
        }

        @Test
        void getOrDefault_WithNullKey_ShouldWork() {
            map.put(null, "value");
            assertThat(map.getOrDefault(null, "default")).isEqualTo("value");
        }

        @Test
        void getOrDefault_WithNullKeyMissing_ShouldReturnDefault() {
            assertThat(map.getOrDefault(null, "default")).isEqualTo("default");
        }

        @Test
        void getOrDefault_WithNullValueStored_ShouldReturnNull() {
            map.put("key", null);
            assertThat(map.getOrDefault("key", "default")).isNull();
        }

        @Test
        void getOrDefault_WithNullKeyNullValueStored_ShouldReturnNull() {
            map.put(null, null);
            assertThat(map.getOrDefault(null, "default")).isNull();
        }

        @Test
        void forEach_ShouldIterateAllEntriesIncludingNullKey() {
            map.put("a", "1");
            map.put(null, "2");
            Map<String, String> collected = new HashMap<>();
            map.forEach(collected::put);
            assertThat(collected).hasSize(2);
            assertThat(collected).containsEntry("a", "1");
            assertThat(collected).containsEntry(null, "2");
        }

        @Test
        void replaceAll_ShouldTransformAllValuesIncludingNullKey() {
            map.put("a", "1");
            map.put(null, "2");
            map.replaceAll((k, v) -> v + "_transformed");
            assertThat(map.get("a")).isEqualTo("1_transformed");
            assertThat(map.get(null)).isEqualTo("2_transformed");
        }

        @Test
        void replaceAll_WithNullValue_ShouldTransform() {
            map.put("key", null);
            map.replaceAll((k, v) -> "replaced");
            assertThat(map.get("key")).isEqualTo("replaced");
        }
    }

    // ─── equals / hashCode ───────────────────────────────────────

    @Nested
    class EqualsHashCodeTests {

        @Test
        void equals_SameContent_ShouldBeEqual() {
            map.put("a", "1");
            map.put("b", "2");

            NullableConcurrentMap<String, String> other = new NullableConcurrentMap<>();
            other.put("a", "1");
            other.put("b", "2");

            assertThat(map).isEqualTo(other);
            assertThat(map.hashCode()).isEqualTo(other.hashCode());
        }

        @Test
        void equals_DifferentContent_ShouldNotBeEqual() {
            map.put("a", "1");
            NullableConcurrentMap<String, String> other = new NullableConcurrentMap<>();
            other.put("a", "2");
            assertThat(map).isNotEqualTo(other);
        }

        @Test
        void equals_WithNullEntries_ShouldBeEqual() {
            map.put(null, "value");
            NullableConcurrentMap<String, String> other = new NullableConcurrentMap<>();
            other.put(null, "value");
            assertThat(map).isEqualTo(other);
        }

        @Test
        void equals_WithNullValues_ShouldBeEqual() {
            map.put("key", null);
            NullableConcurrentMap<String, String> other = new NullableConcurrentMap<>();
            other.put("key", null);
            assertThat(map).isEqualTo(other);
        }

        @Test
        void equals_WithHashMap_ShouldWork() {
            map.put("a", "1");
            map.put("b", "2");
            Map<String, String> hashMap = new HashMap<>(Map.of("a", "1", "b", "2"));
            assertThat(map).isEqualTo(hashMap);
        }

        @Test
        void equals_SameInstance_ShouldReturnTrue() {
            assertThat(map).isEqualTo(map);
        }

        @Test
        void equals_NonMapObject_ShouldReturnFalse() {
            assertThat(map).isNotEqualTo("not a map");
        }

        @Test
        void hashCode_ConsistentAcrossCalls() {
            map.put("a", "1");
            int h1 = map.hashCode();
            int h2 = map.hashCode();
            assertThat(h1).isEqualTo(h2);
        }
    }

    // ─── containsKey + get 语义 ───────────────────────────────────────

    @Nested
    class ContainsKeyGetTests {

        @Test
        void get_WithNullValueStored_ShouldReturnNullAndContainsKeyTrue() {
            map.put("key", null);
            assertThat(map.containsKey("key")).isTrue();
            assertThat(map.get("key")).isNull();
        }

        @Test
        void get_WithNormalValue_ShouldReturnValueAndContainsKeyTrue() {
            map.put("key", "value");
            assertThat(map.containsKey("key")).isTrue();
            assertThat(map.get("key")).isEqualTo("value");
        }

        @Test
        void get_WithNullKeyMappedToNull_ShouldReturnNullAndContainsKeyTrue() {
            map.put(null, null);
            assertThat(map.containsKey(null)).isTrue();
            assertThat(map.get(null)).isNull();
        }

        @Test
        void get_WithMissingKey_ShouldReturnNullAndContainsKeyFalse() {
            assertThat(map.containsKey("missing")).isFalse();
            assertThat(map.get("missing")).isNull();
        }

        @Test
        void get_WithNullKeyMissing_ShouldReturnNullAndContainsKeyFalse() {
            assertThat(map.containsKey(null)).isFalse();
            assertThat(map.get(null)).isNull();
        }
    }

    // ─── 线程安全性 ───────────────────────────────────────

    @Nested
    class ThreadSafetyTests {

        @Test
        void concurrentPutOnNullKey_ShouldBeThreadSafe() throws InterruptedException {
            int threadCount = 20;
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);
            List<Thread> threads = new ArrayList<>(threadCount);

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                Thread thread = new Thread(() -> {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    String prev = map.put(null, "value_" + index);
                    if (prev == null) {
                        successCount.incrementAndGet();
                    }
                });
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            assertThat(map.containsKey(null)).isTrue();
            assertThat(map).hasSize(1);
            assertThat(successCount.get()).isEqualTo(1);
        }

        @Test
        void concurrentPutIfAbsentOnNullKey_ShouldInsertOnlyOnce() throws InterruptedException {
            int threadCount = 20;
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger firstInsertCount = new AtomicInteger(0);
            List<Thread> threads = new ArrayList<>(threadCount);

            for (int i = 0; i < threadCount; i++) {
                Thread thread = new Thread(() -> {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    String prev = map.putIfAbsent(null, "inserted");
                    if (prev == null) {
                        firstInsertCount.incrementAndGet();
                    }
                });
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            assertThat(firstInsertCount.get()).isEqualTo(1);
            assertThat(map.get(null)).isEqualTo("inserted");
        }

        @Test
        void concurrentMixedOperations_ShouldRemainConsistent() throws InterruptedException {
            int threadCount = 10;
            CountDownLatch latch = new CountDownLatch(threadCount * 3);
            List<Thread> threads = new ArrayList<>(threadCount * 3);

            // writers on normal keys
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                Thread thread = new Thread(() -> {
                    latch.countDown();
                    try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    map.put("key_" + index, "value_" + index);
                });
                threads.add(thread);
                thread.start();
            }

            // writers on null key
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                Thread thread = new Thread(() -> {
                    latch.countDown();
                    try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    map.put(null, "nullValue_" + index);
                });
                threads.add(thread);
                thread.start();
            }

            // readers
            for (int i = 0; i < threadCount; i++) {
                Thread thread = new Thread(() -> {
                    latch.countDown();
                    try { latch.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    map.get("key_0");
                    map.get(null);
                    map.containsKey(null);
                    map.size();
                });
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            // All normal keys should be present
            for (int i = 0; i < threadCount; i++) {
                assertThat(map.get("key_" + i)).isEqualTo("value_" + i);
            }
            // Null key should exist with some value
            assertThat(map.containsKey(null)).isTrue();
            // Total size: threadCount normal keys + 1 null key
            assertThat(map).hasSize(threadCount + 1);
        }
    }

}
