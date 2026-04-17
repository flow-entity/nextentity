package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.ReflectType;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.Lazy;

import java.util.Map;

public abstract class AbstractSchema<S extends AttributeSet<T>, T extends Attribute> implements Schema {

    protected final Class<?> type;

    protected final Lazy<S> attributesSupplier = new Lazy<>(this::createAttributes);

    public AbstractSchema(Class<?> type) {
        this.type = type;
    }

    abstract protected S createAttributes();

    @Override
    public ImmutableArray<? extends T> getAttributes() {
        return attributesSupplier.get().attributes();
    }

    @Override
    public ImmutableArray<? extends T> getPrimitives() {
        return attributesSupplier.get().primitives();
    }

    @Override
    public T getAttribute(String name) {
        Map<String, T> index = attributesSupplier.get().index();
        return index.get(name);
    }

    @Override
    public Attribute getAttribute(Iterable<String> fieldNames) {
        ReflectType schema = this;
        for (String fieldName : fieldNames) {
            schema = ((Schema) schema).getAttribute(fieldName);
        }
        return (Attribute) schema;
    }

    @Override
    public Class<?> type() {
        return type;
    }

    protected Lazy<? extends S> getAttributesSupplier() {
        return attributesSupplier;
    }

    @Override
    public String toString() {
        if (this instanceof Attribute attribute) {
            return attribute.path().toString();
        }
        return getClass().toString();
    }
}
