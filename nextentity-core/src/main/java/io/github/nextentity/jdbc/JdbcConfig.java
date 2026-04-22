package io.github.nextentity.jdbc;

import org.jspecify.annotations.Nullable;

/// JDBC 执行配置。
///
/// 封装 JDBC 执行时的可配置参数，包括查询超时、获取大小、批处理设置等。
///
/// @param queryTimeout          查询超时时间（秒），null 表示无超时
/// @param fetchSize             结果集获取大小，0 表示使用驱动默认值
/// @param inlineNumericLiterals 整数是否直接拼接到 SQL
/// @param batchEnabled          是否启用批处理
/// @param batchSize             批处理大小，默认 500。建议范围 100-1000，值越大批量执行效率越高，但内存占用也越大
/// @param returnGeneratedKeys   是否返回生成的主键
/// @author HuangChengwei
/// @since 2.1.0
public record JdbcConfig(
        @Nullable Integer queryTimeout,
        int fetchSize,
        boolean inlineNumericLiterals,
        boolean batchEnabled,
        int batchSize,
        boolean returnGeneratedKeys
) {

    /// 默认配置实例
    public static final JdbcConfig DEFAULT = new JdbcConfig(
            null, 0, true, true, 500, true
    );

    /// 创建默认配置。
    public JdbcConfig() {
        this(null, 0, true, true, 500, true);
    }

    /// 紧凑构造函数，验证参数合法性。
    public JdbcConfig {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive, but was: " + batchSize);
        }
        if (fetchSize < 0) {
            throw new IllegalArgumentException("fetchSize must be non-negative, but was: " + fetchSize);
        }
    }

    /// 创建构建器。
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器。
    public static class Builder {
        private @Nullable Integer queryTimeout = null;
        private int fetchSize = 0;
        private boolean inlineNumericLiterals = true;
        private boolean batchEnabled = true;
        private int batchSize = 500;
        private boolean returnGeneratedKeys = true;

        public Builder queryTimeout(@Nullable Integer queryTimeout) {
            this.queryTimeout = queryTimeout;
            return this;
        }

        public Builder fetchSize(int fetchSize) {
            this.fetchSize = fetchSize;
            return this;
        }

        public Builder inlineNumericLiterals(boolean inlineNumericLiterals) {
            this.inlineNumericLiterals = inlineNumericLiterals;
            return this;
        }

        public Builder batchEnabled(boolean batchEnabled) {
            this.batchEnabled = batchEnabled;
            return this;
        }

        public Builder batchSize(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        public Builder returnGeneratedKeys(boolean returnGeneratedKeys) {
            this.returnGeneratedKeys = returnGeneratedKeys;
            return this;
        }

        public JdbcConfig build() {
            return new JdbcConfig(
                    queryTimeout, fetchSize, inlineNumericLiterals,
                    batchEnabled, batchSize, returnGeneratedKeys
            );
        }
    }
}