package io.github.nextentity.spring;

import io.github.nextentity.api.Path;
import io.github.nextentity.api.Repository;
import io.github.nextentity.api.TypedExpression;
import io.github.nextentity.api.TypedExpression.*;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.core.RepositoryFactory;
import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.expression.InternalPathExpression;
import io.github.nextentity.core.expression.impl.TypedExpressionWrapper;
import io.github.nextentity.core.meta.EntityType;
import lombok.experimental.Delegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class AbstractJdbcRepository<T, ID extends Serializable> {

    protected final Repository<ID, T> repository;
    protected final Class<ID> idType;
    protected final Class<T> entityType;
    protected final EntityRoot<T> root;

    @Autowired
    protected AbstractJdbcRepository(JdbcRepositoryFactoryConfiguration configuration) {
        ResolvableType type = ResolvableType.forClass(getClass()).as(AbstractJdbcRepository.class);
        this.entityType = TypeCastUtil.cast(type.resolveGeneric(0));
        if (entityType == null) {
            throw new RuntimeException();
        }
        idType = TypeCastUtil.cast(type.resolveGeneric(1));
        RepositoryFactory repositoryFactory = configuration.getRepositoryFactory();
        EntityType entity = repositoryFactory.metamodel().getEntity(entityType);
        if (idType != entity.id().type()) {
            throw new RuntimeException();
        }
        repository = repositoryFactory.getRepository(entityType);
        root = repository.root();
    }

    protected Repository<ID, T> repository() {
        return getJdbcRepository();
    }

    protected Repository<ID, T> getJdbcRepository() {
        return repository;
    }

    protected Class<ID> getIdType() {
        return idType;
    }

    protected Class<T> getEntityType() {
        return entityType;
    }

    public void insert(T entity) {
        repository().insert(entity);
    }

    public T update(T entity) {
        return repository().update(entity);
    }

    public void delete(T entity) {
        repository().delete(entity);
    }

    public void insertAll(Iterable<T> entities) {
        repository().insert(entities);
    }

    public List<T> updateAll(Iterable<T> entities) {
        return repository().update(entities);
    }

    public void deleteAll(Iterable<T> entities) {
        repository().delete(entities);
    }

    public T findById(ID id) {
        return repository.get(id);
    }

    public List<T> findByIds(Iterable<? extends ID> ids) {
        return repository.getAll(ids);
    }

    public Map<ID, T> findMapByIds(Iterable<? extends ID> ids) {
        return repository.getMap(ids);
    }

    protected abstract class $string implements StringPathExpression<T> {
    }

    private class $string0 extends $string implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final StringPathExpression<T> expression;
        @Delegate
        private final InternalPathExpression path;

        private $string0(StringPathExpression<T> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $boolean implements BooleanPathExpression<T> {
    }

    private class $boolean0 extends $boolean implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final BooleanPathExpression<T> expression;
        @Delegate
        private final InternalPathExpression path;

        private $boolean0(BooleanPathExpression<T> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $number<X extends Number> implements NumberPathExpression<T, X> {
    }

    private class $number0<X extends Number> extends $number<X> implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, X> expression;
        @Delegate
        private final InternalPathExpression path;

        private $number0(NumberPathExpression<T, X> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $long extends $number<Long> {
    }

    private class $long0 extends $long implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Long> expression;
        @Delegate
        private final InternalPathExpression path;

        private $long0(NumberPathExpression<T, Long> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $int extends $number<Integer> {
    }

    private class $int0 extends $int implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Integer> expression;
        @Delegate
        private final InternalPathExpression path;

        private $int0(NumberPathExpression<T, Integer> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $short extends $number<Short> {
    }

    private class $short0 extends $short implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Short> expression;
        @Delegate
        private final InternalPathExpression path;

        private $short0(NumberPathExpression<T, Short> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $byte extends $number<Byte> {
    }

    private class $byte0 extends $byte implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Byte> expression;
        @Delegate
        private final InternalPathExpression path;

        private $byte0(NumberPathExpression<T, Byte> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $double extends $number<Double> {
    }

    private class $double0 extends $double implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Double> expression;
        @Delegate
        private final InternalPathExpression path;

        private $double0(NumberPathExpression<T, Double> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $float extends $number<Float> {
    }

    private class $float0 extends $float implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, Float> expression;
        @Delegate
        private final InternalPathExpression path;

        private $float0(NumberPathExpression<T, Float> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $decimal extends $number<BigDecimal> {
    }

    private class $decimal0 extends $decimal implements InternalPathExpression, TypedExpressionWrapper {
        @Delegate
        private final NumberPathExpression<T, BigDecimal> expression;
        @Delegate
        private final InternalPathExpression path;

        private $decimal0(NumberPathExpression<T, BigDecimal> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }

        @Override
        public TypedExpression<?, ?> unwrap() {
            return expression;
        }
    }

    protected abstract class $entity<X> implements EntityPathExpression<T, X> {
    }

    private class $entity0<X> extends $entity<X> implements InternalPathExpression {
        @Delegate
        private final EntityPathExpression<T, X> expression;
        @Delegate
        private final InternalPathExpression path;

        private $entity0(EntityPathExpression<T, X> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    protected abstract class $path<X> implements PathExpression<T, X> {
    }

    private class $path0<X> extends $path<X> implements InternalPathExpression {
        @Delegate
        private final PathExpression<T, X> expression;
        @Delegate
        private final InternalPathExpression path;

        private $path0(PathExpression<T, X> expression) {
            if (expression instanceof InternalPathExpression pe) {
                this.path = pe;
                this.expression = expression;
            } else {
                throw new IllegalArgumentException();
            }
        }
    }

    protected $boolean $(Path.BooleanPath<T> path) {
        return new $boolean0(root.get(path));
    }

    protected $string $(Path.StringPath<T> path) {
        return new $string0(root.get(path));
    }

    protected <U extends Number> $number<U> $(Path.NumberPath<T, U> path) {
        return new $number0<>(root.get(path));
    }

    protected $long $(Path.LongPath<T> path) {
        return new $long0(root.get(path));
    }

    protected $int $(Path.IntegerPath<T> path) {
        return new $int0(root.get(path));
    }

    protected $short $(Path.ShortPath<T> path) {
        return new $short0(root.get(path));
    }

    protected $byte $(Path.BytePath<T> path) {
        return new $byte0(root.get(path));
    }

    protected $double $(Path.DoublePath<T> path) {
        return new $double0(root.get(path));
    }

    protected $float $(Path.FloatPath<T> path) {
        return new $float0(root.get(path));
    }

    protected $decimal $(Path.BigDecimalPath<T> path) {
        return new $decimal0(root.get(path));
    }

    protected <U> $path<U> $(Path<T, U> path) {
        return new $path0<>(root.path(path));
    }

    protected <U> $entity<U> entity(Path<T, U> path) {
        return new $entity0<>(root.entity(path));
    }
}
