package io.github.nextentity.core.constructor;

import io.github.nextentity.core.reflect.MethodValueMap;
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

    public JdkProxyConstructor(Class<?> resultType, Collection<PropertyBinding> properties) {
        super(resultType, properties);
    }

    @Override
    protected Object createProxy(MethodValueMap map) {
        return ReflectUtil.newProxyInstance(getResultType(), map);
    }
}