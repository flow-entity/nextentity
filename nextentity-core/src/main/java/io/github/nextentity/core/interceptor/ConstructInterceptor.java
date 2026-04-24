package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.core.constructor.QueryContext;

/// 对象构造拦截器 - 拦截单个对象创建
///
/// 用于扩展对象创建流程，支持代理、类型转换等场景。
/// 每次调用返回一个对象实例，不存在"依次经过多个处理步骤"的场景。
///
/// @see QueryContext#construct(Arguments) (Arguments)
public interface ConstructInterceptor extends Interceptor<QueryContext> {

    /// 拦截构造过程
    ///
    /// @param arguments 参数供应器，用于获取属性值
    /// @param context   查询上下文，包含元模型信息
    /// @param select
    /// @return 构造的对象实例
    ValueConstructor intercept(QueryContext context, Selected select);
}