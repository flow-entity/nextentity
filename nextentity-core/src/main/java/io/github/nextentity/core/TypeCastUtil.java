package io.github.nextentity.core;

import io.github.nextentity.api.Expression;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.util.ImmutableArray;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Utility class for type casting operations.
 * <p>
 * This class provides unsafe type casting methods that bypass compile-time type checking.
 * Use with caution - these methods should only be used when the type relationship is guaranteed
 * by the calling code's logic.
 * <p>
 * <strong>Warning:</strong> Improper use of these methods can lead to {@link ClassCastException}
 * at runtime. Always ensure the source and target types are compatible.
 */
public final class TypeCastUtil {

    private TypeCastUtil() {
        // Utility class, prevent instantiation
    }

    /**
     * Casts a List of unknown type to a List of the target type.
     * <p>
     * This method is safe when the list elements are known to be of type T
     * (e.g., when the list was created with T elements but passed through a generic API).
     *
     * @param expression the list to cast
     * @param <T>        the target element type
     * @return the cast list
     */
    public static <T> List<T> cast(List<?> expression) {
        return unsafeCast(expression);
    }

    /**
     * Casts an ImmutableArray of unknown type to an ImmutableArray of the target type.
     *
     * @param expression the array to cast
     * @param <T>        the target element type
     * @return the cast array
     */
    public static <T> ImmutableArray<T> cast(ImmutableArray<?> expression) {
        return unsafeCast(expression);
    }

    /**
     * Casts a Class of unknown type to a Class of the target type.
     *
     * @param resolve the class to cast
     * @param <T>     the target type
     * @return the cast class
     */
    public static <T> Class<T> cast(Class<?> resolve) {
        return unsafeCast(resolve);
    }

    /**
     * Casts an Expression to a TypedExpression.
     *
     * @param expression the expression to cast
     * @param <T>        the entity type
     * @param <U>        the result type
     * @return the cast typed expression
     */
    public static <T, U> TypedExpression<T, U> cast(Expression expression) {
        return unsafeCast(expression);
    }

    /**
     * Casts an EntityRoot of unknown type to an EntityRoot of the target type.
     *
     * @param builder the entity root to cast
     * @param <T>     the target entity type
     * @return the cast entity root
     */
    public static <T> EntityRoot<T> cast(EntityRoot<?> builder) {
        return unsafeCast(builder);
    }

    /**
     * Performs an unchecked cast of an object to the target type.
     * <p>
     * <strong>Warning:</strong> This method bypasses compile-time type checking.
     * Use only when the type relationship is guaranteed by the calling code's logic.
     * <p>
     * Example safe usage:
     * <pre>{@code
     * // Safe: We know the list contains String elements
     * List<String> strings = TypeCastUtil.unsafeCast(Arrays.asList("a", "b", "c"));
     * }</pre>
     * <p>
     * Example unsafe usage (will cause ClassCastException at runtime):
     * <pre>{@code
     * // Unsafe: List contains Integer, not String
     * List<String> strings = TypeCastUtil.unsafeCast(Arrays.asList(1, 2, 3));
     * String s = strings.get(0); // ClassCastException here
     * }</pre>
     *
     * @param object the object to cast
     * @param <T>    the target type
     * @return the cast object
     */
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(@Nullable Object object) {
        return (T) object;
    }

}
