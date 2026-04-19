package io.github.nextentity.core.interceptor;

import io.github.nextentity.jdbc.QueryContext;

import java.util.List;

/// 结果集拦截器 - 拦截批量结果处理
///
/// 用于扩展结果处理流程，支持缓存、审计、结果转换等场景。
/// 可在结果返回前进行后处理。
///
/// @see QueryContext#setResults(List)
public interface ResultInterceptor {

    /// 是否支持处理当前场景
    ///
    /// @param context 查询上下文
    /// @return true 表示此拦截器可以处理，false 表示跳过
    boolean supports(QueryContext context);

    /// 拦截结果集处理
    ///
    /// @param context  查询上下文
    /// @param results  查询结果列表
    /// @return 处理后的结果列表
    List<?> intercept(QueryContext context, List<?> results);

    /// 拦截器名称，用于日志和诊断
    ///
    /// @return 拦截器名称
    String name();

    /// 优先级（数值越小优先级越高）
    ///
    /// @return 优先级数值
    int order();
}