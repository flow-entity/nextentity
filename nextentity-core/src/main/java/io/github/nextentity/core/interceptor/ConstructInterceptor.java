package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.constructor.QueryContext;

/// 对象构造拦截器 - 拦截单个对象创建
///
/// 用于扩展对象创建流程，支持代理、类型转换等场景。
/// 基于 Select 类型返回对应的 ValueConstructor。
///
/// 注意：该 SPI 已调整为基于 {@link Selected} 选择构造器，
/// 不再使用旧版 `intercept(QueryContext, Arguments)` 形式。
public interface ConstructInterceptor extends Interceptor<QueryContext> {

    /// 拦截构造过程，返回自定义的 ValueConstructor 或 null 使用默认构造
    ValueConstructor intercept(QueryContext context, Selected select);
}
