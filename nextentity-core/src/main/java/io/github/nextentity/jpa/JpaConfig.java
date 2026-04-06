package io.github.nextentity.jpa;

/// JPA 执行配置。
///
/// 封装 JPA 执行时的可配置参数。
///
/// @param stringParameterBinding 字符串是否使用参数绑定（SQL Server Unicode 支持）
/// @param nativeSubqueries       子查询是否使用原生 SQL
///
/// @author HuangChengwei
/// @since 2.1.0
public record JpaConfig(
        boolean stringParameterBinding,
        boolean nativeSubqueries
) {

    /// 默认配置实例
    public static final JpaConfig DEFAULT = new JpaConfig(true, true);

    /// 创建默认配置。
    public JpaConfig() {
        this(true, true);
    }

    /// 创建构建器。
    public static Builder builder() {
        return new Builder();
    }

    /// 配置构建器。
    public static class Builder {
        private boolean stringParameterBinding = true;
        private boolean nativeSubqueries = true;

        public Builder stringParameterBinding(boolean stringParameterBinding) {
            this.stringParameterBinding = stringParameterBinding;
            return this;
        }

        public Builder nativeSubqueries(boolean nativeSubqueries) {
            this.nativeSubqueries = nativeSubqueries;
            return this;
        }

        public JpaConfig build() {
            return new JpaConfig(stringParameterBinding, nativeSubqueries);
        }
    }
}