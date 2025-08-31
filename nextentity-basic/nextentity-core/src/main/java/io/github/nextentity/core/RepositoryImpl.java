package io.github.nextentity.core;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Repository;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.TypedExpression.OperatableExpression;
import io.github.nextentity.api.Update;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.expression.Expressions;
import io.github.nextentity.core.expression.impl.ExpressionImpls;
import io.github.nextentity.core.meta.EntityAttribute;
import io.github.nextentity.core.util.Iterators;
import io.github.nextentity.core.util.Paths;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HuangChengwei
 * @since 2024-04-08 15:08
 */
public class RepositoryImpl<ID extends Serializable, T> extends SelectImpl<T> implements Repository<ID, T> {

    protected final EntityRoot<T> entityRoot = Paths.root();
    protected Update<T> update;

    protected OperatableExpression<T, ID> idExpression;
    protected Function<T, ID> getId;

    public RepositoryImpl() {
    }

    public RepositoryImpl(RepositoryFactory repositoryFactory, Class<T> type) {
        init(repositoryFactory, type);
    }

    public RepositoryImpl(RepositoryFactory repositoryFactory, Class<T> entityType, Path<T, ID> idPath) {
        init(repositoryFactory, entityType, idPath);
    }

    private void init(RepositoryFactory entitiesFactory, Class<T> entityType, Path<T, ID> idPath) {
        super.init(entitiesFactory, entityType);
        this.update = Updaters.create(entitiesFactory.getUpdateExecutor(), entityType);
        this.idExpression = entityRoot.get(idPath);
        this.getId = idPath::apply;
    }

    protected void init(RepositoryFactory entitiesFactory, Class<T> entityType) {
        super.init(entitiesFactory, entityType);
        update = Updaters.create(entitiesFactory.getUpdateExecutor(), entityType);
        EntityAttribute idAttribute = entitiesFactory.getMetamodel().getEntity(entityType).id();
        getId = entity -> TypeCastUtil.unsafeCast(idAttribute.get(entity));
        idExpression = Expressions.of(ExpressionImpls.column(idAttribute.name()));
    }

    public T get(ID id) {
        return where(this.idExpression.eq(id)).getSingle();
    }

    public List<T> getAll(Iterable<? extends ID> ids) {
        return where(this.idExpression.in(Iterators.toList(ids))).getList();
    }

    public Map<ID, T> getMap(Iterable<? extends ID> ids) {
        return getAll(ids).stream().collect(Collectors.toMap(getId, Function.identity()));
    }

    public <U> TypedExpression<T, U> literal(U value) {
        return this.entityRoot.literal(value);
    }

    public <U> TypedExpression.EntityPathExpression<T, U> get(Path<T, U> path) {
        return this.entityRoot.get(path);
    }

    public TypedExpression.BooleanPathExpression<T> get(Path.BooleanPath<T> path) {
        return this.entityRoot.get(path);
    }

    public TypedExpression.StringPathExpression<T> get(Path.StringPath<T> path) {
        return this.entityRoot.get(path);
    }

    public <U extends Number> TypedExpression.NumberPathExpression<T, U> get(Path.NumberPath<T, U> path) {
        return this.entityRoot.get(path);
    }

    public <U> TypedExpression.PathExpression<T, U> path(Path<T, U> path) {
        return this.entityRoot.path(path);
    }

    public <U> TypedExpression.EntityPathExpression<T, U> entity(Path<T, U> path) {
        return this.entityRoot.entity(path);
    }

    public TypedExpression.StringPathExpression<T> string(Path<T, String> path) {
        return this.entityRoot.string(path);
    }

    public <U extends Number> TypedExpression.NumberPathExpression<T, U> number(Path<T, U> path) {
        return this.entityRoot.number(path);
    }

    public TypedExpression.BooleanPathExpression<T> bool(Path<T, Boolean> path) {
        return this.entityRoot.bool(path);
    }

    public <U> TypedExpression.PathExpression<T, U> path(String fieldName) {
        return this.entityRoot.path(fieldName);
    }

    public <U> TypedExpression.EntityPathExpression<T, U> entityPath(String fieldName) {
        return this.entityRoot.entityPath(fieldName);
    }

    public TypedExpression.StringPathExpression<T> stringPath(String fieldName) {
        return this.entityRoot.stringPath(fieldName);
    }

    public <U extends Number> TypedExpression.NumberPathExpression<T, U> numberPath(String fieldName) {
        return this.entityRoot.numberPath(fieldName);
    }

    public TypedExpression.BooleanPathExpression<T> booleanPath(String fieldName) {
        return this.entityRoot.booleanPath(fieldName);
    }

    public T insert(@NotNull T entity) {
        return this.update.insert(entity);
    }

    public List<T> insert(@NotNull Iterable<T> entities) {
        return this.update.insert(entities);
    }

    public List<T> update(@NotNull Iterable<T> entities) {
        return this.update.update(entities);
    }

    public T update(@NotNull T entity) {
        return this.update.update(entity);
    }

    public void delete(@NotNull Iterable<T> entities) {
        this.update.delete(entities);
    }

    public void delete(@NotNull T entity) {
        this.update.delete(entity);
    }

    public T updateNonNullColumn(@NotNull T entity) {
        return this.update.updateNonNullColumn(entity);
    }
}
