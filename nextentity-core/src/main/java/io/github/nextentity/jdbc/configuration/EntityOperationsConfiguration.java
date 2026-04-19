package io.github.nextentity.jdbc.configuration;

import io.github.nextentity.jdbc.SqlDialect;

// TODO
public interface EntityOperationsConfiguration {

    SqlDialect sqlDialect();
    DeleteConfiguration deleteConfiguration();
    UpdateConfiguration updateConfiguration();
    QueryConfiguration queryConfiguration();

}
