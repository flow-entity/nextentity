package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SqlServerUpdateSqlBuilder extends AbstractUpdateSqlBuilder {

    @Override
    protected @NotNull String leftTicks() {
        return "[";
    }

    @Override
    protected @NotNull String rightTicks() {
        return "]";
    }

    @Override
    public List<InsertSqlStatement> buildInsertStatement(Iterable<?> entities, @NotNull EntityType entityType) {
        return buildGroupedInsertStatement(entities, entityType);
    }

}
