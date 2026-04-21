package io.github.nextentity.core.reflect;

import java.lang.reflect.Method;
import java.util.Map;

/// 懒加载属性加载器
public interface AttributeLoader {

    Object load();

    /// 从 map 中加载值，处理 AttributeLoader 延迟加载逻辑
    static Object loadFromMap(Map<Method, Object> map, Method method) {
        Object result = map.get(method);
        if (result instanceof AttributeLoader loader) {
            result = loader.load();
            map.replace(method, loader, result);
        }
        return result;
    }
}