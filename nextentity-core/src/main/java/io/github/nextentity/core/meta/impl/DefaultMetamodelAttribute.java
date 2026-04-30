package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.expression.PathNode;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.reflect.schema.Accessor;

public class DefaultMetamodelAttribute implements MetamodelAttribute {

    private final Accessor accessor;
    private final MetamodelSchema<?> declareBy;
    private final PathNode path;

    public DefaultMetamodelAttribute(MetamodelSchema<?> declareBy, Accessor accessor) {
        this(declareBy, accessor, newPath(declareBy, accessor.name()));
    }

    public DefaultMetamodelAttribute(MetamodelSchema<?> declareBy, Accessor accessor, PathNode path) {
        this.accessor = accessor;
        this.declareBy = declareBy;
        this.path = path;
    }

    public static PathNode newPath(MetamodelSchema<?> declareBy, String name) {
        return declareBy instanceof MetamodelAttribute p
                ? p.path().get(name)
                : new PathNode(name);
    }

    @Override
    public String toString() {
        return path().toString();
    }

    @Override
    public Accessor accessor() {
        return accessor;
    }

    @Override
    public Class<?> type() {
        return accessor().type();
    }

    @Override
    public MetamodelSchema<?> declareBy() {
        return declareBy;
    }

    @Override
    public PathNode path() {
        return path;
    }
}
