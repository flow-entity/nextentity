package io.github.nextentity.api.model;

///
/// 2-tuple interface, representing a tuple containing 2 elements.
/// <p>
/// Provides type-safe methods to get 2 elements.
///
/// @param <A> First element type
/// @param <B> Second element type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Tuple2<A, B> extends Tuple {

    ///
    /// Gets the first element.
    ///
    /// @return First element
    ///
    default A get0() {
        return get(0);
    }

    ///
    /// Gets the second element.
    ///
    /// @return Second element
    ///
    default B get1() {
        return get(1);
    }

}
