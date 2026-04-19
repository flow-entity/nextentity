package io.github.nextentity.core.interceptor;

import io.github.nextentity.jdbc.QueryContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/// 拦截器选择器 - 根据 supports 方法选择合适拦截器
///
/// 按 order 排序拦截器，选择第一个 supports=true 的拦截器。
/// 用于 QueryContext 中选择合适的拦截器处理当前场景。
///
/// @param <T> 拦截器类型（ConstructInterceptor 或 ResultInterceptor）
public class InterceptorSelector<T> {

    private final List<T> interceptors;

    /// 创建拦截器选择器
    ///
    /// @param interceptors 拦截器列表（按 order 排序）
    public InterceptorSelector(@NonNull List<T> interceptors) {
        this.interceptors = interceptors.stream()
                .sorted(Comparator.comparingInt(this::getOrder))
                .toList();
    }

    /// 从空列表创建选择器
    ///
    /// @return 空选择器，select() 总是返回 null
    public static <T> InterceptorSelector<T> empty() {
        return new InterceptorSelector<>(List.of());
    }

    /// 获取拦截器优先级
    private int getOrder(T interceptor) {
        if (interceptor instanceof ConstructInterceptor c) {
            return c.order();
        } else if (interceptor instanceof ResultInterceptor r) {
            return r.order();
        }
        return Integer.MAX_VALUE;
    }

    /// 选择支持当前场景的拦截器
    ///
    /// @param context 查询上下文
    /// @return 第一个 supports=true 的拦截器，若无则返回 null
    @Nullable
    public T select(QueryContext context) {
        for (T interceptor : interceptors) {
            if (supports(interceptor, context)) {
                return interceptor;
            }
        }
        return null;
    }

    /// 检查拦截器是否支持当前场景 TODO 重新设计接口泛型，取消instanceof运算，支持更多的类型
    private boolean supports(T interceptor, QueryContext context) {
        if (interceptor instanceof ConstructInterceptor c) {
            return c.supports(context);
        } else if (interceptor instanceof ResultInterceptor r) {
            return r.supports(context);
        }
        return false;
    }

    /// 获取所有拦截器（用于日志诊断）
    ///
    /// @return 已排序的拦截器列表
    public List<T> all() {
        return interceptors;
    }

    /// 是否有拦截器
    ///
    /// @return true 表示有拦截器，false 表示空
    public boolean isEmpty() {
        return interceptors.isEmpty();
    }
}