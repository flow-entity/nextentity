package io.github.nextentity.core.meta;

import java.util.function.BiFunction;

/// 通过 @SubSelect 注解定义的基于子查询的实体类型。
///
/// 此类扩展 {@link SimpleEntity} 以支持数据来自 SQL 子查询而非直接表映射的实体。
///
/// 子查询实体允许定义由自定义 SQL 支持的虚拟实体。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SubQueryEntity extends SimpleEntity implements SubQueryEntityType {

    private final String subSelectSql;

    /// 创建新的 SubQueryEntity 实例。
    ///
    /// @param type 实体类
    /// @param tableName 表名（在子查询中用作别名）
    /// @param projectionTypeGenerator 生成投影类型的函数
    /// @param subSelectSql 提供实体数据的 SQL 子查询
    public SubQueryEntity(Class<?> type,
                          String tableName,
                          BiFunction<EntityType, Class<?>, ProjectionType> projectionTypeGenerator,
                          String subSelectSql) {
        super(type, tableName, projectionTypeGenerator);
        this.subSelectSql = subSelectSql;
    }

    /// 获取提供实体数据的 SQL 子查询。
    ///
    /// @return 子查询 SQL
    @Override
    public String subSelectSql() {
        return subSelectSql;
    }
}
