package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.QueryConfig;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.constructor.Column;
import io.github.nextentity.core.constructor.ValueConstructor;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import io.github.nextentity.core.reflect.AttributeLoader;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import io.github.nextentity.core.util.NullableConcurrentMap;
import jakarta.persistence.FetchType;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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

    protected final QueryConfig descriptor;

    protected final Map<String, Object> parameters = new ConcurrentHashMap<>();

    protected EntityType entityType;

    protected QueryStructure structure;

    protected boolean expandReferencePath;

    protected boolean enableLazyloading = false;

    private ValueConstructor constructor;

    private SchemaAttributePaths schemaAttributePaths;

    /// 查询结果列表（在 resolve 完成后设置）
    private List<?> results;

    /// 投影查询相关字段
    private SelectProjection selectProjection;
    private ProjectionSchema projection;
    private ImmutableArray<Column> expressions;

    /// 批量加载上下文（延迟初始化，仅投影查询使用）
    private final Map<ProjectionSchemaAttribute, BatchAttributeLoader> batchLoaderContexts = new ConcurrentHashMap<>();

    /// 存储懒加载属性元数据供批量加载使用
    public record LazyAttributeInfo(
            ProjectionSchemaAttribute attribute,
            EntityBasicAttribute sourceAttribute,
            EntityBasicAttribute targetIdAttribute
    ) {
    }

    /// 公开构造函数
    public QueryContext(QueryConfig descriptor) {
        this.descriptor = descriptor;
    }

    /// 初始化核心字段
    ///
    /// 在设置参数后调用，完成字段初始化。
    /// 使用 ConstructorSelector 根据 Select 类型创建对应的 ValueConstructor。
    public void init() {
        if (selectProjection != null) {
            // SelectProjection 有自己的构造逻辑，不使用 ConstructorSelector
            this.projection = entityType.getProjection(selectProjection.type());
            this.schemaAttributePaths = DeepLimitSchemaAttributePaths.of(1);
            this.expressions = separateAttributes(projection, schemaAttributePaths);
        } else {
            if (this.constructor == null) {
                ConstructorSelector selector = new ConstructorSelector(entityType);
                Selected select = structure.select();
                this.constructor = selector.select(select, expandReferencePath);
            }
            if (this.schemaAttributePaths == null) {
                this.schemaAttributePaths = buildSchemaAttributePaths(structure.select());
            }
        }
    }

    /// 根据 Select 类型构建 SchemaAttributePaths
    private SchemaAttributePaths buildSchemaAttributePaths(Selected select) {
        if (select instanceof SelectEntity selectEntity) {
            ImmutableList<PathNode> fetchNodes = selectEntity.fetch();
            if (fetchNodes != null && !fetchNodes.isEmpty() && expandReferencePath) {
                Collection<? extends Attribute> fetch = fetchNodes.stream()
                        .map(it -> it.getAttribute(entityType))
                        .collect(java.util.stream.Collectors.toList());
                return newJoinPaths(fetch);
            }
            return SchemaAttributePaths.empty();
        }
        if (select instanceof SelectProjection) {
            return DeepLimitSchemaAttributePaths.of(1);
        }
        return SchemaAttributePaths.empty();
    }

    /// 从 PathNode 列表构建 SchemaAttributePaths
    protected SchemaAttributePaths newJoinPaths(ImmutableList<PathNode> fetch) {
        DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
        for (PathNode pathNode : fetch) {
            paths.add(pathNode);
        }
        return paths;
    }

    /// 从 Attribute 集合构建 SchemaAttributePaths
    protected SchemaAttributePaths newJoinPaths(Collection<? extends Attribute> fetch) {
        DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
        for (Attribute strings : fetch) {
            paths.add(strings.path());
        }
        return paths;
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
        QueryContext context = newQueryContext(descriptor, structure);
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

    public static QueryContext newQueryContext(QueryConfig descriptor, QueryStructure structure) {
        QueryContext context = new QueryContext(descriptor);
        Selected select = structure.select();
        if (select instanceof SelectProjection selectProjection) {
            context.setSelectProjection(selectProjection);
        }
        return context;
    }

    public <T> List<T> getResultList() {
        return getQueryExecutor().getList(this);
    }

    public QueryContext newContext(QueryStructure structure) {
        QueryContext context = create(descriptor, structure);
        context.setExpandReferencePath(expandReferencePath);
        context.init();
        return context;
    }

    /// 获取 SELECT 子句列列表
    ///
    /// 直接返回 constructor 的列定义，保留 converter 和 tableIndex 信息。
    /// 对于投影查询，返回预计算的 expressions。
    ///
    /// @return Column 不可变数组
    public ImmutableArray<Column> getSelectedExpression() {
        if (expressions != null) {
            return expressions;
        }
        List<Column> columns = constructor.columns();
        return ImmutableList.ofCollection(columns);
    }

    /// 构造对象实例，委托给 ValueConstructor
    ///
    /// @param arguments 参数供应器
    /// @return 构造的对象实例
    protected Object doConstruct(Arguments arguments) {
        if (projection != null && projection.hasLazyAttribute()) {
            var selector = InterceptorSelector.selectConstructor(this);
            if (selector != null && selector.supports(this)) {
                return selector.intercept(this, arguments);
            }
            throw new UnsupportedOperationException(
                    "Lazy loading is not supported for projection type: " + projection.type().getName());
        }
        if (constructor != null) {
            return constructor.construct(arguments);
        }
        return constructSchema(projection, arguments, schemaAttributePaths);
    }

    public QueryStructure getStructure() {
        return this.structure;
    }

    public Metamodel getMetamodel() {
        return descriptor.metamodel();
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

    /// 构造对象实例
    ///
    /// 首先尝试使用拦截器创建对象，若无匹配拦截器则使用默认构造。
    ///
    /// @param arguments 参数供应器
    /// @return 构造的对象实例
    public final Object construct(Arguments arguments) {
        InterceptorSelector<ConstructInterceptor> constructs = descriptor.constructors();
        var interceptor = constructs.select(this);
        if (interceptor != null) {
            return interceptor.intercept(this, arguments);
        }
        return doConstruct(arguments);
    }

    protected Object constructSchema(Schema schema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        if (schema.type().isInterface()) {
            return constructInterfaceSchema(schema, arguments, schemaAttributes);
        } else if (schema.type().isRecord()) {
            return constructRecordSchema(schema, arguments, schemaAttributes);
        } else {
            return constructSimpleSchema(schema, arguments, schemaAttributes);
        }
    }

    /// 构造 Schema 对象（公开供拦截器使用）
    ///
    /// @param schema           Schema 定义
    /// @param arguments        参数供应器
    /// @param schemaAttributes 属性路径
    /// @return 构造的对象实例
    public Object buildSchema(Schema schema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        return constructSchema(schema, arguments, schemaAttributes);
    }

    /// 获取简单属性值（公开供拦截器使用）
    ///
    /// @param arguments 参数供应器
    /// @param attribute 属性定义
    /// @return 属性值
    public Object getAttributeValue(Arguments arguments, Attribute attribute) {
        return getSimpleAttributeValue(arguments, attribute);
    }

    protected Object constructRecordSchema(Schema schema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Object[] objects = getAttributeValues(schema, arguments, schemaAttributes);
        if (objects == null) {
            return null;
        }
        RecordComponent[] components = schema.type().getRecordComponents();
        Class<?>[] parameterTypes = new Class[components.length];
        for (int i = 0; i < components.length; i++) {
            parameterTypes[i] = components[i].getType();
        }
        Object[] args = new Object[components.length];
        ImmutableArray<? extends Attribute> attributes = schema.getAttributes();
        int i = 0;
        for (Attribute attribute : attributes) {
            args[attribute.accessor().ordinal()] = objects[i++];
        }
        try {
            Constructor<?> constructor = schema.type().getConstructor(parameterTypes);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            throw new ReflectiveException(e);
        }
    }

    public Map<Method, Object> collectResultMap(Arguments arguments) {
        if (projection != null) {
            return collectProjectionResultMap(arguments);
        }
        return collectResultMap(getEntityType(), arguments, getSchemaAttributePaths());
    }

    protected Object constructInterfaceSchema(Schema rootSchema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Map<Method, Object> map = collectResultMap(rootSchema, arguments, schemaAttributes);
        if (map == null) return null;
        return ReflectUtil.newProxyInstance(rootSchema.type(), map);
    }

    private Map<Method, Object> collectResultMap(Schema rootSchema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Map<Method, Object> map = new NullableConcurrentMap<>();
        for (Attribute attribute : rootSchema.getAttributes()) {
            if (attribute instanceof Schema schema) {
                var schemaAttributePaths = schemaAttributes == null ? null : schemaAttributes.get(attribute.name());
                if (schemaAttributePaths != null) {
                    Object value = constructSchema(schema, arguments, schemaAttributePaths);
                    map.put(attribute.getter(), value);
                }
            } else {
                ValueConverter<?, ?> convertor;
                if (attribute instanceof EntityBasicAttribute entityAttribute) {
                    convertor = entityAttribute.valueConvertor();
                } else {
                    convertor = IdentityValueConverter.of();
                }
                Object value = arguments.next(convertor);
                map.put(attribute.getter(), value);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    protected Object constructSimpleSchema(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        try {
            ImmutableArray<? extends Attribute> attributes = entityType.getAttributes();
            Object instance = null;
            for (Attribute attribute : attributes) {
                Object value = constructAttribute(attribute, arguments, schemaAttributes);
                if (value != null) {
                    if (instance == null) {
                        instance = entityType.type().getConstructor().newInstance();
                    }
                    attribute.set(instance, value);
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    private Object constructAttribute(Attribute attribute, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Object value = null;
        if (attribute instanceof Schema schema) {
            SchemaAttributePaths schemaAttributePaths = schemaAttributes.get(attribute.name());
            if (schemaAttributePaths != null) {
                value = constructSchema(schema, arguments, schemaAttributePaths);
            }
        } else {
            value = getSimpleAttributeValue(arguments, attribute);
        }
        return value;
    }

    protected Object[] getAttributeValues(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        ImmutableArray<? extends Attribute> attributes = entityType.getAttributes();
        int attributeSize = attributes.size();
        Object[] objects = null;
        for (int i = 0; i < attributeSize; i++) {
            Attribute attribute = attributes.get(i);
            Object value = constructAttribute(attribute, arguments, schemaAttributes);
            if (value != null) {
                if (objects == null) {
                    objects = new Object[attributeSize];
                }
                objects[i] = value;
            }
        }
        return objects;
    }

    protected Object constructSimpleSchema(Schema entityType, Arguments arguments) {
        try {
            ImmutableArray<? extends Attribute> attributes = entityType.getPrimitives();
            int attributeSize = attributes.size();
            Object result = null;
            for (int i = 0; i < attributeSize; i++) {
                Attribute attribute = attributes.get(i);
                Object value = getSimpleAttributeValue(arguments, attribute);
                if (value != null) {
                    if (result == null) {
                        result = entityType.type().getConstructor().newInstance();
                    }
                    attribute.set(result, value);
                }
            }
            return result;
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    protected Object getSimpleAttributeValue(Arguments arguments, Attribute attribute) {
        ValueConverter<?, ?> convertor = null;
        if (attribute instanceof EntityBasicAttribute entityAttribute) {
            convertor = entityAttribute.valueConvertor();
        }
        if (convertor == null) {
            convertor = IdentityValueConverter.of();
        }
        return arguments.next(convertor);
    }

    protected ImmutableArray<Column> getSelectPrimitiveExpressions(EntityType entityType, ExpressionNode expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof PathNode path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths).collect(ImmutableList.collector());
        }
        return ImmutableList.of(Column.ofExpressionNode(expression, 0));
    }

    protected Stream<Column> stream(EntityType entityType, ExpressionNode expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof PathNode path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths);
        }
        return Stream.of(Column.ofExpressionNode(expression, 0));
    }

    protected ImmutableArray<Column> getSelectSchemaExpressions(Schema schema, SchemaAttributePaths schemaAttributePaths) {
        ImmutableArray<? extends Attribute> attributes = schema.getAttributes();
        return attributes.stream()
                .flatMap(it -> stream(it, schemaAttributePaths))
                .collect(ImmutableList.collector());
    }

    protected Stream<Column> stream(Attribute attribute, SchemaAttributePaths schemaAttributePaths) {
        if (attribute instanceof EntityBasicAttribute expression) {
            return Stream.of(Column.fromEntityAttribute(expression, 0));
        } else if (attribute instanceof ProjectionBasicAttribute expression) {
            return Stream.of(Column.fromProjectionBasicAttribute(expression, 0));
        } else if (attribute instanceof Schema schema) {
            SchemaAttributePaths sub = schemaAttributePaths.get(attribute.name());
            if (sub != null) {
                return schema.getAttributes().stream()
                        .flatMap(subAttr -> stream(subAttr, sub));
            }
        }
        return Stream.empty();
    }

    protected Object constructExpression(EntityType entityType, Arguments arguments, Object expression) {
        if (expression instanceof PathNode path) {
            expression = entityType.getAttribute(path);
        } else if (expression instanceof Expression<?, ?> e) {
            expression = ExpressionNodes.getNode(e);
        }
        if (expression instanceof Schema) {
            return constructSchema((Schema) expression, arguments, SchemaAttributePaths.empty());
        } else if (expression instanceof EntityBasicAttribute attribute) {
            ValueConverter<?, ?> valueConvertor = attribute.valueConvertor();
            return arguments.next(valueConvertor);
        } else if (expression instanceof ExpressionNode node) {
            Class<?> expressionType = ExpressionTypeResolver.getExpressionType(node, entityType);
            return arguments.next(new IdentityValueConverter<>(expressionType));
        } else {
            return arguments.next(IdentityValueConverter.of());
        }
    }

    public QueryExecutor getQueryExecutor() {
        return descriptor.queryExecutor();
    }

    public FetchConfig getFetchConfig() {
        return descriptor.fetch();
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

    public SchemaAttributePaths getSchemaAttributePaths() {
        return schemaAttributePaths;
    }

    /// 是否启用懒加载拦截
    ///
    /// 仅在投影查询中默认启用。
    ///
    /// @return true 表示启用懒加载
    public boolean isEnableLazyloading() {
        return enableLazyloading;
    }

    public void setEnableLazyloading(boolean enableLazyloading) {
        this.enableLazyloading = enableLazyloading;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    /// 获取值构造器
    ///
    /// @return 当前 ValueConstructor 实例
    public ValueConstructor getConstructor() {
        return constructor;
    }

    /// 获取所有列的便捷方法
    ///
    /// @return 列列表
    public List<Column> getColumns() {
        return constructor.columns();
    }

    /// 设置投影查询类型
    ///
    /// @param selectProjection 投影查询定义
    public void setSelectProjection(SelectProjection selectProjection) {
        this.selectProjection = selectProjection;
        this.enableLazyloading = true;
    }

    /// 分离投影属性，构建列列表
    ///
    /// @param schema 投影 Schema
    /// @param paths  属性路径
    /// @return 列列表
    private ImmutableArray<Column> separateAttributes(ProjectionSchema schema, SchemaAttributePaths paths) {
        List<Column> eagerList = new ArrayList<>();
        for (ProjectionAttribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionBasicAttribute basicAttr) {
                eagerList.add(Column.fromProjectionBasicAttribute(basicAttr, 0));
            } else if (attr instanceof ProjectionSchemaAttribute schemaAttr) {
                FetchType fetchType = schemaAttr.getFetchType();
                if (fetchType == FetchType.LAZY) {
                    eagerList.add(Column.fromEntityAttribute(schemaAttr.getEntityAttribute().getSourceAttribute(), 0));
                } else {
                    SchemaAttributePaths subPaths = paths.get(attr.name());
                    if (subPaths != null) {
                        streamProjectionSchema(schemaAttr, subPaths).forEach(eagerList::add);
                    }
                }
            }
        }
        return ImmutableList.ofCollection(eagerList);
    }

    private Stream<Column> streamProjectionSchema(ProjectionSchemaAttribute schemaAttr, SchemaAttributePaths paths) {
        return schemaAttr.getAttributes().stream()
                .flatMap(attr -> {
                    if (attr instanceof ProjectionBasicAttribute basicAttr) {
                        return Stream.of(Column.fromProjectionBasicAttribute(basicAttr, 0));
                    } else if (attr instanceof ProjectionSchemaAttribute nestedSchemaAttr) {
                        if (nestedSchemaAttr.getFetchType() == FetchType.LAZY) {
                            return Stream.empty();
                        }
                        SchemaAttributePaths subPaths = paths.get(attr.name());
                        if (subPaths != null) {
                            return streamProjectionSchema(nestedSchemaAttr, subPaths);
                        }
                    }
                    return Stream.empty();
                });
    }

    /// 收集投影查询的结果映射（支持懒加载属性）
    ///
    /// @param arguments 参数供应器
    /// @return 结果映射
    public Map<Method, Object> collectProjectionResultMap(Arguments arguments) {
        ProjectionSchema schema = projection;
        SchemaAttributePaths paths = schemaAttributePaths;
        Map<Method, Object> data = new NullableConcurrentMap<>();
        for (Attribute attr : schema.getAttributes()) {
            if (attr instanceof ProjectionSchemaAttribute schemaAttr
                && schemaAttr.getFetchType() == FetchType.LAZY) {
                Object foreignKey = getSimpleAttributeValue(arguments, attr);
                data.put(attr.getter(), createLazyLoader(schemaAttr, foreignKey));
                continue;
            }
            SchemaAttributePaths subPaths = paths.get(attr.name());
            if (subPaths != null) {
                if (attr instanceof Schema nestedSchema) {
                    Object value = constructSchema(nestedSchema, arguments, subPaths);
                    data.put(attr.getter(), value);
                } else {
                    Object value = getSimpleAttributeValue(arguments, attr);
                    data.put(attr.getter(), value);
                }
            }
        }
        return data;
    }

    private AttributeLoader createLazyLoader(ProjectionSchemaAttribute attribute, Object foreignKey) {
        return getBatchLoaderContext(attribute).getAttributeLoader(foreignKey);
    }

    private BatchAttributeLoader getBatchLoaderContext(ProjectionSchemaAttribute attribute) {
        return batchLoaderContexts.computeIfAbsent(attribute, k -> new BatchAttributeLoader(k, this));
    }

    /// 获取投影 Schema
    ///
    /// @return 投影 Schema，如果不是投影查询则返回 null
    @Nullable
    public ProjectionSchema getProjection() {
        return projection;
    }

}
