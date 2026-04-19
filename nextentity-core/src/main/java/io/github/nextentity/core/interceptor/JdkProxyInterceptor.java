package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.ResultMap;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.jdbc.QueryContext;
import io.github.nextentity.jdbc.SchemaAttributePaths;
import org.jspecify.annotations.Nullable;

/// JDK 代理拦截器 - 为 interface 类型创建代理实例
///
/// 使用 JDK Proxy 创建代理，支持延迟加载属性。
/// 只处理 interface 类型（非普通类、非 record）。
///
/// @see QueryContext#constructWithInterceptor(Arguments)
public class JdkProxyInterceptor implements ConstructInterceptor {

    /// 默认优先级
    private static final int DEFAULT_ORDER = 0;

    private final int order;

    /// 创建默认优先级的拦截器
    public JdkProxyInterceptor() {
        this(DEFAULT_ORDER);
    }

    /// 创建指定优先级的拦截器
    ///
    /// @param order 优先级数值（越小越优先）
    public JdkProxyInterceptor(int order) {
        this.order = order;
    }

    @Override
    public boolean supports(QueryContext context) {
        Schema schema = context.getSchema();
        if (!(schema instanceof ProjectionSchema)) {
            return false;
        }
        // TODO 检查是否有懒加载字段
        // 只处理 interface 类型
        return schema.type().isInterface();
    }

    @Override
    public Object intercept(QueryContext context, Arguments arguments) {
        Schema schema = context.getSchema();
        if (schema == null) {
            return null;
        }
        return constructInterfaceSchema(context, schema, arguments);
    }

    /// 构建 interface 代理对象
    @Nullable
    protected Object constructInterfaceSchema(QueryContext context, Schema schema, Arguments arguments) {
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
        return ReflectUtil.newProxyInstance(schema.type(), map);
    }

    /// 获取 SchemaAttributePaths
    @Nullable
    protected SchemaAttributePaths getSchemaAttributePaths(QueryContext context) {
        // 默认返回 null，子类可覆盖提供具体路径
        return null;
    }

    @Override
    public String name() {
        return "jdk-proxy";
    }

    @Override
    public int order() {
        return order;
    }
}