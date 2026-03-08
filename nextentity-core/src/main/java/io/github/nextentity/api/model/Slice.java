package io.github.nextentity.api.model;

import java.util.List;

/**
 * Slice result interface, containing slice data and related information.
 *
 * @param <T> Data type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Slice<T> {

    /**
     * Gets the slice data list.
     *
     * @return Data list
     */
    List<T> data();

    /**
     * Gets the total record count.
     *
     * @return Total record count
     */
    long total();

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

}
