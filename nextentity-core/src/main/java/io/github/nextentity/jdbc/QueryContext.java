package io.github.nextentity.jdbc;

import io.github.nextentity.api.*;
import io.github.nextentity.api.EntityReference;
import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    protected ExtensionRegistry extensionRegistry;  /// 扩展点注册中心（可选）

    public static QueryContext create(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        Selected select = structure.select();
        if (select instanceof SelectEntity selectEntity) {
            ImmutableList<PathNode> fetch = selectEntity.fetch();
            if (fetch != null && !fetch.isEmpty() && expandObjectAttribute) {
                Collection<? extends Attribute> attributes = fetch
                        .stream()
                        .map(it -> it.getAttribute(metamodel.getEntity(((FromEntity) structure.from()).type())))
                        .toList();
                return new SelectEntityContext(structure, metamodel, attributes);
            } else {
                return new SelectSimpleEntityContext(structure, metamodel, expandObjectAttribute);
            }
        } else if (select instanceof SelectProjection selectProjection) {
            return new SelectProjectionContext(structure, metamodel, expandObjectAttribute, selectProjection);
        } else if (select instanceof SelectExpression selectPrimitive) {
            return new SelectPrimitiveContext(structure, metamodel, expandObjectAttribute, selectPrimitive);
        } else if (select instanceof SelectExpressions selectArray) {
            return new SelectArrayContext(structure, metamodel, expandObjectAttribute, selectArray);
        }
        throw new IllegalArgumentException("Unknown select type: " + select.getClass().getName());
    }

    /// 设置扩展点注册中心。
    ///
    /// @param extensionRegistry 扩展点注册中心
    public void setExtensionRegistry(ExtensionRegistry extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    /// 获取扩展点注册中心。
    ///
    /// @return ExtensionRegistry 实例，可能为 null
    public ExtensionRegistry getExtensionRegistry() {
        return extensionRegistry;
    }

    protected QueryContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        this.structure = structure;
        this.metamodel = metamodel;
        this.expandReferencePath = expandObjectAttribute;
        From from = structure.from();
        this.entityType = from instanceof FromEntity(Class<?> type) ? metamodel.getEntity(type) : null;
    }

    public QueryContext newContext(QueryStructure structure) {
        return create(structure, metamodel, expandReferencePath);
    }

    public abstract ImmutableArray<SelectItem> getSelectedExpression();

    protected SchemaAttributePaths newJoinPaths(Collection<? extends Attribute> fetch) {
        DefaultSchemaAttributePaths paths = new DefaultSchemaAttributePaths();
        for (Attribute strings : fetch) {
            paths.add(strings.path());
        }
        return paths;
    }

    public abstract Object construct(Arguments arguments);

    public QueryStructure getStructure() {
        return this.structure;
    }

    public Metamodel getMetamodel() {
        return this.metamodel;
    }

    public EntityType getEntityType() {
        return this.entityType;
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
        Attributes attributes = schema.attributes();
        int i = 0;
        for (Attribute attribute : attributes) {
            args[attribute.ordinal()] = objects[i++];
        }
        try {
            return schema.type().getConstructor(parameterTypes).newInstance(args);
        } catch (ReflectiveOperationException e) {
            throw new ReflectiveException(e);
        }
    }

    protected Object constructInterfaceSchema(Schema rootSchema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Map<Method, Object> map = new HashMap<>();
        for (Attribute attribute : rootSchema.attributes()) {
            if (attribute instanceof Schema schema) {
                SchemaAttributePaths schemaAttributePaths = schemaAttributes.get(attribute.name());
                if (schemaAttributePaths != null) {
                    Object value = constructSchema(schema, arguments, schemaAttributePaths);
                    map.put(attribute.getter(), value);
                }
            } else {
                ValueConverter<?, ?> convertor;
                if (attribute instanceof DatabaseColumnAttribute entityAttribute) {
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
            Attributes attributes = entityType.attributes();
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
            // 后处理：注入 EntityReference 延迟加载器
            if (instance != null && extensionRegistry != null) {
                postProcessEntityReferences(instance, entityType, attributes);
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
        } else if (attribute instanceof ReferenceAttribute refAttr) {
            // EntityReference 字段：创建引用实例并设置 ID
            value = constructEntityReference(refAttr, arguments);
        } else {
            value = getSimpleAttributeValue(arguments, attribute);
        }
        return value;
    }

    /// 构造 EntityReference 实例。
    ///
    /// 从 Arguments 提取 ID，创建 EntityReference 子类实例并设置 ID。
    private Object constructEntityReference(ReferenceAttribute refAttr, Arguments arguments) {
        // 提取 ID 值
        Object id = arguments.next(new IdentityValueConverter(refAttr.idType()));

        if (id == null) {
            return null;
        }

        // 创建 EntityReference 实例
        try {
            @SuppressWarnings("rawtypes")
            EntityReference ref = (EntityReference) refAttr.type().getDeclaredConstructor().newInstance();
            ref.setId(id);
            return ref;
        } catch (Exception e) {
            throw new ReflectiveException("Failed to create EntityReference instance: " + refAttr.type().getName(), e);
        }
    }

    protected Object [] getAttributeValues(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Attributes attributes = entityType.attributes();
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
            ImmutableArray<Attribute> attributes = entityType.attributes().getPrimitives();
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
        if (attribute instanceof DatabaseColumnAttribute entityAttribute) {
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
        return schema.attributes().stream()
                .flatMap(it -> stream(it, schemaAttributePaths))
                .collect(ImmutableList.collector());
    }

    protected Stream<SelectItem> stream(Attribute attribute, SchemaAttributePaths schemaAttributePaths) {
        if (attribute instanceof EntityAttribute expression) {
            return Stream.of(expression);
        } else if (attribute instanceof ProjectionAttribute expression) {
            return Stream.of(expression.source());
        } else if (attribute instanceof ReferenceAttribute refAttr) {
            // EntityReference 字段：返回 ID 来源属性作为 SelectItem
            Attribute sourceAttr = refAttr.sourceAttribute();
            if (sourceAttr instanceof EntityAttribute entityAttr) {
                return Stream.of(entityAttr);
            } else if (sourceAttr instanceof SimpleEntityAttribute entityAttr) {
                return Stream.of(entityAttr);
            } else if (sourceAttr != null) {
                // 嵌套路径场景
                return stream(sourceAttr, schemaAttributePaths);
            }
            // 如果 sourceAttribute 未设置，尝试从实体获取 ID 字段
            if (entityType != null) {
                Attribute idAttr = entityType.getAttribute(refAttr.idSourcePath());
                if (idAttr instanceof EntityAttribute entityAttr) {
                    return Stream.of(entityAttr);
                }
            }
            return Stream.empty();
        } else if (attribute instanceof Schema schema) {
            SchemaAttributePaths sub = schemaAttributePaths.get(attribute.name());
            if (sub != null) {
                return schema.attributes().stream()
                        .flatMap(subAttr -> stream(subAttr, sub));
            }
        }
        return Stream.empty();
    }

    protected Object constructExpression(EntityType entityType, Arguments arguments, Object expression) {
        if (expression instanceof PathNode path) {
            expression = entityType.getAttribute(path);
        } else if (expression instanceof Expression e) {
            expression = ExpressionNodes.getNode(e);
        }
        if (expression instanceof Schema) {
            return constructSchema((Schema) expression, arguments, SchemaAttributePaths.empty());
        } else if (expression instanceof DatabaseColumnAttribute attribute) {
            ValueConverter<?, ?> valueConvertor = attribute.valueConvertor();
            return arguments.next(valueConvertor);
        } else if (expression instanceof ExpressionNode node) {
            Class<?> expressionType = ExpressionTypeResolver.getExpressionType(node, entityType);
            return arguments.next(new IdentityValueConverter(expressionType));
        } else {
            return arguments.next(IdentityValueConverter.of());
        }
    }

    /// 后处理 EntityReference 字段，注入延迟加载器。
    ///
    /// 在构造完成后遍历所有 ReferenceAttribute 字段，
    /// 通过 ExtensionRegistry 获取 EntityFetcher 并设置 loader。
    private void postProcessEntityReferences(Object instance, Schema schema, Attributes attributes) {
        if (extensionRegistry == null || extensionRegistry.getEntityFetcher() == null) {
            return;
        }

        EntityFetcher fetcher = extensionRegistry.getEntityFetcher();

        for (Attribute attribute : attributes) {
            if (attribute instanceof ReferenceAttribute refAttr) {
                try {
                    Object value = attribute.get(instance);
                    if (value instanceof EntityReference<?, ?> ref && ref.getId() != null) {
                        // 注入延迟加载器
                        Class<?> targetType = refAttr.targetType();
                        Object id = ref.getId();
                        @SuppressWarnings("unchecked")
                        java.util.function.Supplier<Object> loader = () -> {
                            @SuppressWarnings("rawtypes")
                            Optional opt = fetcher.fetch(targetType, id);
                            return opt.orElse(null);
                        };
                        ref.setLoader(loader);
                    }
                } catch (Exception e) {
                    // 忽略获取失败
                }
            }
        }
    }

    /// 创建 ProjectionContext 用于后处理。
    private ProjectionContext createProjectionContext(EntityFetcher fetcher) {
        return new ProjectionContext() {
            @Override
            public EntityFetcher entityFetcher() {
                return fetcher;
            }

            @Override
            public EntityType entityType() {
                return QueryContext.this.entityType;
            }

            @Override
            public <ID> Collection<?> fetchEntities(Class<?> entityType, Collection<ID> ids) {
                return fetcher.fetchBatch(entityType, ids).values();
            }

            @Override
            public <T, ID> java.util.function.Supplier<T> createLoader(Class<T> entityType, ID id) {
                return () -> fetcher.<T, ID>fetch(entityType, id).orElse(null);
            }
        };
    }
}
