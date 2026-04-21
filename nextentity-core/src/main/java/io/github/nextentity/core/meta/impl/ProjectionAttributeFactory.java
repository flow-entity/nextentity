package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for creating ProjectionAttribute instances.
 * Extracted from DefaultProjectionJoinAttribute and DefaultProjectionSchemaAttribute to eliminate code duplication.
 */
public final class ProjectionAttributeFactory {

    private ProjectionAttributeFactory() {
    }

    public static ProjectionAttribute createAttribute(
            DefaultProjectionSchema declareBy,
            EntityAttribute source,
            ProjectionAttribute original,
            DefaultMetamodel metamodel,
            AtomicInteger ordinal) {
        return switch (original) {
            case ProjectionBasicAttribute basic -> new DefaultProjectionBasicAttribute(
                    declareBy,
                    basic.getEntityAttribute(),
                    basic,
                    ordinal.getAndIncrement());
            case ProjectionSchemaAttribute schema -> new DefaultProjectionSchemaAttribute(
                    declareBy,
                    schema.getEntityAttribute(),
                    schema,
                    metamodel,
                    ordinal.getAndIncrement());
            case ProjectionJoinAttribute join -> new DefaultProjectionJoinAttribute(
                    declareBy,
                    join.getSourceAttribute(),
                    join.getTargetAttribute(),
                    join.getTargetEntityType(),
                    metamodel,
                    ordinal.getAndIncrement(),
                    join.getFetchType(),
                    join);
        };
    }
}