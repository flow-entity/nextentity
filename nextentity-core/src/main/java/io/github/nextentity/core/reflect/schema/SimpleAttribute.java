package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

///
/// Simple implementation of {@link Attribute}.
/// <p>
/// This class provides a concrete implementation for attribute metadata
/// including name, type, getter/setter methods, field reference, and
/// path computation for nested attributes.
///
/// @author HuangChengwei
/// @since 1.0.0
///
public class SimpleAttribute implements Attribute {

    private Class<?> type;

    private String name;

    private Method getter;

    private Method setter;

    private Field field;

    private Schema declareBy;

    private int ordinal;

    private volatile ImmutableList<String> path;

    ///
    /// Creates an empty SimpleAttribute instance.
    ///
    public SimpleAttribute() {
    }

    ///
    /// Creates a new SimpleAttribute instance with all properties.
    ///
    /// @param type the attribute type
    /// @param name the attribute name
    /// @param getter the getter method
    /// @param setter the setter method
    /// @param field the field
    /// @param declareBy the declaring schema
    /// @param ordinal the ordinal position
    ///
    public SimpleAttribute(Class<?> type, String name, Method getter, Method setter, Field field, Schema declareBy, int ordinal) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
        this.declareBy = declareBy;
        this.ordinal = ordinal;
    }

    ///
    /// Copies properties from another attribute.
    ///
    /// @param attribute the source attribute
    ///
    public void setAttribute(Attribute attribute) {
        this.type = attribute.type();
        this.name = attribute.name();
        this.getter = attribute.getter();
        this.setter = attribute.setter();
        this.field = attribute.field();
        this.declareBy = attribute.declareBy();
        this.ordinal = attribute.ordinal();
    }

    public Class<?> type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public Method getter() {
        return this.getter;
    }

    public Method setter() {
        return this.setter;
    }

    public Field field() {
        return this.field;
    }

    public Schema declareBy() {
        return this.declareBy;
    }

    ///
    /// Computes the path for this attribute.
    /// <p>
    /// For nested attributes, the path includes parent attribute names.
    /// Uses double-checked locking for thread-safe lazy initialization.
    ///
    /// @return the attribute path as an immutable list of names
    ///
    @Override
    public ImmutableList<String> path() {
        if(path == null) {
            synchronized (this) {
                if(path == null) {
                    Schema schema = declareBy();
                    if (schema instanceof Attribute p) {
                        ImmutableList<String> pp = p.path();
                        String[] strings = new String[pp.size() + 1];
                        for (int i = 0; i < pp.size(); i++) {
                            strings[i] = pp.get(i);
                        }
                        strings[pp.size()] = name();
                        path = ImmutableList.of(strings);
                    } else {
                        path = ImmutableList.of(name());
                    }
                }
            }
        }
        return path;
    }

    public int ordinal() {
        return this.ordinal;
    }

    ///
    /// Sets the attribute type.
    ///
    /// @param type the type
    /// @return this instance for chaining
    ///
    public SimpleAttribute type(Class<?> type) {
        this.type = type;
        return this;
    }

    ///
    /// Sets the attribute name.
    ///
    /// @param name the name
    /// @return this instance for chaining
    ///
    public SimpleAttribute name(String name) {
        this.name = name;
        return this;
    }

    ///
    /// Sets the getter method.
    ///
    /// @param getter the getter method
    /// @return this instance for chaining
    ///
    public SimpleAttribute getter(Method getter) {
        this.getter = getter;
        return this;
    }

    ///
    /// Sets the setter method.
    ///
    /// @param setter the setter method
    /// @return this instance for chaining
    ///
    public SimpleAttribute setter(Method setter) {
        this.setter = setter;
        return this;
    }

    ///
    /// Sets the field.
    ///
    /// @param field the field
    /// @return this instance for chaining
    ///
    public SimpleAttribute field(Field field) {
        this.field = field;
        return this;
    }

    ///
    /// Sets the declaring schema.
    ///
    /// @param declareBy the declaring schema
    /// @return this instance for chaining
    ///
    public SimpleAttribute declareBy(Schema declareBy) {
        this.declareBy = declareBy;
        return this;
    }

    ///
    /// Sets the ordinal position.
    ///
    /// @param ordinal the ordinal
    /// @return this instance for chaining
    ///
    public SimpleAttribute ordinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }
}
