package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.BasicAttribute;
import io.github.nextentity.core.meta.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class PostgresqlUpdateSqlBuilder extends AbstractUpdateSqlBuilder {

    @Override
    protected @NotNull String rightTicks() {
        return "\"";
    }

    @Override
    protected @NotNull String leftTicks() {
        return "\"";
    }

    protected @NotNull String typedPlaceholder(BasicAttribute attribute) {
        if (attribute.type() == Date.class || attribute.type().isAssignableFrom(java.sql.Timestamp.class)) {
            return "?::timestamp";
        }
        return super.typedPlaceholder(attribute);
    }

    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NotNull EntityType entityType) {
        return buildGroupedInsertStatement(entities, entityType);
    }
}
