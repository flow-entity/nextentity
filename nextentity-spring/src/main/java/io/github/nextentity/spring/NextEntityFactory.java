package io.github.nextentity.spring;

import io.github.nextentity.api.Select;
import io.github.nextentity.core.UpdateExecutor;

import java.io.Serializable;

public interface RepositoryContext<T, ID extends Serializable> {

    Select<T> queryBuilder();

    UpdateExecutor updateExecutor();

}
