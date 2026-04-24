package io.github.nextentity.core.interceptor;

import io.github.nextentity.core.constructor.DeepLimitSchemaAttributePaths;
import io.github.nextentity.core.constructor.ProjectionConstructorBuilder;
import io.github.nextentity.core.constructor.QueryContext;
import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.expression.SelectProjection;
import io.github.nextentity.core.expression.Selected;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.ProjectionSchema;

/// JDK 代理拦截器 - 为 interface 类型创建代理实例
///
/// 使用 JDK Proxy 创建代理，支持延迟加载属性。
/// 只处理 interface 类型（非普通类、非 record）。
public class JdkProxyInterceptor implements ConstructInterceptor {

    private static final JdkProxyInterceptor INSTANCE = new JdkProxyInterceptor();

    /// 默认优先级
    private static final int DEFAULT_ORDER = 0;

    private final int order;

    public static JdkProxyInterceptor of() {
        return INSTANCE;
    }

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
    public boolean supports(QueryContext context, Selected select) {
        if (select instanceof SelectProjection selectProjection) {
            ProjectionSchema projection = context.getEntityType().getProjection(selectProjection.type());
            return projection.hasLazyAttribute();
        } else {
            return false;
        }
    }

    @Override
    public ValueConstructor intercept(QueryContext context, Selected select) {
        if (supports(context, select)) {
            SelectProjection selectProjection = (SelectProjection) select;
            EntityType entityType = context.getEntityType();
            ProjectionSchema projection = entityType.getProjection(selectProjection.type());
            return new ProjectionConstructorBuilder(context.getConfig(),
                    projection,
                    DeepLimitSchemaAttributePaths.of(1)
            ).build();
        }
        // TODO 打日志
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