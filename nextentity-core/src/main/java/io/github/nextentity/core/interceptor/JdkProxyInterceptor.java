package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.meta.ProjectionSchema;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.ResultMap;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.jdbc.Arguments;
import io.github.nextentity.jdbc.QueryContext;

/// JDK 代理拦截器 - 为 interface 类型创建代理实例
///
/// 使用 JDK Proxy 创建代理，支持延迟加载属性。
/// 只处理 interface 类型（非普通类、非 record）。
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
        if (!(schema instanceof ProjectionSchema projectionSchema)) {
            return false;
        }
        if (context.getSchema() == null) {
            return false;
        }
        // 只处理有懒加载字段的投影
        if (LazyLoadSupport.hasLazyAttribute(projectionSchema)) {
            return schema.type().isInterface();
        } else {
            return false;
        }

    }

    @Override
    public Object intercept(QueryContext context, Arguments arguments) {
        if (!supports(context)) {
            // TODO 改进异常类和消息
            throw new IllegalStateException("JdkProxyInterceptor supports only JdkProxyInterceptor");
        }
        ResultMap resultMap = context.collecteResultMap(arguments);
        Schema schema = context.getSchema();
        if (schema == null) {
            return null;
        }
        return ReflectUtil.newProxyInstance(schema.type(), resultMap);
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