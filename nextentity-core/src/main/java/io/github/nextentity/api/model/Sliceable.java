package io.github.nextentity.api.model;

import java.util.List;

/**
 * Slice parameter interface, defining the basic parameters for slicing.
 *
 * @param <T> Data type
 * @param <U> Result type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Sliceable<T, U> {

    /**
     * Gets the offset.
     *
     * @return Offset
     */
    int offset();

    /**
     * Gets the limit number.
     *
     * @return Limit number
     */
    int limit();

    /**
     * Collects slice data and builds the result.
     *
     * @param list Data list
     * @param total Total record count
     * @return Result object
     */
    U collect(List<T> list, long total);

}
