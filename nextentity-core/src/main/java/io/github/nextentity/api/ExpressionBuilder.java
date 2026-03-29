package io.github.nextentity.api;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/// Expression builder interface, providing methods to build various expressions.
///
/// Used to build query condition expressions, supporting operations such as equals, not equals, greater than, less than, etc.
///
/// @param <T> Entity type
/// @param <U> Expression value type
/// @param <B> Builder return type
/// @author HuangChengwei
/// @since 1.0.0
public interface ExpressionBuilder<T, U, B> {

    /// Equals the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B eq(U value);

    /// Equals the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B eqIfNotNull(U value);

    /// Equals another expression.
    ///
    /// @param expression Another expression
    /// @return Builder instance
    B eq(TypedExpression<T, U> expression);

    /// Not equals the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B ne(U value);

    /// Not equals the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B neIfNotNull(U value);

    /// Not equals another expression.
    ///
    /// @param expression Another expression
    /// @return Builder instance
    B ne(TypedExpression<T, U> expression);

    /// In the specified value array.
    ///
    /// @param values Value array
    /// @return Builder instance
    @SuppressWarnings({"unchecked"})
    B in(U... values);

    /// In the specified expression list.
    ///
    /// @param expressions Expression list
    /// @return Builder instance
    B in(@NonNull List<? extends TypedExpression<T, U>> expressions);

    /// In the value list of the specified expression.
    ///
    /// @param expressions Expression
    /// @return Builder instance
    B in(@NonNull TypedExpression<T, List<U>> expressions);

    /// In the specified value collection.
    ///
    /// @param values Value collection
    /// @return Builder instance
    B in(@NonNull Collection<? extends U> values);

    /// Not in the specified value array.
    ///
    /// @param values Value array
    /// @return Builder instance
    @SuppressWarnings({"unchecked"})
    B notIn(U... values);

    /// Not in the specified expression list.
    ///
    /// @param expressions Expression list
    /// @return Builder instance
    B notIn(@NonNull List<? extends TypedExpression<T, U>> expressions);

    /// Not in the specified value collection.
    ///
    /// @param values Value collection
    /// @return Builder instance
    B notIn(@NonNull Collection<? extends U> values);

    /// Is null.
    ///
    /// @return Builder instance
    B isNull();

    /// Not null.
    ///
    /// @return Builder instance
    B isNotNull();

    /// Greater than or equal to the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B ge(U value);

    /// Greater than the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B gt(U value);

    /// Less than or equal to the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B le(U value);

    /// Less than the specified value.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B lt(U value);

    /// Greater than or equal to the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B geIfNotNull(U value);

    /// Greater than the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B gtIfNotNull(U value);

    /// Less than or equal to the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B leIfNotNull(U value);

    /// Less than the specified value if the value is not null.
    ///
    /// @param value Comparison value
    /// @return Builder instance
    B ltIfNotNull(U value);

    /// In the specified range.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary value
    /// @return Builder instance
    B between(U l, U r);

    /// Not in the specified range.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary value
    /// @return Builder instance
    B notBetween(U l, U r);

    /**
     * Greater than or equal to another expression.
     *
     * @param expression Another expression
     * @return Builder instance
     */
    B ge(TypedExpression<T, U> expression);

    /**
     * Greater than another expression.
     *
     * @param expression Another expression
     * @return Builder instance
     */
    B gt(TypedExpression<T, U> expression);

    /**
     * Less than or equal to another expression.
     *
     * @param expression Another expression
     * @return Builder instance
     */
    B le(TypedExpression<T, U> expression);

    /**
     * Less than another expression.
     *
     * @param expression Another expression
     * @return Builder instance
     */
    B lt(TypedExpression<T, U> expression);

