package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

@FunctionalInterface
public interface Path<T, R> extends Serializable {

    R apply(T t);

    interface NumberPath<T, R extends Number> extends Path<T, R> {
    }

    interface BooleanPath<T> extends Path<T, Boolean> {
    }

    interface StringPath<T> extends Path<T, String> {
    }

    interface LongPath<T> extends NumberPath<T, Long> {
    }

    interface IntegerPath<T> extends NumberPath<T, Integer> {
    }

    interface ShortPath<T> extends NumberPath<T, Short> {
    }

    interface BytePath<T> extends NumberPath<T, Byte> {
    }

    interface DoublePath<T> extends NumberPath<T, Double> {
    }

    interface FloatPath<T> extends NumberPath<T, Float> {
    }

    interface BigDecimalPath<T> extends NumberPath<T, BigDecimal> {
    }

}
