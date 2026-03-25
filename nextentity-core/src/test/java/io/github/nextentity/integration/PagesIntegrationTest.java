package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.core.Pages;
import io.github.nextentity.integration.config.IntegrationTestContext;
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
        void shouldPaginateResultsCorrectly(IntegrationTestContext context) {
            // Given - get total count first
            long total = context.queryEmployees().count();

            // When - get first page
            List<Employee> page1 = context.queryEmployees()
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
        void shouldGetSecondPageCorrectly(IntegrationTestContext context) {
            // Given
            long total = context.queryEmployees().count();
            if (total < 10) {
                return; // Skip if not enough data
            }

            // When
            List<Employee> page1 = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 5);
            List<Employee> page2 = context.queryEmployees()
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
        void shouldHandlePageBeyondData(IntegrationTestContext context) {
            // When
            List<Employee> page = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(10000, 10);

            // Then
            assertThat(page).isEmpty();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle zero offset")
        void shouldHandleZeroOffset(IntegrationTestContext context) {
            // When
            List<Employee> page = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 3);

            // Then
            assertThat(page).isNotNull();
            assertThat(page.size()).isLessThanOrEqualTo(3);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle small limit")
        void shouldHandleSmallLimit(IntegrationTestContext context) {
            // When
            List<Employee> page = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 1);

            // Then
            assertThat(page).hasSize(1);
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create page from query results")
        void shouldCreatePageFromQueryResults(IntegrationTestContext context) {
            // Given
            List<Employee> items = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .getList(0, 10);
            long total = context.queryEmployees().count();

            // When
            Page<Employee> page = Pages.page(items, total);

            // Then
            assertThat(page.getItems()).isEqualTo(items);
            assertThat(page.getTotal()).isEqualTo(total);
        }
    }
}