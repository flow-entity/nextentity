package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

public interface Attributes extends ImmutableArray<Attribute> {

    /**
     * Returns the attribute with the specified name.
     *
     * @param name the name of the attribute
     * @return the attribute with the specified name, or null if not found
     */
    Attribute get(String name);

    ImmutableArray<Attribute> getPrimitives();

}
