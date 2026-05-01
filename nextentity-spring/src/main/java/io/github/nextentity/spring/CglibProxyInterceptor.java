package io.github.nextentity.spring;

import io.github.nextentity.core.constructor.*;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.MetamodelAttribute;
import io.github.nextentity.core.meta.MetamodelSchema;
import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.LazyValue;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.jdbc.Arguments;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/// CGLIB 代理拦截器 - 为普通类创建代理实例
///
/// 使用 Spring 内置的 CGLIB 创建代理，支持延迟加载属性。
/// 只处理普通类（非 interface、非 record、非 final）。
public record CglibProxyInterceptor(int order) implements ConstructInterceptor {

    /// 默认优先级
    private static final int DEFAULT_ORDER = 0;

    /// 创建默认优先级的拦截器
    public CglibProxyInterceptor() {
        this(DEFAULT_ORDER);
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

            if (schema.hasLazyAttribute()) {
                Class<?> type = schema.type();
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
    public ValueConstructor intercept(QueryContext context, Selected select) {
        if (!supports(context, select)) {
            throw new UnsupportedOperationException("CglibProxyInterceptor cannot handle the given QueryContext");
        }

        MetamodelSchema<?> schema = context.getSchema();
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
            protected @NonNull ValueConstructor getObjectConstructor(ProjectionSchema schema, List<PropertyBinding> bindings, boolean root) {
                return new CglibProxyConstructor(schema.type(), bindings, root);
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
    public static class CglibProxyConstructor extends AbstractObjectConstructor {

        private final boolean root;
        private final Constructor<?> constructor;

        public CglibProxyConstructor(Class<?> resultType, Collection<PropertyBinding> properties, boolean root) {
            if (resultType.isInterface()) {
                throw new ReflectiveException("Cannot create ObjectConstructor for interface types");
            }
            super(resultType, properties);
            Constructor<?> constructor = ReflectUtil.getDefaultConstructor(resultType);
            this.constructor = Objects.requireNonNull(constructor);
            this.root = root;
        }

        @Override
        public Object constructConcrete(Arguments arguments) throws ReflectiveOperationException {
            Object instance = root ? constructor.newInstance() : null;
            Map<Method, LazyProperty> lazyProperties = new ConcurrentHashMap<>();
            for (PropertyBinding prop : properties) {
                Object value = prop.valueConstructor().construct(arguments);
                if (value != null) {
                    if (instance == null) {
                        instance = constructor.newInstance();
                    }
                    MetamodelAttribute attribute = prop.attribute();
                    if (value instanceof LazyValue lazyValue) {
                        lazyProperties.put(attribute.getter(), new LazyProperty(lazyValue, attribute));
                    } else {
                        attribute.set(instance, value);
                    }
                }
            }
            if (instance == null) {
                return null;
            } else {
                return createProxy(lazyProperties, instance);
            }
        }

        private Object createProxy(Map<Method, LazyProperty> map, Object target) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(getResultType());
            MethodInterceptorImpl interceptor = new MethodInterceptorImpl(map, target);
            enhancer.setCallback(interceptor);
            return enhancer.create();
        }

        private record MethodInterceptorImpl(
                Map<Method, LazyProperty> lazyProperties,
                Object target
        ) implements MethodInterceptor {

            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                lazyProperties().computeIfPresent(method, (_, property) -> {
                    Object propertyValue = property.lazyValue().get();
                    property.attribute().set(target(), propertyValue);
                    return null;
                });
                return proxy.invoke(target(), args);
            }
        }
    }

    private record LazyProperty(LazyValue lazyValue, MetamodelAttribute attribute) {
    }

}
