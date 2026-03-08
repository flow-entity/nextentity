package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class SqlServerUpdateSqlBuilder extends AbstractUpdateSqlBuilder {

    @Override
    protected @NonNull String leftTicks() {
        return "[";
    }

    @Override
    protected @NonNull String rightTicks() {
        return "]";
    }

    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NonNull EntityType entityType) {
        return buildGroupedInsertStatement(entities, entityType);
    }

}
