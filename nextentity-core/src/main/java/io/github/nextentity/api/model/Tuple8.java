package io.github.nextentity.api.model;

///
/// 8-tuple interface, representing a tuple containing 8 elements.
/// <p>
/// Provides type-safe methods to get 8 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @param <C> Third element type
/// @param <D> Fourth element type
/// @param <E> Fifth element type
/// @param <F> Sixth element type
/// @param <G> Seventh element type
/// @param <H> Eighth element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple8<A, B, C, D, E, F, G, H> extends Tuple7<A, B, C, D, E, F, G> {
    ///
    /// Gets the eighth element.
    ///
    /// @return Eighth element
    ///
    default H get7() {
        return get(7);
    }
}
