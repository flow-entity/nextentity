package io.github.nextentity.plugin;

import io.github.nextentity.api.*;
import io.github.nextentity.core.annotation.DirectReference;
import io.github.nextentity.core.meta.IdentityValueConverter;
import io.github.nextentity.core.meta.ValueConverter;
import io.github.nextentity.jdbc.Arguments;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

/// EntityReference 延迟加载插件实现。
///
/// 处理 EntityReference 类型字段：
/// - 查询时只加载 ID
/// - mapValue() 创建 EntityReference 实例并设置 ID
/// - postProcess() 批量注入延迟加载器
///
/// ## 支持判断
/// - 字段类型是 EntityReference 子类
/// - 没有标注 @DirectReference（立即加载）
///
/// @author HuangChengwei
/// @since 2.2.0
public class EntityReferencePlugin implements ProjectionFieldHandler<Object> {

    /// 泛型类型解析缓存
    private final Map<Class<?>, GenericTypeInfo> typeInfoCache = new HashMap<>();

    @Override
    public boolean supports(@NonNull FieldInfo field, @NonNull ProjectionContext context) {
        // 1. 字段类型必须是 EntityReference 子类
        if (!EntityReference.class.isAssignableFrom(field.type())) {
            return false;
        }

        // 2. 排除标注 @DirectReference 的字段（立即加载，不延迟）
        if (field.hasAnnotation(DirectReference.class)) {
            return false;
        }

        return true;
    }

    @Override
    public FieldTypeDescriptor resolveFieldType(@NonNull FieldInfo field, @NonNull ProjectionContext context) {
        Class<?> fieldType = field.type();
        GenericTypeInfo typeInfo = resolveGenericTypes(fieldType);

        // 从 @ReferenceId 注解获取 ID 来源路径
        String idSourcePath = resolveIdSourcePath(field, typeInfo);

        return FieldTypeDescriptor.builder()
                .fieldType(fieldType)
                .targetType(typeInfo.entityType)
                .idType(typeInfo.idType)
                .idSourcePath(idSourcePath)
                .isNested(isNestedReference(fieldType))
                .requiresJoin(false) // EntityReference 不需要 JOIN，只查 ID
                .build();
    }

    @Override
    public Object mapValue(@NonNull FieldInfo field, @NonNull Arguments arguments,
                           @NonNull ProjectionContext context, @NonNull FieldTypeDescriptor descriptor) {
        // 从 Arguments 提取 ID
        Object id = extractId(arguments, descriptor);

        if (id == null) {
            return null;
        }

        // 创建 EntityReference 实例
        @SuppressWarnings("rawtypes")
        EntityReference ref = createReferenceInstance(descriptor.fieldType());
        ref.setId(id);

        return ref;
    }

    @Override
    public void postProcess(@Nullable Object instance, @NonNull FieldInfo field, @Nullable Object value,
                            @NonNull ProjectionContext context) {
        // 单个引用的后处理（通过 setLoader）
        if (value instanceof EntityReference<?, ?> ref) {
            Object id = ref.getId();
            if (id != null) {
                Class<?> targetType = descriptorTargetType(ref.getClass());
                Supplier<?> loader = context.createLoader(targetType, id);
                ref.setLoader(loader);
            }
        }
    }

    /// 批量后处理多个引用。
    ///
    /// 用于批量加载优化，避免 N+1 查询。
    ///
    /// @param references EntityReference 集合
    /// @param context 投影上下文
    public void postProcessBatch(@NonNull Iterable<? extends EntityReference<?, ?>> references, @NonNull ProjectionContext context) {
        // 按实体类型分组
        Map<Class<?>, List<EntityReference<?, ?>>> grouped = groupByEntityType(references);

        for (Map.Entry<Class<?>, List<EntityReference<?, ?>>> entry : grouped.entrySet()) {
            Class<?> entityType = entry.getKey();
            List<EntityReference<?, ?>> refs = entry.getValue();

            // 提取所有 ID
            List<Object> ids = new ArrayList<>();
            for (EntityReference<?, ?> ref : refs) {
                if (ref.getId() != null) {
                    ids.add(ref.getId());
                }
            }

            if (ids.isEmpty()) {
                continue;
            }

            // 批量加载实体
            Map<?, ?> entityMap = context.entityFetcher().fetchBatch(entityType, ids);

            // 为每个引用设置加载器
            for (EntityReference<?, ?> ref : refs) {
                Object id = ref.getId();
                if (id != null) {
                    Object entity = entityMap.get(id);
                    if (entity != null) {
                        // 直接设置已加载的实体（如果批量加载返回了实体）
                        setEntity(ref, entity);
                    } else {
                        // 设置延迟加载器
                        Supplier<?> loader = context.createLoader(entityType, id);
                        ref.setLoader(loader);
                    }
                }
            }
        }
    }

