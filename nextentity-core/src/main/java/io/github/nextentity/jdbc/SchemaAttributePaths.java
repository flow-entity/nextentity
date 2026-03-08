package io.github.nextentity.jdbc;

public interface SchemaAttributePaths {

    SchemaAttributePaths get(String path);

    boolean isEmpty();

    static SchemaAttributePaths empty() {
        return DefaultSchemaAttributePaths.EMPTY;
    }

}
