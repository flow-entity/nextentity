package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;

/// Utility class for creating ProjectionAttribute instances.
/// Extracted from DefaultProjectionJoinAttribute and DefaultProjectionSchemaAttribute to eliminate code duplication.
public final class ProjectionAttributeFactory {

    private ProjectionAttributeFactory() {
    }

    public static ProjectionAttribute createAttribute(
            DefaultProjectionSchema declareBy,
            EntityAttribute source,
            ProjectionAttribute original,
            DefaultMetamodel metamodel) {
        return switch (original) {
            case ProjectionBasicAttribute basic -> new DefaultProjectionBasicAttribute(
                    declareBy,
                    basic.getEntityAttribute(),
                    basic);
            case ProjectionSchemaAttribute schema -> new DefaultProjectionSchemaAttribute(
                    declareBy,
                    schema.getEntityAttribute(),
                    schema,
                    metamodel);
            case ProjectionJoinAttribute join -> new DefaultProjectionJoinAttribute(
                    declareBy,
                    join.getSourceAttribute(),
                    join.getTargetAttribute(),
                    join.getTargetEntityType(),
                    metamodel,
                    join);
        };
    }
}