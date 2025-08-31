package io.github.nextentity.jdbc;

import io.github.nextentity.api.Expression;
import io.github.nextentity.core.ExpressionTypeResolver;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.ProjectionAttribute;
import io.github.nextentity.core.meta.ValueConvertor;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.ImmutableArray;
import io.github.nextentity.core.util.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

interface SelectedContext {

    static Object constructSchema(Schema schema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        if (schema.type().isInterface()) {
            return constructInterfaceSchema(schema, arguments, schemaAttributes);
        } else if (schema.type().isRecord()) {
            return constructRecordSchema(schema, arguments, schemaAttributes);
        } else {
            return constructSimpleSchema(schema, arguments, schemaAttributes);
        }
    }

    static Object constructRecordSchema(Schema schema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static Object constructInterfaceSchema(Schema rootSchema, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Map<Method, Object> map = new HashMap<>();
        for (Attribute attribute : rootSchema.attributes()) {
            if (attribute instanceof Schema schema) {
                SchemaAttributePaths schemaAttributePaths = schemaAttributes.get(attribute.name());
                if (schemaAttributePaths != null) {
                    Object value = constructSchema(schema, arguments, schemaAttributePaths);
                    map.put(attribute.getter(), value);
                }
            } else {
                Class<?> type;
                if (attribute instanceof EntityAttribute entityAttribute) {
                    type = entityAttribute.valueConvertor().getDatabaseType();
                } else if (attribute instanceof ProjectionAttribute projectionAttribute) {
                    type = projectionAttribute.source().valueConvertor().getDatabaseType();
                } else {
                    type = attribute.type();
                }
                Object value = arguments.next(type);
                map.put(attribute.getter(), value);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        return ReflectUtil.newProxyInstance(rootSchema.type(), map);
    }

    private static Object constructSimpleSchema(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        try {
            Object[] objects = getAttributeValues(entityType, arguments, schemaAttributes);
            if (objects != null) {
                Object result = entityType.type().getConstructor().newInstance();
                for (int i = 0; i < objects.length; i++) {
                    entityType.attributes().get(i).set(result, objects[i]);
                }
                return result;
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static Object @Nullable [] getAttributeValues(Schema entityType, Arguments arguments, SchemaAttributePaths schemaAttributes) {
        Attributes attributes = entityType.attributes();
        int attributeSize = attributes.size();
        Object[] objects = null;
        for (int i = 0; i < attributeSize; i++) {
            Attribute attribute = attributes.get(i);
            Object value = null;
            if (attribute instanceof Schema schema) {
                SchemaAttributePaths schemaAttributePaths = schemaAttributes.get(attribute.name());
                if (schemaAttributePaths != null) {
                    value = constructSchema(schema, arguments, schemaAttributePaths);
                }
            } else {
                Class<?> type;
                if (attribute instanceof EntityAttribute entityAttribute) {
                    type = entityAttribute.valueConvertor().getDatabaseType();
                } else if (attribute instanceof ProjectionAttribute projectionAttribute) {
                    type = projectionAttribute.source().valueConvertor().getDatabaseType();
                } else {
                    type = attribute.type();
                }
                value = arguments.next(type);
            }
            if (value != null) {
                if (objects == null) {
                    objects = new Object[attributeSize];
                }
                objects[i] = value;
            }
        }
        return objects;
    }


    static Object constructExpression(EntityType entityType, Arguments arguments, Object expression) {
        if (expression instanceof Schema) {
            return SelectedContext.constructSchema((Schema) expression, arguments, SchemaAttributePaths.empty());
        } else if (expression instanceof EntityAttribute attribute) {
            ValueConvertor valueConvertor = attribute.valueConvertor();
            Object value = arguments.next(valueConvertor.getDatabaseType());
            return valueConvertor.toAttributeValue(value);
        } else if (expression instanceof Expression e) {
            return arguments.next(ExpressionTypeResolver.getExpressionType(e, entityType));
        } else {
            return arguments.next(Object.class);
        }
    }

    ImmutableArray<Expression> expressions();

    Object construct(Arguments arguments);

    static ImmutableArray<Expression> getSelectPrimitiveExpressions(EntityType entityType, Expression expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof InternalPathExpression path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths).collect(ImmutableList.collector());
        }
        return ImmutableList.of(expression);
    }

    static Stream<Expression> stream(EntityType entityType, Expression expression, SchemaAttributePaths schemaAttributePaths) {
        if (expression instanceof InternalPathExpression path) {
            Attribute attribute = entityType.getAttribute(path);
            return stream(attribute, schemaAttributePaths);
        }
        return Stream.of(expression);
    }

    static ImmutableArray<Expression> getSelectSchemaExpressions(Schema schema, SchemaAttributePaths schemaAttributePaths) {
        return schema.attributes().stream()
                .flatMap(it -> stream(it, schemaAttributePaths))
                .collect(ImmutableList.collector());
    }

    static Stream<Expression> stream(Attribute attribute, SchemaAttributePaths schemaAttributePaths) {
        if (attribute instanceof Expression expression) {
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
}
