package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.meta.Fetchable;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.meta.ProjectionSchemaAttribute;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import jakarta.persistence.FetchType;

/// 懒加载支持工具类
///
/// 提供检查投影是否包含懒加载属性的静态方法。
/// 供拦截器实现类使用，无需直接依赖 JPA FetchType。
public final class LazyLoadSupport {

    private LazyLoadSupport() {}

    /// 检查投影是否包含懒加载属性
    ///
    /// 遍历投影的所有属性，检查是否有 FetchType.LAZY 的嵌套属性。
    /// 如果存在懒加载属性，需要创建代理对象来支持延迟加载。
    ///
    /// @param schema 投影元模型
    /// @return true 表示存在懒加载属性，false 表示全部为立即加载
    public static boolean hasLazyAttribute(Schema schema) {
        for (Attribute attr : schema.getAttributes()) {
            if (attr instanceof Fetchable fetchable) {
                if (fetchable.fetchType() == FetchType.LAZY) {
                    return true;
                }
            }
        }
        return false;
    }
}