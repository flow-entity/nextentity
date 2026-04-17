package io.github.nextentity.core.meta;

import io.github.nextentity.core.meta.impl.DefaultEntitySchema;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;

public interface MetamodelResolver {
    String getTableName(Class<?> type);

    boolean isTransient(Attribute attribute);

    boolean isBasicField(Attribute attribute);

    boolean isVersionField(Attribute attribute);

    String getColumnName(Attribute attribute);

    boolean isMarkedId(Attribute attribute);

    ValueConverter<?,?> databaseType(Attribute attribute);

    boolean isUpdatable(Attribute attribute);

    String getJoinColumnName(Attribute attribute);

    String getReferencedColumnName(Attribute attribute);

    boolean isAnyToOne(SchemaAttribute attribute);

    EntityBasicAttribute getJoinSourceAttribute(DefaultEntitySchema sourceSchema, Attribute attribute);

    EntityBasicAttribute getJoinTargetAttribute(DefaultEntitySchema targetSchema, Attribute attribute);

    Iterable<String> getMappedEntityPath(Attribute attribute);

    boolean matchProjectionSchemaAttribute(EntitySchemaAttribute entitySchemaAttribute, SchemaAttribute schemaAttribute);

    boolean matchProjectionBasicAttribute(EntityBasicAttribute entityBasicAttribute, Attribute attribute);

    String getEntityName(Class<?> type);
}
