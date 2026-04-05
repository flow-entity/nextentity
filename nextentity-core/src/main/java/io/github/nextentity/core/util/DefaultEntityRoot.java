package io.github.nextentity.core.util;

import io.github.nextentity.api.*;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.*;

public class DefaultEntityRoot<T> implements EntityRoot<T> {

    private static final DefaultEntityRoot<?> INSTANCE = new DefaultEntityRoot<>();

    public static <T> EntityRoot<T> of() {
        return TypeCastUtil.cast(INSTANCE);
    }

    protected DefaultEntityRoot() {
    }

    @Override
    public <U> Expression<T, U> literal(U value) {
        return new SimpleExpressionImpl<>(new LiteralNode(value));
    }

    @Override
    public <U> Path<T, U> path(PathRef<T, U> path) {
        return new SimpleExpressionImpl<>(PathNode.of(path));
    }

    @Override
    public <U> EntityPath<T, U> entity(PathRef<T, U> path) {
        return new SimpleExpressionImpl<>(PathNode.of(path));
    }

    @Override
    public <U> EntityPath<T, U> get(PathRef<T, U> path) {
        return new SimpleExpressionImpl<>(PathNode.of(path));
    }

    @Override
    public BooleanPath<T> get(PathRef.BooleanRef<T> path) {
        return new PredicateImpl<>(PathNode.of(path));
    }

    @Override
    public StringPath<T> get(PathRef.StringRef<T> path) {
        return string(path);
    }

    @Override
    public <U extends Number> NumberPath<T, U> get(PathRef.NumberRef<T, U> path) {
        return number(path);
    }

    @Override
    public StringPath<T> string(PathRef<T, String> path) {
        return new StringExpressionImpl<>(PathNode.of(path));
    }

    @Override
    public <U extends Number> NumberPath<T, U> number(PathRef<T, U> path) {
        return new NumberExpressionImpl<>(PathNode.of(path));
    }

    @Override
    public BooleanPath<T> bool(PathRef<T, Boolean> path) {
        return new PredicateImpl<>(PathNode.of(path));
    }

    @Override
    public <U> Path<T, U> path(String fieldName) {
        return new SimpleExpressionImpl<>(new PathNode(new String[]{fieldName}));
    }

    @Override
    public <U> EntityPath<T, U> entityPath(String fieldName) {
        return new SimpleExpressionImpl<>(new PathNode(new String[]{fieldName}));
    }

    @Override
    public StringPath<T> stringPath(String fieldName) {
        return new StringExpressionImpl<>(new PathNode(new String[]{fieldName}));
    }

    @Override
    public <U extends Number> NumberPath<T, U> numberPath(String fieldName) {
        return new NumberExpressionImpl<>(new PathNode(new String[]{fieldName}));
    }

    @Override
    public BooleanPath<T> booleanPath(String fieldName) {
        return new PredicateImpl<>(new PathNode(new String[]{fieldName}));
    }

}
