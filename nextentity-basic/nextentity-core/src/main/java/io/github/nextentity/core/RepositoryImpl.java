package io.github.nextentity.core;

import io.github.nextentity.api.*;
import io.github.nextentity.api.SimpleExpression;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.expression.SimpleExpressionImpl;
import io.github.nextentity.core.expression.PathNode;
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

    protected SimpleExpression<T, ID> idExpression;
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
        idExpression = new SimpleExpressionImpl<>(new PathNode(idAttribute.name()));
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

    public <U> EntityPath<T, U> get(Path<T, U> path) {
        return this.entityRoot.get(path);
    }

    public BooleanPath<T> get(Path.BooleanRef<T> path) {
        return this.entityRoot.get(path);
    }

    public StringPath<T> get(Path.StringRef<T> path) {
        return this.entityRoot.get(path);
    }

    public <U extends Number> NumberPath<T, U> get(Path.NumberRef<T, U> path) {
        return this.entityRoot.get(path);
    }

    public <U> PathExpression<T, U> path(Path<T, U> path) {
        return this.entityRoot.path(path);
    }

    public <U> EntityPath<T, U> entity(Path<T, U> path) {
        return this.entityRoot.entity(path);
    }

    public StringPath<T> string(Path<T, String> path) {
        return this.entityRoot.string(path);
    }

    public <U extends Number> NumberPath<T, U> number(Path<T, U> path) {
        return this.entityRoot.number(path);
    }

    public BooleanPath<T> bool(Path<T, Boolean> path) {
        return this.entityRoot.bool(path);
    }

    public <U> PathExpression<T, U> path(String fieldName) {
        return this.entityRoot.path(fieldName);
    }

    public <U> EntityPath<T, U> entityPath(String fieldName) {
        return this.entityRoot.entityPath(fieldName);
    }

    public StringPath<T> stringPath(String fieldName) {
        return this.entityRoot.stringPath(fieldName);
    }

    public <U extends Number> NumberPath<T, U> numberPath(String fieldName) {
        return this.entityRoot.numberPath(fieldName);
    }

    public BooleanPath<T> booleanPath(String fieldName) {
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
