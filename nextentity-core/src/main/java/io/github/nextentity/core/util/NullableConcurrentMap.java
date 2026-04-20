package io.github.nextentity.core.util;

import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/// 基于 {@link ConcurrentHashMap} 的线程安全 {@link Map} 实现，支持 {@code null} 键和 {@code null} 值。
///
/// 语义对齐 {@link Map} 默认方法：当值为 {@code null} 时，按“缺失映射”处理。
///
/// @param <K> 键的类型
/// @param <V> 值的类型
public class NullableConcurrentMap<K, V> implements Map<K, V>, Serializable {

    @Serial
    private static final long serialVersionUID = 8763780967753881415L;

    private static final Object NULL_KEY = new Object();
    private static final Object NULL_VALUE = new Object();

    private final ConcurrentHashMap<Object, Object> target;

    public NullableConcurrentMap() {
        this.target = new ConcurrentHashMap<>();
    }

    public NullableConcurrentMap(int initialCapacity) {
        this.target = new ConcurrentHashMap<>(initialCapacity);
    }

    public NullableConcurrentMap(Map<? extends K, ? extends V> m) {
        this.target = new ConcurrentHashMap<>(m.size());
        putAll(m);
    }

    public static boolean isNull(Object result) {
        return result == NULL_VALUE || result == null;
    }

    @Override
    public int size() {
        return target.size();
    }

    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @Override
    public V get(Object key) {
        return unwrapValue(target.get(wrapKey(key)));
    }

