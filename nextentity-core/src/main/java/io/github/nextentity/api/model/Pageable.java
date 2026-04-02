package io.github.nextentity.api.model;

public interface Pageable<T> extends Sliceable<T, Page<T>> {

    ///
    /// Gets the current page number.
    ///
    /// @return Current page number
    ///
    int page();

    ///
    /// Gets the page size.
    ///
    /// @return Page size
    ///
    int size();

    ///
    /// Gets the pagination offset.
    ///
    /// @return Pagination offset
    ///
    @Override
    default int offset() {
        return (page() - 1) * size();
    }

    ///
    /// Gets the page size as the slice limit.
    ///
    /// @return Page size
    ///
    @Override
    default int limit() {
        return size();
    }
}
