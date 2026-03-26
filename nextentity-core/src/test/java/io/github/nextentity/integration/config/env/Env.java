package io.github.nextentity.integration.config.env;

import java.util.List;

public class Env {

    private static final List<DatabaseEnvironmentVariables> DBS = List.of(
            new MysqlEnvironmentVariables(),
            new PostgresqlEnvironmentVariables()
    );

    public static List<DatabaseEnvironmentVariables> dbs() {
        return DBS;
    }

}
