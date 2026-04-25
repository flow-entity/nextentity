package io.github.nextentity.core.constructor;

/// 按深度限制的 Schema 属性路径实现
///
/// 每次 {@link #get(String)} 调用深度减 1，深度为 0 时返回空路径。
/// 用于控制嵌套属性的展开层级（如仅展开第一层关联）。
///
/// 预分配 16 个实例（depth 0-15）以避免频繁创建对象。
public class DeepLimitSchemaAttributePaths implements SchemaAttributePaths {
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

    /// 获取指定深度限制的路径实例
    ///
    /// @param limit 最大展开深度，< 0 时返回空路径
    /// @return SchemaAttributePaths 实例
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