    /// 返回原始的（可能被哨兵值包装的）值，不进行解包。
    public Object getRaw(K key) {
        return target.get(wrapKey(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return target.containsKey(wrapKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return target.containsValue(wrapValue(value));
    }

    @Override
    public V put(K key, V value) {
        return unwrapValue(target.put(wrapKey(key), wrapValue(value)));
    }

    @Override
    public V remove(Object key) {
        return unwrapValue(target.remove(wrapKey(key)));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        target.clear();
    }

    @Override
    @NonNull
    public Set<K> keySet() {
        return new AbstractSet<>() {
            @Override
            @NonNull
            public Iterator<K> iterator() {
                Iterator<Object> it = target.keySet().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public K next() {
                        return unwrapKey(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return target.size();
            }

            @Override
            public boolean contains(Object o) {
                return target.containsKey(wrapKey(o));
            }

            @Override
            public boolean remove(Object o) {
                return target.remove(wrapKey(o)) != null;
            }

            @Override
            public void clear() {
                target.clear();
            }
        };
    }

    @Override
    @NonNull
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            @NonNull
            public Iterator<V> iterator() {
                Iterator<Object> it = target.values().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public V next() {
                        return unwrapValue(it.next());
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return target.size();
            }

            @Override
            public boolean contains(Object o) {
                return target.containsValue(wrapValue(o));
            }

            @Override
            public void clear() {
                target.clear();
            }
        };
    }

    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            @NonNull
            public Iterator<Entry<K, V>> iterator() {
                Iterator<Entry<Object, Object>> it = target.entrySet().iterator();
                return new Iterator<>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public Entry<K, V> next() {
                        Entry<Object, Object> raw = it.next();
                        return new EntryView(raw);
                    }

                    @Override
                    public void remove() {
                        it.remove();
                    }
                };
            }

            @Override
            public int size() {
                return target.size();
            }

            @Override
            public void clear() {
                target.clear();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Entry<?, ?> entry)) {
                    return false;
                }
                Object raw = target.get(wrapKey(entry.getKey()));
                return raw != null && Objects.equals(raw, wrapValue(entry.getValue()));
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof Entry<?, ?> entry)) {
                    return false;
                }
                return target.remove(wrapKey(entry.getKey()), wrapValue(entry.getValue()));
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map<?, ?> other)) {
            return false;
        }
        if (size() != other.size()) {
            return false;
        }
        for (Entry<K, V> entry : entrySet()) {
            if (!Objects.equals(entry.getValue(), other.get(entry.getKey()))) {
                return false;
            }
            if (entry.getValue() == null && !other.containsKey(entry.getKey())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (Entry<K, V> entry : entrySet()) {
            hash += entry.hashCode();
        }
        return hash;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        Object wrappedKey = wrapKey(key);
        Object wrappedValue = wrapValue(value);
        while (true) {
            Object current = target.get(wrappedKey);
            if (current == null) {
                Object previous = target.putIfAbsent(wrappedKey, wrappedValue);
                if (previous == null) {
                    return null;
                }
                continue;
            }
            if (current == NULL_VALUE) {
                if (target.replace(wrappedKey, NULL_VALUE, wrappedValue)) {
                    return null;
                }
                continue;
            }
            return unwrapValue(current);
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        return target.remove(wrapKey(key), wrapValue(value));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return target.replace(wrapKey(key), wrapValue(oldValue), wrapValue(newValue));
    }

    @Override
    public V replace(K key, V value) {
        return unwrapValue(target.replace(wrapKey(key), wrapValue(value)));
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        Object raw = target.get(wrapKey(key));
        return raw == null ? defaultValue : unwrapValue(raw);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        target.forEach((k, v) -> action.accept(unwrapKey(k), unwrapValue(v)));
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        target.replaceAll((k, v) -> wrapValue(function.apply(unwrapKey(k), unwrapValue(v))));
    }

    @Override
    public V computeIfAbsent(K key, @NonNull Function<? super K, ? extends V> mappingFunction) {
        Object wrappedKey = wrapKey(key);
        Object raw = target.compute(wrappedKey, (_, current) -> {
            if (current != null && current != NULL_VALUE) {
                return current;
            }
            V newValue = mappingFunction.apply(key);
            if (newValue == null) {
                return current;
            }
            return wrapValue(newValue);
        });
        return unwrapValue(raw);
    }

    @Override
    public V computeIfPresent(K key, @NonNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Object wrappedKey = wrapKey(key);
        Object raw = target.compute(wrappedKey, (_, current) -> {
            if (current == null || current == NULL_VALUE) {
                return current;
            }
            V newValue = remappingFunction.apply(key, unwrapValue(current));
            return newValue == null ? null : wrapValue(newValue);
        });
        return unwrapValue(raw);
    }

    @Override
    public V compute(K key, @NonNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Object wrappedKey = wrapKey(key);
        Object raw = target.compute(wrappedKey, (_, current) -> {
            V oldValue = current == null ? null : unwrapValue(current);
            V newValue = remappingFunction.apply(key, oldValue);
            return newValue == null ? null : wrapValue(newValue);
        });
        return unwrapValue(raw);
    }

    @Override
    public V merge(K key, @NonNull V value, @NonNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Object wrappedKey = wrapKey(key);
        Object wrappedValue = wrapValue(value);
        while (true) {
            Object current = target.get(wrappedKey);
            if (current == null || current == NULL_VALUE) {
                if (current == null) {
                    if (target.putIfAbsent(wrappedKey, wrappedValue) == null) {
                        return value;
                    }
                } else if (target.replace(wrappedKey, NULL_VALUE, wrappedValue)) {
                    return value;
                }
                continue;
            }
            V oldValue = unwrapValue(current);
            V newValue = remappingFunction.apply(oldValue, value);
            if (newValue == null) {
                if (target.remove(wrappedKey, current)) {
                    return null;
                }
            } else {
                Object next = wrapValue(newValue);
                if (target.replace(wrappedKey, current, next)) {
                    return newValue;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public V unwrap(Object value) {
        return value == NULL_VALUE ? null : (V) value;
    }

    private static Object wrapKey(Object key) {
        return key == null ? NULL_KEY : key;
    }

    private static Object wrapValue(Object value) {
        return value == null ? NULL_VALUE : value;
    }

    @SuppressWarnings("unchecked")
    private K unwrapKey(Object key) {
        return key == NULL_KEY ? null : (K) key;
    }

    @SuppressWarnings("unchecked")
    private V unwrapValue(Object value) {
        return value == NULL_VALUE ? null : (V) value;
    }

    private final class EntryView implements Entry<K, V> {
        private final Object rawKey;

        EntryView(Entry<Object, Object> rawEntry) {
            this.rawKey = rawEntry.getKey();
        }

        @Override
        public K getKey() {
            return unwrapKey(rawKey);
        }

        @Override
        public V getValue() {
            return unwrapValue(target.get(rawKey));
        }

        @Override
        public V setValue(V value) {
            Object raw = target.replace(rawKey, wrapValue(value));
            if (raw == null) {
                throw new ConcurrentModificationException("Entry no longer present");
            }
            return unwrapValue(raw);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Entry<?, ?> other)) {
                return false;
            }
            return Objects.equals(getKey(), other.getKey()) && Objects.equals(getValue(), other.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }
    }
}
