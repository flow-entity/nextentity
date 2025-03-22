package io.github.nextentity.core.expression.impl;

import static io.github.nextentity.core.expression.QueryStructure.From.FromEntity;

record FromEntityImpl(Class<?> type) implements FromEntity {
}
