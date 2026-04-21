package io.github.nextentity.core.interceptor;

import io.github.nextentity.jdbc.QueryContext;

import java.util.List;

/// 结果集拦截器 - 拦截批量结果处理
///
/// 用于扩展结果处理流程，支持缓存、审计、结果转换等场景。
/// 可在结果返回前进行后处理。
///
/// @see QueryContext#setResults(List)
public interface ResultInterceptor extends Interceptor<QueryContext> {

    /// 拦截结果集处理
    ///
    /// @param context  查询上下文
    /// @param results  查询结果列表
    /// @return 处理后的结果列表
    List<?> intercept(QueryContext context, List<?> results);
}