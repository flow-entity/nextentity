package io.github.nextentity.api.model;

///
/// 5-tuple interface, representing a tuple containing 5 elements.
/// <p>
/// Provides type-safe methods to get 5 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @param <C> Third element type
/// @param <D> Fourth element type
/// @param <E> Fifth element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple5<A, B, C, D, E> extends Tuple4<A, B, C, D> {

    ///
    /// Gets the fifth element.
    ///
    /// @return Fifth element
    ///
    default E get4() {
        return get(4);
    }

}
