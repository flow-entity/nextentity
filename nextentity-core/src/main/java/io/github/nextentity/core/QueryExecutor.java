package io.github.nextentity.core;

import io.github.nextentity.core.expression.QueryStructure;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Query executor interface for executing SELECT queries.
 * <p>
 * This interface is responsible for executing query structures built by the
 * query builder and returning the results as a list of entities.
 * <p>
 * Implementations typically use JDBC or JPA to interact with the database.
 *
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface QueryExecutor {

    /**
     * Executes a query and returns the result as a list of entities.
     * <p>
     * The query structure contains all information needed to build and execute
     * the SQL query, including selection, filtering, ordering, and pagination.
     *
     * @param <T> the entity type of the result
     * @param queryStructure the query structure to execute
     * @return a list of entities matching the query criteria
     * @throws NullPointerException if queryStructure is null
     */
    <T> List<T> getList(@NonNull QueryStructure queryStructure);
}
