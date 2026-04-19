package io.github.nextentity.core.configuration;

import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.SqlDialect;

/// JDBC 实体操作配置接口
///
/// 定义 JDBC 实体操作所需的所有配置项。
/// 工厂内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public interface EntityOperationsConfiguration {

    /// SQL 方言
    SqlDialect sqlDialect();

    /// 更新配置（保留）
    PersistConfiguration persistConfiguration();

    /// 查询配置（包含分页配置）
    QueryConfiguration queryConfiguration();

}
