package io.github.nextentity.core.interceptor;

import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.jdbc.QueryContext;

/// 对象构造拦截器 - 拦截单个对象创建
///
/// 用于扩展对象创建流程，支持代理、类型转换等场景。
/// 每次调用返回一个对象实例，不存在"依次经过多个处理步骤"的场景。
///
/// @see QueryContext#doConstruct(Arguments)
public interface ConstructInterceptor {

    /// 是否支持处理当前场景
    ///
    /// @param context 查询上下文
    /// @return true 表示此拦截器可以处理，false 表示跳过
    boolean supports(QueryContext context);

    /// 拦截构造过程
    ///
    /// @param context    查询上下文，包含元模型信息
    /// @param arguments  参数供应器，用于获取属性值
    /// @return 构造的对象实例
    Object intercept(QueryContext context, Arguments arguments);

    /// 拦截器名称，用于日志和诊断
    ///
    /// @return 拦截器名称
    String name();

    /// 优先级（数值越小优先级越高）
    ///
    /// @return 优先级数值
    int order();
}