package io.github.nextentity.core.expression;

///
/// Record representing a FROM clause that selects from an entity table.
/// <p>
/// This is the most common FROM clause type, specifying the entity class
/// whose table will be queried.
///
/// @param type the entity class
/// @author HuangChengwei
/// @since 1.0.0
///
public record FromEntity(Class<?> type) implements From {
}
