package io.github.nextentity.core.expression;

/**
 * Sealed interface representing the SELECT clause specification.
 * <p>
 * Defines what columns/expressions are selected in a query and whether
 * to apply DISTINCT.
 * <p>
 * Permitted subtypes:
 * <ul>
 *   <li>{@link SelectEntity} - select all columns from an entity</li>
 *   <li>{@link SelectExpression} - select a single expression</li>
 *   <li>{@link SelectExpressions} - select multiple expressions</li>
 *   <li>{@link SelectProjection} - select into a DTO/projection class</li>
 * </ul>
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public sealed interface Selected permits SelectEntity, SelectExpression, SelectExpressions, SelectProjection {

    /**
     * Indicates whether to apply DISTINCT to the selection.
     *
     * @return true if DISTINCT should be applied
     */
    boolean distinct();
}
