package io.github.nextentity.core.meta;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Attributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/// {@link EntityType} 的简单实现。
///
/// 该类为实体类型元数据提供了具体实现
/// 支持延迟投影类型生成和缓存。
///
/// 实体属性包括 ID 和版本在构造后通过
/// {@link #setAttributes(Attributes)} 设置。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleEntity implements EntityType {

    private final Map<Class<?>, ProjectionType> projections = new ConcurrentHashMap<>();
    private final Class<?> type;
    private final String tableName;
    private final BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator;
    private Attributes attributes;
    private EntityAttribute id;
    private EntityAttribute version;

    /// 创建新的 SimpleEntity 实例。
    ///
    /// @param type 实体类
    /// @param tableName 数据库表名
    /// @param projectionTypeGenerator 生成投影类型的函数
    public SimpleEntity(Class<?> type,
                        String tableName,
                        BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator) {
        this.type = type;
        this.tableName = tableName;
        this.projectionTypeGenerator = projectionTypeGenerator;
    }

    /// 设置实体属性并提取 ID 和版本属性。
    ///
    /// @param attributes 实体属性
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
        EntityAttribute version = null;
        EntityAttribute id = null;
        for (Attribute attribute : attributes) {
            if (attribute instanceof EntityAttribute column) {
                if (column.isId()) {
                    id = column;
                } else if (column.isVersion()) {
                    version = column;
                }
            }
        }
        this.id = id;
        this.version = version;
    }

    /// 获取指定类的投影类型，并缓存结果。
    ///
    /// @param type 投影类
    /// @return 缓存的或新生成的投影类型
    @Override
    public ProjectionType getProjection(Class<?> type) {
        return projections.computeIfAbsent(type, this::generateProjectionType);
    }

    private ProjectionType generateProjectionType(Class<?> type) {
        return projectionTypeGenerator.apply(this, type);
    }

    @Override
    public EntityAttribute id() {
        return id;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public EntityAttribute version() {
        return version;
    }

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public Class<?> type() {
        return type;
    }
}
