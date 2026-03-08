package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Query result collector interface, providing multiple result retrieval methods.
 *
 * @param <T> Entity type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Collector<T> {

    /**
     * Gets the total record count.
     *
     * @return Total record count
     */
    long count();

    /**
     * Gets the list with specified offset and maximum result count, and specified lock mode.
     *
     * @param offset Offset
     * @param maxResult Maximum result count
     * @param lockModeType Lock mode
     * @return Result list
     */
    List<T> getList(int offset, int maxResult, LockModeType lockModeType);

    /**
     * Gets the list with specified offset and maximum result count.
     *
     * @param offset Offset
     * @param maxResult Maximum result count
     * @return Result list
     */
    default List<T> getList(int offset, int maxResult) {
        return getList(offset, maxResult, null);
    }

    /**
     * Gets all results from the specified offset.
     *
     * @param offset Offset
     * @return Result list
     */
    default List<T> offset(int offset) {
        return getList(offset, -1, null);
    }

    /**
     * Gets the specified number of results.
     *
     * @param limit Maximum result count
     * @return Result list
     */
    default List<T> limit(int limit) {
        return getList(0, limit, null);
    }

    /**
     * Checks if records exist from the specified offset.
     *
     * @param offset Offset
     * @return Whether records exist
     */
    boolean exist(int offset);

    /**
     * Gets the first result as Optional.
     *
     * @return First result as Optional
     */
    default Optional<T> first() {
        return Optional.ofNullable(getFirst());
    }

    /**
     * Gets the first result from the specified offset as Optional.
     *
     * @param offset Offset
     * @return First result as Optional
     */
    default Optional<T> first(int offset) {
        return Optional.ofNullable(getFirst(offset));
    }

    /**
     * Gets the first result.
     *
     * @return First result, null if not exists
     */
    default T getFirst() {
        return getFirst(-1);
    }

    /**
     * Gets the first result from the specified offset.
     *
     * @param offset Offset
     * @return First result, null if not exists
     */
    default T getFirst(int offset) {
        List<T> list = getList(offset, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Gets a single result, throws an exception if not exists.
     *
     * @return Single result
     * @throws NullPointerException If result does not exist
     */
    default T requireSingle() {
        return Objects.requireNonNull(getSingle(-1));
    }

    /**
     * Gets a single result as Optional.
     *
     * @return Single result as Optional
     */
    default Optional<T> single() {
        return Optional.ofNullable(getSingle());
    }

    /**
     * Gets a single result from the specified offset as Optional.
     *
     * @param offset Offset
     * @return Single result as Optional
     */
    default Optional<T> single(int offset) {
        return Optional.ofNullable(getSingle(offset));
    }

    /**
     * Gets a single result.
     *
     * @return Single result, null if not exists
     */
    default T getSingle() {
        return getSingle(-1);
    }

    /**
     * Gets a single result from the specified offset.
     *
     * @param offset Offset
     * @return Single result, null if not exists
     * @throws IllegalStateException If multiple results found
     */
    default T getSingle(int offset) {
        List<T> list = getList(offset, 2);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Gets all results.
     *
     * @return List of all results
     */
    default List<T> getList() {
        return getList(-1, -1);
    }

    /**
     * Checks if records exist.
     *
     * @return Whether records exist
     */
    default boolean exist() {
        return exist(-1);
    }

    /**
     * Gets the first result as Optional, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return First result as Optional
     */
    default Optional<T> first(LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(lockModeType));
    }

    /**
     * Gets the first result from the specified offset as Optional, and specifies lock mode.
     *
     * @param offset Offset
     * @param lockModeType Lock mode
     * @return First result as Optional
     */
    default Optional<T> first(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getFirst(offset, lockModeType));
    }

    /**
     * Gets the first result, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return First result, null if not exists
     */
    default T getFirst(LockModeType lockModeType) {
        return getFirst(-1, lockModeType);
    }

    /**
     * Gets the first result from the specified offset, and specifies lock mode.
     *
     * @param offset Offset
     * @param lockModeType Lock mode
     * @return First result, null if not exists
     */
    default T getFirst(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 1, lockModeType);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Gets a single result, throws an exception if not exists, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return Single result
     * @throws NullPointerException If result does not exist
     */
    default T requireSingle(LockModeType lockModeType) {
        return Objects.requireNonNull(getSingle(-1, lockModeType));
    }

    /**
     * Gets a single result as Optional, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return Single result as Optional
     */
    default Optional<T> single(LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(lockModeType));
    }

    /**
     * Gets a single result from the specified offset as Optional, and specifies lock mode.
     *
     * @param offset Offset
     * @param lockModeType Lock mode
     * @return Single result as Optional
     */
    default Optional<T> single(int offset, LockModeType lockModeType) {
        return Optional.ofNullable(getSingle(offset, lockModeType));
    }

    /**
     * Gets a single result, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return Single result, null if not exists
     */
    default T getSingle(LockModeType lockModeType) {
        return getSingle(-1, lockModeType);
    }

    /**
     * Gets a single result from the specified offset, and specifies lock mode.
     *
     * @param offset Offset
     * @param lockModeType Lock mode
     * @return Single result, null if not exists
     * @throws IllegalStateException If multiple results found
     */
    default T getSingle(int offset, LockModeType lockModeType) {
        List<T> list = getList(offset, 2, lockModeType);
        if (list.size() > 1) {
            throw new IllegalStateException("found more than one");
        }
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Gets all results from the specified offset, and specifies lock mode.
     *
     * @param offset Offset
     * @param lockModeType Lock mode
     * @return Result list
     */
    default List<T> offset(int offset, LockModeType lockModeType) {
        return getList(offset, -1, lockModeType);
    }

    /**
     * Gets the specified number of results, and specifies lock mode.
     *
     * @param limit Maximum result count
     * @param lockModeType Lock mode
     * @return Result list
     */
    default List<T> limit(int limit, LockModeType lockModeType) {
        return getList(0, limit, lockModeType);
    }

    /**
     * Gets all results, and specifies lock mode.
     *
     * @param lockModeType Lock mode
     * @return List of all results
     */
    default List<T> getList(LockModeType lockModeType) {
        return getList(-1, -1, lockModeType);
    }

    /**
     * Slices results using the specified slicer.
     *
     * @param sliceable Slicer
     * @param <R> Result type
     * @return Slice result
     */
    <R> R slice(Sliceable<T, R> sliceable);

    /**
     * Slices results.
     *
     * @param offset Offset
     * @param limit Maximum result count
     * @return Slice result
     */
    Slice<T> slice(int offset, int limit);

    /**
     * Converts the query to a subquery builder.
     *
     * @param <X> Subquery type
     * @return Subquery builder
     */
    <X> SubQueryBuilder<X, T> asSubQuery();

    /**
     * Maps result transformation.
     *
     * @param mapper Mapping function
     * @param <R> Mapped result type
     * @return Mapped collector
     */
    <R> Collector<R> map(Function<? super T, ? extends R> mapper);

    /**
     * Gets the pagination result.
     *
     * @param pageable Pagination parameters
     * @return Pagination result
     */
    Page<T> getPage(Pageable pageable);

    /**
     * Gets the pagination result using the specified page collector.
     *
     * @param collector Page collector
     * @param <R> Result type
     * @return Pagination result
     */
    default <R> R getPage(PageCollector<T, R> collector) {
        return slice(collector);
    }
}
