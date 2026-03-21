package io.github.nextentity.integration.config;

import org.jspecify.annotations.NonNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {
    @NonNull Connection get()  throws SQLException;
}
