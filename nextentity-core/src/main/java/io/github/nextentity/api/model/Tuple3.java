package io.github.nextentity.api.model;

///
/// 3-tuple interface, representing a tuple containing 3 elements.
/// <p>
/// Provides type-safe methods to get 3 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @param <C> Third element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple3<A, B, C> extends Tuple2<A, B> {
    ///
    /// Gets the third element.
    ///
    /// @return Third element
    ///
    default C get2() {
        return get(2);
    }
}
