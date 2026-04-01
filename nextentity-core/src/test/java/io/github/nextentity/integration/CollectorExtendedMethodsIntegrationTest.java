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
 * - first(int): Get first result with offset
 * - single(int): Get single result with offset
 * - first/singleton with LockModeType
 * - offset(int, LockModeType)
 * - getPage(PageCollector)
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.Collector
 */
@DisplayName("Collector Extended Methods Integration Tests")
public class CollectorExtendedMethodsIntegrationTest {

    // ==================== first(int offset) Tests ====================

    /**
     * Tests first(int) with zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with zero offset")
    void shouldGetFirstWithZeroOffset(IntegrationTestContext context) {
        // When
        Employee first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getFirst(0);

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(1L);
    }

    /**
     * Tests first(int) with positive offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with positive offset")
    void shouldGetFirstWithPositiveOffset(IntegrationTestContext context) {
        // When
        Employee first = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getFirst(2);

        // Then
        assertThat(first).isNotNull();
        assertThat(first.getId()).isEqualTo(3L);
    }

    /**
     * Tests first(int) returns null when offset exceeds result count.
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
                .getFirst((int) totalCount + 10);

        // Then
        assertThat(first).isNull();
    }

    // ==================== single(int offset) Tests ====================

    /**
     * Tests single(int) with zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with zero offset")
    void shouldGetSingleWithZeroOffset(IntegrationTestContext context) {
        // Given - ensure only one result
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .getFirst();

        // When
        Employee single = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .getSingle(0);

        // Then
        assertThat(single).isNotNull();
        assertThat(single.getId()).isEqualTo(firstId);
    }

    /**
     * Tests single(int) throws exception when multiple results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle throw exception when multiple results")
    void shouldGetSingleThrowExceptionWhenMultipleResults(IntegrationTestContext context) {
        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> {
            context.queryEmployees()
                    .where(Employee::getSalary).gt(50000.0)
                    .getSingle(0);
        });
    }

    // ==================== first() Optional Tests ====================

    /**
     * Tests first() returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first return Optional with value")
    void shouldFirstReturnOptionalWithValue(IntegrationTestContext context) {
        // When
        var optional = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first();

        // Then
        assertThat(optional).isPresent();
        assertThat(optional.get().getId()).isEqualTo(1L);
    }

    /**
     * Tests first(int) returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with offset return Optional with value")
    void shouldFirstWithOffsetReturnOptionalWithValue(IntegrationTestContext context) {
        // When
        var optional = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .first(1);

        // Then
        assertThat(optional).isPresent();
        assertThat(optional.get().getId()).isEqualTo(2L);
    }

    // ==================== single() Optional Tests ====================

    /**
     * Tests single() returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single return Optional with value")
    void shouldSingleReturnOptionalWithValue(IntegrationTestContext context) {
        // Given
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .getFirst();

        // When
        var optional = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .single();

        // Then
        assertThat(optional).isPresent();
        assertThat(optional.get().getId()).isEqualTo(firstId);
    }

    /**
     * Tests single(int) returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with offset return Optional with value")
    void shouldSingleWithOffsetReturnOptionalWithValue(IntegrationTestContext context) {
        // Given
        Long firstId = context.queryEmployees()
                .select(Employee::getId)
                .orderBy(Employee::getId).asc()
                .getFirst();

        // When
        var optional = context.queryEmployees()
                .where(Employee::getId).eq(firstId)
                .single(0);

        // Then
        assertThat(optional).isPresent();
    }

    // ==================== exist(int offset) Tests ====================

    /**
     * Tests exist(int) with zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exist with zero offset return true")
    void shouldExistWithZeroOffsetReturnTrue(IntegrationTestContext context) {
        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .exist(0);

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exist(int) with offset within bounds.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exist with offset within bounds return true")
    void shouldExistWithOffsetWithinBoundsReturnTrue(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .exist((int) totalCount - 1);

        // Then
        assertThat(exists).isTrue();
    }

    /**
     * Tests exist(int) with offset exceeding count.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should exist with offset exceeding count return false")
    void shouldExistWithOffsetExceedingCountReturnFalse(IntegrationTestContext context) {
        // Given
        long totalCount = context.queryEmployees().count();

        // When
        boolean exists = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .exist((int) totalCount + 10);

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
        Pageable pageable = Pages.pageable(1, 10);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

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
        Pageable pageable = Pages.pageable(2, 5);

        // When
        Page<Employee> page = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .getPage(pageable);

        // Then
        if (totalCount > 5) {
            assertThat(page.getItems()).isNotEmpty();
            assertThat(page.getItems().get(0).getId()).isEqualTo(6L);
        }
    }

    // ==================== map() Tests ====================

    /**
     * Tests map(Function) method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map results to different type")
    void shouldMapResultsToDifferentType(IntegrationTestContext context) {
        // When
        List<String> names = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .map(Employee::getName)
                .limit(5);

        // Then
        assertThat(names).hasSize(5);
        assertThat(names).doesNotContainNull();
    }

    /**
     * Tests map with complex transformation.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should map with complex transformation")
    void shouldMapWithComplexTransformation(IntegrationTestContext context) {
        // When
        List<String> emailDomains = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .map(e -> e.getEmail().substring(e.getEmail().indexOf('@') + 1))
                .limit(5);

        // Then
        assertThat(emailDomains).hasSize(5);
        assertThat(emailDomains).allMatch(d -> d.contains("."));
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
                .asSubQuery();

        // Then
        assertThat(subQuery).isNotNull();
    }
}