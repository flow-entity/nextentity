package io.github.nextentity.core.constructor;

import java.util.HashMap;

class DefaultSchemaAttributePaths extends HashMap<String, DefaultSchemaAttributePaths> implements SchemaAttributePaths {
    static final SchemaAttributePaths EMPTY = new EmptySchemaAttributePaths();

    @Override
    public SchemaAttributePaths get(String path) {
        return super.get(path);
    }

    public void add(Iterable<String> paths) {
        DefaultSchemaAttributePaths cur = this;
        for (String path : paths) {
            cur = cur.computeIfAbsent(path, k -> new DefaultSchemaAttributePaths());
        }
    }

}
