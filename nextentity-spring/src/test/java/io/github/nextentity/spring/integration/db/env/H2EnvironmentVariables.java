package io.github.nextentity.spring.integration.db.env;

import java.util.List;

public class H2EnvironmentVariables implements DatabaseEnvironmentVariables {

    @Override
    public String getName() {
        return "h2";
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:h2:mem:nextentity;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    }

    @Override
    public String getUsername() {
        return "sa";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }

    @Override
    public String getPidNullSql() {
        return "update `users` set pid = null";
    }

}
