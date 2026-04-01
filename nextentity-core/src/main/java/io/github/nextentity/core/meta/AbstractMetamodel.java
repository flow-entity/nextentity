package io.github.nextentity.core.meta;

import io.github.nextentity.core.PathReference;
import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.core.annotation.SubSelect;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.reflect.schema.*;
import io.github.nextentity.core.util.ImmutableList;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

///
/// Abstract base class for metamodel implementations.
///
/// This class provides the core functionality for building entity type metadata
/// from Java classes, including attribute discovery, type conversion, and
/// projection support.
///
/// Subclasses implement abstract methods to provide annotation-specific
/// metadata extraction for different persistence frameworks (JPA, JDBC, etc.).
///
/// @author HuangChengwei
/// @since 1.0.0
///
public abstract class AbstractMetamodel implements Metamodel {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AbstractMetamodel.class);
    private final Map<Class<?>, EntityType> entityTypes = new ConcurrentHashMap<>();

    ///
    /// Gets the entity type metadata for the specified class, caching the result.
    ///
    /// @param entityType the entity class
    /// @return the cached or newly created entity type metadata
    ///
    @Override
    public EntityType getEntity(Class<?> entityType) {
        return entityTypes.computeIfAbsent(entityType, this::createEntityType);
    }

    @NonNull
    protected ProjectionType createProjection(EntityType entity, Class<?> projectionType) {
        SimpleProjection result = new SimpleProjection(projectionType, entity);
        Attributes projectionProperties = getProjectionProperties(result);
        result.setAttributes(projectionProperties);
        return result;
    }

    protected Attributes getProjectionProperties(ProjectionType projectionType) {
        List<Attribute> attributes = new ArrayList<>();
        List<Attribute> javaAttributes = getJavaAttributes(projectionType);
        Schema entity = projectionType.source();
        for (Attribute javaAttribute : javaAttributes) {
            Attribute entityAttribute = getEntityAttribute(javaAttribute, entity);
            if (entityAttribute == null) {
                continue;
            }
            if (entityAttribute instanceof JoinAttribute joinAttribute) {
                SimpleProjectionJoinAttribute attribute = new SimpleProjectionJoinAttribute(joinAttribute, this::getProjectionProperties);
                attribute.setAttribute(javaAttribute);
                attribute.declareBy(projectionType);
                attributes.add(attribute);
            } else if (javaAttribute.type() == entityAttribute.type()) {
                SimpleProjectionAttribute attribute = new SimpleProjectionAttribute((EntityAttribute) entityAttribute);
                attribute.setAttribute(javaAttribute);
                attribute.declareBy(projectionType);
                attributes.add(attribute);
            }
        }
        return new SimpleAttributes(attributes);
    }

    private List<Attribute> getJavaAttributes(Schema owner) {
        Class<?> projectionType = owner.type();
        if (projectionType.isInterface()) {
            return getInterfaceAttributes(projectionType, owner);
        } else if (projectionType.isRecord()) {
            return getRecordAttributes(projectionType, owner);
        }
        return getBeanAttributes(projectionType, owner);
    }

    private List<Attribute> getRecordAttributes(Class<?> projectionType, Schema owner) {
        RecordComponent[] components = projectionType.getRecordComponents();
        AtomicInteger ordinal = new AtomicInteger();
        return Arrays.stream(components)
                .map(it -> newAttribute(null, it.getAccessor(), null, owner, ordinal.getAndIncrement()))
                .collect(ImmutableList.collector(components.length));
    }

    protected Attribute getEntityAttribute(Attribute attribute, Schema entity) {
        EntityAttribute entityAttribute = getEntityAttributeByAnnotation(attribute, entity);
        return entityAttribute == null ? entity.getAttribute(attribute.name()) : entityAttribute;
    }

    private EntityAttribute getEntityAttributeByAnnotation(Attribute attribute, Schema entity) {
        EntityPath entityPath = getAnnotation(attribute, EntityPath.class);
        if (entityPath == null || entityPath.value().isEmpty()) {
            return null;
        }
        String value = entityPath.value();
        String[] split = value.split("\\.");
        ReflectType cur = entity;
        for (String s : split) {
            if (cur instanceof Schema) {
                cur = ((Schema) cur).getAttribute(s);
            } else {
                throw new IllegalStateException("entity attribute " + value + " not exist");
            }
        }
        if (cur instanceof EntityAttribute) {
            if (attribute.type() != cur.type()) {
                throw new IllegalStateException("entity attribute " + value + " type mismatch");
            }
            return (EntityAttribute) cur;
        } else {
            throw new IllegalStateException("entity attribute " + value + " not exist");
        }
    }

    @NonNull
    private List<Attribute> getInterfaceAttributes(Class<?> clazz, Schema owner) {
        Method[] methods = clazz.getMethods();
        AtomicInteger ordinal = new AtomicInteger();
        return Arrays.stream(methods)
                .map(it -> newAttribute(null, it, null, owner, ordinal.getAndIncrement()))
                .collect(ImmutableList.collector(methods.length));
    }

    protected abstract String getTableName(Class<?> javaType);

    protected abstract boolean isMarkedId(Attribute attribute);

    protected abstract boolean isUpdatable(Attribute attribute);

    protected abstract String getReferencedColumnName(Attribute attribute);

    protected abstract String getJoinColumnName(Attribute attribute);

    protected abstract boolean isVersionField(Attribute attribute);

    protected abstract boolean isTransient(Attribute attribute);

    protected abstract boolean isBasicField(Attribute attribute);

    protected abstract boolean isAnyToOne(Attribute attribute);

    protected abstract String getColumnName(Attribute attribute);

    protected abstract Field[] getSuperClassField(Class<?> baseClass, Class<?> superClass);

    protected ValueConverter<?, ?> databaseType(Attribute attribute) {
        Class<?> type = attribute.type();
        if (type.isEnum()) {
            return new EnumConverter<>(attribute);
        } else if (type == Instant.class) {
            return InstantConverter.of();
        } else {
            return new IdentityValueConverter(type);
        }
    }

    protected EntityType createEntityType(Class<?> entityType) {
        SimpleEntity result;
        SubSelect[] type = entityType.getAnnotationsByType(SubSelect.class);
        String tableName = getTableName(entityType);

        if (type.length == 1) {
            result = new SubQueryEntity(entityType, tableName, this::createProjection, type[0].value());
        } else {
            result = new SimpleEntity(entityType, tableName, this::createProjection);
        }
        Attributes attributes = getAttributes(result);
        result.setAttributes(attributes);
        return result;
    }

    protected Attributes getAttributes(Schema owner) {
        Class<?> entityType = owner.type();
        List<Attribute> beanAttributes = getBeanAttributes(entityType, owner);
        List<Attribute> attributes = new ArrayList<>(beanAttributes.size());
        boolean hasVersion = false;
        for (Attribute attr : beanAttributes) {
            if (isTransient(attr)) {
                continue;
            }
            if (isBasicField(attr)) {
                boolean versionColumn = false;
                if (isVersionField(attr)) {
                    if (hasVersion) {
                        log.warn("duplicate attributes: {}, ignored", attr.name());
                    } else {
                        versionColumn = hasVersion = true;
                    }
                }
                SimpleEntityAttribute attribute = new SimpleEntityAttribute();
                attribute.setAttribute(attr);
                attribute.setColumnName(getColumnName(attr));
                attribute.setValueConverter(databaseType(attr));
                attribute.setVersion(versionColumn);
                boolean isMarkedId = isMarkedId(attribute);
                if (isMarkedId || "id".equals(attr.name())) {
                    attribute.setId(true);
                    attribute.setUpdatable(false);
                } else {
                    attribute.setUpdatable(isUpdatable(attribute));
                }
                attributes.add(attribute);
            } else if (isAnyToOne(attr)) {
                SimpleJoinAttribute attribute = new SimpleJoinAttribute(this::getAttributes);
                attribute.setAttribute(attr);
                attribute.setJoinName(getJoinColumnName(attr));
                attribute.setReferencedColumnName(getReferencedColumnName(attr));
                attribute.setTableName(getTableName(attribute.type()));
                attributes.add(attribute);
            } else {
                log.warn("ignored attribute {}", attr.field());
            }

        }
        SimpleAttributes result = new SimpleAttributes(attributes);
        setAnyToOneAttributeColumnName(result);
        return result;
    }

    protected void setAnyToOneAttributeColumnName(Attributes attributes) {
        for (Attribute attribute : attributes) {
            if (attribute instanceof SimpleJoinAttribute attr) {
                String joinColumnName = getJoinColumnName(attributes, attr);
                attr.setJoinName(joinColumnName);
            }
        }
    }

    protected String getJoinColumnName(Attributes attributes, SimpleJoinAttribute attr) {
        String joinName = attr.joinName();
        EntityAttribute join = (EntityAttribute) attributes.get(joinName);
        return join != null && join.isPrimitive()
                ? join.columnName()
                : joinName;
    }

    protected List<Attribute> getBeanAttributes(Class<?> type, Schema owner) {
        Map<String, PropertyDescriptor> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                Field field = ReflectUtil.getDeclaredField(type, descriptor.getName());
                if (field != null) {
                    map.put(field.getName(), descriptor);
                }
            }
        } catch (IntrospectionException e) {
            throw new ReflectiveException(e);
        }
        Collection<Field> declaredFields = getDeclaredFields(type);
        AtomicInteger ordinal = new AtomicInteger();
        List<Attribute> attributes = declaredFields.stream()
                .map(field -> newAttribute(owner, field, map.remove(field.getName()), ordinal.getAndIncrement()))
                .collect(Collectors.toList());
        map.values().stream()
                .map(descriptor -> newAttribute(owner, null, descriptor, ordinal.getAndIncrement()))
                .forEach(attributes::add);
        return attributes;
    }

    protected Collection<Field> getDeclaredFields(Class<?> clazz) {
        Map<String, Field> map = new LinkedHashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        putFieldsIfAbsent(map, fields);
        getSuperClassDeclaredFields(clazz, clazz.getSuperclass(), map);
        return map.values();
    }

    protected void putFieldsIfAbsent(Map<String, Field> map, Field[] fields) {
        for (Field field : fields) {
            if (filterDeclaredField(field)) {
                map.putIfAbsent(field.getName(), field);
            }
        }
    }

    protected void getSuperClassDeclaredFields(Class<?> baseClass, Class<?> clazz, Map<String, Field> map) {
        if (clazz == null) {
            return;
        }
        Field[] superClassField = getSuperClassField(baseClass, clazz);
        if (superClassField != null) {
            putFieldsIfAbsent(map, superClassField);
        }
        Class<?> superclass = clazz.getSuperclass();
        getSuperClassDeclaredFields(baseClass, superclass, map);
    }

    private Attribute newAttribute(Schema owner, Field field, PropertyDescriptor descriptor, int ordinal) {
        Method getter, setter;
        if (descriptor != null) {
            getter = descriptor.getReadMethod();
            setter = descriptor.getWriteMethod();
        } else {
            getter = setter = null;
        }
        return newAttribute(field, getter, setter, owner, ordinal);
    }

    protected boolean filterDeclaredField(@NonNull Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && !Modifier.isFinal(modifiers);
    }

    protected <T extends Schema> Attribute newAttribute(Field field, Method getter, Method setter, T owner, int ordinal) {
        Class<?> javaType = getter != null ? getter.getReturnType() : field.getType();
        String name = field != null ? field.getName() : PathReference.getFieldName(getter.getName());
        return new SimpleAttribute(javaType, name, getter, setter, field, owner, ordinal);
    }

    protected <T extends Annotation> T getAnnotation(Attribute attribute, Class<T> annotationClass) {
        T column = null;
        if (attribute.field() != null) {
            column = attribute.field().getAnnotation(annotationClass);
        }
        if (column == null) {
            Method getter = attribute.getter();
            if (getter != null) {
                column = getter.getAnnotation(annotationClass);
            }
        }
        return column;
    }

}
