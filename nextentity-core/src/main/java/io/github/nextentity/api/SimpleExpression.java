package io.github.nextentity.api;

import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.core.util.Paths;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/// Simple expression interface, providing basic expression operation methods.
///
/// @param <T> Entity type
/// @param <U> Expression value type
/// @author HuangChengwei
/// @since 1.0.0
public interface SimpleExpression<T, U> extends TypedExpression<T, U> {

    private EntityRoot<T> root() {
        return Paths.root();
    }

    /// Counts the number of expression values.
    ///
    /// @return Count expression
    NumberExpression<T, Long> count();

    /// Counts the number of distinct expression values.
    ///
    /// @return Distinct count expression
    NumberExpression<T, Long> countDistinct();

    /// Equals the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> eq(U value);

    /// Equals the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> eqIfNotNull(U value);

    /// Equals the value of another expression.
    ///
    /// @param value Another expression
    /// @return Predicate object
    Predicate<T> eq(TypedExpression<T, U> value);

    /// Not equals the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> ne(U value);

    /// Not equals the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> neIfNotNull(U value);

    /// Not equals the value of another expression.
    ///
    /// @param value Another expression
    /// @return Predicate object
    Predicate<T> ne(TypedExpression<T, U> value);

    /// In the values of the specified expression list.
    ///
    /// @param expressions Expression list
    /// @return Predicate object
    Predicate<T> in(@NonNull TypedExpression<T, List<U>> expressions);

    /// In the specified value array.
    ///
    /// @param values Value array
    /// @return Predicate object
    @SuppressWarnings("unchecked")
    Predicate<T> in(U... values);

    /// In the values of the specified expression list.
    ///
    /// @param values Expression list
    /// @return Predicate object
    Predicate<T> in(@NonNull List<? extends TypedExpression<T, U>> values);

    /// In the specified collection.
    ///
    /// @param values Value collection
    /// @return Predicate object
    Predicate<T> in(@NonNull Collection<? extends U> values);

    /// Not in the specified value array.
    ///
    /// @param values Value array
    /// @return Predicate object
    @SuppressWarnings("unchecked")
    Predicate<T> notIn(U... values);

    /// Not in the values of the specified expression list.
    ///
    /// @param values Expression list
    /// @return Predicate object
    Predicate<T> notIn(@NonNull List<? extends TypedExpression<T, U>> values);

    /// Not in the specified collection.
    ///
    /// @param values Value collection
    /// @return Predicate object
    Predicate<T> notIn(@NonNull Collection<? extends U> values);

    /// Value is null.
    ///
    /// @return Predicate object
    Predicate<T> isNull();

    /// Value is not null.
    ///
    /// @return Predicate object
    Predicate<T> isNotNull();

    /// Greater than or equal to the value of another expression.
    ///
    /// @param expression Another expression
    /// @return Predicate object
    Predicate<T> ge(TypedExpression<T, U> expression);

    /// Greater than the value of another expression.
    ///
    /// @param expression Another expression
    /// @return Predicate object
    Predicate<T> gt(TypedExpression<T, U> expression);

    /// Less than or equal to the value of another expression.
    ///
    /// @param expression Another expression
    /// @return Predicate object
    Predicate<T> le(TypedExpression<T, U> expression);

    /// Less than the value of another expression.
    ///
    /// @param expression Another expression
    /// @return Predicate object
    Predicate<T> lt(TypedExpression<T, U> expression);

    /// Between the values of two expressions.
    ///
    /// @param l Left boundary expression
    /// @param r Right boundary expression
    /// @return Predicate object
    Predicate<T> between(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /// Not between the values of two expressions.
    ///
    /// @param l Left boundary expression
    /// @param r Right boundary expression
    /// @return Predicate object
    Predicate<T> notBetween(TypedExpression<T, U> l, TypedExpression<T, U> r);

    /// Sorts in ascending order.
    ///
    /// @return Order object
    default Order<T> asc() {
        return sort(SortOrder.ASC);
    }

    /// Sorts in descending order.
    ///
    /// @return Order object
    default Order<T> desc() {
        return sort(SortOrder.DESC);
    }

    /// Sorts by the specified sort order.
    ///
    /// @param order Sort order
    /// @return Order object
    Order<T> sort(SortOrder order);

    /// Greater than or equal to the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> ge(U value);

    /// Greater than the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> gt(U value);

    /// Less than or equal to the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> le(U value);

    /// Less than the specified value.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> lt(U value);

    /// Greater than or equal to the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> geIfNotNull(U value);

    /// Greater than the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> gtIfNotNull(U value);

    /// Less than or equal to the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> leIfNotNull(U value);

    /// Less than the specified value if not null.
    ///
    /// @param value Comparison value
    /// @return Predicate object
    Predicate<T> ltIfNotNull(U value);

    /// Between two values.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary value
    /// @return Predicate object
    Predicate<T> between(U l, U r);

    /// Not between two values.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary value
    /// @return Predicate object
    Predicate<T> notBetween(U l, U r);

    /// Between expression and value.
    ///
    /// @param l Left boundary expression
    /// @param r Right boundary value
    /// @return Predicate object
    Predicate<T> between(TypedExpression<T, U> l, U r);

    /// Between value and expression.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary expression
    /// @return Predicate object
    Predicate<T> between(U l, TypedExpression<T, U> r);

    /// Not between expression and value.
    ///
    /// @param l Left boundary expression
    /// @param r Right boundary value
    /// @return Predicate object
    Predicate<T> notBetween(TypedExpression<T, U> l, U r);

    /// Not between value and expression.
    ///
    /// @param l Left boundary value
    /// @param r Right boundary expression
    /// @return Predicate object
    Predicate<T> notBetween(U l, TypedExpression<T, U> r);

    /// Gets the maximum value of the expression.
    ///
    /// @return Maximum expression
    SimpleExpression<T, U> max();

    /// Gets the minimum value of the expression.
    ///
    /// @return Minimum expression
    SimpleExpression<T, U> min();
}
