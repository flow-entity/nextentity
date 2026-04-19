package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.QueryExecutor;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.InterceptorSelector;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.meta.impl.IdentityValueConverter;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.ResultMap;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class QueryContext {

    protected final QueryStructure structure;
    protected final Metamodel metamodel;
    protected final EntityType entityType;
    protected final boolean expandReferencePath;
    protected QueryExecutor queryExecutor;
    protected FetchConfig fetchConfig = FetchConfig.DEFAULT;

    /// 拦截器选择器（用于对象构造）
    protected InterceptorSelector<ConstructInterceptor> interceptorSelector = InterceptorSelector.empty();

    /// 查询结果列表（在 resolve 完成后设置）
    private List<?> results;

    public static QueryContext create(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        Selected select = structure.select();
        if (select instanceof SelectEntity selectEntity) {
            ImmutableList<PathNode> fetch = selectEntity.fetch();
            if (fetch != null && !fetch.isEmpty() && expandObjectAttribute) {
                Collection<? extends Attribute> attributes = fetch
                        .stream()
                        .map(it -> it.getAttribute(metamodel.getEntity(((FromEntity) structure.from()).type())))
                        .toList();
                return new SelectEntityContext(executor, structure, metamodel, attributes);
            } else {
                return new SelectSimpleEntityContext(executor, structure, metamodel, expandObjectAttribute);
            }
        } else if (select instanceof SelectProjection selectProjection) {
            return new SelectProjectionContext(executor, structure, metamodel, expandObjectAttribute, selectProjection);
        } else if (select instanceof SelectExpression selectPrimitive) {
            return new SelectPrimitiveContext(executor, structure, metamodel, expandObjectAttribute, selectPrimitive);
        } else if (select instanceof SelectExpressions selectArray) {
            return new SelectArrayContext(executor, structure, metamodel, expandObjectAttribute, selectArray);
        } else if (select instanceof SelectNested selectNested) {
            return new SelectNestedContext(executor, structure, metamodel, expandObjectAttribute, selectNested);
        }
        throw new IllegalArgumentException("Unknown select type: " + select.getClass().getName());
    }

    protected QueryContext(QueryExecutor executor, QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        this.queryExecutor = executor;
        this.structure = structure;
        this.metamodel = metamodel;
        this.expandReferencePath = expandObjectAttribute;
        From from = structure.from();
        this.entityType = from instanceof FromEntity(Class<?> type) ? metamodel.getEntity(type) : null;
    }

    public QueryContext newContext(QueryStructure structure) {
        return create(queryExecutor, structure, metamodel, expandReferencePath);
    }

    public abstract ImmutableArray<SelectItem> getSelectedExpression();

    protected SchemaAttributePaths newJoinPaths(Collection<? extends Attribute> fetch) {
        DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
        for (Attribute strings : fetch) {
            paths.add(strings.path());
        }
        return paths;
    }

    protected abstract Object doConstruct(Arguments arguments);

    public QueryStructure getStructure() {
        return this.structure;
    }

    public Metamodel getMetamodel() {
        return this.metamodel;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    /// 获取当前构造的 Schema
    ///
    /// 用于拦截器判断是否支持处理当前场景。
    /// 子类根据实际情况返回对应的 Schema：
    /// - SelectProjectionContext 返回 projection
    /// - SelectEntityContext 返回 entityType
    /// - 其他子类可能返回 null
    ///
    /// @return 当前构造的 Schema，如果没有则返回 null
    @Nullable
    public Schema getSchema() {
        return entityType;
    }

    /// 获取拦截器选择器
    ///
    /// @return 拦截器选择器
    public InterceptorSelector<ConstructInterceptor> getInterceptorSelector() {
        return interceptorSelector;
    }

    /// 设置拦截器选择器
    ///
    /// @param interceptorSelector 拦截器选择器
    public void setInterceptorSelector(InterceptorSelector<ConstructInterceptor> interceptorSelector) {
        this.interceptorSelector = interceptorSelector != null
                ? interceptorSelector
                : InterceptorSelector.empty();
    }

    /// 构造对象实例
    ///
    /// 首先尝试使用拦截器创建对象，若无匹配拦截器则使用默认构造。
    ///
    /// @param arguments 参数供应器
    /// @return 构造的对象实例
    public final Object construct(Arguments arguments) {
        var interceptor = interceptorSelector.select(this);
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

    protected Object constructInterfaceSchema(Schema rootSchema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        ResultMap map = new ResultMap();
        for (Attribute attribute : rootSchema.getAttributes()) {
            if (attribute instanceof Schema schema) {
                SchemaAttributePaths schemaAttributePaths = schemaAttributes.get(attribute.name());
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
        return ReflectUtil.newProxyInstance(rootSchema.type(), map);
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

    protected Object [] getAttributeValues(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
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

    protected ImmutableArray<SelectItem> getSelectPrimitiveExpressions(EntityType entityType, ExpressionNode expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof PathNode path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths).collect(ImmutableList.collector());
        }
        return ImmutableList.of((SelectItem) expression);
    }

    protected Stream<SelectItem> stream(EntityType entityType, ExpressionNode expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof PathNode path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths);
        }
        return Stream.of((SelectItem) expression);
    }

    protected ImmutableArray<SelectItem> getSelectSchemaExpressions(Schema schema, SchemaAttributePaths schemaAttributePaths) {
        ImmutableArray<? extends Attribute> attributes = schema.getAttributes();
        return attributes.stream()
                .flatMap(it -> stream(it, schemaAttributePaths))
                .collect(ImmutableList.collector());
    }

    protected Stream<SelectItem> stream(Attribute attribute, SchemaAttributePaths schemaAttributePaths) {
        if (attribute instanceof EntityBasicAttribute expression) {
            return Stream.of(expression);
        } else if (attribute instanceof ProjectionBasicAttribute expression) {
            return Stream.of(expression.source());
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
        return queryExecutor;
    }

    public void setQueryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    public FetchConfig getFetchConfig() {
        return fetchConfig;
    }

    public void setFetchConfig(FetchConfig fetchConfig) {
        this.fetchConfig = fetchConfig;
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
}
