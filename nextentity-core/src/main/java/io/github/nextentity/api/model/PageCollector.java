package io.github.nextentity.api.model;

import java.util.List;

///
/// Page collector interface, used to collect page data and build page results.
///
/// @param <T> Data type
/// @param <R> Page result type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface PageCollector<T, R> extends Pageable, Sliceable<T, R> {

    ///
    /// Collects page data and builds page result.
    ///
    /// @param list Data list for the current page
    /// @param total Total record count
    /// @return Page result
    ///
    R collect(List<T> list, long total);

    ///
    /// Gets the pagination offset.
    ///
    /// @return Pagination offset
    ///
    @Override
    default int offset() {
        return Pageable.super.offset();
    }
}
