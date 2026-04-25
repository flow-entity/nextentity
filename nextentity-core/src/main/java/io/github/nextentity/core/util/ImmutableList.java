package io.github.nextentity.core.util;


import io.github.nextentity.core.TypeCastUtil;
import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collector;
import java.util.stream.Stream;

/// 不可变列表实现，提供线程安全的不可变列表功能。
///
/// @param <E> 元素类型
/// @author HuangChengwei
/// @since 1.0.0
public class ImmutableList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable, ImmutableArray<E> {

    private static final ImmutableList<?> EMPTY = new ImmutableList<>(EmptyArrays.OBJECT);

    private final Object[] elements;

    /// 从给定元素创建不可变列表。
    ///
    /// 注意：此方法不会创建输入数组的防御副本。
    /// 返回的列表将直接引用原始数组。
    /// 在调用此方法后对原始数组的任何修改都会反映在列表中，反之亦然。
    ///
    /// 何时使用此方法：
    /// - 当你想避免复制数组的开销时
    /// - 当你知道创建列表后数组不会被修改时
    /// - 当你使用一个不会再共享的新创建数组时
    ///
    /// 何时不要使用此方法：
    /// - 当输入数组可能被外部修改时
    /// - 当你需要真正的不可变保证时
    /// - 当数组与不受信任的代码共享时
    ///
    /// 如需创建防御性副本的版本，请使用 {@link #copyOf(Object[])}。
    ///
    /// @param elements 要包含在列表中的元素
    /// @param <T>      元素类型
    /// @return 包含指定元素的不可变列表
    @SafeVarargs
    public static <T> ImmutableList<T> of(T... elements) {
        if (elements.length == 0) {
            return empty();
        }
        return new ImmutableList<>(elements);
    }

    /// 从给定数组创建带有防御性副本的不可变列表。
    ///
    /// 此方法创建输入数组的副本，确保对原始数组的修改不会影响返回的列表，反之亦然。
    ///
    /// 何时使用此方法：
    /// - 当你需要真正的不可变保证时
    /// - 当输入数组可能被外部修改时
    /// - 当你希望确保列表独立于源数组时
    ///
    /// 何时改用 {@link #of(Object[])}：
    /// - 当性能至关重要且你可以保证数组不会改变时
    /// - 当处理新创建的私有数组时
    ///
    /// @param array 要复制并从中创建列表的数组
    /// @param <T>   元素类型
    /// @return 包含数组元素副本的不可变列表
    public static <T> ImmutableList<T> copyOf(T[] array) {
        if (array.length == 0) {
            return empty();
        }
        return new ImmutableList<>(array.clone());
    }

    /// 从可迭代对象创建不可变列表。
    ///
    /// @param iterable 可迭代对象
    /// @param <T>      元素类型
    /// @return 包含可迭代对象元素的不可变列表
    public static <T> ImmutableList<T> ofIterable(Iterable<T> iterable) {
        return iterable instanceof Collection<T> collection
                ? ofCollection(collection)
                : new ImmutableList<>(Iterators.toArray(iterable));
    }

    /// 从集合创建不可变列表。
    ///
    /// @param collection 集合
    /// @param <T>        元素类型
    /// @return 包含集合元素的不可变列表
    public static <T> @NonNull ImmutableList<T> ofCollection(Collection<T> collection) {
        if (collection instanceof ImmutableList<T> list) {
            return list;
        } else if (collection.isEmpty()) {
            return empty();
        }
        return new ImmutableList<>(collection);
    }

    /// 连接两个集合创建不可变列表。
    ///
    /// @param a   第一个集合
    /// @param b   第二个集合
    /// @param <T> 元素类型
    /// @return 连接后的新不可变列表
    public static <T> ImmutableList<T> concat(Collection<? extends T> a, Collection<? extends T> b) {
        Builder<T> list = new Builder<>(a.size() + b.size());
        list.addAll(a);
        list.addAll(b);
        return list.build();
    }

    /// 获取空的不可变列表实例。
    ///
    /// @param <T> 元素类型
    /// @return 空的不可变列表
    public static <T> @NonNull ImmutableList<T> empty() {
        return TypeCastUtil.unsafeCast(EMPTY);
    }

    /// 从集合创建不可变列表。
    ///
    /// @param collection 集合
    public ImmutableList(Collection<? extends E> collection) {
        this(collection.toArray());
    }

    /// 从对象数组创建不可变列表。
    ///
    /// @param elements 元素数组
    protected ImmutableList(Object[] elements) {
        this.elements = elements;
    }

    /// 获取列表大小。
    ///
    /// @return 列表中元素的数量
    @Override
    public int size() {
        return elements.length;
    }

