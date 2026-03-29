package io.github.nextentity.api.model;

///
/// 7-tuple interface, representing a tuple containing 7 elements.
/// <p>
/// Provides type-safe methods to get 7 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @param <C> Third element type
/// @param <D> Fourth element type
/// @param <E> Fifth element type
/// @param <F> Sixth element type
/// @param <G> Seventh element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple7<A, B, C, D, E, F, G> extends Tuple6<A, B, C, D, E, F> {
    ///
    /// Gets the seventh element.
    ///
    /// @return Seventh element
    ///
    default G get6() {
        return get(6);
    }
}
