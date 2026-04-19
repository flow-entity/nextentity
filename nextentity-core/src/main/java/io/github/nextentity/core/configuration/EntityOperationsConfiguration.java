package io.github.nextentity.core.configuration;

import io.github.nextentity.jdbc.SqlDialect;
import org.jspecify.annotations.NonNull;

/// 实体操作配置基类
///
/// 定义实体操作的通用配置项。
/// SqlDialect 用于原生查询（JDBC 直接执行、JPA 原生 SQL 子查询等）。
/// 子类通过继承添加特定配置（JDBC 的 ConnectionProvider，JPA 的 EntityManager）。
public abstract class EntityOperationsConfiguration {

    protected final SqlDialect sqlDialect;
    protected final PersistConfiguration persistConfiguration;
    protected final QueryConfiguration queryConfiguration;
    protected final MetamodelConfiguration metamodelConfiguration;

    /// 构造实体操作配置
    ///
    /// @param sqlDialect          SQL 方言（必填）
    /// @param persistConfiguration 持久化配置（可为 null）
    /// @param queryConfiguration  查询配置（可为 null，使用默认值）
    /// @param metamodelConfiguration 元模型配置（可为 null，使用默认值）
    protected EntityOperationsConfiguration(
            @NonNull SqlDialect sqlDialect,
            PersistConfiguration persistConfiguration,
            QueryConfiguration queryConfiguration,
            MetamodelConfiguration metamodelConfiguration) {
        this.sqlDialect = sqlDialect;
        this.persistConfiguration = persistConfiguration;
        this.queryConfiguration = queryConfiguration != null
                ? queryConfiguration
                : DefaultQueryConfiguration.DEFAULT;
        this.metamodelConfiguration = metamodelConfiguration != null
                ? metamodelConfiguration
                : DefaultMetamodelConfiguration.DEFAULT;
    }

    /// SQL 方言
    ///
    /// 用于原生 SQL 查询的语法适配。
    /// JDBC 使用它构建 SQL 语句，JPA 使用它构建原生子查询。
    ///
    /// @return SQL 方言实例
    public SqlDialect sqlDialect() {
        return sqlDialect;
    }

    /// 持久化配置
    ///
    /// @return 持久化配置实例，可为 null
    public PersistConfiguration persistConfiguration() {
        return persistConfiguration;
    }

    /// 查询配置（包含分页配置）
    ///
    /// @return 查询配置实例
    public QueryConfiguration queryConfiguration() {
        return queryConfiguration;
    }

    /// 元模型配置（包含投影懒加载配置）
    ///
    /// @return 元模型配置实例
    public MetamodelConfiguration metamodelConfiguration() {
        return metamodelConfiguration;
    }

    /// 配置构建器基类
    ///
    /// 使用泛型 SELF 实现 fluent API 返回类型协变。
    /// @param <SELF> 构建器自身类型
    /// @param <RESULT> build() 返回的配置类型
    public abstract static class Builder<SELF extends Builder<SELF, RESULT>, RESULT extends EntityOperationsConfiguration> {
        protected SqlDialect sqlDialect;
        protected PersistConfiguration persistConfiguration;
        protected QueryConfiguration queryConfiguration = DefaultQueryConfiguration.DEFAULT;
        protected MetamodelConfiguration metamodelConfiguration = DefaultMetamodelConfiguration.DEFAULT;

        /// 设置 SQL 方言
        @SuppressWarnings("unchecked")
        public SELF sqlDialect(@NonNull SqlDialect sqlDialect) {
            this.sqlDialect = sqlDialect;
            return (SELF) this;
        }

        /// 设置持久化配置
        @SuppressWarnings("unchecked")
        public SELF persistConfiguration(PersistConfiguration persistConfiguration) {
            this.persistConfiguration = persistConfiguration;
            return (SELF) this;
        }

        /// 设置查询配置
        @SuppressWarnings("unchecked")
        public SELF queryConfiguration(QueryConfiguration queryConfiguration) {
            this.queryConfiguration = queryConfiguration;
            return (SELF) this;
        }

        /// 设置元模型配置
        @SuppressWarnings("unchecked")
        public SELF metamodelConfiguration(MetamodelConfiguration metamodelConfiguration) {
            this.metamodelConfiguration = metamodelConfiguration;
            return (SELF) this;
        }

        /// 构建配置实例
        public abstract RESULT build();

        /// 验证必填字段
        protected void validateBaseFields() {
            if (sqlDialect == null) {
                throw new IllegalStateException("sqlDialect must be set");
            }
        }
    }
}