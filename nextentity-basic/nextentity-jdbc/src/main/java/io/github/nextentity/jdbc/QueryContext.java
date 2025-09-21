package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.SelectItem;
import io.github.nextentity.core.exception.BeanReflectiveException;
import io.github.nextentity.core.expression.*;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author HuangChengwei
 * @since 2024/4/20 下午12:03
 */
public abstract class QueryContext {

    protected final QueryStructure structure;
    protected final Metamodel metamodel;
    protected final EntityType entityType;
    protected final boolean expandReferencePath;

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

    protected QueryContext(QueryStructure structure, Metamodel metamodel, boolean expandObjectAttribute) {
        this.structure = structure;
        this.metamodel = metamodel;
        this.expandReferencePath = expandObjectAttribute;
        From from = structure.from();
        this.entityType = from instanceof FromEntity fromEntity ? metamodel.getEntity(fromEntity.type()) : null;
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
            throw new BeanReflectiveException(e);
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
                ValueConvertor<?, ?> convertor = null;
                if (attribute instanceof DatabaseColumnAttribute entityAttribute) {
                    convertor = entityAttribute.valueConvertor();
                } else {
                    convertor = IdentityValueConvertor.of();
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
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new BeanReflectiveException(e);
        }
    }

    private @Nullable Object constructAttribute(Attribute attribute, Arguments arguments, SchemaAttributePaths schemaAttributes) {
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

    protected Object @Nullable [] getAttributeValues(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
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
            throw new BeanReflectiveException(e);
        }
    }

    protected Object getSimpleAttributeValue(Arguments arguments, Attribute attribute) {
        ValueConvertor<?, ?> convertor = null;
        if (attribute instanceof DatabaseColumnAttribute entityAttribute) {
            convertor = entityAttribute.valueConvertor();
        }
        if (convertor == null) {
            convertor = IdentityValueConvertor.of();
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
        if (expression instanceof Schema) {
            return constructSchema((Schema) expression, arguments, SchemaAttributePaths.empty());
        } else if (expression instanceof DatabaseColumnAttribute attribute) {
            ValueConvertor<?, ?> valueConvertor = attribute.valueConvertor();
            return arguments.next(valueConvertor);
        } else if (expression instanceof Expression e) {
            ExpressionNode node = ExpressionNodes.getNode(e);
            Class<?> expressionType = ExpressionTypeResolver.getExpressionType(node, entityType);
            return arguments.next(new IdentityValueConvertor(expressionType));
        } else {
            return arguments.next(IdentityValueConvertor.of());
        }
    }
}
