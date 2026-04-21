package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.EntityBasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.meta.MetamodelResolver;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.impl.DefaultAttribute;

public class DefaultEntityBasicAttribute extends DefaultAttribute implements EntityBasicAttribute {

    private final String columnName;
    private final boolean updatable;
    private final ValueConverter<?, ?> valueConverter;
    private final PathNode pathNode;

    public DefaultEntityBasicAttribute(Attribute attribute,
                                       DefaultEntitySchema declareBy,
                                       MetamodelResolver resolver,
                                       int ordinal) {
        super(declareBy, attribute, ordinal);
        this.columnName = resolver.getColumnName(attribute);
        this.updatable = resolver.isUpdatable(attribute);
        this.valueConverter = resolver.databaseType(attribute);
        this.pathNode = new PathNode(super.path().toArray(String[]::new), this);
    }

    @Override
    public EntitySchema declareBy() {
        return (EntitySchema) super.declareBy();
    }

    @Override
    public String columnName() {
        return columnName;
    }

    @Override
    public boolean isVersion() {
        return declareBy().version() == this;
    }

    @Override
    public boolean isId() {
        return declareBy().id() == this;
    }

    @Override
    public ValueConverter<?, ?> valueConvertor() {
        return valueConverter;
    }

    @Override
    public boolean isUpdatable() {
        return updatable && !isId();
    }

    @Override
    public PathNode path() {
        return pathNode;
    }
}