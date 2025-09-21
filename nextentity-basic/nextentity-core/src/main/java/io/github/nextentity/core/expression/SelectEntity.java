package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;

public record SelectEntity(ImmutableList<PathNode> fetch, boolean distinct) implements Selected {
}
