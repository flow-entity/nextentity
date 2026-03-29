package io.github.nextentity.core.reflect.schema;


import io.github.nextentity.core.exception.ReflectiveException;
import io.github.nextentity.core.reflect.ReflectUtil;
import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

///
/// Attribute interface representing a field/property of a schema.
/// <p>
/// This interface provides metadata about a single attribute including:
/// <ul>
///   <li>Name and type</li>
///   <li>Getter and setter methods</li>
///   <li>Field reference</li>
///   <li>Declaring schema</li>
///   <li>Path for nested attributes</li>
/// </ul>
/// <p>
/// Also provides methods to get and set attribute values on entity instances.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public non-sealed interface Attribute extends ReflectType {

    ///
    /// Gets the attribute name.
    ///
    /// @return the name
    ///
    String name();

    ///
    /// Gets the getter method for this attribute.
    ///
    /// @return the getter method, or null if not available
    ///
    Method getter();

    ///
    /// Gets the setter method for this attribute.
    ///
    /// @return the setter method, or null if not available
    ///
    Method setter();

    ///
    /// Gets the field for this attribute.
    ///
    /// @return the field, or null if not available
    ///
    Field field();

    ///
    /// Gets the schema that declares this attribute.
    ///
    /// @return the declaring schema
    ///
    Schema declareBy();

    ///
    /// Gets the path of this attribute.
    /// <p>
    /// For nested attributes, the path includes all parent attribute names.
    ///
    /// @return the attribute path as an immutable list of names
    ///
    ImmutableList<String> path();

    ///
    /// Gets the ordinal position of this attribute.
    ///
    /// @return the ordinal
    ///
    int ordinal();

    ///
    /// Gets the depth of this attribute in the path hierarchy.
    ///
    /// @return the path depth
    ///
    default int deep() {
        return path().size();
    }

    ///
    /// Gets the attribute value from an entity instance.
    /// <p>
    /// Uses the getter method if accessible, otherwise accesses the field directly.
    ///
    /// @param entity the entity instance
    /// @return the attribute value
    /// @throws ReflectiveException if access fails
    ///
    default Object get(Object entity) {
        try {
            Method getter = getter();
            if (getter != null && ReflectUtil.isAccessible(getter, entity)) {
                return getter.invoke(entity);
            } else {
                return ReflectUtil.getFieldValue(field(), entity);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

    ///
    /// Sets the attribute value on an entity instance.
    /// <p>
    /// Uses the setter method if accessible, otherwise sets the field directly.
    ///
    /// @param entity the entity instance
    /// @param value the value to set
    /// @throws ReflectiveException if access fails
    ///
    default void set(Object entity, Object value) {
        try {
            Method setter = setter();
            if (setter != null && ReflectUtil.isAccessible(setter, entity)) {
                ReflectUtil.typeCheck(value, setter.getParameterTypes()[0]);
                setter.invoke(entity, value);
            } else {
                ReflectUtil.typeCheck(value, field().getType());
                ReflectUtil.setFieldValue(field(), entity, value);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectiveException(e);
        }
    }

}
