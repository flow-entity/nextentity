package io.github.nextentity.api.model;

///
/// 10-tuple interface, representing a tuple containing 10 elements.
/// <p>
/// Provides type-safe methods to get 10 elements.
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
/// @param <J> Tenth element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple10<A, B, C, D, E, F, G, H, I, J> extends Tuple9<A, B, C, D, E, F, G, H, I> {
    ///
    /// Gets the tenth element.
    ///
    /// @return Tenth element
    ///
    default J get9() {
        return get(9);
    }
}
