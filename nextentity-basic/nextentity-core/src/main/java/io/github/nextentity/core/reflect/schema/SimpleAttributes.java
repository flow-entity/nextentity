package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.util.*;

public class SimpleAttributes extends ImmutableList<Attribute> implements Attributes {

    private final Map<String, Attribute> index = new HashMap<>();
    private final ImmutableArray<Attribute> primitives;

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

    @Override
    public Attribute get(String name) {
        return index.get(name);
    }

    @Override
    public ImmutableArray<Attribute> getPrimitives() {
        return primitives;
    }
}
