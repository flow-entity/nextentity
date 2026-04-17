package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;

public sealed interface ProjectionAttribute
        extends Attribute permits
        ProjectionBasicAttribute,
        ProjectionJoinAttribute,
        ProjectionSchemaAttribute {
    EntityAttribute source();
}
