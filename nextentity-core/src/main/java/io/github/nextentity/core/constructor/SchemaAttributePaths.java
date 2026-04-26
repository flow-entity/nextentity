package io.github.nextentity.core.constructor;

/// Schema 属性路径树
///
/// 表示实体或投影的属性访问路径，用于决定构造器应展开哪些嵌套属性。
/// 实现类包括：
/// - {@link DefaultSchemaAttributePaths}：基于 HashMap 的完整路径树
/// - {@link DeepLimitSchemaAttributePaths}：按深度限制的路径树
/// - {@link EmptySchemaAttributePaths}：空路径（不展开任何嵌套属性）
public interface SchemaAttributePaths {

    /// 获取指定属性名的子路径
    ///
    /// @param path 属性名
    /// @return 子路径，不存在则返回 null
    SchemaAttributePaths get(String path);

    /// 是否为空路径（不包含任何嵌套属性）
    boolean isEmpty();

    /// 获取空路径实例
    static SchemaAttributePaths empty() {
        return DefaultSchemaAttributePaths.EMPTY;
    }

}
