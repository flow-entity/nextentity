package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.Date;
import java.util.List;

public class PostgreSqlUpdateSqlBuilder extends AbstractUpdateSqlBuilder {

    @Override
    protected @NonNull String rightTicks() {
        return "\"";
    }

    @Override
    protected @NonNull String leftTicks() {
        return "\"";
    }

    protected @NonNull String typedPlaceholder(EntityAttribute attribute) {
        if (attribute.type() == Date.class || attribute.type().isAssignableFrom(java.sql.Timestamp.class)) {
            return "?::timestamp";
        }
        return super.typedPlaceholder(attribute);
    }

    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        return buildGroupedInsertStatement(entities, entityType);
    }
}
