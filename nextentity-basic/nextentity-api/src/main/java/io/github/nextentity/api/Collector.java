package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * 查询结果收集器接口，提供多种结果获取方法。
 *
 * @param <T> 实体类型
 * @author HuangChengwei
 * @since 2026/1/7
 */
public interface Collector<T> {

    /**
     * 获取记录总数。
     *
     * @return 记录总数
     */
    long count();

    /**
     * 获取指定偏移量和最大结果数的列表，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param maxResult 最大结果数
     * @param lockModeType 锁定模式
     * @return 结果列表
     */
    List<T> getList(int offset, int maxResult, LockModeType lockModeType);

    /**
     * 获取指定偏移量和最大结果数的列表。
     *
     * @param offset 偏移量
     * @param maxResult 最大结果数
     * @return 结果列表
     */
    default List<T> getList(int offset, int maxResult) {
        return getList(offset, maxResult, null);
    }

    /**
     * 从指定偏移量开始获取所有结果。
     *
     * @param offset 偏移量
     * @return 结果列表
     */
    default List<T> offset(int offset) {
        return getList(offset, -1, null);
    }

    /**
     * 获取指定数量的结果。
     *
     * @param limit 最大结果数
     * @return 结果列表
     */
    default List<T> limit(int limit) {
        return getList(0, limit, null);
    }

    /**
     * 检查从指定偏移量开始是否存在记录。
     *
     * @param offset 偏移量
     * @return 是否存在记录
     */
    boolean exist(int offset);

    /**
     * 获取第一个结果的Optional。
     *
     * @return 第一个结果的Optional
     */
    default Optional<T> first() {
        return Optional.ofNullable(getFirst());
    }

    /**
     * 从指定偏移量开始获取第一个结果的Optional。
     *
     * @param offset 偏移量
     * @return 第一个结果的Optional
     */
    default Optional<T> first(int offset) {
        return Optional.ofNullable(getFirst(offset));
    }

    /**
     * 获取第一个结果。
     *
     * @return 第一个结果，不存在则返回null
     */
    default T getFirst() {
        return getFirst(-1);
    }

    /**
     * 从指定偏移量开始获取第一个结果。
     *
     * @param offset 偏移量
     * @return 第一个结果，不存在则返回null
     */
    default T getFirst(int offset) {
        List<T> list = getList(offset, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取单个结果，如果不存在则抛出异常。
     *
     * @return 单个结果
     * @throws NullPointerException 如果结果不存在
     */
    default T requireSingle() {
        return Objects.requireNonNull(getSingle(-1));
    }

    /**
     * 获取单个结果的Optional。
     *
     * @return 单个结果的Optional
     */
    default Optional<T> single() {
        return Optional.ofNullable(getSingle());
    }

    /**
     * 从指定偏移量开始获取单个结果的Optional。
     *
     * @param offset 偏移量
     * @return 单个结果的Optional
     */
    default Optional<T> single(int offset) {
        return Optional.ofNullable(getSingle(offset));
    }

    /**
     * 获取单个结果。
     *
     * @return 单个结果，不存在则返回null
     */
    default T getSingle() {
        return getSingle(-1);
    }

    /**
     * 从指定偏移量开始获取单个结果。
     *
     * @param offset 偏移量
     * @return 单个结果，不存在则返回null
     * @throws IllegalStateException 如果找到多个结果
     */
    default T getSingle(int offset) {
        List<T> list = getList(offset, 2);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取所有结果。
     *
     * @return 所有结果的列表
     */
    default List<T> getList() {
        return getList(-1, -1);
    }

    /**
     * 检查是否存在记录。
     *
     * @return 是否存在记录
     */
    default boolean exist() {
        return exist(-1);
    }

    /**
     * 获取第一个结果的Optional，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 第一个结果的Optional
     */
    default Optional<T> first(LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(lockModeType));
    }

    /**
     * 从指定偏移量开始获取第一个结果的Optional，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param lockModeType 锁定模式
     * @return 第一个结果的Optional
     */
    default Optional<T> first(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(offset, lockModeType));
    }

    /**
     * 获取第一个结果，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 第一个结果，不存在则返回null
     */
    default T getFirst(LockModeType lockModeType) {
        return getFirst(-1, lockModeType);
    }

    /**
     * 从指定偏移量开始获取第一个结果，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param lockModeType 锁定模式
     * @return 第一个结果，不存在则返回null
     */
    default T getFirst(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 1, lockModeType);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 获取单个结果，如果不存在则抛出异常，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 单个结果
     * @throws NullPointerException 如果结果不存在
     */
    default T requireSingle(LockModeType lockModeType) {
        return Objects.requireNonNull(getSingle(-1, lockModeType));
    }

    /**
     * 获取单个结果的Optional，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 单个结果的Optional
     */
    default Optional<T> single(LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(lockModeType));
    }

    /**
     * 从指定偏移量开始获取单个结果的Optional，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param lockModeType 锁定模式
     * @return 单个结果的Optional
     */
    default Optional<T> single(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(offset, lockModeType));
    }

    /**
     * 获取单个结果，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 单个结果，不存在则返回null
     */
    default T getSingle(LockModeType lockModeType) {
        return getSingle(-1, lockModeType);
    }

    /**
     * 从指定偏移量开始获取单个结果，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param lockModeType 锁定模式
     * @return 单个结果，不存在则返回null
     * @throws IllegalStateException 如果找到多个结果
     */
    default T getSingle(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 2, lockModeType);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 从指定偏移量开始获取所有结果，并指定锁定模式。
     *
     * @param offset 偏移量
     * @param lockModeType 锁定模式
     * @return 结果列表
     */
    default List<T> offset(int offset, LockModeType lockModeType) {
        return getList(offset, -1, lockModeType);
    }

    /**
     * 获取指定数量的结果，并指定锁定模式。
     *
     * @param limit 最大结果数
     * @param lockModeType 锁定模式
     * @return 结果列表
     */
    default List<T> limit(int limit, LockModeType lockModeType) {
        return getList(0, limit, lockModeType);
    }

    /**
     * 获取所有结果，并指定锁定模式。
     *
     * @param lockModeType 锁定模式
     * @return 所有结果的列表
     */
    default List<T> getList(LockModeType lockModeType) {
        return getList(-1, -1, lockModeType);
    }

    /**
     * 使用指定的切片器对结果进行切片。
     *
     * @param sliceable 切片器
     * @param <R> 结果类型
     * @return 切片结果
     */
    <R> R slice(Sliceable<T, R> sliceable);

    /**
     * 对结果进行切片。
     *
     * @param offset 偏移量
     * @param limit 最大结果数
     * @return 切片结果
     */
    Slice<T> slice(int offset, int limit);

    /**
     * 将查询转换为子查询构建器。
     *
     * @param <X> 子查询类型
     * @return 子查询构建器
     */
    <X> SubQueryBuilder<X, T> asSubQuery();

    /**
     * 对结果进行映射转换。
     *
     * @param mapper 映射函数
     * @param <R> 映射结果类型
     * @return 映射后的收集器
     */
    <R> Collector<R> map(Function<? super T, ? extends R> mapper);

    /**
     * 获取分页结果。
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<T> getPage(Pageable pageable);

    /**
     * 使用指定的页面收集器获取分页结果。
     *
     * @param collector 页面收集器
     * @param <R> 结果类型
     * @return 分页结果
     */
    default <R> R getPage(PageCollector<T, R> collector) {
        return slice(collector);
    }
}
