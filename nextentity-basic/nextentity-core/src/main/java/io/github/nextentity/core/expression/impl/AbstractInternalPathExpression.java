package io.github.nextentity.core.expression.impl;

import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.meta.BasicAttribute;
import io.github.nextentity.core.meta.EntitySchema;
import io.github.nextentity.core.reflect.schema.Schema;
import io.github.nextentity.core.util.Iterators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.Stream;

public interface AbstractInternalPathExpression extends InternalPathExpression {
    String[] paths();

    default InternalPathExpression newInstance(String[] paths) {
        return new InternalPathExpressionImpl(paths);
    }

    @Override
    default int deep() {
        return paths().length;
    }

    @Override
    default String get(int i) {
        return paths()[i];
    }

    @Override
    default InternalPathExpression get(String path) {
        String[] strings = new String[deep() + 1];
        System.arraycopy(paths(), 0, strings, 0, paths().length);
        strings[deep()] = path;
        return newInstance(strings);
    }

    @Override
    default InternalPathExpression parent() {
        return sub(deep() - 1);
    }

    @Override
    default InternalPathExpression subLength(int len) {
        if (len == deep()) {
            return this;
        }
        if (len > deep()) {
            throw new IndexOutOfBoundsException();
        }
        return sub(len);
    }

    @Override
    default BasicAttribute toAttribute(EntitySchema entityType) {
        Schema type = entityType;
        for (String s : this) {
            type = ((EntitySchema) type).getAttribute(s);
        }
        return (BasicAttribute) type;
    }

    @Override
    default Stream<String> stream() {
        return Stream.of(paths());
    }

    @Nullable
    private InternalPathExpression sub(int len) {
        if (len <= 0) {
            return null;
        }
        String[] strings = new String[len];
        System.arraycopy(paths(), 0, strings, 0, strings.length);
        return newInstance(strings);
    }

    @NotNull
    @Override
    default Iterator<String> iterator() {
        return Iterators.iterate(paths());
    }

    @Override
    default InternalPathExpression get(InternalPathExpression path) {
        String[] paths = new String[deep() + path.deep()];
        int i = 0;
        for (String s : this) {
            paths[i++] = s;
        }
        for (String s : path) {
            paths[i++] = s;
        }
        return newInstance(paths);
    }

}
