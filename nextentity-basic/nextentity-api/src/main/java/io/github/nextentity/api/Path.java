package io.github.nextentity.api;

import java.io.Serializable;
import java.math.BigDecimal;

@FunctionalInterface
public interface Path<T, R> extends Serializable {

    R apply(T t);

    interface NumberRef<T, R extends Number> extends Path<T, R> {
    }

    interface BooleanRef<T> extends Path<T, Boolean> {
    }

    interface StringRef<T> extends Path<T, String> {
    }

    interface LongRef<T> extends NumberRef<T, Long> {
    }

    interface IntegerRef<T> extends NumberRef<T, Integer> {
    }

    interface ShortRef<T> extends NumberRef<T, Short> {
    }

    interface ByteRef<T> extends NumberRef<T, Byte> {
    }

    interface DoubleRef<T> extends NumberRef<T, Double> {
    }

    interface FloatRef<T> extends NumberRef<T, Float> {
    }

    interface BigDecimalRef<T> extends NumberRef<T, BigDecimal> {
    }

}
