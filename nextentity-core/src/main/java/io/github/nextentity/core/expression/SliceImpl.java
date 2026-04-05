package io.github.nextentity.core.expression;

import io.github.nextentity.api.model.Slice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SliceImpl<T>(List<T> data, long total, int offset, int limit) implements Slice<T> {

    @Override
    public <R> Slice<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mappedData = data().stream().map(mapper).collect(Collectors.toList());
        return new SliceImpl<>(mappedData, total(), offset(), limit());
    }

}
