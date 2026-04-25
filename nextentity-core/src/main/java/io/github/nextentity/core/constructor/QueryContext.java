package io.github.nextentity.core.constructor;

import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// 查询上下文，负责从 ResultSet 构造查询结果对象。
///
/// 持有 ValueConstructor 实例，通过 ConstructInterceptor 根据 Select 类型
/// 创建对应的构造器（ObjectConstructor / RecordConstructor / JdkProxyConstructor 等）。
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

    protected boolean expandReferencePath;// TODO DELETE

    protected boolean enableLazyloading = false;

    /// 查询结果列表（在 resolve 完成后设置）
    private List<?> results;

    private ProjectionSchema projection;

    public MetamodelSchema<?> getSchema() {
        return projection == null ? entityType : (projection);
    }

    /// 存储懒加载属性元数据供批量加载使用
    ///
    /// @param attribute         投影属性元数据
    /// @param sourceAttribute   源实体属性（外键所在属性）
    /// @param targetIdAttribute 目标实体的 ID 属性
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
        return newConstructor(entityType);
    }

    public ValueConstructor newConstructor(EntityType entityType) {
        return newConstructor(entityType, structure.select());
    }

    private ValueConstructor newConstructor(EntityType entityType, Selected select) {
        ConstructInterceptor interceptor = config.constructors().select(this, select);
        if (select instanceof SelectProjection selectProjection) {
            projection = entityType.getProjection(selectProjection.type());
        }
        if (interceptor != null) {
            ValueConstructor intercept = interceptor.intercept(this, select);
            if (intercept != null) {
                return intercept;
            }
        }
        return switch (select) {
            case SelectProjection _ -> newConstructor(projection);
            case SelectEntity selectEntity -> newConstructor(selectEntity, entityType);
            case SelectExpression selectExpression -> newConstructor(entityType, selectExpression.expression());
            case SelectExpressions selectExpressions -> newConstructor(entityType, selectExpressions);
            case SelectNested selectNested -> newConstructor(entityType, selectNested);
            case null -> throw new ConfigurationException("Query select clause must not be null");
        };
    }

    public ValueConstructor newConstructor(EntityType type, SelectNested selectNested) {
        List<ValueConstructor> constructors = selectNested.items()
                .stream()
                .map(selected -> newConstructor(type, selected))
                .toList();
        return new ArrayConstructor(constructors);
    }

    public ValueConstructor newConstructor(EntityType entityType, SelectExpressions selectExpressions) {
        List<ValueConstructor> constructors = selectExpressions.items().stream()
                .map(node -> newConstructor(entityType, node))
                .toList();
        return new ArrayConstructor(constructors);
    }

    public ValueConstructor newConstructor(EntityType entityType, ExpressionNode expression) {
        ValueConverter<?, ?> converter = null;
        if (expression instanceof PathNode pathNode) {
            if (pathNode.getAttribute() instanceof EntityBasicAttribute entityBasicAttribute) {
                converter = entityBasicAttribute.valueConvertor();
            } else {
                EntityAttribute attribute = entityType.getAttribute(pathNode);
                if (attribute instanceof EntityBasicAttribute basicAttribute) {
                    converter = basicAttribute.valueConvertor();
                }
            }
        }
        if (converter == null) {
            converter = IdentityValueConverter.of(ExpressionTypeResolver.getExpressionType(expression, entityType));
        }

        Column column = new Column(expression, converter, 0);
        return new SingleValueConstructor(column);
    }

    public ValueConstructor newConstructor(SelectEntity selectEntity, EntityType entityType) {
        DefaultSchemaAttributePaths attributePaths = new DefaultSchemaAttributePaths();
        for (PathNode fetch : selectEntity.fetch()) {
            attributePaths.add(fetch);
        }
        return new EntityConstructorBuilder(entityType, config.metamodel(), attributePaths).build();
    }

    public ValueConstructor newConstructor(ProjectionSchema projection) {
        var schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
        return new ProjectionConstructorBuilder(
                config, projection, schemaAttributePaths).build();
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

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    private static QueryContext newQueryContext(QueryConfig descriptor) {
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

    public QueryConfig getConfig() {
        return config;
    }
}
