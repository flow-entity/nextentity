package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.core.annotation.Join;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.SchemaAttribute;
import io.github.nextentity.meta.jpa.AttributeConverterWrapper;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * JPA 注解元模型解析器实现。
 * 从 JPA 注解提取实体元数据（表名、列名、关系等）。
 *
 * @author HuangChengwei
 * @since 2.0.0
 */
public class DefaultMetamodelResolver implements MetamodelResolver {

    private static final DefaultMetamodelResolver DEFAULT_INSTANCE = new DefaultMetamodelResolver();
    private static final Logger log = LoggerFactory.getLogger(DefaultMetamodelResolver.class);

    private final MetamodelConfiguration config;

    private final List<Class<? extends Annotation>> JOIN_ANNOTATIONS =
            Arrays.asList(ManyToOne.class, OneToMany.class, ManyToMany.class, OneToOne.class);

    protected DefaultMetamodelResolver() {
        this.config = MetamodelConfiguration.DEFAULT;
    }

    public DefaultMetamodelResolver(MetamodelConfiguration config) {
        this.config = config != null ? config : MetamodelConfiguration.DEFAULT;
    }

    public static DefaultMetamodelResolver of() {
        return DEFAULT_INSTANCE;
    }

    public static DefaultMetamodelResolver of(MetamodelConfiguration config) {
        return new DefaultMetamodelResolver(config);
    }

    public MetamodelConfiguration getConfig() {
        return config;
    }

    @Override
    public String getTableName(Class<?> type) {
        String tableName = getTableNameByAnnotation(type);
        return tableName != null ? unwrapSymbol(tableName) : getTableNameByClassName(type);
    }

    protected String getTableNameByClassName(Class<?> javaType) {
        String tableName = camelbackToUnderline(javaType.getSimpleName());
        return unwrapSymbol(tableName);
    }

