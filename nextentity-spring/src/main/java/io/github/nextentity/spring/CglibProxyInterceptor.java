package io.github.nextentity.spring;

import io.github.nextentity.core.constructor.*;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.LazyValueMap;
import io.github.nextentity.core.reflect.schema.Schema;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

/// CGLIB 代理拦截器 - 为普通类创建代理实例
///
/// 使用 Spring 内置的 CGLIB 创建代理，支持延迟加载属性。
/// 只处理普通类（非 interface、非 record、非 final）。
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
        if (!context.isEnableLazyLoading()) {
            return false;
        }
        if (select instanceof SelectProjection selectProjection) {
            MetamodelSchema<?> schema = context.getEntityType().getProjection(selectProjection.type());
            if (schema == null) {
                return false;
            }

            // 只处理有懒加载字段的投影
            if (schema.hasLazyAttribute()) {
                Class<?> type = schema.type();
                // 只处理普通类（非 interface、非 record、非 final）
                return !type.isInterface()
                       && !type.isRecord()
                       && !Modifier.isFinal(type.getModifiers())
                       && isProxyable(type);
            }
        }
        return false;
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
        if (!supports(context, select)) {
            throw new UnsupportedOperationException("CglibProxyInterceptor cannot handle the given QueryContext");
        }

        Schema schema = context.getSchema();
        if (schema == null) {
            return null;
        }
        SelectProjection selectProjection = (SelectProjection) select;
        EntityType entityType = context.getEntityType();
        ProjectionSchema projection = entityType.getProjection(selectProjection.type());
        ProjectionConstructorBuilder builder = new ProjectionConstructorBuilder(context.getConfig(),
                projection,
                DeepLimitSchemaAttributePaths.of(1)
        ) {
            @Override
            protected @NonNull ValueConstructor getObjectConstructor(ProjectionSchema schema, List<PropertyBinding> bindings) {
                return new CglibProxyConstructor(schema.type(), bindings);
            }
        };
        return builder.build();

    }

    private boolean isProxyable(Class<?> type) {
        if (Modifier.isFinal(type.getModifiers())) {
            return false;
        }
        try {
            type.getConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /// CGLIB 代理构造器
    ///
    /// @author HuangChengwei
    /// @since 2.2.2
    public static class CglibProxyConstructor extends ProxyConstructor {

        public CglibProxyConstructor(Class<?> resultType, Collection<PropertyBinding> properties) {
            super(resultType, properties);
        }

        @Override
        protected Object createProxy(LazyValueMap map) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(getResultType());
            enhancer.setCallback(new MethodInterceptorImpl(map));
            return enhancer.create();
        }

        private record MethodInterceptorImpl(LazyValueMap map) implements MethodInterceptor {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (map.containsKey(method)) {
                    return map.get(method);
                }
                return proxy.invokeSuper(obj, args);
            }
        }

    }
}
