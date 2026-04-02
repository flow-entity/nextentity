package io.github.nextentity.api.model;

import java.util.List;

public interface SliceBuilder<R> {

    <T> R build(List<T> list, long total, int offset, int limit);
}
