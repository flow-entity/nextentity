package io.github.nextentity.proxy.spring;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.ResultMap;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.jdbc.QueryContext;
import io.github.nextentity.jdbc.SchemaAttributePaths;
import org.jspecify.annotations.Nullable;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Modifier;

/// CGLIB 代理拦截器 - 为普通类创建代理实例
///
/// 使用 Spring 内置的 CGLIB 创建代理，支持延迟加载属性。
/// 只处理普通类（非 interface、非 record、非 final）。
///
/// @see QueryContext#constructWithInterceptor(Arguments)
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
        Schema schema = context.getSchema();
        if (!(schema instanceof ProjectionSchema)) {
            return false;
        }
        // TODO 检查是否有懒加载字段
        Class<?> type = schema.type();
        // 只处理普通类（非 interface、非 record、非 final）
        return !type.isInterface()
               && !type.isRecord()
               && !Modifier.isFinal(type.getModifiers());
    }

    @Override
    public Object intercept(QueryContext context, Arguments arguments) {
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
        ResultMap map = new ResultMap();
        SchemaAttributePaths paths = getSchemaAttributePaths(context);
        for (Attribute attribute : schema.getAttributes()) {
            if (attribute instanceof Schema nestedSchema) {
                SchemaAttributePaths subPaths = paths != null ? paths.get(attribute.name()) : null;
                if (subPaths != null) {
                    Object value = context.buildSchema(nestedSchema, arguments, subPaths);
                    map.put(attribute.getter(), value);
                }
            } else {
                Object value = context.getAttributeValue(arguments, attribute);
                map.put(attribute.getter(), value);
            }
        }
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
    private MethodInterceptor createMethodInterceptor(ResultMap map, Class<?> resultType) {
        return (proxy, method, args, methodProxy) -> {
            Object result = map.get(method);
            if (result != null) {
                if (map.isNull(result)) {
                    return null;
                }
                if (result instanceof AttributeLoader loader) {
                    result = loader.load();
                    map.replace(method, loader, result);
                }
                return result;
            }
            if (method.getDeclaringClass() == Object.class) {
                return methodProxy.invokeSuper(proxy, args);
            }
            // 调用父类方法（默认值）
            return methodProxy.invokeSuper(proxy, args);
        };
    }

    /// 获取 SchemaAttributePaths
    @Nullable
    protected SchemaAttributePaths getSchemaAttributePaths(QueryContext context) {
        // 默认返回 null，子类可覆盖提供具体路径
        return null;
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