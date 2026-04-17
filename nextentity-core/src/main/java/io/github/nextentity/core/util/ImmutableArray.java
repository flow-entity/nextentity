package io.github.nextentity.core.util;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public interface ImmutableArray<E> extends Sizeable, Iterable<E> {

    Stream<E> stream();

    default List<? extends E> asList() {
        return Iterators.toList(this);
    }

    E get(int index);

    <T> T[] toArray(@NonNull IntFunction<T[]> generator);

}
