package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.Lazy;

public abstract class AbstractSchemaAttribute extends SimpleAttribute implements SchemaAttribute {

    private final Lazy<Attributes> attributes = new Lazy<>(this::buildAttributes);

    protected abstract Attributes buildAttributes();

    public Attributes attributes() {
        return attributes.get();
    }

}
