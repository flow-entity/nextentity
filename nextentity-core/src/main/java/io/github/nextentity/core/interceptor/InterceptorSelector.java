package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.QueryContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.List;

/// 拦截器选择器，按 order 排序并选择第一个 supports=true 的拦截器
public class InterceptorSelector<T extends Interceptor<? super QueryContext>> {
    private static final InterceptorSelector<ConstructInterceptor> CONSTRUCT_INTERCEPTOR
            = new InterceptorSelector<>(List.of(JdkProxyInterceptor.of()));

    private final List<T> interceptors;

    public InterceptorSelector(@NonNull List<T> interceptors) {
        this.interceptors = interceptors.stream()
                .sorted(Comparator.comparingInt(Interceptor::order))
                .toList();
    }

    public static ConstructInterceptor selectConstructor(QueryContext context) {
        return CONSTRUCT_INTERCEPTOR.select(context);
    }

    public static InterceptorSelector<ConstructInterceptor> empty() {
        return new InterceptorSelector<>(List.of());
    }

    @Nullable
    public T select(QueryContext context) {
        for (T interceptor : interceptors) {
            if (interceptor.supports(context)) {
                return interceptor;
            }
        }
        return null;
    }

    public List<T> all() {
        return interceptors;
    }

    public boolean isEmpty() {
        return interceptors.isEmpty();
    }
}