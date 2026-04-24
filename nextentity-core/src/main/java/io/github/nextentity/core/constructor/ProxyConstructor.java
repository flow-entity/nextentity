package io.github.nextentity.core.constructor;

import io.github.nextentity.jdbc.Arguments;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/// 代理构造器抽象基类
///
/// 统一 JDK/CGLIB 代理构造器的公共逻辑：
/// - 从 ArrayConstructor 获取值数组
/// - 根据 getter 索引提取值并构建 Map<Method, Object>
/// - 子类实现 createProxy() 创建具体代理类型
///
/// @author HuangChengwei
/// @since 2.2.2
public abstract class ProxyConstructor extends AbstractObjectConstructor {

    public ProxyConstructor(Class<?> resultType, Collection<PropertyBinding> properties) {
        super(resultType, properties);
    }

    @Override
    public Object constructConcrete(Arguments arguments) {
        Map<Method, Object> map = new HashMap<>();
        for (PropertyBinding property : properties) {
            Method getter = property.attribute().getter();
            Object value = property.valueConstructor().construct(arguments);
            map.put(getter, value);
        }
        return createProxy(map);
    }

    /// 创建代理对象
    ///
    /// @param map getter 到值的映射
    /// @return 代理对象实例
    protected abstract Object createProxy(Map<Method, Object> map);

}