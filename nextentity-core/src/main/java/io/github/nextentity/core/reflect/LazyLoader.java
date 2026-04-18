package io.github.nextentity.core.reflect;

import java.util.Map;
import java.lang.reflect.Method;

/// 懒加载属性加载器接口。
///
/// 用于投影对象中 LAZY 属性的延迟加载。
/// 当首次访问懒加载属性时，此接口的 {@link #load(Map)} 方法被调用，
/// 从数据映射中获取必要信息并计算属性值。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface LazyLoader {

    /// 加载懒加载属性值。
    ///
    /// @param data 投影对象的属性数据映射，包含所有已加载的 EAGER 属性
    /// @return 加载后的属性值，可以为 null
    Object load(Map<Method, Object> data);
}