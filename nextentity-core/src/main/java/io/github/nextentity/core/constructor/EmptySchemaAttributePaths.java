package io.github.nextentity.core.constructor;

class EmptySchemaAttributePaths implements SchemaAttributePaths {
    EmptySchemaAttributePaths() {
    }

    @Override
    public SchemaAttributePaths get(String path) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
