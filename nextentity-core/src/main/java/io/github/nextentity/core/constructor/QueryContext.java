package io.github.nextentity.core.constructor;

import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.jdbc.ConstructorSelector;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 查询上下文，负责从 ResultSet 构造查询结果对象。
///
/// 持有 ValueConstructor 实例，通过 ConstructorSelector 根据 Select 类型
/// 创建对应的构造器（ObjectConstructor / SingleValueConstructor / ArrayConstructor）。
/// 对于 SelectProjection 类型，支持 LAZY 加载属性的批量加载。
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class QueryContext {

    protected final QueryConfig config;

    protected final Map<String, Object> parameters = new ConcurrentHashMap<>();

    protected EntityType entityType;

    protected QueryStructure structure;

    protected boolean expandReferencePath;

    protected boolean enableLazyloading = false;

    /// 查询结果列表（在 resolve 完成后设置）
    private List<?> results;

    private ProjectionSchema projection;

    /// 存储懒加载属性元数据供批量加载使用
    public record LazyAttributeInfo(
            ProjectionSchemaAttribute attribute,
            EntityBasicAttribute sourceAttribute,
            EntityBasicAttribute targetIdAttribute
    ) {
    }

    /// 公开构造函数
    public QueryContext(QueryConfig config) {
        this.config = config;
    }

    public ValueConstructor newConstructor() {
        ConstructInterceptor interceptor = config.constructors().select(this);
        if (interceptor != null) {
            ValueConstructor intercept = interceptor.intercept(this);
            if (intercept != null) {
                return intercept;
            }
        }
        if (structure.select() instanceof SelectProjection selectProjection) {
            this.projection = entityType.getProjection(selectProjection.type());
            var schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
            return new ProjectionConstructorBuilder(
                    config, projection, schemaAttributePaths, true, false).build();
        } else {
            if (structure.select() instanceof SelectEntity selectEntity) {
                DefaultSchemaAttributePaths attributePaths = new DefaultSchemaAttributePaths();
                for (PathNode fetch : selectEntity.fetch()) {
                    attributePaths.add(fetch);
                }
                return new EntityConstructorBuilder(entityType, config.metamodel(), attributePaths).build();
            } else {
                ConstructorSelector selector = new ConstructorSelector(entityType);
                Selected select = structure.select();
                return selector.select(select, expandReferencePath);
            }
        }
    }

    /// 设置查询结构
    public void setStructure(QueryStructure structure) {
        this.structure = structure;
    }

    /// 设置是否展开引用路径
    public void setExpandReferencePath(boolean expandReferencePath) {
        this.expandReferencePath = expandReferencePath;
    }

    public static QueryContext create(QueryConfig descriptor, QueryStructure structure) {
        QueryContext context = newQueryContext(descriptor);
        context.setStructure(structure);
        if (structure.from() instanceof FromEntity(Class<?> type)) {
            EntityType entity = descriptor.metamodel().getEntity(type);
            context.setEntityType(entity);
        }
        return context;
    }

    private void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public static QueryContext newQueryContext(QueryConfig descriptor) {
        return new QueryContext(descriptor);
    }

    public <T> List<T> getResultList() {
        return getQueryExecutor().getList(this);
    }

    public QueryContext newContext(QueryStructure structure) {
        QueryContext context = create(config, structure);
        context.setExpandReferencePath(expandReferencePath);
        return context;
    }

    public QueryStructure getStructure() {
        return this.structure;
    }

    public Metamodel getMetamodel() {
        return config.metamodel();
    }

    public EntityType getEntityType() {
        return entityType;
    }

    /// 获取当前构造的 Schema
    ///
    /// 用于拦截器判断是否支持处理当前场景。
    /// 对于投影查询返回 projection，否则返回 entityType。
    ///
    /// @return 当前构造的 Schema，如果没有则返回 null
    @Nullable
    public MetamodelSchema<?> getSchema() {
        if (projection != null) {
            return projection;
        }
        return entityType;
    }

    public QueryExecutor getQueryExecutor() {
        return config.queryExecutor();
    }

    /// 获取查询结果列表
    ///
    /// @return 查询结果列表，在 resolve 完成后可用
    public List<?> getResults() {
        return results;
    }

    /// 设置查询结果列表
    ///
    /// 由 JdbcResultCollector 在完成所有行构建后调用。
    /// 子类可覆盖此方法实现后处理逻辑（如批量加载 LAZY 属性）。
    ///
    /// @param results 查询结果列表
    public void setResults(List<?> results) {
        this.results = results;
    }

    /// 是否启用懒加载拦截
    ///
    /// 仅在投影查询中默认启用。
    ///
    /// @return true 表示启用懒加载
    public boolean isEnableLazyloading() {
        return enableLazyloading;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /// 获取投影 Schema
    ///
    /// @return 投影 Schema，如果不是投影查询则返回 null
    @Nullable
    public ProjectionSchema getProjection() {
        return projection;
    }

    public QueryConfig getConfig() {
        return config;
    }
}
