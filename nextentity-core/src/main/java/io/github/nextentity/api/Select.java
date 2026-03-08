package io.github.nextentity.api;

import io.github.nextentity.api.model.*;

import java.util.Collection;
import java.util.List;

/**
 * Query operation interface, providing multiple query methods.
 *
 * @param <T> Entity type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Select<T> extends FetchStep<T> {

    /**
     * Select projection results of the specified type.
     *
     * @param projectionType Projection type
     * @param <R> Projection result type
     * @return Query condition construction step
     */
    <R> BaseWhereStep<T, R> select(Class<R> projectionType);

    /**
     * Select results of multiple expressions.
     *
     * @param paths List of expressions
     * @return Multi-row query condition construction step
     */
    WhereStep<T, Tuple> select(List<? extends TypedExpression<T, ?>> paths);

    /**
     * Select result of a single expression.
     *
     * @param expression Expression
     * @param <R> Expression result type
     * @return Multi-row query condition construction step
     */
    <R> WhereStep<T, R> select(TypedExpression<T, R> expression);

    /**
     * Select result of a single path.
     *
     * @param path Path
     * @param <R> Path result type
     * @return Multi-row query condition construction step
     */
    <R> WhereStep<T, R> select(Path<T, ? extends R> path);

    /**
     * Select results of multiple paths.
     *
     * @param paths Collection of paths
     * @return Multi-row query condition construction step
     */
    WhereStep<T, Tuple> select(Collection<Path<T, ?>> paths);

    /**
     * Select results of two paths.
     *
     * @param a First path
     * @param b Second path
     * @param <A> First path type
     * @param <B> Second path type
     * @return Multi-row query condition construction step
     */
    <A, B> WhereStep<T, Tuple2<A, B>> select(Path<T, A> a, Path<T, B> b);

    /**
     * Select results of three paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @return Multi-row query condition construction step
     */
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c);

    /**
     * Select results of four paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d);

    /**
     * Select results of five paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e);

    /**
     * Select results of six paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f);

    /**
     * Select results of seven paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g);

    /**
     * Select results of eight paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h);

    /**
     * Select results of nine paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param i Ninth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @param <I> Ninth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i);

    /**
     * Select results of ten paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param i Ninth path
     * @param j Tenth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @param <I> Ninth path type
     * @param <J> Tenth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i, Path<T, J> j);

    /**
     * Select results of two expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @return Multi-row query condition construction step
     */
    <A, B> WhereStep<T, Tuple2<A, B>> select(TypedExpression<T, A> a, TypedExpression<T, B> b);

    /**
     * Select results of three expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c);

    /**
     * Select results of four expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d);

    /**
     * Select results of five expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e);

    /**
     * Select results of six expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f);

    /**
     * Select results of seven expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g);

    /**
     * Select results of eight expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h);

    /**
     * Select results of nine expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param i Ninth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @param <I> Ninth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i);

    /**
     * Select results of ten expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param i Ninth expression
     * @param j Tenth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @param <I> Ninth expression type
     * @param <J> Tenth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> select(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i, TypedExpression<T, J> j);

    /**
     * Select distinct projection results of the specified type.
     *
     * @param projectionType Projection type
     * @param <R> Projection result type
     * @return Query condition construction step
     */
    <R> BaseWhereStep<T, R> selectDistinct(Class<R> projectionType);

    /**
     * Select distinct results of multiple expressions.
     *
     * @param paths List of expressions
     * @return Multi-row query condition construction step
     */
    WhereStep<T, Tuple> selectDistinct(List<? extends TypedExpression<T, ?>> paths);

    /**
     * Select distinct result of a single expression.
     *
     * @param expression Expression
     * @param <R> Expression result type
     * @return Multi-row query condition construction step
     */
    <R> WhereStep<T, R> selectDistinct(TypedExpression<T, R> expression);

    /**
     * Select distinct result of a single path.
     *
     * @param path Path
     * @param <R> Path result type
     * @return Multi-row query condition construction step
     */
    <R> WhereStep<T, R> selectDistinct(Path<T, ? extends R> path);

    /**
     * Select distinct results of multiple paths.
     *
     * @param paths Collection of paths
     * @return Multi-row query condition construction step
     */
    WhereStep<T, Tuple> selectDistinct(Collection<Path<T, ?>> paths);

    /**
     * Select distinct results of two paths.
     *
     * @param a First path
     * @param b Second path
     * @param <A> First path type
     * @param <B> Second path type
     * @return Multi-row query condition construction step
     */
    <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(Path<T, A> a, Path<T, B> b);

    /**
     * Select distinct results of three paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @return Multi-row query condition construction step
     */
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c);

    /**
     * Select distinct results of four paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d);

    /**
     * Select distinct results of five paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e);

    /**
     * Select distinct results of six paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f);

    /**
     * Select distinct results of seven paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g);

    /**
     * Select distinct results of eight paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h);

    /**
     * Select distinct results of nine paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param i Ninth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @param <I> Ninth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i);

    /**
     * Select distinct results of ten paths.
     *
     * @param a First path
     * @param b Second path
     * @param c Third path
     * @param d Fourth path
     * @param e Fifth path
     * @param f Sixth path
     * @param g Seventh path
     * @param h Eighth path
     * @param i Ninth path
     * @param j Tenth path
     * @param <A> First path type
     * @param <B> Second path type
     * @param <C> Third path type
     * @param <D> Fourth path type
     * @param <E> Fifth path type
     * @param <F> Sixth path type
     * @param <G> Seventh path type
     * @param <H> Eighth path type
     * @param <I> Ninth path type
     * @param <J> Tenth path type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(Path<T, A> a, Path<T, B> b, Path<T, C> c, Path<T, D> d, Path<T, E> e, Path<T, F> f, Path<T, G> g, Path<T, H> h, Path<T, I> i, Path<T, J> j);

    /**
     * Select distinct results of two expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @return Multi-row query condition construction step
     */
    <A, B> WhereStep<T, Tuple2<A, B>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b);

    /**
     * Select distinct results of three expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C> WhereStep<T, Tuple3<A, B, C>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c);

    /**
     * Select distinct results of four expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D> WhereStep<T, Tuple4<A, B, C, D>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d);

    /**
     * Select distinct results of five expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E> WhereStep<T, Tuple5<A, B, C, D, E>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e);

    /**
     * Select distinct results of six expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F> WhereStep<T, Tuple6<A, B, C, D, E, F>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f);

    /**
     * Select distinct results of seven expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G> WhereStep<T, Tuple7<A, B, C, D, E, F, G>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g);

    /**
     * Select distinct results of eight expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H> WhereStep<T, Tuple8<A, B, C, D, E, F, G, H>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h);

    /**
     * Select distinct results of nine expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param i Ninth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @param <I> Ninth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I> WhereStep<T, Tuple9<A, B, C, D, E, F, G, H, I>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i);

    /**
     * Select distinct results of ten expressions.
     *
     * @param a First expression
     * @param b Second expression
     * @param c Third expression
     * @param d Fourth expression
     * @param e Fifth expression
     * @param f Sixth expression
     * @param g Seventh expression
     * @param h Eighth expression
     * @param i Ninth expression
     * @param j Tenth expression
     * @param <A> First expression type
     * @param <B> Second expression type
     * @param <C> Third expression type
     * @param <D> Fourth expression type
     * @param <E> Fifth expression type
     * @param <F> Sixth expression type
     * @param <G> Seventh expression type
     * @param <H> Eighth expression type
     * @param <I> Ninth expression type
     * @param <J> Tenth expression type
     * @return Multi-row query condition construction step
     */
    <A, B, C, D, E, F, G, H, I, J> WhereStep<T, Tuple10<A, B, C, D, E, F, G, H, I, J>> selectDistinct(TypedExpression<T, A> a, TypedExpression<T, B> b, TypedExpression<T, C> c, TypedExpression<T, D> d, TypedExpression<T, E> e, TypedExpression<T, F> f, TypedExpression<T, G> g, TypedExpression<T, H> h, TypedExpression<T, I> i, TypedExpression<T, J> j);

}
