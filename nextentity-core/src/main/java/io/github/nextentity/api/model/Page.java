package io.github.nextentity.api.model;

import java.util.List;

///
/// Page result interface, containing page data and total record count.
///
/// @param <T> Data type
/// @author HuangChengwei
/// @since 1.0.0
///
public interface Page<T> {
    ///
    /// Gets the data list for the current page.
    ///
    /// @return Data list
    ///
    List<T> getItems();

    ///
    /// Gets the total record count.
    ///
    /// @return Total record count
    ///
    long getTotal();
}
