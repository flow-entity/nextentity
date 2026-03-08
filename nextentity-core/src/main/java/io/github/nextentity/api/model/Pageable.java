package io.github.nextentity.api.model;

/**
 * Pageable interface, defining the basic parameters for pagination.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Pageable {

    /**
     * Gets the current page number.
     *
     * @return Current page number
     */
    int page();

    /**
     * Gets the page size.
     *
     * @return Page size
     */
    int size();

    /**
     * Calculates the pagination offset.
     *
     * @return Pagination offset
     */
    default int offset() {
        return (page() - 1) * size();
    }

}
