package io.github.nextentity.core.configuration;

import io.github.nextentity.core.interceptor.ConstructInterceptor;
import io.github.nextentity.core.interceptor.ResultInterceptor;
import io.github.nextentity.jdbc.SqlDialect;
import org.jspecify.annotations.NonNull;

import java.util.List;

/// 实体操作配置基类，SqlDialect 用于原生查询
public abstract class EntityOperationsConfiguration {

    protected final SqlDialect sqlDialect;
    protected final PersistConfiguration persistConfiguration;
    protected final QueryConfiguration queryConfiguration;
    protected final MetamodelConfiguration metamodelConfiguration;
    protected final List<ConstructInterceptor> constructInterceptors;
    protected final List<ResultInterceptor> resultInterceptors;

    protected EntityOperationsConfiguration(
            @NonNull SqlDialect sqlDialect,
            PersistConfiguration persistConfiguration,
            QueryConfiguration queryConfiguration,
            MetamodelConfiguration metamodelConfiguration,
            List<ConstructInterceptor> constructInterceptors,
            List<ResultInterceptor> resultInterceptors) {
        this.sqlDialect = sqlDialect;
        this.persistConfiguration = persistConfiguration;
        this.queryConfiguration = queryConfiguration != null
                ? queryConfiguration
                : DefaultQueryConfiguration.DEFAULT;
        this.metamodelConfiguration = metamodelConfiguration != null
                ? metamodelConfiguration
                : DefaultMetamodelConfiguration.DEFAULT;
        this.constructInterceptors = constructInterceptors != null
                ? constructInterceptors
                : List.of();
        this.resultInterceptors = resultInterceptors != null
                ? resultInterceptors
                : List.of();
    }

    public SqlDialect sqlDialect() {
        return sqlDialect;
    }

    public PersistConfiguration persistConfiguration() {
        return persistConfiguration;
    }

    public QueryConfiguration queryConfiguration() {
        return queryConfiguration;
    }

    public MetamodelConfiguration metamodelConfiguration() {
        return metamodelConfiguration;
    }

    public List<ConstructInterceptor> constructInterceptors() {
        return constructInterceptors;
    }

    public List<ResultInterceptor> resultInterceptors() {
        return resultInterceptors;
    }

    /// 配置构建器基类，使用泛型 SELF 实现 fluent API
    public abstract static class Builder<SELF extends Builder<SELF, RESULT>, RESULT extends EntityOperationsConfiguration> {
        protected SqlDialect sqlDialect;
        protected PersistConfiguration persistConfiguration;
        protected QueryConfiguration queryConfiguration = DefaultQueryConfiguration.DEFAULT;
        protected MetamodelConfiguration metamodelConfiguration = DefaultMetamodelConfiguration.DEFAULT;
        protected List<ConstructInterceptor> constructInterceptors = List.of();
        protected List<ResultInterceptor> resultInterceptors = List.of();

        @SuppressWarnings("unchecked")
        public SELF sqlDialect(@NonNull SqlDialect sqlDialect) {
            this.sqlDialect = sqlDialect;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF persistConfiguration(PersistConfiguration persistConfiguration) {
            this.persistConfiguration = persistConfiguration;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF queryConfiguration(QueryConfiguration queryConfiguration) {
            this.queryConfiguration = queryConfiguration;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF metamodelConfiguration(MetamodelConfiguration metamodelConfiguration) {
            this.metamodelConfiguration = metamodelConfiguration;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF constructInterceptors(List<ConstructInterceptor> constructInterceptors) {
            this.constructInterceptors = constructInterceptors != null ? constructInterceptors : List.of();
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF resultInterceptors(List<ResultInterceptor> resultInterceptors) {
            this.resultInterceptors = resultInterceptors != null ? resultInterceptors : List.of();
            return (SELF) this;
        }

        public abstract RESULT build();

        protected void validateBaseFields() {
            if (sqlDialect == null) {
                throw new IllegalStateException("sqlDialect must be set");
            }
        }
    }
}