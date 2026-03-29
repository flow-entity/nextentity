package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import jakarta.persistence.LockModeType;

/**
 * Immutable record representing the complete structure of a SQL query.
 * <p>
 * Contains all components of a SELECT query including selection, FROM clause,
 * WHERE clause, GROUP BY, ORDER BY, HAVING, pagination (offset/limit), and lock mode.
 * <p>
 * QueryStructure instances are immutable; all modifier methods return new instances.
 *
 * @param select the selection specification (entity or projection)
 * @param from the FROM clause specification (entity or subquery)
 * @param where the WHERE clause expression
 * @param groupBy the GROUP BY expressions
 * @param orderBy the ORDER BY expressions
 * @param having the HAVING clause expression
 * @param offset the result offset for pagination
 * @param limit the maximum number of results
 * @param lockType the JPA lock mode type
 * @author HuangChengwei
 * @since 1.0.0
 */
public record QueryStructure(

        Selected select,

        From from,

        ExpressionNode where,

        ImmutableList<ExpressionNode> groupBy,

        ImmutableList<SortExpression> orderBy,

        ExpressionNode having,

        Integer offset,

        Integer limit,

        LockModeType lockType

) implements ExpressionNode {

    /**
     * Creates a QueryStructure with the specified select and from clauses.
     *
     * @param select the selection specification
     * @param from the FROM clause specification
     * @return a new QueryStructure instance
     */
    public static QueryStructure of(Selected select, From from) {
        return new QueryStructure(
                select,
                from,
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    /**
     * Creates a QueryStructure for selecting from an entity type.
     *
     * @param type the entity class
     * @return a new QueryStructure instance
     */
    public static QueryStructure of(Class<?> type) {
        return new QueryStructure(
                new SelectEntity(ImmutableList.empty(), false),
                new FromEntity(type),
                EmptyNode.INSTANCE,
                ImmutableList.of(),
                ImmutableList.of(),
                EmptyNode.INSTANCE,
                null,
                null,
                LockModeType.NONE);
    }

    /**
     * Returns a new QueryStructure with updated select and from clauses.
     *
     * @param select the new selection specification
     * @param from the new FROM clause specification
     * @return a new QueryStructure instance
     */
    public QueryStructure selectFrom(Selected select, From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated select clause.
     *
     * @param select the new selection specification
     * @return a new QueryStructure instance
     */
    public QueryStructure select(Selected select) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated FROM clause.
     *
     * @param from the new FROM clause specification
     * @return a new QueryStructure instance
     */
    public QueryStructure from(From from) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated WHERE clause.
     *
     * @param where the new WHERE expression
     * @return a new QueryStructure instance
     */
    public QueryStructure where(ExpressionNode where) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with updated GROUP BY expressions.
     *
     * @param groupBy the new GROUP BY expressions
     * @return a new QueryStructure instance
     */
    public QueryStructure groupBy(ImmutableList<ExpressionNode> groupBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with updated ORDER BY expressions.
     *
     * @param orderBy the new ORDER BY expressions
     * @return a new QueryStructure instance
     */
    public QueryStructure orderBy(ImmutableList<SortExpression> orderBy) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated HAVING clause.
     *
     * @param having the new HAVING expression
     * @return a new QueryStructure instance
     */
    public QueryStructure having(ExpressionNode having) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated offset.
     *
     * @param offset the new offset value
     * @return a new QueryStructure instance
     */
    public QueryStructure offset(Integer offset) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated limit.
     *
     * @param limit the new limit value
     * @return a new QueryStructure instance
     */
    public QueryStructure limit(Integer limit) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with an updated lock mode.
     *
     * @param lockType the new lock mode
     * @return a new QueryStructure instance
     */
    public QueryStructure lockType(LockModeType lockType) {
        return new QueryStructure(select, from, where, groupBy, orderBy, having, offset, limit, lockType);
    }

    /**
     * Returns a new QueryStructure with offset and limit removed.
     * <p>
     * Used for count queries where pagination is not needed.
     *
     * @return a new QueryStructure instance without offset/limit
     */
    public QueryStructure removeOffsetLimit() {
        if (offset == null && limit == null) {
            return this;
        }
        return new QueryStructure(select, from, where, groupBy, orderBy, having, null, null, lockType);
    }
}