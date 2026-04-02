package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.core.Pages;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Collector extended methods.
 * <p>
 * Tests default methods in Collector interface including:
 * - first(): Get first result or null
 * - single(): Get single result or null
 * - window/limit terminal operations
 * - slice(PageCollector)
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.Collector
 */
@DisplayName("Collector Extended Methods Integration Tests")
public class CollectorExtendedMethodsIntegrationTest {

    // ==================== getFirst() Tests ====================

    /**
     * Tests getFirst() returns first result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst return first result")
    void shouldGetFirstReturnFirstResult(IntegrationTestContext context) {
        // When
        Employee first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(1L);
    }

    /**
     * Tests getFirst with positive offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with positive offset")
    void shouldGetFirstWithPositiveOffset(IntegrationTestContext context) {
        // When
        Employee first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window(2, 1).stream().findFirst().orElse(null);

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(3L);
    }

    /**
     * Tests getFirst returns null when offset exceeds result count.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst return null when offset exceeds count")
    void shouldGetFirstReturnNullWhenOffsetExceedsCount(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        Employee first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window((int) totalCount + 10, 1).stream().findFirst().orElse(null);

        // Then
        assertThat(first).isNull();
    }

    // ==================== getSingle() Tests ====================

    /**
     * Tests getSingle with single result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with single result")
    void shouldGetSingleWithSingleResult(IntegrationTestContext context) {
        // Given - ensure only one result
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .first();

        // When
        Employee single = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .single();

        // Then
        assertThat(single).isNotNull();
        assertThat(single.getId()).isEqualTo(firstId);
    }

    /**
     * Tests getSingle throws exception when multiple results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle throw exception when multiple results")
    void shouldGetSingleThrowExceptionWhenMultipleResults(IntegrationTestContext context) {
        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            context.queryEmployees()
                    .where(Employee::getSalary).gt(50000.0)
                    .single();
        });
    }

    // ==================== first() Tests ====================

    /**
     * Tests first() returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first return value")
    void shouldFirstReturnValue(IntegrationTestContext context) {
        // When
        var first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(1L);
    }

    /**
     * Tests first with offset returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with offset return value")
    void shouldFirstWithOffsetReturnValue(IntegrationTestContext context) {
        // When
        var first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window(1, 1).stream().findFirst().orElse(null);

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(2L);
    }

    // ==================== single() Tests ====================

    /**
     * Tests single() returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single return value")
    void shouldSingleReturnValue(IntegrationTestContext context) {
        // Given
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .first();

        // When
        var single = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .single();

        // Then
        assertThat(single).isNotNull();
        assertThat(single.getId()).isEqualTo(firstId);
    }

    /**
     * Tests single with offset returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with offset return value")
    void shouldSingleWithOffsetReturnValue(IntegrationTestContext context) {
        // Given
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .first();

        // When
        var single = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .single();

        // Then
        assertThat(single).isNotNull();
    }

    // ==================== exists() Tests ====================

    /**
     * Tests exists returns true when results exist.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exists return true when results exist")
    void shouldExistsReturnTrue(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .exists();

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exists with offset within bounds returns true.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exists with offset within bounds return true")
    void shouldExistsWithOffsetWithinBoundsReturnTrue(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        boolean exists = !context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window((int) totalCount - 1, 1).isEmpty();

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exists with offset exceeding count returns false.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exists with offset exceeding count return false")
    void shouldExistsWithOffsetExceedingCountReturnFalse(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        boolean exists = !context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .window((int) totalCount + 10, 1).isEmpty();

        // Then
        assertThat(exists).isFalse();
    }

    // ==================== Slice Tests ====================

    /**
     * Tests slice(int, int) method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with offset and limit")
    void shouldSliceWithOffsetAndLimit(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(0, 5);

        // Then
        assertThat(slice.data()).hasSize(5);
        assertThat(slice.data().get(0).getId()).isEqualTo(1L);
    }

    /**
     * Tests slice with non-zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with non-zero offset")
    void shouldSliceWithNonZeroOffset(IntegrationTestContext context) {
        // When
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(2, 3);

        // Then
        assertThat(slice.data()).hasSize(3);
        assertThat(slice.data().get(0).getId()).isEqualTo(3L);
    }

    // ==================== getPage Tests ====================

    /**
     * Tests getPage(Pageable) method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getPage with Pageable")
    void shouldGetPageWithPageable(IntegrationTestContext context) {
        // Given
        Pageable<Employee> pageable = Pages.pageable(1, 10);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        assertThat(page.getItems()).isNotEmpty();
        assertThat(page.getItems()).hasSize(Math.min(10, (int) context.queryEmployees().count()));
    }

    /**
     * Tests getPage with second page.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getPage with second page")
    void shouldGetPageWithSecondPage(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();
        Pageable<Employee> pageable = Pages.pageable(2, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .slice(pageable);

        // Then
        if (totalCount > 5) {
            assertThat(page.getItems()).isNotEmpty();
            assertThat(page.getItems().getFirst().getId()).isEqualTo(6L);
        }
    }

    // ==================== asSubQuery Tests ====================

    /**
     * Tests asSubQuery method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should convert to subquery")
    void shouldConvertToSubquery(IntegrationTestContext context) {
        // When
        var subQuery = context.queryEmployees()
                .select(Employee::getId)
                .where(Employee::getSalary).gt(50000.0)
                .toSubQuery();

        // Then
        assertThat(subQuery).isNotNull();
    }
}

