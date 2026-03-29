package io.github.nextentity.core.expression;

///
/// Record representing a FROM clause that selects from a subquery.
/// <p>
/// Used when the data source is a nested SELECT statement rather than
/// a direct entity table.
///
/// @param structure the nested query structure
/// @author HuangChengwei
/// @since 1.0.0
///
public record FromSubQuery(QueryStructure structure) implements From {
}
