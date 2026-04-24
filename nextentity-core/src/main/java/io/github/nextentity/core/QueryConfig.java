package io.github.nextentity.core;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.jdbc.FetchConfig;

public interface QueryConfig {

    QueryExecutor queryExecutor();

    FetchConfig fetch();

    Metamodel metamodel();

    PaginationConfig pagination();

    InterceptorSelector<ConstructInterceptor> constructors();

    /// Interface投影是否支持懒加载
    ///
    /// Interface投影通过动态代理实现，原生支持懒加载。
    /// 默认启用（true）。
    ///
    /// @return 是否启用Interface投影懒加载
    boolean interfaceLazyEnabled();

    /// Dto投影是否支持字段懒加载
    ///
    /// Dto投影通过反射构造，不支持真正的懒加载。
    /// 默认禁用（false）。启用后需要特殊处理。
    ///
    /// @return 是否启用Dto投影懒加载
    boolean dtoObjectLazyEnabled();

}
