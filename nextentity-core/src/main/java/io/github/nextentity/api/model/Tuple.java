package io.github.nextentity.api.model;

import java.util.List;

/**
 * Tuple interface, representing an object containing multiple elements.
 * <p>
 * Used to represent a row of data in query results, supporting element retrieval by index.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Tuple extends Iterable<Object> {

    /**
     * Gets the element by index.
     *
     * @param index Index
     * @param <T> Element type
     * @return Element
     */
    <T> T get(int index);

    /**
     * Gets the tuple size.
     *
     * @return Tuple size
     */
    int size();

    /**
     * Converts to list.
     *
     * @return Element list
     */
    List<Object> toList();

    /**
     * Converts to array.
     *
     * @return Element array
     */
    Object[] toArray();

}
