package io.github.nextentity.api.model;

/**
 * 6-tuple interface, representing a tuple containing 6 elements.
 * <p>
 * Provides type-safe methods to get 6 elements.
 *
 * @param <A> First element type
 * @param <B> Second element type
 * @param <C> Third element type
 * @param <D> Fourth element type
 * @param <E> Fifth element type
 * @param <F> Sixth element type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Tuple6<A, B, C, D, E, F> extends Tuple5<A, B, C, D, E> {
    /**
     * Gets the sixth element.
     *
     * @return Sixth element
     */
    default F get5() {
        return get(5);
    }
}
