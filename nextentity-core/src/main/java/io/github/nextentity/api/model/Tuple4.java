package io.github.nextentity.api.model;

/**
 * 4-tuple interface, representing a tuple containing 4 elements.
 * <p>
 * Provides type-safe methods to get 4 elements.
 *
 * @param <A> First element type
 * @param <B> Second element type
 * @param <C> Third element type
 * @param <D> Fourth element type
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Tuple4<A, B, C, D> extends Tuple3<A, B, C> {

    /**
     * Gets the fourth element.
     *
     * @return Fourth element
     */
    default D get3() {
        return get(3);
    }

}
