package io.github.nextentity.core.reflect;

import java.util.function.Supplier;

/// 批量懒加载属性加载器。
///
/// 实现 LazyLoader 接口，封装投影对象 LAZY 属性的批量加载逻辑。
/// 首次访问时触发 WHERE IN 批量查询，避免 N+1 问题。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class AttributeLoader {

    private final Supplier<Object> supplier;
    private final Object foreignKey;

    public AttributeLoader(BatchAttributeLoader batchAttributeLoader, Object foreignKey) {
        this.foreignKey = foreignKey;
        this.supplier = batchAttributeLoader.addForeignKey(this);
    }

    public Object getForeignKey() {
        return foreignKey;
    }

    public Object load() {
        return supplier.get();
    }
}