package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.*;

public class AttributeSet<T extends MetamodelAttribute> {
    private final Map<String, T> index;
    private final ImmutableArray<T> attributes;
    private final ImmutableArray<T> primitives;

    public AttributeSet(Collection<T> attributes) {
        index = new HashMap<>();
        this.attributes = ImmutableList.ofCollection(attributes);
        List<T> primitives = new ArrayList<>();
        for (T attribute : attributes) {
            index.put(attribute.name(), attribute);
            if (attribute.isPrimitive()) {
                primitives.add(attribute);
            }
        }
        this.primitives = ImmutableList.ofCollection(primitives);
    }

    public Map<String, T> index() {
        return index;
    }

    public ImmutableArray<T> attributes() {
        return attributes;
    }

    public ImmutableArray<T> primitives() {
        return primitives;
    }
}
