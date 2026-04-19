package io.github.nextentity.jdbc.configuration;

import io.github.nextentity.core.configuration.EntityOperationsConfiguration;
import io.github.nextentity.jdbc.ConnectionProvider;
import io.github.nextentity.jdbc.JdbcConfig;

/// JDBC 实体操作配置接口
///
/// 定义 JDBC 实体操作所需的所有配置项。
/// 工厂内部使用 DefaultMetamodel.of(DefaultMetamodelResolver.of())，无需配置 Metamodel。
public interface JdbcEntityOperationsConfiguration extends EntityOperationsConfiguration {

    /// 数据库连接提供者
    ConnectionProvider connectionProvider();

    JdbcConfig jdbcConfig();

}
