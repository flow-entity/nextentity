package io.github.nextentity.core.interceptor;

/// 拦截器基础接口，定义通用行为
///
/// 所有拦截器都应实现此接口，提供统一的支持检查、命名和优先级机制。
/// 泛型参数 C 定义 supports 方法的上下文类型。
///
/// @param <C> 上下文类型（supports 方法的参数类型）
/// @see ConstructInterceptor
/// @see ResultInterceptor
public interface Interceptor<C> {

    /// 是否支持处理当前场景
    ///
    /// 拦截器通过此方法判断是否可以处理当前查询上下文。
    /// 选择器会按优先级依次检查，选择第一个返回 true 的拦截器。
    ///
    /// @param context 上下文对象，包含必要信息
    /// @return true 表示此拦截器可以处理，false 表示跳过
    boolean supports(C context);

    /// 拦截器名称，用于日志和诊断
    ///
    /// 建议使用简短、有意义的名称，便于识别拦截器类型。
    ///
    /// @return 拦截器名称（如 "jdk-proxy"、"cglib-proxy"）
    String name();

    /// 优先级（数值越小优先级越高）
    ///
    /// 多个拦截器按优先级排序，数值小的先执行。
    /// 建议范围：0-100，默认值 0。
    ///
    /// @return 优先级数值
    int order();
}