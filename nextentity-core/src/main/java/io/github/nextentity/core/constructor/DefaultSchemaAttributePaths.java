package io.github.nextentity.core.constructor;

import java.util.HashMap;

/// 默认 Schema 属性路径树实现
///
/// 继承 HashMap，键为属性名，值为子路径树。
/// 支持通过 {@link #add(Iterable)} 逐层构建路径。
class DefaultSchemaAttributePaths extends HashMap<String, DefaultSchemaAttributePaths> implements SchemaAttributePaths {
    static final SchemaAttributePaths EMPTY = new EmptySchemaAttributePaths();

    @Override
    public SchemaAttributePaths get(String path) {
        return super.get(path);
    }

    /// 添加属性路径，逐层构建嵌套结构
    public void add(Iterable<String> paths) {
        DefaultSchemaAttributePaths cur = this;
        for (String path : paths) {
            cur = cur.computeIfAbsent(path, _ -> new DefaultSchemaAttributePaths());
        }
    }

}
