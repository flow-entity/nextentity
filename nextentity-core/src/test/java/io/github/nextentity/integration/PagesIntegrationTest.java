package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.core.Pages;
import io.github.nextentity.integration.config.DbConfig;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Pages class and pagination functionality.
 * <p>
 * Tests pagination integration with database queries.
 *
 * @author HuangChengwei
 */
@DisplayName("Pages Integration Tests")
public class PagesIntegrationTest {

    @Nested
    @DisplayName("Pagination Integration Tests")
    class PaginationIntegrationTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should paginate results correctly")
        void shouldPaginateResultsCorrectly(DbConfig config) {
            // Given - get total count first
            long total = config.queryEmployees().count();

            // When - get first page
            List<Employee> page1 = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 5);

            // Then
            assertThat(page1).isNotNull();
            assertThat(page1.size()).isLessThanOrEqualTo(5);
            assertThat(page1.size()).isLessThanOrEqualTo((int) total);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should get second page correctly")
        void shouldGetSecondPageCorrectly(DbConfig config) {
            // Given
            long total = config.queryEmployees().count();
            if (total < 10) {
                return; // Skip if not enough data
            }

            // When
            List<Employee> page1 = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 5);
            List<Employee> page2 = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(5, 5);

            // Then - pages should have different data
            assertThat(page1).isNotEmpty();
            assertThat(page2).isNotEmpty();
            assertThat(page1.get(0).getId()).isNotEqualTo(page2.get(0).getId());
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle page beyond data")
        void shouldHandlePageBeyondData(DbConfig config) {
            // When
            List<Employee> page = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(10000, 10);

            // Then
            assertThat(page).isEmpty();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle zero offset")
        void shouldHandleZeroOffset(DbConfig config) {
            // When
            List<Employee> page = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 3);

            // Then
            assertThat(page).isNotNull();
            assertThat(page.size()).isLessThanOrEqualTo(3);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle small limit")
        void shouldHandleSmallLimit(DbConfig config) {
            // When
            List<Employee> page = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 1);

            // Then
            assertThat(page).hasSize(1);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create page from query results")
        void shouldCreatePageFromQueryResults(DbConfig config) {
            // Given
            List<Employee> items = config.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 10);
            long total = config.queryEmployees().count();

            // When
            Page<Employee> page = Pages.page(items, total);

            // Then
            assertThat(page.getItems()).isEqualTo(items);
            assertThat(page.getTotal()).isEqualTo(total);
        }
    }
}