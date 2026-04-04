package io.github.nextentity.core.meta;

/// 基于子查询的实体类型接口。
///
/// 此接口扩展 {@link EntityType}，提供对定义实体数据源的 SQL 子查询的访问。
///
/// 子查询实体使用 @SubSelect 注解定义。
///
/// @author HuangChengwei
/// @since 1.0.0
public interface SubQueryEntityType extends EntityType {

    /// 获取提供实体数据的 SQL 子查询。
    ///
    /// @return 子查询 SQL 字符串
    String subSelectSql();

}
