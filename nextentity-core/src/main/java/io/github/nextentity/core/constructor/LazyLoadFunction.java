package io.github.nextentity.core.constructor;

import java.util.Collection;
import java.util.Map;

/// 懒加载函数接口
///
/// 执行批量查询并构造结果，返回外键到结果对象的映射。
/// resultConstructor 参数用于构造查询结果，同时可用于优化查询策略。
///
/// @author HuangChengwei
/// @since 2.2.2
public interface LazyLoadFunction {

    /// 执行批量加载查询并构造结果
    ///
    /// @param resultConstructor 结果构造器
    /// @param foreignKeys 待加载的外键集合
    /// @return 外键 → 构造结果的映射
    Map<Object, Object> apply(ValueConstructor resultConstructor, Collection<Object> foreignKeys);
}