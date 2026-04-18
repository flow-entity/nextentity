package io.github.nextentity.integration.config.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class Env {

    private static final Map<String, Supplier<DatabaseEnvironmentVariables>> SUPPLIERS = new LinkedHashMap<>();
    static {
        SUPPLIERS.put("mysql", MysqlEnvironmentVariables::new);
        SUPPLIERS.put("postgresql", PostgresqlEnvironmentVariables::new);
        SUPPLIERS.put("sqlserver", SqlServerEnvironmentVariables::new);
        SUPPLIERS.put("h2", H2EnvironmentVariables::new);
    }

    private static final Map<String, DatabaseEnvironmentVariables> CACHE = new ConcurrentHashMap<>();

    private static final List<String> SELECTED = parseSelectedDbTypes();

    private static List<String> parseSelectedDbTypes() {
        String property = System.getProperty("dbc");
        if (property == null || property.isBlank()) {
            return new ArrayList<>(SUPPLIERS.keySet());
        }
        return Arrays.stream(property.split(","))
                .map(String::trim)
                .filter(SUPPLIERS::containsKey)
                .toList();
    }

    public static List<DatabaseEnvironmentVariables> dbs() {
        return SELECTED.stream()
                .map(Env::load)
                .toList();
    }

    private static DatabaseEnvironmentVariables load(String name) {
        return CACHE.computeIfAbsent(name, k -> SUPPLIERS.get(k).get());
    }

    public static List<String> getSelectedDbTypes() {
        return SELECTED;
    }

}