    protected String camelbackToUnderline(String simpleName) {
        return simpleName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    protected String getTableNameByAnnotation(Class<?> javaType) {
        Table table = javaType.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        Entity entity = javaType.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        return null;
    }

    @Override
    public boolean isTransient(Attribute attribute) {
        return attribute == null
               || attribute.field() == null
               || Modifier.isTransient(attribute.field().getModifiers())
               || Modifier.isStatic(attribute.field().getModifiers())
               || getAnnotation(attribute, Transient.class) != null;
    }

    @Override
    public boolean isBasicField(Attribute attribute) {
        for (Class<? extends Annotation> type : JOIN_ANNOTATIONS) {
            if (getAnnotation(attribute, type) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isVersionField(Attribute attribute) {
        Version version = getAnnotation(attribute, Version.class);
        if (version != null) {
            Class<?> type = attribute.type();
            if (isSupportVersion(type)) {
                return true;
            } else {
                throw new ConfigurationException("not support version type: " + type);
            }
        }
        return false;
    }

    protected boolean isSupportVersion(Class<?> type) {
        return type == long.class || type == Long.class || type == Integer.class || type == int.class;
    }

    @Override
    public boolean isMarkedId(Attribute attribute) {
        return getAnnotation(attribute, Id.class) != null;
    }

    @Override
    public String getColumnName(Attribute attribute) {
        String columnName = getColumnNameByAnnotation(attribute);
        if (columnName == null) {
            columnName = camelbackToUnderline(attribute.name());
        }
        return unwrapSymbol(columnName);
    }

    protected String getColumnNameByAnnotation(Attribute attribute) {
        Column column = getAnnotation(attribute, Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return null;
    }

    @Override
    public boolean isUpdatable(Attribute attribute) {
        Column column = getAnnotation(attribute, Column.class);
        return column == null || column.updatable();
    }

    @Override
    public ValueConverter<?, ?> databaseType(Attribute attribute) {
        Convert convert = getAnnotation(attribute, Convert.class);
        if (convert != null) {
            Class<?> type = convert.converter();
            if (type != void.class) {
                try {
                    AttributeConverter<?, ?> converter = (AttributeConverter<?, ?>) type.getConstructor().newInstance();
                    return AttributeConverterWrapper.of(converter);
                } catch (ReflectiveOperationException e) {
                    log.error("create AttributeConverter error, attribute: {}", attribute);
                    throw new ReflectiveException(e.getMessage(), e);
                }
            }
        }
        return resolveConverter(attribute.type());
    }

    private ValueConverter<?, ?> resolveConverter(Class<?> type) {
        if (type.isEnum()) {
            return EnumValueConverter.of(type);
        }
        if (type == Instant.class) {
            return IdentityValueConverter.of();
        }
        if (type == LocalDate.class) {
            return LocalDateValueConverter.of();
        }
        if (type == LocalDateTime.class) {
            return LocalDateTimeValueConverter.of();
        }
        if (type == LocalTime.class) {
            return LocalTimeValueConverter.of();
        }
        return new IdentityValueConverter<>(type);
    }

    @Override
    public String getJoinColumnName(Attribute attribute) {
        JoinColumn annotation = getAnnotation(attribute, JoinColumn.class);
        return annotation != null ? annotation.name() : null;
    }

    @Override
    public String getReferencedColumnName(Attribute attribute) {
        JoinColumn annotation = getAnnotation(attribute, JoinColumn.class);
        return annotation != null ? annotation.referencedColumnName() : null;
    }

    @Override
    public boolean isAnyToOne(SchemaAttribute attribute) {
        return getAnnotation(attribute, ManyToOne.class) != null
               || getAnnotation(attribute, OneToOne.class) != null;
    }

    @Override
    public EntityBasicAttribute getJoinSourceAttribute(DefaultEntitySchema sourceSchema,
                                                       Attribute attribute) {
        String joinColumnName = getJoinColumnName(attribute);
        if (joinColumnName != null) {
            return (EntityBasicAttribute) sourceSchema.getAttribute(joinColumnName);
        }
        return sourceSchema.id();
    }

    @Override
    public EntityBasicAttribute getJoinTargetAttribute(DefaultEntitySchema targetSchema,
                                                       Attribute attribute) {
        String referencedColumnName = getReferencedColumnName(attribute);
        if (referencedColumnName != null && !referencedColumnName.isEmpty()) {
            return (EntityBasicAttribute) targetSchema.getAttribute(referencedColumnName);
        }
        return targetSchema.id();
    }

    @Override
    public Iterable<String> getMappedEntityPath(Attribute attribute) {
        EntityPath annotation = getAnnotation(attribute, EntityPath.class);
        if (annotation != null) {
            String value = annotation.value();
            String[] split = value.split("\\.");
            return List.of(split);
        }
        return List.of(attribute.name());
    }

    @Override
    public Class<?> getProjectionJoinTarget(Attribute attribute) {
        Join annotation = getAnnotation(attribute, Join.class);
        return annotation != null ? annotation.target() : null;
    }

    @Override
    public String getProjectionJoinSourceAttribute(Attribute attribute) {
        Join annotation = getAnnotation(attribute, Join.class);
        return annotation != null ? annotation.sourceAttribute() : null;
    }

    @Override
    public String getProjectionJoinTargetAttribute(Attribute attribute) {
        Join annotation = getAnnotation(attribute, Join.class);
        return annotation != null ? annotation.targetAttribute() : null;
    }

    @Override
    public boolean matchProjectionSchemaAttribute(EntitySchemaAttribute entitySchemaAttribute, SchemaAttribute schemaAttribute) {
        return entitySchemaAttribute.type() == schemaAttribute.type()
               || entitySchemaAttribute.getAttributes().iterator().hasNext();
    }

    @Override
    public boolean matchProjectionBasicAttribute(EntityBasicAttribute entityBasicAttribute, Attribute attribute) {
        return entityBasicAttribute.type() == attribute.type();
    }

    @Override
    public String getEntityName(Class<?> type) {
        Entity entity = type.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        return type.getSimpleName();
    }

    @Override
    public FetchType getFetchType(Attribute attribute) {
        Class<?> declareType = attribute.declareBy().type();
        Fetch fetch = getAnnotation(attribute, Fetch.class);
        if (fetch == null) {
            return null;
        }
        FetchType fetchType = fetch.value();

        // 检查配置兼容性并发出告警
        if (fetchType == FetchType.LAZY) {
            // Interface 投影检查
            if (declareType.isInterface() && !config.interfaceProjectionLazyLoadEnabled()) {
                log.warn("Interface projection '{}' has @Fetch(LAZY) on attribute '{}' but lazy load is disabled in configuration, " +
                         "will be treated as EAGER", declareType.getName(), attribute.name());
            }
            // Dto 投影检查（非接口、非 Record）
            if (!declareType.isInterface() && !declareType.isRecord()
                && !config.dtoProjectionLazyLoadEnabled()) {
                log.warn("Dto projection '{}' has @Fetch(LAZY) on attribute '{}' but Dto lazy load is not enabled in configuration, " +
                         "will be treated as EAGER", declareType.getName(), attribute.name());
            }
        }

        return fetchType;
    }

    protected <T extends Annotation> T getAnnotation(Attribute attribute, Class<T> annotationClass) {
        T annotation = null;
        if (attribute.field() != null) {
            annotation = attribute.field().getAnnotation(annotationClass);
        }
        if (annotation == null && attribute.getter() != null) {
            annotation = attribute.getter().getAnnotation(annotationClass);
        }
        return annotation;
    }

    private static final String[][] AROUND_SYMBOL = {
            {"[", "]"},
            {"`", "`"},
            {"\"", "\""}
    };

    protected String unwrapSymbol(String symbol) {
        for (String[] strings : AROUND_SYMBOL) {
            if (symbol.startsWith(strings[0]) && symbol.endsWith(strings[1])) {
                symbol = symbol.substring(1, symbol.length() - 1);
                break;
            }
        }
        return symbol;
    }
}
