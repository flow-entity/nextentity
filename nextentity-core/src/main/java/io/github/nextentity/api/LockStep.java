package io.github.nextentity.api;

import jakarta.persistence.LockModeType;

/// Query step that supports applying a lock mode before collecting results.
///
/// @param <T> Result type
public interface LockStep<T> extends Collector<T> {

    /// Sets the lock mode for the query.
    ///
    /// @param lockModeType Lock mode type
    /// @return Locked collector view
    Collector<T> lock(LockModeType lockModeType);
}
