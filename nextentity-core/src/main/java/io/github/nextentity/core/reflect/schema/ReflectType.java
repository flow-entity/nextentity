package io.github.nextentity.core.reflect.schema;

/**
 * Base interface for reflective type information.
 * <p>
 * This sealed interface is the root of the type hierarchy for schema
 * and attribute metadata. It permits two subtypes:
 * <ul>
 *   <li>{@link Attribute} - represents a single field/property</li>
 *   <li>{@link Schema} - represents a structured type with multiple attributes</li>
 * </ul>
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public sealed interface ReflectType permits Attribute, Schema {

    /**
     * Gets the Java type represented by this reflective type.
     *
     * @return the Java class
     */
    Class<?> type();

    /**
     * Indicates if this type is an object type (has attributes).
     *
     * @return true if this is an object type, false if primitive
     */
    default boolean isObject() {
        return false;
    }

    /**
     * Indicates if this type is a primitive type (no attributes).
     *
     * @return true if this is a primitive type, false if object
     */
    default boolean isPrimitive() {
        return !isObject();
    }

}
