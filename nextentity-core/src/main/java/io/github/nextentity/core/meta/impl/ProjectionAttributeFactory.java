package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import jakarta.persistence.FetchType;

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
                    basic.source(),
                    basic,
                    ordinal.getAndIncrement());
            case ProjectionSchemaAttribute schema -> new DefaultProjectionSchemaAttribute(
                    declareBy,
                    schema.source(),
                    schema,
                    metamodel,
                    ordinal.getAndIncrement());
            case ProjectionJoinAttribute join -> new DefaultProjectionJoinAttribute(
                    declareBy,
                    join.sourceAttribute(),
                    join.targetAttribute(),
                    join.target(),
                    metamodel,
                    ordinal.getAndIncrement(),
                    join.fetchType(),
                    join);
        };
    }
}