    /// 获取迭代器。
    ///
    /// @return 列表迭代器
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /// 获取列表迭代器。
    ///
    /// @return 列表迭代器
    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        return new Itr();
    }

    /// 获取流。
    ///
    /// @return 元素流
    @Override
    public Stream<E> stream() {
        return super.stream();
    }

    /// 返回自身作为列表。
    ///
    /// @return 列表
    @Override
    public List<E> asList() {
        return this;
    }

    /// 列表迭代器内部类。
    private class Itr implements ListIterator<E> {
        int cursor;

        /// 检查是否有下一个元素。
        ///
        /// @return 如果有下一个元素返回true，否则返回false
        @Override
        public boolean hasNext() {
            return cursor < elements.length;
        }

        /// 获取下一个元素。
        ///
        /// @return 下一个元素
        @Override
        public E next() {
            return get(cursor++);
        }

        /// 检查是否有前一个元素。
        ///
        /// @return 如果有前一个元素返回true，否则返回false
        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        /// 获取前一个元素。
        ///
        /// @return 前一个元素
        @Override
        public E previous() {
            return get(--cursor);
        }

        /// 获取下一个索引。
        ///
        /// @return 下一个索引
        @Override
        public int nextIndex() {
            return cursor;
        }

        /// 获取前一个索引。
        ///
        /// @return 前一个索引
        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        /// 移除元素（不支持）。
        ///
        /// @throws UnsupportedOperationException 始终抛出此异常
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /// 设置元素（不支持）。
        ///
        /// @param e 要设置的元素
        /// @throws UnsupportedOperationException 始终抛出此异常
        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        /// 添加元素（不支持）。
        ///
        /// @param e 要添加的元素
        /// @throws UnsupportedOperationException 始终抛出此异常
        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

    /// 转换为对象数组。
    ///
    /// @return 对象数组副本
    @NonNull
    @Override
    public Object @NonNull [] toArray() {
        return elements.clone();
    }

    /// 转换为指定类型的数组。
    ///
    /// @param a   目标数组
    /// @param <T> 数组元素类型
    /// @return 指定类型的数组
    @Override
    public <T> T @NonNull [] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            return TypeCastUtil.unsafeCast(Arrays.copyOf(elements, size, a.getClass()));
        }
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    /// 添加元素（不支持）。
    ///
    /// @param t 要添加的元素
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean add(E t) {
        throw new UnsupportedOperationException();
    }

    /// 移除元素（不支持）。
    ///
    /// @param o 要移除的元素
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /// 添加所有元素（不支持）。
    ///
    /// @param c 要添加的集合
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /// 在指定位置添加所有元素（不支持）。
    ///
    /// @param index 索引位置
    /// @param c     要添加的集合
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /// 移除所有匹配的元素（不支持）。
    ///
    /// @param c 要移除的集合
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /// 保留指定集合中的元素（不支持）。
    ///
    /// @param c 要保留的集合
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /// 清空列表（不支持）。
    ///
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /// 获取指定位置的元素。
    ///
    /// @param index 索引
    /// @return 元素
    @Override
    public E get(int index) {
        return TypeCastUtil.unsafeCast(elements[index]);
    }

    /// 设置指定位置的元素（不支持）。
    ///
    /// @param index   索引
    /// @param element 新元素
    /// @return 旧元素（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /// 在指定位置添加元素（不支持）。
    ///
    /// @param index   索引
    /// @param element 要添加的元素
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /// 移除指定位置的元素（不支持）。
    ///
    /// @param index 索引
    /// @return 被移除的元素（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    /// 替换所有元素（不支持）。
    ///
    /// @param operator 替换操作
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        throw new UnsupportedOperationException();
    }

    /// 排序（不支持）。
    ///
    /// @param c 比较器
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public void sort(Comparator<? super E> c) {
        throw new UnsupportedOperationException();
    }

    /// 条件移除（不支持）。
    ///
    /// @param filter 过滤条件
    /// @return 布尔值（不适用）
    /// @throws UnsupportedOperationException 始终抛出此异常
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new UnsupportedOperationException();
    }

    /// 克隆列表。
    ///
    /// @return 克隆的不可变列表
    @Override
    public ImmutableList<E> clone() {
        try {
            return TypeCastUtil.unsafeCast(super.clone());
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /// 获取子列表。
    ///
    /// @param fromIndex 起始索引
    /// @param toIndex   结束索引
    /// @return 子列表
    @NonNull
    @Override
    public ImmutableList<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size());
        int newSize = toIndex - fromIndex;
        if (newSize == 0) {
            return empty();
        } else if (size() == newSize) {
            return this;
        }
        Object[] objects = new Object[newSize];
        System.arraycopy(elements, fromIndex, objects, 0, newSize);
        return new ImmutableList<>(objects);
    }

    /// 检查子列表范围的有效性。
    ///
    /// @param fromIndex 起始索引
    /// @param toIndex   结束索引
    /// @param size      列表大小
    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }

    /// 获取收集器。
    ///
    /// @param <T> 元素类型
    /// @return 收集器
    public static <T> Collector<T, ?, ImmutableList<T>> collector() {
        return Collector.of(
                Builder<T>::new, Builder::add,
                Builder::addAll, Builder::build);
    }

    /// 获取具有初始容量的收集器。
    ///
    /// @param initialCapacity 初始容量
    /// @param <T>             元素类型
    /// @return 收集器
    public static <T> @NonNull Collector<T, ?, ImmutableList<T>> collector(int initialCapacity) {
        return Collector.of(
                () -> new Builder<T>(initialCapacity), Builder::add,
                Builder::addAll, Builder::build);
    }

    /// 不可变列表构建器。
    ///
    /// @param <E> 元素类型
    public static class Builder<E> {
        public static final int DEFAULT_INITIAL_CAPACITY = 8;
        /// 最大数组长度。
        /// 该值为Integer.MAX_VALUE - 8，因为某些JVM在数组中保留了头部字词。
        /// 这与ArrayList和其他JDK集合的行为相匹配。
        /// 参见：java.util.ArrayList.MAX_ARRAY_SIZE
        public static final int MAX_LENGTH = Integer.MAX_VALUE - 8;
        /// MAX_LENGTH的一半，用于检测双倍容量时的潜在溢出。
        /// 如果当前容量>=HALF_MAX_LENGTH，则直接设置为MAX_LENGTH。
        public static final int HALF_MAX_LENGTH = MAX_LENGTH >> 1;

        Object[] elements;
        int size;

        /// 构建器构造函数。
        ///
        /// @param initialCapacity 初始容量
        public Builder(int initialCapacity) {
            this.elements = initialCapacity <= 0 ? EmptyArrays.OBJECT : new Object[initialCapacity];
        }

        /// 默认构建器构造函数。
        public Builder() {
            this.elements = EmptyArrays.OBJECT;
        }

        /// 添加元素。
        ///
        /// @param o 要添加的元素
        public void add(E o) {
            ensureCapacity(size + 1);
            elements[size++] = o;
        }

        /// 添加另一个构建器的所有元素。
        ///
        /// @param builder 另一个构建器
        /// @return 当前构建器实例
        public Builder<E> addAll(Builder<? extends E> builder) {
            ensureCapacity(size + builder.elements.length);
            System.arraycopy(builder.elements, 0, elements, size, builder.size);
            size += builder.size;
            return this;
        }

        /// 确保容量满足最小需求。
        ///
        /// @param minCapacity 最小容量需求
        void ensureCapacity(int minCapacity) {
            if (elements.length >= minCapacity) {
                return;
            }
            int newCapacity = Math.max(elements.length, DEFAULT_INITIAL_CAPACITY);
            while (newCapacity < minCapacity) {
                if (newCapacity >= HALF_MAX_LENGTH) {
                    newCapacity = MAX_LENGTH;
                    break;
                } else {
                    newCapacity <<= 1;
                }
            }
            elements = Arrays.copyOf(elements, newCapacity);
        }

        /// 添加集合的所有元素。
        ///
        /// @param c 要添加的集合
        public void addAll(Collection<? extends E> c) {
            if (c.isEmpty()) {
                return;
            }
            ensureCapacity(size + c.size());
            for (E e : c) {
                elements[size++] = e;
            }
        }

        /// 添加不可变数组的所有元素。
        ///
        /// @param c 要添加的不可变数组
        public void addAll(ImmutableArray<? extends E> c) {
            if (c.isEmpty()) {
                return;
            }
            ensureCapacity(size + c.size());
            for (E e : c) {
                elements[size++] = e;
            }
        }

        /// 添加数组的所有元素。
        ///
        /// @param c 要添加的数组
        public void addAll(E[] c) {
            if (c == null || c.length == 0) {
                return;
            }
            ensureCapacity(size + c.length);
            for (E e : c) {
                elements[size++] = e;
            }
        }

        /// 构建不可变列表。
        ///
        /// @return 构建的不可变列表
        public ImmutableList<E> build() {
            if (elements.length == size) {
                return new ImmutableList<>(elements);
            } else if (size == 0) {
                return empty();
            } else {
                Object[] element = new Object[size];
                System.arraycopy(elements, 0, element, 0, size);
                return new ImmutableList<>(element);
            }
        }

        /// 检查构建器是否为空。
        ///
        /// @return 如果为空返回true，否则返回false
        public boolean isEmpty() {
            return size == 0;
        }
    }

    @Override
    public <T> T[] toArray(@NonNull IntFunction<T[]> generator) {
        return super.toArray(generator);
    }
}