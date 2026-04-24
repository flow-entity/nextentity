package io.github.nextentity.spring;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.core.constructor.QueryContext;
import org.jspecify.annotations.Nullable;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

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
    public boolean supports(QueryContext context) {
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
    public Object intercept(QueryContext context, Arguments arguments) {
        if (!supports(context)) {
            throw new UnsupportedOperationException("CglibProxyInterceptor cannot handle the given QueryContext");
        }
        Schema schema = context.getSchema();
        if (schema == null) {
            return null;
        }
        validateProxyable(schema.type());
        return createCglibProxy(context, schema, arguments);
    }

    /// 验证类是否可代理
    private void validateProxyable(Class<?> type) {
        if (Modifier.isFinal(type.getModifiers())) {
            throw new ProxyException("Cannot proxy final class: " + type.getName());
        }
        try {
            type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ProxyException("Cannot proxy class without default constructor: " + type.getName());
        }
    }

    /// 创建 CGLIB 代理
    @Nullable
    protected Object createCglibProxy(QueryContext context, Schema schema, Arguments arguments) {
        Map<Method, Object> map = context.collectResultMap(arguments);
        if (map.isEmpty()) {
            return null;
        }

        Class<?> type = schema.type();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(createMethodInterceptor(map, type));

        return enhancer.create();
    }

    /// 创建方法拦截器
    private MethodInterceptor createMethodInterceptor(Map<Method, Object> map, Class<?> resultType) {
        return (proxy, method, args, methodProxy) -> {
            if (map.containsKey(method)) {
                return AttributeLoader.loadFromMap(map, method);
            }
            return methodProxy.invokeSuper(proxy, args);
        };
    }

    @Override
    public String name() {
        return "cglib-proxy";
    }

    @Override
    public int order() {
        return order;
    }
}