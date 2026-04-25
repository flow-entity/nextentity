package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.core.constructor.QueryContext;

/// 对象构造拦截器 - 拦截单个对象创建
///
/// 用于扩展对象创建流程，支持代理、类型转换等场景。
/// 每次调用返回一个对象实例，不存在"依次经过多个处理步骤"的场景。
public interface ConstructInterceptor extends Interceptor<QueryContext> {

    /// 拦截构造过程，返回自定义的 ValueConstructor 或 null 使用默认构造
    ValueConstructor intercept(QueryContext context, Selected select);
}