package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;

public class DefaultAttribute implements Attribute {

    private final Accessor accessor;
    private final Schema declareBy;
    private final ImmutableList<String> path;

    public DefaultAttribute(Schema declareBy, Attribute attribute) {
        this(declareBy, attribute.accessor());
    }

    public DefaultAttribute(Schema declareBy, Accessor accessor) {
        this(declareBy, accessor, newPath(declareBy, accessor.name()));
    }

    public DefaultAttribute(Schema declareBy, Accessor accessor, ImmutableList<String> path) {
        this.accessor = accessor;
        this.declareBy = declareBy;
        this.path = path;
    }

    public static ImmutableList<String> newPath(Schema declareBy, String name) {
        if (declareBy instanceof Attribute p) {
            ImmutableArray<String> attr = p.path();
            String[] strings = new String[attr.size() + 1];
            for (int i = 0; i < attr.size(); i++) {
                strings[i] = attr.get(i);
            }
            strings[attr.size()] = name;
            return ImmutableList.of(strings);
        } else {
            return ImmutableList.of(name);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return path().toString();
    }

    @Override
    public Accessor accessor() {
        return accessor;
    }

    @Override
    public Schema declareBy() {
        return declareBy;
    }

    @Override
    public ImmutableArray<String> path() {
        return path;
    }
}
