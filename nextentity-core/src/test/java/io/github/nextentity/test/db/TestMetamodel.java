package io.github.nextentity.test.db;

import io.github.nextentity.core.meta.AbstractMetamodel;
import io.github.nextentity.core.meta.Metamodel;
import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.test.entity.Department;
import io.github.nextentity.test.entity.Employee;
import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Test metamodel implementation using Jakarta Persistence annotations.
 */
public class TestMetamodel extends AbstractMetamodel implements Metamodel {

    public static Metamodel create() {
        return new TestMetamodel();
    }

    @Override
    protected String getTableName(Class<?> javaType) {
        Entity entity = javaType.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        Table table = javaType.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        return javaType.getSimpleName();
    }

    @Override
    protected boolean isMarkedId(Attribute attribute) {
        return hasAnnotation(attribute, Id.class);
    }

    @Override
    protected boolean isUpdatable(Attribute attribute) {
        Column column = getAnnotation(attribute, Column.class);
        return column == null || column.updatable();
    }

    @Override
    protected String getReferencedColumnName(Attribute attribute) {
        JoinColumn joinColumn = getAnnotation(attribute, JoinColumn.class);
        return joinColumn != null && !joinColumn.referencedColumnName().isEmpty()
                ? joinColumn.referencedColumnName()
                : "";
    }

    @Override
    protected String getJoinColumnName(Attribute attribute) {
        JoinColumn joinColumn = getAnnotation(attribute, JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isEmpty()) {
            return joinColumn.name();
        }
        return attribute.name() + "Id";
    }

    @Override
    protected boolean isVersionField(Attribute attribute) {
        return hasAnnotation(attribute, Version.class);
    }

    @Override
    protected boolean isTransient(Attribute attribute) {
        return hasAnnotation(attribute, Transient.class);
    }

    @Override
    protected boolean isBasicField(Attribute attribute) {
        return !isAnyToOne(attribute) && !hasAnnotation(attribute, OneToMany.class);
    }

    @Override
    protected boolean isAnyToOne(Attribute attribute) {
        return hasAnnotation(attribute, ManyToOne.class) ||
               hasAnnotation(attribute, OneToOne.class);
    }

    @Override
    protected String getColumnName(Attribute attribute) {
        Column column = getAnnotation(attribute, Column.class);
        if (column != null && !column.name().isEmpty()) {
            return column.name();
        }
        return attribute.name();
    }

    @Override
    protected Field[] getSuperClassField(Class<?> baseClass, Class<?> superClass) {
        return superClass.getDeclaredFields();
    }

    private <T extends Annotation> boolean hasAnnotation(Attribute attribute, Class<T> annotationClass) {
        return getAnnotation(attribute, annotationClass) != null;
    }
}