    /**
     * Between two expressions.
     *
     * @param l Left boundary expression
     * @param r Right boundary expression
     * @return Builder instance
     */
    B between(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * Between expression and value.
     *
     * @param l Left boundary expression
     * @param r Right boundary value
     * @return Builder instance
     */
    B between(TypedExpression<T, U> l, U r);

    /**
     * Between value and expression.
     *
     * @param l Left boundary value
     * @param r Right boundary expression
     * @return Builder instance
     */
    B between(U l, TypedExpression<T, U> r);

    /**
     * Not between two expressions.
     *
     * @param l Left boundary expression
     * @param r Right boundary expression
     * @return Builder instance
     */
    B notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /**
     * Not between expression and value.
     *
     * @param l Left boundary expression
     * @param r Right boundary value
     * @return Builder instance
     */
    B notBetween(TypedExpression<T, U> l, U r);

    /**
     * Not between value and expression.
     *
     * @param l Left boundary value
     * @param r Right boundary expression
     * @return Builder instance
     */
    B notBetween(U l, TypedExpression<T, U> r);

    /// Number operator interface, providing number-specific operation methods.
    ///
    /// @param <T> Entity type
    /// @param <U> Number type
    /// @param <B> Builder return type
    interface NumberOperator<T, U extends Number, B> extends ExpressionBuilder<T, U, B> {
        /// Addition operation, adds the specified value.
        ///
        /// @param value Value to add
        /// @return Number operator instance
        NumberOperator<T, U, B> add(U value);

        /// Subtraction operation, subtracts the specified value.
        ///
        /// @param value Value to subtract
        /// @return Number operator instance
        NumberOperator<T, U, B> subtract(U value);

        /// Multiplication operation, multiplies the specified value.
        ///
        /// @param value Value to multiply
        /// @return Number operator instance
        NumberOperator<T, U, B> multiply(U value);

        /// Division operation, divides the specified value.
        ///
        /// @param value Value to divide
        /// @return Number operator instance
        NumberOperator<T, U, B> divide(U value);

        /// Modulo operation, modulo the specified value.
        ///
        /// @param value Value to modulo
        /// @return Number operator instance
        NumberOperator<T, U, B> mod(U value);

        /// Conditional addition operation, adds the specified value if not null.
        ///
        /// @param value Value to add
        /// @return Number operator instance
        default NumberOperator<T, U, B> addIfNotNull(U value) {
            return value == null ? this : add(value);
        }

        /// Conditional subtraction operation, subtracts the specified value if not null.
        ///
        /// @param value Value to subtract
        /// @return Number operator instance
        default NumberOperator<T, U, B> subtractIfNotNull(U value) {
            return value == null ? this : subtract(value);
        }

        /// Conditional multiplication operation, multiplies the specified value if not null.
        ///
        /// @param value Value to multiply
        /// @return Number operator instance
        default NumberOperator<T, U, B> multiplyIfNotNull(U value) {
            return value == null ? this : multiply(value);
        }

        /// Conditional division operation, divides the specified value if not null.
        ///
        /// @param value Value to divide
        /// @return Number operator instance
        default NumberOperator<T, U, B> divideIfNotNull(U value) {
            return value == null ? this : divide(value);
        }

        /// Conditional modulo operation, modulo the specified value if not null.
        ///
        /// @param value Value to modulo
        /// @return Number operator instance
        default NumberOperator<T, U, B> modIfNotNull(U value) {
            return value == null ? this : mod(value);
        }

        /// Addition operation, adds another expression.
        ///
        /// @param expression Another expression
        /// @return Number operator instance
        NumberOperator<T, U, B> add(TypedExpression<T, U> expression);

        /// Subtraction operation, subtracts another expression.
        ///
        /// @param expression Another expression
        /// @return Number operator instance
        NumberOperator<T, U, B> subtract(TypedExpression<T, U> expression);

        /// Multiplication operation, multiplies another expression.
        ///
        /// @param expression Another expression
        /// @return Number operator instance
        NumberOperator<T, U, B> multiply(TypedExpression<T, U> expression);

        /// Division operation, divides another expression.
        ///
        /// @param expression Another expression
        /// @return Number operator instance
        NumberOperator<T, U, B> divide(TypedExpression<T, U> expression);

        /// Modulo operation, modulo another expression.
        ///
        /// @param expression Another expression
        /// @return Number operator instance
        NumberOperator<T, U, B> mod(TypedExpression<T, U> expression);

    }

    /// Path operator interface, providing path-related operation methods.
    ///
    /// @param <T> Entity type
    /// @param <U> Path value type
    /// @param <B> Builder return type
    interface PathOperator<T, U, B> extends ExpressionBuilder<T, U, B> {

        /// Gets the path operator of the specified path.
        ///
        /// @param path Path
        /// @param <V> Path value type
        /// @return Path operator instance
        <V> PathOperator<T, V, B> get(PathRef<U, V> path);

        /// Gets the string operator of the specified string path.
        ///
        /// @param path String path
        /// @return String operator instance
        StringOperator<T, B> get(PathRef.StringRef<U> path);

        /// Gets the number operator of the specified number path.
        ///
        /// @param path Number path
        /// @param <V> Number type
        /// @return Number operator instance
        <V extends Number> NumberOperator<T, V, B> get(PathRef.NumberRef<U, V> path);

    }

    /// String operator interface, providing string-specific operation methods.
    ///
    /// @param <T> Entity type
    /// @param <B> Builder return type
    interface StringOperator<T, B> extends ExpressionBuilder<T, String, B> {

        /// Equals the specified value if the string is not empty.
        ///
        /// @param value Comparison value
        /// @return String operator instance
        B eqIfNotEmpty(String value);

        /// Fuzzy matches the specified value.
        ///
        /// @param value Match value
        /// @return String operator instance
        B like(String value);

        /// Starts with the specified value.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B startsWith(String value) {
            return like(value + '%');
        }

        /// Ends with the specified value.
        ///
        /// @param value End value
        /// @return String operator instance
        default B endsWith(String value) {
            return like('%' + value);
        }

        /// Contains the specified value.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B contains(String value) {
            return like('%' + value + '%');
        }

