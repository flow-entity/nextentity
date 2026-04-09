package io.github.nextentity.spring;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/// JDBC 配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   jdbc:
///     dialect: io.github.nextentity.jdbc.MySqlDialect
///     query:
///       timeout: 30
///       fetch-size: 100
///     batch:
///       enabled: true
///       size: 100
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
public class JdbcProperties {

    /// SQL 方言配置
    ///
    /// 支持的值：
    /// - `null`: 自动检测（默认）
    /// - 全类名: SqlDialect 实现类的全限定名
    ///
    /// 示例：
    /// - `io.github.nextentity.jdbc.MySqlDialect`
    /// - `io.github.nextentity.jdbc.PostgresqlDialect`
    /// - `io.github.nextentity.jdbc.SqlServerDialect`
    /// - `io.github.nextentity.jdbc.DefaultDialect`
    /// - 自定义实现类全名
    private String dialect;

    /// 查询配置
    @NestedConfigurationProperty
    private final QueryProperties query = new QueryProperties();

    /// 批处理配置
    @NestedConfigurationProperty
    private final BatchProperties batch = new BatchProperties();

    /// 插入配置
    @NestedConfigurationProperty
    private final InsertProperties insert = new InsertProperties();

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public QueryProperties getQuery() {
        return query;
    }

    public BatchProperties getBatch() {
        return batch;
    }

    public InsertProperties getInsert() {
        return insert;
    }

    /// 查询配置
    public static class QueryProperties {

        /// 查询超时时间（秒）
        /// null 表示无超时
        private Integer timeout;

        /// 结果集获取大小
        /// 0 表示使用驱动默认值
        private int fetchSize = 0;

        /// 整数是否直接拼接到 SQL
        /// true: 整数直接拼接，减少参数数量
        /// false: 使用参数绑定
        private boolean inlineNumericLiterals = true;

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public int getFetchSize() {
            return fetchSize;
        }

        public void setFetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        public boolean isInlineNumericLiterals() {
            return inlineNumericLiterals;
        }

        public void setInlineNumericLiterals(boolean inlineNumericLiterals) {
            this.inlineNumericLiterals = inlineNumericLiterals;
        }
    }

    /// 批处理配置
    public static class BatchProperties {

        /// 是否启用批处理
        private boolean enabled = true;

        /// 批处理大小
        private int size = 100;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
    }

    /// 插入配置
    public static class InsertProperties {

        /// 是否返回生成的主键
        private boolean returnGeneratedKeys = true;

        public boolean isReturnGeneratedKeys() {
            return returnGeneratedKeys;
        }

        public void setReturnGeneratedKeys(boolean returnGeneratedKeys) {
            this.returnGeneratedKeys = returnGeneratedKeys;
        }
    }
}