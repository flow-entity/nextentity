package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SimpleAttribute implements Attribute {

    private Class<?> type;

    private String name;

    private Method getter;

    private Method setter;

    private Field field;

    private Schema declareBy;

    private int ordinal;

    private volatile ImmutableList<String> path;

    public SimpleAttribute() {
    }

    public SimpleAttribute(Class<?> type, String name, Method getter, Method setter, Field field, Schema declareBy, int ordinal) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
        this.declareBy = declareBy;
        this.ordinal = ordinal;
    }

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

    public SimpleAttribute type(Class<?> type) {
        this.type = type;
        return this;
    }

    public SimpleAttribute name(String name) {
        this.name = name;
        return this;
    }

    public SimpleAttribute getter(Method getter) {
        this.getter = getter;
        return this;
    }

    public SimpleAttribute setter(Method setter) {
        this.setter = setter;
        return this;
    }

    public SimpleAttribute field(Field field) {
        this.field = field;
        return this;
    }

    public SimpleAttribute declareBy(Schema declareBy) {
        this.declareBy = declareBy;
        return this;
    }

    public SimpleAttribute ordinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }
}
