package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

/**
 * Record representing a SELECT clause that selects entity columns.
 * <p>
 * Used when selecting complete entities or specific fields from an entity.
 * The fetch list specifies which associated entities to eagerly fetch.
 *
 * @param fetch the paths of associations to fetch
 * @param distinct whether to apply DISTINCT
 * @author HuangChengwei
 * @since 1.0.0
 */
public record SelectEntity(ImmutableList<PathNode> fetch, boolean distinct) implements Selected {
}
