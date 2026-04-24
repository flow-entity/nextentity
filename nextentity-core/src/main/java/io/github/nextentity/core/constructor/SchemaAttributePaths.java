package io.github.nextentity.core.constructor;

public interface SchemaAttributePaths {

    SchemaAttributePaths get(String path);

    boolean isEmpty();

    static SchemaAttributePaths empty() {
        return DefaultSchemaAttributePaths.EMPTY;
    }

}
