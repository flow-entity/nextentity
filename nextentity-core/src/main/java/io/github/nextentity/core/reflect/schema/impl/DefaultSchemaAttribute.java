package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.reflect.schema.Accessor;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.core.util.ImmutableArray;

public class DefaultSchemaAttribute extends DefaultSchema implements SchemaAttribute {

    private final Accessor accessor;
    private final Schema declareBy;
    private final ImmutableArray<String> path;

    public DefaultSchemaAttribute(Accessor accessor, Schema declareBy) {
        super(accessor.type());
        this.accessor = accessor;
        this.declareBy = declareBy;
        this.path = DefaultAttribute.newPath(declareBy, accessor.name());
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
