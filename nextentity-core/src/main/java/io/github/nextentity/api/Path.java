package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Path interface, representing the path reference to an entity attribute.
 * <p>
 * Used to reference entity attributes in queries, and is a functional interface.
 * <p>
 * Usage: This interface and all its sub-interfaces (NumberRef, BooleanRef, StringRef, LongRef, etc.)
 * are not intended to be implemented directly by user classes. Instead, they are designed to be
 * used as method references for type-safe property access in query expressions. For example:
 * <pre>{@code
 * // Using method reference as a path expression
 * repository.select(User.class)
 *     .where(User::getId).eq(1L)
 *     .getList();
 *
 * // User::getId is automatically converted to a Path<User, Long>
 * // User::getName becomes Path<User, String> (StringRef)
 * // User::isActive becomes Path<User, Boolean> (BooleanRef)
 * }</pre>
 *
 * @param <T> Entity type
 * @param <R> Attribute type
 * @author HuangChengwei
 * @since 1.0.0
 */
@FunctionalInterface
public interface Path<T, R> extends Serializable {

    /**
     * Applies the path to the entity and gets the attribute value.
     *
     * @param t Entity object
     * @return Attribute value
     */
    R apply(T t);

    /**
     * Number reference interface, representing the path of the entity's numeric type attribute.
     *
     * @param <T> Entity type
     * @param <R> Numeric type
     */
    interface NumberRef<T, R extends Number> extends Path<T, R> {
    }

    /**
     * Boolean reference interface, representing the path of the entity's boolean type attribute.
     *
     * @param <T> Entity type
     */
    interface BooleanRef<T> extends Path<T, Boolean> {
    }

    /**
     * String reference interface, representing the path of the entity's string type attribute.
     *
     * @param <T> Entity type
     */
    interface StringRef<T> extends Path<T, String> {
    }

    /**
     * Long reference interface, representing the path of the entity's long type attribute.
     *
     * @param <T> Entity type
     */
    interface LongRef<T> extends NumberRef<T, Long> {
    }

    /**
     * Integer reference interface, representing the path of the entity's integer type attribute.
     *
     * @param <T> Entity type
     */
    interface IntegerRef<T> extends NumberRef<T, Integer> {
    }

    /**
     * Short reference interface, representing the path of the entity's short type attribute.
     *
     * @param <T> Entity type
     */
    interface ShortRef<T> extends NumberRef<T, Short> {
    }

    /**
     * Byte reference interface, representing the path of the entity's byte type attribute.
     *
     * @param <T> Entity type
     */
    interface ByteRef<T> extends NumberRef<T, Byte> {
    }

    /**
     * Double reference interface, representing the path of the entity's double type attribute.
     *
     * @param <T> Entity type
     */
    interface DoubleRef<T> extends NumberRef<T, Double> {
    }

    /**
     * Float reference interface, representing the path of the entity's float type attribute.
     *
     * @param <T> Entity type
     */
    interface FloatRef<T> extends NumberRef<T, Float> {
    }

    /**
     * BigDecimal reference interface, representing the path of the entity's BigDecimal type attribute.
     *
     * @param <T> Entity type
     */
    interface BigDecimalRef<T> extends NumberRef<T, BigDecimal> {
    }

}
