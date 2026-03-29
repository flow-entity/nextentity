package io.github.nextentity.core.util;

import io.github.nextentity.api.*;
import io.github.nextentity.api.PathRef.BooleanRef;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

@Deprecated
public interface Paths {

    @Deprecated
    static <T> EntityRoot<T> root() {
        return DefaultEntityRoot.of();
    }

    @Deprecated
    static <T, U> EntityPath<T, U> get(PathRef<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    @Deprecated
    static <T> BooleanPath<T> get(BooleanRef<T> path) {
        return Paths.<T>root().get(path);
    }

    @Deprecated
    static <T> StringPath<T> get(StringRef<T> path) {
        return Paths.<T>root().get(path);
    }

    @Deprecated
    static <T, U extends Number> NumberPath<T, U> get(NumberRef<T, U> path) {
        return Paths.<T>root().get(path);
    }

    @Deprecated
    static <T, U> Path<T, U> path(PathRef<T, U> path) {
        return Paths.<T>root().get(path);
    }

    @Deprecated
    static <T, U> EntityPath<T, U> entity(PathRef<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    @Deprecated
    static <T> StringPath<T> string(PathRef<T, String> path) {
        return Paths.<T>root().string(path);
    }

    @Deprecated
    static <T, U extends Number> NumberPath<T, U> number(PathRef<T, U> path) {
        return Paths.<T>root().number(path);
    }

    @Deprecated
    static <T> BooleanPath<T> bool(PathRef<T, Boolean> path) {
        return Paths.<T>root().bool(path);
    }

    // type-unsafe

    @Deprecated
    static <T, U> Path<T, U> path(String fieldName) {
        return Paths.<T>root().path(fieldName);
    }

    @Deprecated
    static <T, U> EntityPath<T, U> entityPath(String fieldName) {
        return Paths.<T>root().entityPath(fieldName);
    }

    @Deprecated
    static <T> StringPath<T> stringPath(String fieldName) {
        return Paths.<T>root().stringPath(fieldName);
    }

    @Deprecated
    static <T, U extends Number> NumberPath<T, U> numberPath(String fieldName) {
        return Paths.<T>root().numberPath(fieldName);
    }

    @Deprecated
    static <T> BooleanPath<T> booleanPath(String fieldName) {
        return Paths.<T>root().booleanPath(fieldName);
    }

}