    /// 解析 ID 来源路径。
    private String resolveIdSourcePath(FieldInfo field, GenericTypeInfo typeInfo) {
        // 从 @ReferenceId 注解获取路径
        io.github.nextentity.core.annotation.ReferenceId refId =
            field.getAnnotation(io.github.nextentity.core.annotation.ReferenceId.class);

        if (refId != null) {
            // 显式指定了 ID 字段名
            if (!refId.value().isEmpty()) {
                return refId.value();
            }
            // 嵌套路径
            if (!refId.path().isEmpty()) {
                return refId.path();
            }
        }

        // 默认：字段名 + "Id"
        return field.name() + "Id";
    }

    /// 解析泛型类型信息。
    private GenericTypeInfo resolveGenericTypes(Class<?> fieldType) {
        return typeInfoCache.computeIfAbsent(fieldType, this::doResolveGenericTypes);
    }

    private GenericTypeInfo doResolveGenericTypes(Class<?> fieldType) {
        Class<?> entityType = null;
        Class<?> idType = null;

        // 查找 EntityReference 父类
        Type genericSuper = fieldType.getGenericSuperclass();
        while (genericSuper != null) {
            if (genericSuper instanceof ParameterizedType pt) {
                Type rawType = pt.getRawType();
                if (rawType == EntityReference.class) {
                    // EntityReference<T, ID> 的泛型参数
                    Type[] args = pt.getActualTypeArguments();
                    if (args.length >= 1) {
                        entityType = resolveClass(args[0]);
                    }
                    if (args.length >= 2) {
                        idType = resolveClass(args[1]);
                    }
                    break;
                }
            }

            Class<?> rawClass = genericSuper instanceof Class<?> c ? c :
                    genericSuper instanceof ParameterizedType pt ? (Class<?>) pt.getRawType() : null;
            if (rawClass != null && rawClass != Object.class) {
                genericSuper = rawClass.getGenericSuperclass();
            } else {
                break;
            }
        }

        // 如果无法解析，使用默认值
        if (entityType == null) {
            entityType = Object.class;
        }
        if (idType == null) {
            idType = Object.class;
        }

        return new GenericTypeInfo(entityType, idType);
    }

    private Class<?> resolveClass(Type type) {
        if (type instanceof Class<?> c) {
            return c;
        } else if (type instanceof ParameterizedType pt) {
            return (Class<?>) pt.getRawType();
        }
        return Object.class;
    }

    /// 判断是否嵌套 EntityReference。
    private boolean isNestedReference(Class<?> fieldType) {
        // 检查目标实体是否有 EntityReference 字段
        GenericTypeInfo typeInfo = resolveGenericTypes(fieldType);
        Class<?> entityType = typeInfo.entityType;

        if (entityType == Object.class) {
            return false;
        }

        // 检查字段
        for (Field f : entityType.getDeclaredFields()) {
            if (EntityReference.class.isAssignableFrom(f.getType())) {
                return true;
            }
        }
        return false;
    }

    /// 从 Arguments 提取 ID。
    private Object extractId(Arguments arguments, FieldTypeDescriptor descriptor) {
        ValueConverter<?, ?> converter = new IdentityValueConverter(descriptor.idType());
        return arguments.next(converter);
    }

    /// 创建 EntityReference 实例。
    @SuppressWarnings("rawtypes")
    private EntityReference createReferenceInstance(Class<?> fieldType) {
        try {
            return (EntityReference) fieldType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create EntityReference instance: " + fieldType.getName(), e);
        }
    }

    /// 获取引用的目标实体类型。
    private Class<?> descriptorTargetType(Class<?> refType) {
        GenericTypeInfo info = resolveGenericTypes(refType);
        return info.entityType;
    }

    /// 按实体类型分组。
    private Map<Class<?>, List<EntityReference<?, ?>>> groupByEntityType(Iterable<? extends EntityReference<?, ?>> references) {
        Map<Class<?>, List<EntityReference<?, ?>>> map = new LinkedHashMap<>();
        for (EntityReference<?, ?> ref : references) {
            Class<?> entityType = descriptorTargetType(ref.getClass());
            map.computeIfAbsent(entityType, k -> new ArrayList<>()).add(ref);
        }
        return map;
    }

    /// 设置实体到引用（反射）。
    private void setEntity(EntityReference<?, ?> ref, Object entity) {
        try {
            Field entityField = EntityReference.class.getDeclaredField("entity");
            entityField.setAccessible(true);
            entityField.set(ref, entity);
        } catch (Exception e) {
            // 忽略，使用 loader 替代
        }
    }

    /// 泛型类型信息。
    private static class GenericTypeInfo {
        final Class<?> entityType;
        final Class<?> idType;

        GenericTypeInfo(Class<?> entityType, Class<?> idType) {
            this.entityType = entityType;
            this.idType = idType;
        }
    }
}