        /// Not fuzzy matches the specified value.
        ///
        /// @param value Match value
        /// @return String operator instance
        B notLike(String value);

        /// Does not start with the specified value.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B notStartsWith(String value) {
            return notLike(value + '%');
        }

        /// Does not end with the specified value.
        ///
        /// @param value End value
        /// @return String operator instance
        default B notEndsWith(String value) {
            return notLike('%' + value);
        }

        /// Does not contain the specified value.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B notContains(String value) {
            return notLike('%' + value + '%');
        }

        /// Fuzzy matches the specified value if the value is not null.
        ///
        /// @param value Match value
        /// @return String operator instance
        B likeIfNotNull(String value);

        /// Starts with the specified value if the value is not null.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B startsWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : value + '%');
        }

        /// Ends with the specified value if the value is not null.
        ///
        /// @param value End value
        /// @return String operator instance
        default B endsWithIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value);
        }

        /// Contains the specified value if the value is not null.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B containsIfNotNull(String value) {
            return likeIfNotNull(value == null ? null : '%' + value + '%');
        }

        /// Not fuzzy matches the specified value if the value is not null.
        ///
        /// @param value Match value
        /// @return String operator instance
        B notLikeIfNotNull(String value);

        /// Does not start with the specified value if the value is not null.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B notStartsWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : value + '%');
        }

        /// Does not end with the specified value if the value is not null.
        ///
        /// @param value End value
        /// @return String operator instance
        default B notEndsWithIfNotNull(String value) {
            return notLikeIfNotNull(value == null ? null : '%' + value);
        }

        /// Does not contain the specified value if the value is not null.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B notContainsIfNotNull(String value) {
            return notLikeIfNotNull(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /// Fuzzy matches the specified value if the string is not empty.
        ///
        /// @param value Match value
        /// @return String operator instance
        default B likeIfNotEmpty(String value) {
            return value == null || value.isEmpty() ? likeIfNotNull(null) : like(value);
        }

        /// Starts with the specified value if the string is not empty.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B startsWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /// Ends with the specified value if the string is not empty.
        ///
        /// @param value End value
        /// @return String operator instance
        default B endsWithIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /// Contains the specified value if the string is not empty.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B containsIfNotEmpty(String value) {
            return likeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /// Not fuzzy matches the specified value if the string is not empty.
        ///
        /// @param value Match value
        /// @return String operator instance
        B notLikeIfNotEmpty(String value);

        /// Does not start with the specified value if the string is not empty.
        ///
        /// @param value Start value
        /// @return String operator instance
        default B notStartsWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : value + '%');
        }

        /// Does not end with the specified value if the string is not empty.
        ///
        /// @param value End value
        /// @return String operator instance
        default B notEndsWithIfNotEmpty(String value) {
            return notLikeIfNotEmpty(value == null || value.isEmpty() ? null : '%' + value);
        }

        /// Does not contain the specified value if the string is not empty.
        ///
        /// @param value Contain value
        /// @return String operator instance
        default B notContainsIfNotEmpty(String value) {
            return notLikeIfNotNull(value == null || value.isEmpty() ? null : '%' + value + '%');
        }

        /// Converts to lowercase.
        ///
        /// @return String operator instance
        StringOperator<T, B> lower();

        /// Converts to uppercase.
        ///
        /// @return String operator instance
        StringOperator<T, B> upper();

        /// Substrings the string.
        ///
        /// @param offset Offset
        /// @param length Length
        /// @return String operator instance
        StringOperator<T, B> substring(int offset, int length);

        /**
         * Substrings the string, from the specified offset to the end.
         *
         * @param offset Offset
         * @return String operator instance
         */
        default StringOperator<T, B> substring(int offset) {
            return substring(offset, Integer.MAX_VALUE);
        }

        /// Trims leading and trailing spaces.
        ///
        /// @return String operator instance
        StringOperator<T, B> trim();

        /// Gets the string length.
        ///
        /// @return Number operator instance
        NumberOperator<T, Integer, B> length();

    }

    /// Conjunction operator interface, providing logical AND operations.
    ///
    /// @param <T> Entity type
    interface Conjunction<T> extends TypedExpression<T, Boolean> {

        /// Joins with the path operator of the specified path.
        ///
        /// @param path Path
        /// @param <R> Path value type
        /// @return Path operator instance
        <R> PathOperator<T, R, Conjunction<T>> and(PathRef<T, R> path);

        /// Joins with the number operator of the specified number path.
        ///
        /// @param path Number path
        /// @param <R> Number type
        /// @return Number operator instance
        <R extends Number> NumberOperator<T, R, Conjunction<T>> and(PathRef.NumberRef<T, R> path);

        /// Joins with the string operator of the specified string path.
        ///
        /// @param path String path
        /// @return String operator instance
        StringOperator<T, Conjunction<T>> and(PathRef.StringRef<T> path);

        /// Joins with another expression.
        ///
        /// @param expression Another expression
        /// @return Conjunction operator instance
        Conjunction<T> and(TypedExpression<T, Boolean> expression);

        /// Joins with multiple expressions.
        ///
        /// @param expressions Expression collection
        /// @return Conjunction operator instance
        Conjunction<T> and(Iterable<? extends TypedExpression<T, Boolean>> expressions);

        /// Converts to predicate.
        ///
        /// @return Predicate instance
        Predicate<T> toPredicate();

    }

    /// Disjunction operator interface, providing logical OR operations.
    ///
    /// @param <T> Entity type
    interface Disjunction<T> extends TypedExpression<T, Boolean> {

        /// Disjoins with the path operator of the specified path.
        ///
        /// @param path Path
        /// @param <N> Path value type
        /// @return Path operator instance
        <N> PathOperator<T, N, Disjunction<T>> or(PathRef<T, N> path);

        /// Disjoins with the number operator of the specified number path.
        ///
        /// @param path Number path
        /// @param <N> Number type
        /// @return Number operator instance
        <N extends Number> NumberOperator<T, N, Disjunction<T>> or(PathRef.NumberRef<T, N> path);

        /// Disjoins with the string operator of the specified string path.
        ///
        /// @param path String path
        /// @return String operator instance
        StringOperator<T, ? extends Disjunction<T>> or(PathRef.StringRef<T> path);

        /// Disjoins with another expression.
        ///
        /// @param predicate Another expression
        /// @return Disjunction operator instance
        Disjunction<T> or(TypedExpression<T, Boolean> predicate);

        /// Disjoins with multiple expressions.
        ///
        /// @param expressions Expression collection
        /// @return Disjunction operator instance
        Disjunction<T> or(Iterable<? extends TypedExpression<T, Boolean>> expressions);

        /// Converts to predicate.
        ///
        /// @return Predicate instance
        Predicate<T> toPredicate();

    }
}
