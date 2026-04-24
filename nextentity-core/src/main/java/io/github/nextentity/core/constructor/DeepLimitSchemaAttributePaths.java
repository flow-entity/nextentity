package io.github.nextentity.core.constructor;

class DeepLimitSchemaAttributePaths implements SchemaAttributePaths {
    static final DeepLimitSchemaAttributePaths[] instances = new DeepLimitSchemaAttributePaths[16];

    static {
        for (int i = 0; i < instances.length; i++) {
            instances[i] = new DeepLimitSchemaAttributePaths(i);
        }
    }

    private final int limit;

    private DeepLimitSchemaAttributePaths(int limit) {
        this.limit = limit;
    }

    public static SchemaAttributePaths of(int limit) {
        if (limit < 0) {
            return DefaultSchemaAttributePaths.EMPTY;
        } else if (limit < instances.length) {
            return instances[limit];
        } else {
            return new DeepLimitSchemaAttributePaths(limit);
        }
    }

    @Override
    public SchemaAttributePaths get(String path) {
        int i = limit - 1;
        if (i < 0) {
            return DefaultSchemaAttributePaths.EMPTY;
        } else if (i < instances.length) {
            return instances[i];
        } else {
            return new DeepLimitSchemaAttributePaths(i);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
