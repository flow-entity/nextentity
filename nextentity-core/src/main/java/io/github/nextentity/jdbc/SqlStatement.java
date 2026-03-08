package io.github.nextentity.jdbc;

import io.github.nextentity.core.SqlLogger;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface SqlStatement {
    String sql();

    Iterable<?> parameters();

    default void debug() {
        SqlLogger.debug(sql());
        SqlLogger.debug("sql parameters:{}", parameters());
    }

}
