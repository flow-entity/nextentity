package io.github.nextentity.spring;

import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.constructor.QueryContext;

import java.lang.reflect.Modifier;

/// CGLIB 代理拦截器 - 为普通类创建代理实例
///
/// 使用 Spring 内置的 CGLIB 创建代理，支持延迟加载属性。
/// 只处理普通类（非 interface、非 record、非 final）。
///
public class CglibProxyInterceptor implements ConstructInterceptor {

    /// 默认优先级
    private static final int DEFAULT_ORDER = 0;

    private final int order;

    /// 创建默认优先级的拦截器
    public CglibProxyInterceptor() {
        this(DEFAULT_ORDER);
    }

    /// 创建指定优先级的拦截器
    ///
    /// @param order 优先级数值（越小越优先）
    public CglibProxyInterceptor(int order) {
        this.order = order;
    }

    @Override
    public boolean supports(QueryContext context, Selected select) {
        if (!context.isEnableLazyloading()) {
            return false;
        }
        MetamodelSchema<?> schema = context.getSchema();
        if (schema == null) {
            return false;
        }
        // 只处理有懒加载字段的投影
        if (schema.hasLazyAttribute()) {
            Class<?> type = schema.type();
            // 只处理普通类（非 interface、非 record、非 final）
            return !type.isInterface()
                   && !type.isRecord()
                   && !Modifier.isFinal(type.getModifiers());
        } else {
            return false;
        }
    }



    @Override
    public String name() {
        return "cglib-proxy";
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public ValueConstructor intercept(QueryContext context, Selected select) {
        // TODO
        return null;
    }
}