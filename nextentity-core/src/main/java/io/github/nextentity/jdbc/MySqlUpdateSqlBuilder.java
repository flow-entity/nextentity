package io.github.nextentity.jdbc;

import org.jspecify.annotations.NonNull;

public class MySqlUpdateSqlBuilder extends AbstractUpdateSqlBuilder {

    @Override
    protected @NonNull String rightTicks() {
        return "`";
    }

    @Override
    protected @NonNull String leftTicks() {
        return "`";
    }
}
