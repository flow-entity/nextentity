package io.github.nextentity.spring.integration.db;

import io.github.nextentity.api.*;
import io.github.nextentity.api.model.*;
import io.github.nextentity.spring.AbstractRepository;
import io.github.nextentity.spring.integration.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author HuangChengwei
 */
public class UserRepository extends AbstractRepository<User, Integer> implements Select<User> {

    private List<User> users;
    private final String name;

    public UserRepository(JdbcTemplate jdbcTemplate, String name) {
        super(jdbcTemplate, jdbc(jdbcTemplate));
        this.name =  name;
    }

    public UserRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate, String name) {
        super(jdbcTemplate, jpa(entityManager, jdbcTemplate));
        this.name = name;
    }

    public void delete(@NonNull Iterable<User> entities) {
        super.deleteAll(entities);
    }

    public void insert(@NonNull Iterable<User> entities) {
        super.insertAll(entities);
    }

    public void update(@NonNull Iterable<User> entities) {
        super.updateAll(entities);
    }

    @Override
    public Select<User> query() {
        return super.query();
    }

    public Select<User> getQuery() {
        return super.query();
    }

    public <R> BaseWhereStep<User, R> select(Class<R> projectionType) {
        return getQuery().select(projectionType);
    }

    public Optional<User> first() {
        return getQuery().first();
    }

    public long count() {
        return getQuery().count();
    }

    public List<User> getList(int offset, int maxResult, LockModeType lockModeType) {
        return getQuery().getList(offset, maxResult, lockModeType);
    }

    public List<User> getList(int offset, int maxResult) {
        return getQuery().getList(offset, maxResult);
    }

    public boolean exist() {
        return getQuery().exist();
    }

    public <R> Collector<R> map(Function<? super User, ? extends R> mapper) {
        return getQuery().map(mapper);
    }

    public User getSingle(int offset) {
        return getQuery().getSingle(offset);
    }

    public User getSingle(int offset, LockModeType lockModeType) {
        return getQuery().getSingle(offset, lockModeType);
    }

    public User getFirst(LockModeType lockModeType) {
        return getQuery().getFirst(lockModeType);
    }

    public User getSingle(LockModeType lockModeType) {
        return getQuery().getSingle(lockModeType);
    }

    public List<User> offset(int offset) {
        return getQuery().offset(offset);
    }

    public Optional<User> single() {
        return getQuery().single();
    }

    public Optional<User> single(int offset) {
        return getQuery().single(offset);
    }

    public Optional<User> first(LockModeType lockModeType) {
        return getQuery().first(lockModeType);
    }

    public List<User> limit(int limit) {
        return getQuery().limit(limit);
    }

    public Optional<User> single(int offset, LockModeType lockModeType) {
        return getQuery().single(offset, lockModeType);
    }

    public User requireSingle() {
        return getQuery().requireSingle();
    }

    public Slice<User> slice(int offset, int limit) {
        return getQuery().slice(offset, limit);
    }

    public Page<User> getPage(Pageable pageable) {
        return getQuery().getPage(pageable);
    }

    public List<User> getList() {
        return getQuery().getList();
    }

    public boolean exist(int offset) {
        return getQuery().exist(offset);
    }

    public User getFirst(int offset, LockModeType lockModeType) {
        return getQuery().getFirst(offset, lockModeType);
    }

    public List<User> getList(LockModeType lockModeType) {
        return getQuery().getList(lockModeType);
    }

    public <X> SubQueryBuilder<X, User> asSubQuery() {
        return getQuery().asSubQuery();
    }

    public List<User> offset(int offset, LockModeType lockModeType) {
        return getQuery().offset(offset, lockModeType);
    }

    public Optional<User> first(int offset) {
        return getQuery().first(offset);
    }

    public Optional<User> single(LockModeType lockModeType) {
        return getQuery().single(lockModeType);
    }

    public User requireSingle(LockModeType lockModeType) {
        return getQuery().requireSingle(lockModeType);
    }

    public <R> R getPage(PageCollector<User, R> collector) {
        return getQuery().getPage(collector);
    }

    public List<User> limit(int limit, LockModeType lockModeType) {
        return getQuery().limit(limit, lockModeType);
    }

    public <R> R slice(Sliceable<User, R> sliceable) {
        return getQuery().slice(sliceable);
    }

    public User getFirst() {
        return getQuery().getFirst();
    }

    public User getSingle() {
        return getQuery().getSingle();
    }

    public Optional<User> first(int offset, LockModeType lockModeType) {
        return getQuery().first(offset, lockModeType);
    }

    public User getFirst(int offset) {
        return getQuery().getFirst(offset);
    }

    public BaseWhereStep<User, User> where(Expression<User, Boolean> predicate) {
        return getQuery().where(predicate);
    }

    public <N> ExpressionBuilder.PathOperator<User, N, ? extends BaseWhereStep<User, User>> where(PathRef<User, N> path) {
        return getQuery().where(path);
    }

    public <N extends Number> ExpressionBuilder.NumberOperator<User, N, ? extends BaseWhereStep<User, User>> where(PathRef.NumberRef<User, N> path) {
        return getQuery().where(path);
    }

    public ExpressionBuilder.StringOperator<User, ? extends BaseWhereStep<User, User>> where(PathRef.StringRef<User> path) {
        return getQuery().where(path);
    }

    public <N> ExpressionBuilder.PathOperator<User, N, ? extends BaseWhereStep<User, User>> where(Path<User, N> path) {
        return getQuery().where(path);
    }

    public <N extends Number> ExpressionBuilder.NumberOperator<User, N, ? extends BaseWhereStep<User, User>> where(NumberPath<User, N> path) {
        return getQuery().where(path);
    }

    public ExpressionBuilder.StringOperator<User, ? extends BaseWhereStep<User, User>> where(StringPath<User> path) {
        return getQuery().where(path);
    }

    public Collector<User> orderBy(List<? extends Order<User>> orders) {
        return getQuery().orderBy(orders);
    }

    public Collector<User> orderBy(Order<User> order) {
        return getQuery().orderBy(order);
    }

    public Collector<User> orderBy(Order<User> p0, Order<User> p1) {
        return getQuery().orderBy(p0, p1);
    }

    public Collector<User> orderBy(Order<User> order1, Order<User> order2, Order<User> order3) {
        return getQuery().orderBy(order1, order2, order3);
    }

    public OrderOperator<User, User> orderBy(Collection<PathRef<User, ? extends Comparable<?>>> paths) {
        return getQuery().orderBy(paths);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> path) {
        return getQuery().orderBy(path);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> p1, PathRef<User, ? extends Comparable<?>> p2) {
        return getQuery().orderBy(p1, p2);
    }

    public OrderOperator<User, User> orderBy(PathRef<User, ? extends Comparable<?>> p1, PathRef<User, ? extends Comparable<?>> p2, PathRef<User, ? extends Comparable<?>> p3) {
        return getQuery().orderBy(p1, p2, p3);
    }

    public WhereStep<User, Tuple> select(List<? extends Expression<User, ?>> paths) {
        return getQuery().select(paths);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g) {
        return getQuery().select(a, b, c, d, e, f, g);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f) {
        return getQuery().selectDistinct(a, b, c, d, e, f);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h) {
        return getQuery().select(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d) {
        return getQuery().selectDistinct(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i, PathRef<User, J> j) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i, Expression<User, J> j) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e) {
        return getQuery().selectDistinct(a, b, c, d, e);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e) {
        return getQuery().selectDistinct(a, b, c, d, e);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c) {
        return getQuery().select(a, b, c);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> selectDistinct(Expression<User, A> a, Expression<User, B> b) {
        return getQuery().selectDistinct(a, b);
    }

    public <R> WhereStep<User, R> select(Expression<User, R> expression) {
        return getQuery().select(expression);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d) {
        return getQuery().select(a, b, c, d);
    }

    public <R> WhereStep<User, R> select(PathRef<User, ? extends R> path) {
        return getQuery().select(path);
    }

    public WhereStep<User, Tuple> select(Collection<PathRef<User, ?>> paths) {
        return getQuery().select(paths);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f) {
        return getQuery().select(a, b, c, d, e, f);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i, Expression<User, J> j) {
        return getQuery().select(a, b, c, d, e, f, g, h, i, j);
    }

    public WhereStep<User, Tuple> selectDistinct(List<? extends Expression<User, ?>> paths) {
        return getQuery().selectDistinct(paths);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h) {
        return getQuery().select(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f) {
        return getQuery().selectDistinct(a, b, c, d, e, f);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> select(PathRef<User, A> a, PathRef<User, B> b) {
        return getQuery().select(a, b);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c) {
        return getQuery().select(a, b, c);
    }

    public <R> WhereStep<User, R> selectDistinct(PathRef<User, ? extends R> path) {
        return getQuery().selectDistinct(path);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b) {
        return getQuery().selectDistinct(a, b);
    }

    public WhereStep<User, Tuple> selectDistinct(Collection<PathRef<User, ?>> paths) {
        return getQuery().selectDistinct(paths);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E, F, G, H, I, J> WhereStep<User, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i, PathRef<User, J> j) {
        return getQuery().select(a, b, c, d, e, f, g, h, i, j);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c) {
        return getQuery().selectDistinct(a, b, c);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d) {
        return getQuery().select(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g, Expression<User, H> h, Expression<User, I> i) {
        return getQuery().select(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D> WhereStep<User, Tuple4<A, B, C, D>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d) {
        return getQuery().selectDistinct(a, b, c, d);
    }

    public <A, B, C, D, E, F, G, H, I> WhereStep<User, Tuple9<A, B, C, D, E, F, G, H, I>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h, PathRef<User, I> i) {
        return getQuery().select(a, b, c, d, e, f, g, h, i);
    }

    public <A, B, C, D, E, F, G, H> WhereStep<User, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g, PathRef<User, H> h) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g, h);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e) {
        return getQuery().select(a, b, c, d, e);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e, Expression<User, F> f, Expression<User, G> g) {
        return getQuery().selectDistinct(a, b, c, d, e, f, g);
    }

    public <A, B> WhereStep<User, Tuple2<A, B>> select(Expression<User, A> a, Expression<User, B> b) {
        return getQuery().select(a, b);
    }

    public <A, B, C, D, E, F> WhereStep<User, Tuple6<A, B, C, D, E, F>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f) {
        return getQuery().select(a, b, c, d, e, f);
    }

    public <A, B, C, D, E> WhereStep<User, Tuple5<A, B, C, D, E>> select(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c, Expression<User, D> d, Expression<User, E> e) {
        return getQuery().select(a, b, c, d, e);
    }

    public <R> BaseWhereStep<User, R> selectDistinct(Class<R> projectionType) {
        return getQuery().selectDistinct(projectionType);
    }

    public <A, B, C> WhereStep<User, Tuple3<A, B, C>> selectDistinct(Expression<User, A> a, Expression<User, B> b, Expression<User, C> c) {
        return getQuery().selectDistinct(a, b, c);
    }

    public <R> WhereStep<User, R> selectDistinct(Expression<User, R> expression) {
        return getQuery().selectDistinct(expression);
    }

    public <A, B, C, D, E, F, G> WhereStep<User, Tuple7<A, B, C, D, E, F, G>> select(PathRef<User, A> a, PathRef<User, B> b, PathRef<User, C> c, PathRef<User, D> d, PathRef<User, E> e, PathRef<User, F> f, PathRef<User, G> g) {
        return getQuery().select(a, b, c, d, e, f, g);
    }

    public BaseWhereStep<User, User> fetch(List<Path<User, ?>> pathExpressions) {
        return getQuery().fetch(pathExpressions);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> path) {
        return getQuery().fetch(path);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> p0, Path<User, ?> p1) {
        return getQuery().fetch(p0, p1);
    }

    public BaseWhereStep<User, User> fetch(Path<User, ?> p0, Path<User, ?> p1, Path<User, ?> p3) {
        return getQuery().fetch(p0, p1, p3);
    }

    public BaseWhereStep<User, User> fetch(Collection<PathRef<User, ?>> paths) {
        return getQuery().fetch(paths);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> path) {
        return getQuery().fetch(path);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> p0, PathRef<User, ?> p1) {
        return getQuery().fetch(p0, p1);
    }

    public BaseWhereStep<User, User> fetch(PathRef<User, ?> p0, PathRef<User, ?> p1, PathRef<User, ?> p3) {
        return getQuery().fetch(p0, p1, p3);
    }

    public List<User> users() {
        if (users == null) {
            List<User> list = query().orderBy(User::getId).asc().getList();
            Map<Integer, User> map = list.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            for (User user : list) {
                Integer pid = user.getPid();
                User p = map.get(pid);
                user.setParentUser(p);
                user.setRandomUser(map.get(user.getRandomNumber()));
                user.setTestUser(map.get(user.getTestInteger()));
            }
            users = list;
        }
        return users;
    }

    @Transactional
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public String toString() {
        return name;
    }
}
