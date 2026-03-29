package io.github.nextentity.api.model;

///
/// 9-tuple interface, representing a tuple containing 9 elements.
/// <p>
/// Provides type-safe methods to get 9 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @param <C> Third element type
/// @param <D> Fourth element type
/// @param <E> Fifth element type
/// @param <F> Sixth element type
/// @param <G> Seventh element type
/// @param <H> Eighth element type
/// @param <I> Ninth element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple9<A, B, C, D, E, F, G, H, I> extends Tuple8<A, B, C, D, E, F, G, H> {
    ///
    /// Gets the ninth element.
    ///
    /// @return Ninth element
    ///
    default I get8() {
        return get(8);
    }
}
