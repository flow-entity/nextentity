package io.github.nextentity.spring;

/// JPA 配置属性。
///
/// 配置示例：
/// ```yaml
/// nextentity:
///   jpa:
///     string-parameter-binding: true
///     native-subqueries: true
/// ```
///
/// @author HuangChengwei
/// @since 2.1.0
public class JpaProperties {

    /// 字符串是否使用参数绑定
    ///
    /// true: 使用参数绑定，确保 SQL Server Unicode 字符正确处理 (N prefix)
    /// false: 使用 cb.literal()，可能导致 SQL Server Unicode 问题
    private boolean stringParameterBinding = true;

    /// 子查询是否使用原生 SQL
    ///
    /// true: 子查询使用 JDBC 原生 SQL，性能更好
    /// false: 子查询使用 JPA Criteria API
    private boolean nativeSubqueries = true;

    /// 事务模板 Bean 名称
    ///
    /// 用于自定义事务处理
    private String transactionTemplate = "default";

    public boolean isStringParameterBinding() {
        return stringParameterBinding;
    }

    public void setStringParameterBinding(boolean stringParameterBinding) {
        this.stringParameterBinding = stringParameterBinding;
    }

    public boolean isNativeSubqueries() {
        return nativeSubqueries;
    }

    public void setNativeSubqueries(boolean nativeSubqueries) {
        this.nativeSubqueries = nativeSubqueries;
    }

    public String getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(String transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}