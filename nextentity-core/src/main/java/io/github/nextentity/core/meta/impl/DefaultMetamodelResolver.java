package io.github.nextentity.core.meta.impl;

import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.core.annotation.Fetch;
import io.github.nextentity.core.annotation.Join;
import io.github.nextentity.core.converter.InstantConverter;
import io.github.nextentity.core.exception.ConfigurationException;
import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.meta.*;
import io.github.nextentity.core.reflect.schema.Accessor;
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
    public boolean isTransient(Accessor accessor) {
        return accessor == null
               || accessor.field() == null
               || Modifier.isTransient(accessor.field().getModifiers())
               || Modifier.isStatic(accessor.field().getModifiers())
               || getAnnotation(accessor, Transient.class) != null;
    }

    @Override
    public boolean isBasicField(Accessor accessor) {
        for (Class<? extends Annotation> type : JOIN_ANNOTATIONS) {
            if (getAnnotation(accessor, type) != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isVersionField(Accessor accessor) {
        Version version = getAnnotation(accessor, Version.class);
        if (version != null) {
            Class<?> type = accessor.type();
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
    public boolean isMarkedId(Accessor accessor) {
        return getAnnotation(accessor, Id.class) != null;
    }

    @Override
    public String getColumnName(Accessor accessor) {
        String columnName = getColumnNameByAnnotation(accessor);
        if (columnName == null) {
            columnName = camelbackToUnderline(accessor.name());
        }
        return unwrapSymbol(columnName);
    }

    protected String getColumnNameByAnnotation(Accessor accessor) {
        Column column = getAnnotation(accessor, Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return null;
    }

    @Override
    public boolean isUpdatable(Accessor accessor) {
        Column column = getAnnotation(accessor, Column.class);
        return column == null || column.updatable();
    }

    @Override
    public ValueConverter<?, ?> databaseType(Accessor accessor) {
        Convert convert = getAnnotation(accessor, Convert.class);
        if (convert != null) {
            Class<?> type = convert.converter();
            if (type != void.class) {
                try {
                    AttributeConverter<?, ?> converter = (AttributeConverter<?, ?>) type.getConstructor().newInstance();
                    return AttributeConverterWrapper.of(converter);
                } catch (ReflectiveOperationException e) {
                    log.error("create AttributeConverter error, accessor: {}", accessor);
                    throw new ReflectiveException(e.getMessage(), e);
                }
            }
        }
        return resolveConverter(accessor.type());
    }

    private ValueConverter<?, ?> resolveConverter(Class<?> type) {
        if (type.isEnum()) {
            return EnumValueConverter.of(type);
        }
        if (type == Instant.class) {
            return InstantConverter.of();
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
    public String getJoinColumnName(Accessor accessor) {
        JoinColumn annotation = getAnnotation(accessor, JoinColumn.class);
        return annotation != null ? annotation.name() : null;
    }

    @Override
    public String getReferencedColumnName(Accessor accessor) {
        JoinColumn annotation = getAnnotation(accessor, JoinColumn.class);
        return annotation != null ? annotation.referencedColumnName() : null;
    }

    @Override
    public boolean isAnyToOne(MetamodelAttribute attribute) {
        Accessor accessor = attribute.accessor();
        return getAnnotation(accessor, ManyToOne.class) != null
               || getAnnotation(accessor, OneToOne.class) != null;
    }

    @Override
    public EntityBasicAttribute getJoinSourceAttribute(DefaultEntitySchema sourceSchema,
                                                       Accessor accessor) {
        String joinColumnName = getJoinColumnName(accessor);
        if (joinColumnName != null) {
            return (EntityBasicAttribute) sourceSchema.getAttribute(joinColumnName);
        }
        return sourceSchema.id();
    }

    @Override
    public EntityBasicAttribute getJoinTargetAttribute(DefaultEntitySchema targetSchema,
                                                       Accessor accessor) {
        String referencedColumnName = getReferencedColumnName(accessor);
        if (referencedColumnName != null && !referencedColumnName.isEmpty()) {
            return (EntityBasicAttribute) targetSchema.getAttribute(referencedColumnName);
        }
        return targetSchema.id();
    }

    @Override
    public Iterable<String> getMappedEntityPath(Accessor accessor) {
        EntityPath annotation = getAnnotation(accessor, EntityPath.class);
        if (annotation != null) {
            String value = annotation.value();
            String[] split = value.split("\\.");
            return List.of(split);
        }
        return List.of(accessor.name());
    }

    @Override
    public Class<?> getProjectionJoinTarget(Accessor accessor) {
        Join annotation = getAnnotation(accessor, Join.class);
        return annotation != null ? annotation.target() : null;
    }

    @Override
    public String getProjectionJoinSourceAttribute(Accessor accessor) {
        Join annotation = getAnnotation(accessor, Join.class);
        return annotation != null ? annotation.sourceAttribute() : null;
    }

    @Override
    public String getProjectionJoinTargetAttribute(Accessor accessor) {
        Join annotation = getAnnotation(accessor, Join.class);
        return annotation != null ? annotation.targetAttribute() : null;
    }

    @Override
    public boolean matchProjectionSchemaAttribute(EntitySchemaAttribute entitySchemaAttribute, MetamodelAttribute schemaAttribute) {
        return entitySchemaAttribute.type() == schemaAttribute.type()
               || entitySchemaAttribute.schema().getAttributes().iterator().hasNext();
    }

    @Override
    public boolean matchProjectionBasicAttribute(EntityBasicAttribute entityBasicAttribute, MetamodelAttribute attribute) {
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
    public FetchType getFetchType(MetamodelAttribute attribute) {
        Class<?> declareType = attribute.declareBy().type();
        Fetch fetch = getAnnotation(attribute.accessor(), Fetch.class);
        if (fetch == null) {
            return null;
        }
        FetchType fetchType = fetch.value();

        if (fetchType == FetchType.LAZY) {
            if (declareType.isInterface() && !config.interfaceProjectionLazyLoadEnabled()) {
                log.warn("Interface projection '{}' has @Fetch(LAZY) on attribute '{}' but lazy load is disabled in configuration, " +
                         "will be treated as EAGER", declareType.getName(), attribute.name());
                return FetchType.EAGER;
            }
            if (!declareType.isInterface() && !declareType.isRecord()
                && !config.dtoProjectionLazyLoadEnabled()) {
                log.warn("Dto projection '{}' has @Fetch(LAZY) on attribute '{}' but Dto lazy load is not enabled in configuration, " +
                         "will be treated as EAGER", declareType.getName(), attribute.name());
                return FetchType.EAGER;
            }
        }

        return fetchType;
    }

    protected <T extends Annotation> T getAnnotation(Accessor accessor, Class<T> annotationClass) {
        T annotation = null;
        if (accessor.field() != null) {
            annotation = accessor.field().getAnnotation(annotationClass);
        }
        if (annotation == null && accessor.getter() != null) {
            annotation = accessor.getter().getAnnotation(annotationClass);
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
