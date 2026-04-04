package io.github.nextentity.core.expression;

/// 定义所有支持的 SQL 运算符和函数的枚举。
///
/// 运算符用于构建表达式树，并具有关联的
/// 优先级值以进行正确的 SQL 生成。一些运算符支持
/// 多个操作数或聚合函数。
///
/// @author HuangChengwei
/// @since 1.0.0
public enum Operator {

    /// 逻辑 NOT 运算符。
    NOT("not", 10),

    /// 逻辑 AND 运算符（支持多个操作数）。
    AND("and", 11, true),

    /// 逻辑 OR 运算符（支持多个操作数）。
    OR("or", 13, true),

    /// 大于比较运算符。
    GT(">", 8),

    /// 等于比较运算符。
    EQ("=", 8),

    /// 不等于比较运算符。
    NE("!=", 8),

    /// 大于等于比较运算符。
    GE(">=", 8),

    /// 小于比较运算符。
    LT("<", 8),

    /// 小于等于比较运算符。
    LE("<=", 8),

    /// LIKE 模式匹配运算符。
    LIKE("like", 8),

    /// IS NULL 空值检查运算符。
    IS_NULL("is null", 13),

    /// IS NOT NULL 非空检查运算符。
    IS_NOT_NULL("is not null", 13),

    /// IN 列表成员运算符。
    IN("in", 0),

    /// BETWEEN 范围运算符。
    BETWEEN("between", 8),

    /// LOWER 字符串函数。
    LOWER("lower", 0),

    /// UPPER 字符串函数。
    UPPER("upper", 0),

    /// SUBSTRING 字符串函数。
    SUBSTRING("substring", 0),

    /// TRIM 字符串函数。
    TRIM("trim", 0),

    /// LENGTH 字符串函数。
    LENGTH("length", 0),

    /// 加法算术运算符（支持多个操作数）。
    ADD("+", 4, true),

    /// 减法算术运算符（支持多个操作数）。
    SUBTRACT("-", 4, true),

    /// 乘法算术运算符（支持多个操作数）。
    MULTIPLY("*", 3, true),

    /// 除法算术运算符（支持多个操作数）。
    DIVIDE("/", 3, true),

    /// 模运算算术运算符（支持多个操作数）。
    MOD("%", 3, true),

    /// NULLIF 条件函数。
    NULLIF("nullif", 0),

    /// IFNULL/COALESCE 条件函数。
    IF_NULL("ifnull", 0),

    // 聚合函数

    /// MIN 聚合函数。
    MIN("min", 0, false, true),

    /// MAX 聚合函数。
    MAX("max", 0, false, true),

    /// COUNT 聚合函数。
    COUNT("count", 0, false, true),

    /// AVG 聚合函数。
    AVG("avg", 0, false, true),

    /// SUM 聚合函数。
    SUM("sum", 0, false, true),

    /// DISTINCT 关键字。
    DISTINCT("distinct", 0, false, false);

    private final String sign;
    private final int priority;
    private final boolean multivalued;
    private final boolean agg;

    Operator(String sign, int priority, boolean multivalued, boolean agg) {
        this.sign = sign;
        this.priority = priority;
        this.multivalued = multivalued;
        this.agg = agg;
    }

    Operator(String sign, int priority, boolean multivalued) {
        this(sign, priority, multivalued, false);
    }

    Operator(String sign, int priority) {
        this(sign, priority, false);

    }

    /// 获取此运算符的 SQL 符号。
    ///
    /// @return SQL 符号
    public String sign() {
        return sign;
    }

    /// 返回此运算符的字符串表示形式（SQL 符号）。
    ///
    /// @return SQL 符号
    @Override
    public String toString() {
        return sign;
    }

    /// 获取此运算符用于表达式求值的优先级。
    ///
    /// 较低的值具有更高的优先级。
    ///
    /// @return 优先级值
    public int priority() {
        return priority;
    }

    /// 指示此运算符是否支持多个操作数。
    ///
    /// @return 如果支持多个操作数则返回 true
    public boolean isMultivalued() {
        return this.multivalued;
    }

    /// 指示此运算符是否是聚合函数。
    ///
    /// @return 如果是聚合函数则返回 true
    public boolean isAgg() {
        return this.agg;
    }
}
