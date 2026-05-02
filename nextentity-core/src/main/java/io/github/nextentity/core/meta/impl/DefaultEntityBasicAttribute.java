package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;

public class DefaultEntityBasicAttribute extends DefaultMetamodelAttribute implements EntityBasicAttribute {

    private final String columnName;
    private final boolean updatable;
    private final ValueConverter<?, ?> valueConverter;

    public DefaultEntityBasicAttribute(MetamodelAttribute attribute,
                                       DefaultEntitySchema declareBy,
                                       MetamodelResolver resolver) {
        this(attribute, declareBy, resolver, resolver.getColumnName(attribute.accessor()));
    }

    public DefaultEntityBasicAttribute(MetamodelAttribute attribute,
                                       DefaultEntitySchema declareBy,
                                       MetamodelResolver resolver,
                                       String columnName) {
        super(declareBy, attribute.accessor(), declareBy.getPath(attribute.name()));
        this.columnName = columnName;
        this.updatable = resolver.isUpdatable(attribute.accessor());
        this.valueConverter = resolver.databaseType(attribute.accessor());
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
}
