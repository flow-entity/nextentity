package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.*;

///
/// Simple implementation of {@link Attributes}.
/// <p>
/// This class provides a concrete implementation for attribute collections
/// with name-based lookup and separation of primitive attributes.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleAttributes extends ImmutableList<Attribute> implements Attributes {

    private final Map<String, Attribute> index = new HashMap<>();
    private final ImmutableArray<Attribute> primitives;

    ///
    /// Creates a new SimpleAttributes instance from a collection.
    /// <p>
    /// Builds an index for name-based lookup and separates primitive attributes.
    ///
    /// @param attributes the attributes to include
    ///
    public SimpleAttributes(Collection<? extends Attribute> attributes) {
        super(attributes);
        List<Attribute> primitives = new ArrayList<>();
        for (Attribute attribute : attributes) {
            index.put(attribute.name(), attribute);
            if (attribute.isPrimitive()) {
                primitives.add(attribute);
            }
        }
        this.primitives = ImmutableList.ofCollection(primitives);
    }

    ///
    /// Gets an attribute by name.
    ///
    /// @param name the attribute name
    /// @return the attribute, or null if not found
    ///
    @Override
    public Attribute get(String name) {
        return index.get(name);
    }

    ///
    /// Gets the primitive (non-association) attributes.
    ///
    /// @return an immutable array of primitive attributes
    ///
    @Override
    public ImmutableArray<Attribute> getPrimitives() {
        return primitives;
    }
}
