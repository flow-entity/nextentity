package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.SchemaAttribute;

public non-sealed interface ProjectionSchemaAttribute extends ProjectionAttribute, SchemaAttribute, Fetchable {
    EntitySchemaAttribute source();
}
