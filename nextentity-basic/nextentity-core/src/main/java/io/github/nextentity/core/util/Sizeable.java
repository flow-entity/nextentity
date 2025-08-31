package io.github.nextentity.core.util;

public interface Sizeable {
    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    default boolean idNotEmpty() {
        return !isEmpty();
    }
}
