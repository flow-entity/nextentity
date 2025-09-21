package io.github.nextentity.core.util;

import io.github.nextentity.api.*;
import io.github.nextentity.api.Path.BooleanRef;
import io.github.nextentity.api.Path.NumberRef;
import io.github.nextentity.api.Path.StringRef;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.*;

public interface Paths {

    static <T> EntityRoot<T> root() {
        return RootImpl.of();
    }

    static <T, U> EntityPath<T, U> get(Path<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    static <T> BooleanPath<T> get(BooleanRef<T> path) {
        return Paths.<T>root().get(path);
    }

    static <T> StringPath<T> get(StringRef<T> path) {
        return Paths.<T>root().get(path);
    }

    static <T, U extends Number> NumberPath<T, U> get(NumberRef<T, U> path) {
        return Paths.<T>root().get(path);
    }

    static <T, U> PathExpression<T, U> path(Path<T, U> path) {
        return Paths.<T>root().get(path);
    }

    static <T, U> EntityPath<T, U> entity(Path<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    static <T> StringPath<T> string(Path<T, String> path) {
        return Paths.<T>root().string(path);
    }

    static <T, U extends Number> NumberPath<T, U> number(Path<T, U> path) {
        return Paths.<T>root().number(path);
    }

    static <T> BooleanPath<T> bool(Path<T, Boolean> path) {
        return Paths.<T>root().bool(path);
    }

    // type-unsafe

    static <T, U> PathExpression<T, U> path(String fieldName) {
        return Paths.<T>root().path(fieldName);
    }

    static <T, U> EntityPath<T, U> entityPath(String fieldName) {
        return Paths.<T>root().entityPath(fieldName);
    }

    static <T> StringPath<T> stringPath(String fieldName) {
        return Paths.<T>root().stringPath(fieldName);
    }

    static <T, U extends Number> NumberPath<T, U> numberPath(String fieldName) {
        return Paths.<T>root().numberPath(fieldName);
    }

    static <T> BooleanPath<T> booleanPath(String fieldName) {
        return Paths.<T>root().booleanPath(fieldName);
    }

    class RootImpl<T> implements EntityRoot<T> {

        private static final RootImpl<?> INSTANCE = new RootImpl<>();

        public static <T> EntityRoot<T> of() {
            return TypeCastUtil.cast(INSTANCE);
        }

        protected RootImpl() {
        }

        @Override
        public <U> TypedExpression<T, U> literal(U value) {
            return new SimpleExpressionImpl<>(new LiteralNode(value));
        }

        @Override
        public <U> PathExpression<T, U> path(Path<T, U> path) {
            return new SimpleExpressionImpl<>(PathNode.of(path));
        }

        @Override
        public <U> EntityPath<T, U> entity(Path<T, U> path) {
            return new SimpleExpressionImpl<>(PathNode.of(path));
        }

        @Override
        public <U> EntityPath<T, U> get(Path<T, U> path) {
            return new SimpleExpressionImpl<>(PathNode.of(path));
        }

        @Override
        public BooleanPath<T> get(BooleanRef<T> path) {
            return new PredicateImpl<>(PathNode.of(path));
        }

        @Override
        public StringPath<T> get(StringRef<T> path) {
            return string(path);
        }

        @Override
        public <U extends Number> NumberPath<T, U> get(NumberRef<T, U> path) {
            return number(path);
        }

        @Override
        public StringPath<T> string(Path<T, String> path) {
            return new StringExpressionImpl<>(PathNode.of(path));
        }

        @Override
        public <U extends Number> NumberPath<T, U> number(Path<T, U> path) {
            return new NumberExpressionImpl<>(PathNode.of(path));
        }

        @Override
        public BooleanPath<T> bool(Path<T, Boolean> path) {
            return new PredicateImpl<>(PathNode.of(path));

        }

        @Override
        public <U> PathExpression<T, U> path(String fieldName) {
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
}
