package io.github.nextentity.core.util;

import java.util.List;
import java.util.stream.Stream;

public interface ImmutableArray<E> extends Sizeable, Iterable<E> {

    Stream<E> stream();

    default List<E> asList() {
        return Iterators.toList(this);
    }

    E get(int index);

}
