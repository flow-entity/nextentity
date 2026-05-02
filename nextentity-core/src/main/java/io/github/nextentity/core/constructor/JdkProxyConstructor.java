package io.github.nextentity.core.constructor;

import io.github.nextentity.core.reflect.LazyValueMap;
import io.github.nextentity.core.reflect.ReflectUtil;

import java.util.Collection;

/// JDK 代理构造器
///
/// 用于 interface 投影类型，使用 JDK 动态代理创建实例。
/// 复用现有的 ReflectUtil.newProxyInstance 处理方法调用。
///
/// @author HuangChengwei
/// @since 2.2.2
public class JdkProxyConstructor extends ProxyConstructor {

    /// 是否为根级投影/实体构造。
    /// 根级构造即使所有属性值为 null 也必须返回非 null 代理对象，
    /// 而非根级的嵌入式属性在所有字段为 null 时返回 null。
    private final boolean root;

    public JdkProxyConstructor(Class<?> type, Collection<PropertyBinding> bindings, boolean root) {
        super(type, bindings);
        this.root = root;
    }

    @Override
    protected Object createProxy(LazyValueMap map) {
        if (root || map.hasNonNullValue()) {
            return ReflectUtil.newProxyInstance(getResultType(), map);
        } else {
            return null;
        }
    }
}