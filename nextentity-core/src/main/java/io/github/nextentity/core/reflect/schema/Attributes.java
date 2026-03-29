package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

/**
 * Collection interface for schema attributes.
 * <p>
 * This interface extends {@link ImmutableArray} to provide named access
 * to attributes and separation of primitive vs. association attributes.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Attributes extends ImmutableArray<Attribute> {

    /**
     * Returns the attribute with the specified name.
     *
     * @param name the name of the attribute
     * @return the attribute with the specified name, or null if not found
     */
    Attribute get(String name);

    /**
     * Gets the primitive (non-association) attributes.
     * <p>
     * Primitive attributes are simple fields that map directly to
     * database columns, not associations to other entities.
     *
     * @return an immutable array of primitive attributes
     */
    ImmutableArray<Attribute> getPrimitives();

}
