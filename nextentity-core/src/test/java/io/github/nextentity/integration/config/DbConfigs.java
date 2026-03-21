package io.github.nextentity.integration.config;

import io.github.nextentity.core.util.ImmutableList;

import java.util.List;

/**
 * Unified database configurations for integration tests.
 *
 * @author HuangChengwei
 */
public interface DbConfigs {

    DbConfig MYSQL = new Mysql().getConfig();
    DbConfig POSTGRESQL = new Postgresql().getConfig();
    List<DbConfig> CONFIGS = ImmutableList.of(MYSQL, POSTGRESQL);

}
