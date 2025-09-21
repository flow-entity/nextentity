package io.github.nextentity.core.expression;

import io.github.nextentity.api.model.Slice;

import java.util.List;

public record SliceImpl<T>(List<T> data, long total, int offset, int limit) implements Slice<T> {
}
