package io.github.nextentity.core.meta;

/**
 * 实体类型接口，扩展 EntitySchema 并提供投影支持。
 */
public interface EntityType extends EntitySchema {

    /**
     * 获取指定投影类的投影模式元数据。
     *
     * @param type 投影类
     * @return 投影模式元数据
     */
    ProjectionSchema getProjection(Class<?> type);
}