package io.github.nextentity.core.meta;

///
/// Entity type interface for subquery-based entities.
///
/// This interface extends {@link EntityType} to provide access to the
/// SQL subquery that defines the entity's data source.
///
/// Subquery entities are defined using the @SubSelect annotation.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public interface SubQueryEntityType extends EntityType {

    ///
    /// Gets the SQL subquery that provides the entity data.
    ///
    /// @return the subquery SQL string
    ///
    String subSelectSql();

}
