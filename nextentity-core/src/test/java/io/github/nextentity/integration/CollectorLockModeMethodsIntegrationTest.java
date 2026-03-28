package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.PageCollector;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.api.model.Sliceable;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.LockableEntity;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for Collector methods with LockModeType parameters.
 * <p>
 * Tests default methods in Collector interface that accept LockModeType:
 * - first(LockModeType): Get first result as Optional with lock
 * - getFirst(LockModeType): Get first result with lock
 * - requireSingle(LockModeType): Get single result or throw with lock
 * - single(LockModeType): Get single result as Optional with lock
 * - getSingle(LockModeType): Get single result with lock
 * - offset(int, LockModeType): Get results from offset with lock
 * - limit(int, LockModeType): Get limited results with lock
 * - getList(LockModeType): Get all results with lock
 * - first(int, LockModeType): Get first result with offset and lock
 * - single(int, LockModeType): Get single result with offset and lock
 * - getPage(PageCollector): Get page using PageCollector
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 * @see io.github.nextentity.api.Collector
 */
@DisplayName("Collector LockMode Methods Integration Tests")
public class CollectorLockModeMethodsIntegrationTest {

    // ==================== first(LockModeType) Tests ====================

    /**
     * Tests first(LockModeType) returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with LockModeType return Optional with value")
    void shouldFirstWithLockModeReturnOptionalWithValue(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .first(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    /**
     * Tests first(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with pessimistic write lock")
    void shouldFirstWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .first(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    // ==================== getFirst(LockModeType) Tests ====================

    /**
     * Tests getFirst(LockModeType) returns result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with LockModeType return result")
    void shouldGetFirstWithLockModeReturnResult(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .getFirst(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    /**
     * Tests getFirst(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getFirst with pessimistic write lock")
    void shouldGetFirstWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .getFirst(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    // ==================== requireSingle(LockModeType) Tests ====================

    /**
     * Tests requireSingle(LockModeType) returns result when single exists.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should requireSingle with LockModeType return result")
    void shouldRequireSingleWithLockModeReturnResult(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .requireSingle(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    /**
     * Tests requireSingle(LockModeType) throws when no result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should requireSingle with LockModeType throw when no result")
    void shouldRequireSingleWithLockModeThrowWhenNoResult(IntegrationTestContext context) {
        // When & Then
        assertThatThrownBy(() ->
                context.getUpdateExecutor().doInTransaction(() ->
                        context.queryLockableEntities()
                                .where(LockableEntity::getId).eq(999L)
                                .requireSingle(LockModeType.PESSIMISTIC_READ)
                )
        ).isInstanceOf(NullPointerException.class);
    }

    /**
     * Tests requireSingle(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should requireSingle with pessimistic write lock")
    void shouldRequireSingleWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .requireSingle(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    // ==================== single(LockModeType) Tests ====================

    /**
     * Tests single(LockModeType) returns Optional with value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with LockModeType return Optional with value")
    void shouldSingleWithLockModeReturnOptionalWithValue(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .single(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    /**
     * Tests single(LockModeType) returns empty when no result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with LockModeType return empty when no result")
    void shouldSingleWithLockModeReturnEmptyWhenNoResult(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(999L)
                        .single(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isEmpty();
    }

    /**
     * Tests single(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with pessimistic write lock")
    void shouldSingleWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .single(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(result).isPresent();
    }

    // ==================== getSingle(LockModeType) Tests ====================

    /**
     * Tests getSingle(LockModeType) returns result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with LockModeType return result")
    void shouldGetSingleWithLockModeReturnResult(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .getSingle(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    /**
     * Tests getSingle(LockModeType) returns null when no result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with LockModeType return null when no result")
    void shouldGetSingleWithLockModeReturnNullWhenNoResult(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(999L)
                        .getSingle(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entity).isNull();
    }

    /**
     * Tests getSingle(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with pessimistic write lock")
    void shouldGetSingleWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        LockableEntity entity = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .getSingle(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entity).isNotNull();
    }

    // ==================== offset(int, LockModeType) Tests ====================

    /**
     * Tests offset(int, LockModeType) returns results from offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset with LockModeType return results from offset")
    void shouldOffsetWithLockModeReturnResultsFromOffset(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .offset(2, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.get(0).getId()).isEqualTo(3L);
    }

    /**
     * Tests offset(int, LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset with pessimistic write lock")
    void shouldOffsetWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .offset(1, LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.get(0).getId()).isEqualTo(2L);
    }

    /**
     * Tests offset(int, LockModeType) with zero offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset zero with LockModeType return all results")
    void shouldOffsetZeroWithLockModeReturnAllResults(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .offset(0, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entities).hasSize(5);
    }

    // ==================== limit(int, LockModeType) Tests ====================

    /**
     * Tests limit(int, LockModeType) returns limited results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with LockModeType return limited results")
    void shouldLimitWithLockModeReturnLimitedResults(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .limit(3, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entities).hasSize(3);
    }

    /**
     * Tests limit(int, LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with pessimistic write lock")
    void shouldLimitWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .limit(2, LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entities).hasSize(2);
    }

    // ==================== getList(LockModeType) Tests ====================

    /**
     * Tests getList(LockModeType) returns all results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getList with LockModeType return all results")
    void shouldGetListWithLockModeReturnAllResults(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .getList(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entities).hasSize(5);
    }

    /**
     * Tests getList(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getList with pessimistic write lock")
    void shouldGetListWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .getList(LockModeType.PESSIMISTIC_WRITE)
        );

        // Then
        assertThat(entities).hasSize(5);
    }

    // ==================== first(int, LockModeType) Tests ====================

    /**
     * Tests first(int, LockModeType) with offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with offset and LockModeType return Optional")
    void shouldFirstWithOffsetAndLockModeReturnOptional(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .first(2, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3L);
    }

    // ==================== single(int, LockModeType) Tests ====================

    /**
     * Tests single(int, LockModeType) with offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with offset and LockModeType return Optional")
    void shouldSingleWithOffsetAndLockModeReturnOptional(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(3L)
                        .single(0, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3L);
    }

    // ==================== slice(Sliceable) Tests ====================

    /**
     * Tests slice(Sliceable) method.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with Sliceable")
    void shouldSliceWithSliceable(IntegrationTestContext context) {
        // Given
        Sliceable<LockableEntity, Slice<LockableEntity>> sliceable = createSliceable(0, 3);

        // When
        Slice<LockableEntity> slice = context.queryLockableEntities()
                .orderBy(LockableEntity::getId).asc()
                .slice(sliceable);

        // Then
        assertThat(slice.data()).hasSize(3);
        assertThat(slice.offset()).isEqualTo(0);
        assertThat(slice.limit()).isEqualTo(3);
    }

    // ==================== getPage(PageCollector) Tests ====================

    /**
     * Tests getPage(PageCollector) with custom collector.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getPage with PageCollector return page")
    void shouldGetPageWithPageCollectorReturnPage(IntegrationTestContext context) {
        // Given
        PageCollector<LockableEntity, Page<LockableEntity>> collector = createPageCollector(1, 5);

        // When
        Page<LockableEntity> page = context.queryLockableEntities()
                .orderBy(LockableEntity::getId).asc()
                .getPage(collector);

        // Then
        assertThat(page).isNotNull();
        assertThat(page.getItems()).isNotEmpty();
        assertThat(page.getTotal()).isEqualTo(5);
    }

    /**
     * Tests getPage(PageCollector) with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getPage with PageCollector and where clause")
    void shouldGetPageWithPageCollectorAndWhereClause(IntegrationTestContext context) {
        // Given
        PageCollector<LockableEntity, Page<LockableEntity>> collector = createPageCollector(1, 5);

        // When
        Page<LockableEntity> page = context.queryLockableEntities()
                .where(LockableEntity::getId).lt(4L)
                .orderBy(LockableEntity::getId).asc()
                .getPage(collector);

        // Then
        assertThat(page).isNotNull();
        assertThat(page.getItems()).isNotEmpty();
        assertThat(page.getTotal()).isEqualTo(3);
    }

    // ==================== Combined Tests ====================

    /**
     * Tests first(LockModeType) with where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with LockModeType and where clause")
    void shouldFirstWithLockModeAndWhereClause(IntegrationTestContext context) {
        // When
        Optional<LockableEntity> result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).gt(2L)
                        .orderBy(LockableEntity::getId).asc()
                        .first(LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(3L);
    }

    /**
     * Tests getSingle(LockModeType) throws when multiple results.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should getSingle with LockModeType throw when multiple results")
    void shouldGetSingleWithLockModeThrowWhenMultipleResults(IntegrationTestContext context) {
        // When & Then
        assertThatThrownBy(() ->
                context.getUpdateExecutor().doInTransaction(() ->
                        context.queryLockableEntities()
                                .where(LockableEntity::getId).lt(3L)
                                .getSingle(LockModeType.PESSIMISTIC_READ)
                )
        ).isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("found more than one");
    }

    /**
     * Tests offset with lock and where clause.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should offset with LockModeType and where clause")
    void shouldOffsetWithLockModeAndWhereClause(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).lt(5L)
                        .orderBy(LockableEntity::getId).asc()
                        .offset(1, LockModeType.PESSIMISTIC_READ)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.get(0).getId()).isEqualTo(2L);
    }

    // ==================== Helper Methods ====================

    private <T> Sliceable<T, Slice<T>> createSliceable(int offset, int limit) {
        return new Sliceable<>() {
            @Override
            public int offset() {
                return offset;
            }

            @Override
            public int limit() {
                return limit;
            }

            @Override
            public Slice<T> collect(List<T> data, long total) {
                return new Slice<>() {
                    @Override
                    public List<T> data() {
                        return data;
                    }

                    @Override
                    public long total() {
                        return total;
                    }

                    @Override
                    public int offset() {
                        return offset;
                    }

                    @Override
                    public int limit() {
                        return limit;
                    }
                };
            }
        };
    }

    private <T> PageCollector<T, Page<T>> createPageCollector(int page, int size) {
        return new PageCollector<>() {
            @Override
            public int page() {
                return page;
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public int limit() {
                return size;
            }

            @Override
            public Page<T> collect(List<T> data, long total) {
                return new Page<>() {
                    @Override
                    public List<T> getItems() {
                        return data;
                    }

                    @Override
                    public long getTotal() {
                        return total;
                    }
                };
            }
        };
    }
}