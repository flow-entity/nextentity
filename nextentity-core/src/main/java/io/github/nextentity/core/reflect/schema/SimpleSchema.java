package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableArray;

public class SimpleSchema implements Schema {

    private Class<?> type;
    private Attributes attributes;

    public Class<?> type() {
        return this.type;
    }

    public Attributes attributes() {
        return this.attributes;
    }

    public SimpleSchema type(Class<?> type) {
        this.type = type;
        return this;
    }

    public SimpleSchema attributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public ImmutableArray<? extends Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public ImmutableArray<? extends Attribute> getPrimitives() {
        return attributes.getPrimitives();
    }

    @Override
    public Attribute getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Attribute getAttribute(Iterable<String> fieldNames) {
        ReflectType schema = this;
        for (String fieldName : fieldNames) {
            schema = ((Schema) schema).getAttribute(fieldName);
        }
        return (Attribute) schema;
    }
}
