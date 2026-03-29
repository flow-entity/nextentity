package io.github.nextentity.core.meta;

import java.util.function.BiFunction;

///
/// Entity type for subquery-based entities defined via @SubSelect annotation.
///
/// This class extends {@link SimpleEntity} to support entities whose data
/// comes from a SQL subquery rather than a direct table mapping.
///
/// Subquery entities allow defining virtual entities backed by custom SQL.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SubQueryEntity extends SimpleEntity implements SubQueryEntityType {

    private final String subSelectSql;

    ///
    /// Creates a new SubQueryEntity instance.
    ///
    /// @param type the entity class
    /// @param tableName the table name (used as alias in the subquery)
    /// @param projectionTypeGenerator function to generate projection types
    /// @param subSelectSql the SQL subquery that provides the entity data
    ///
    public SubQueryEntity(Class<?> type,
                          String tableName,
                          BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator,
                          String subSelectSql) {
        super(type, tableName, projectionTypeGenerator);
        this.subSelectSql = subSelectSql;
    }

    ///
    /// Gets the SQL subquery that provides the entity data.
    ///
    /// @return the subquery SQL
    ///
    @Override
    public String subSelectSql() {
        return subSelectSql;
    }
}
