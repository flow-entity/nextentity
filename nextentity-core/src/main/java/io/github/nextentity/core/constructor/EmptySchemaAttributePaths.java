package io.github.nextentity.core.constructor;

/// 空 Schema 属性路径实现
///
/// 所有查询均返回 null，表示不展开任何嵌套属性。
/// 用作 {@link DefaultSchemaAttributePaths#EMPTY} 的类型。
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
