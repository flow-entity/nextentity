package io.github.nextentity.core.meta;

public interface ProjectionSchema extends MetamodelSchema<ProjectionAttribute> {
    EntitySchema getEntitySchema();
}
