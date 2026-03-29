package io.github.nextentity.core.expression;

///
/// Enum defining all supported SQL operators and functions.
/// <p>
/// Operators are used to build expression trees and have associated
/// priority values for proper SQL generation. Some operators support
/// multiple operands or are aggregate functions.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public enum Operator {

    ///
    /// Logical NOT operator.
    ///
    NOT("not", 10),

    ///
    /// Logical AND operator (supports multiple operands).
    ///
    AND("and", 11, true),

    ///
    /// Logical OR operator (supports multiple operands).
    ///
    OR("or", 13, true),

    ///
    /// Greater than comparison operator.
    ///
    GT(">", 8),

    ///
    /// Equality comparison operator.
    ///
    EQ("=", 8),

    ///
    /// Not equal comparison operator.
    ///
    NE("!=", 8),

    ///
    /// Greater than or equal comparison operator.
    ///
    GE(">=", 8),

    ///
    /// Less than comparison operator.
    ///
    LT("<", 8),

    ///
    /// Less than or equal comparison operator.
    ///
    LE("<=", 8),

    ///
    /// LIKE pattern matching operator.
    ///
    LIKE("like", 8),

    ///
    /// IS NULL null check operator.
    ///
    IS_NULL("is null", 13),

    ///
    /// IS NOT NULL null check operator.
    ///
    IS_NOT_NULL("is not null", 13),

    ///
    /// IN list membership operator.
    ///
    IN("in", 0),

    ///
    /// BETWEEN range operator.
    ///
    BETWEEN("between", 8),

    ///
    /// LOWER string function.
    ///
    LOWER("lower", 0),

    ///
    /// UPPER string function.
    ///
    UPPER("upper", 0),

    ///
    /// SUBSTRING string function.
    ///
    SUBSTRING("substring", 0),

    ///
    /// TRIM string function.
    ///
    TRIM("trim", 0),

    ///
    /// LENGTH string function.
    ///
    LENGTH("length", 0),

    ///
    /// Addition arithmetic operator (supports multiple operands).
    ///
    ADD("+", 4, true),

    ///
    /// Subtraction arithmetic operator (supports multiple operands).
    ///
    SUBTRACT("-", 4, true),

    ///
    /// Multiplication arithmetic operator (supports multiple operands).
    ///
    MULTIPLY("*", 3, true),

    ///
    /// Division arithmetic operator (supports multiple operands).
    ///
    DIVIDE("/", 3, true),

    ///
    /// Modulo arithmetic operator (supports multiple operands).
    ///
    MOD("%", 3, true),

    ///
    /// NULLIF conditional function.
    ///
    NULLIF("nullif", 0),

    ///
    /// IFNULL/COALESCE conditional function.
    ///
    IF_NULL("ifnull", 0),

    // aggregate functions

    ///
    /// MIN aggregate function.
    ///
    MIN("min", 0, false, true),

    ///
    /// MAX aggregate function.
    ///
    MAX("max", 0, false, true),

    ///
    /// COUNT aggregate function.
    ///
    COUNT("count", 0, false, true),

    ///
    /// AVG aggregate function.
    ///
    AVG("avg", 0, false, true),

    ///
    /// SUM aggregate function.
    ///
    SUM("sum", 0, false, true),

    ///
    /// DISTINCT keyword.
    ///
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

    ///
    /// Gets the SQL sign/symbol for this operator.
    ///
    /// @return the SQL sign
    ///
    public String sign() {
        return sign;
    }

    ///
    /// Returns the string representation (SQL sign) of this operator.
    ///
    /// @return the SQL sign
    ///
    @Override
    public String toString() {
        return sign;
    }

    ///
    /// Gets the priority of this operator for expression evaluation.
    /// <p>
    /// Lower values have higher precedence.
    ///
    /// @return the priority value
    ///
    public int priority() {
        return priority;
    }

    ///
    /// Indicates if this operator supports multiple operands.
    ///
    /// @return true if multivalued
    ///
    public boolean isMultivalued() {
        return this.multivalued;
    }

    ///
    /// Indicates if this operator is an aggregate function.
    ///
    /// @return true if aggregate function
    ///
    public boolean isAgg() {
        return this.agg;
    }
}
