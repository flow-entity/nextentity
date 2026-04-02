package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.LockableEntity;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for Collector methods with LockModeType parameters.
 * <p>
 * Tests default methods in Collector interface that accept LockModeType:
 * - first()/single() with lock
 * - window()/limit()/list() with lock
 * - slice(int, int)
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
     * Tests first(LockModeType) returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with LockModeType return value")
    void shouldFirstWithLockModeReturnValue(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).first()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    /**
     * Tests first(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should first with pessimistic write lock")
    void shouldFirstWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_WRITE).first()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
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
                        .lock(LockModeType.PESSIMISTIC_READ).first()
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).first()
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
                        .lock(LockModeType.PESSIMISTIC_READ).single()
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).single()
        );

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
    }

    // ==================== single(LockModeType) Tests ====================

    /**
     * Tests single(LockModeType) returns value.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with LockModeType return value")
    void shouldSingleWithLockModeReturnValue(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.PESSIMISTIC_READ).single()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    /**
     * Tests single(LockModeType) returns empty when no result.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with LockModeType return empty when no result")
    void shouldSingleWithLockModeReturnEmptyWhenNoResult(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(999L)
                        .lock(LockModeType.PESSIMISTIC_READ).single()
        );

        // Then
        assertThat(result).isNull();
    }

    /**
     * Tests single(LockModeType) with pessimistic write lock.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with pessimistic write lock")
    void shouldSingleWithPessimisticWriteLock(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.PESSIMISTIC_WRITE).single()
        );

        // Then
        assertThat(result).isNotNull();
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
                        .lock(LockModeType.PESSIMISTIC_READ).single()
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
                        .lock(LockModeType.PESSIMISTIC_READ).single()
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).single()
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
                        .lock(LockModeType.PESSIMISTIC_READ).window(2, 10)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.getFirst().getId()).isEqualTo(3L);
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).window(1, 10)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.getFirst().getId()).isEqualTo(2L);
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
                        .lock(LockModeType.PESSIMISTIC_READ).list()
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
                        .lock(LockModeType.PESSIMISTIC_READ).limit(3)
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).limit(2)
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
                        .lock(LockModeType.PESSIMISTIC_READ).list()
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
                        .lock(LockModeType.PESSIMISTIC_WRITE).list()
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
    @DisplayName("Should first with offset and LockModeType return value")
    void shouldFirstWithOffsetAndLockModeReturnValue(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).window(2, 1).getFirst()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
    }

    // ==================== single(int, LockModeType) Tests ====================

    /**
     * Tests single(int, LockModeType) with offset.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should single with offset and LockModeType return value")
    void shouldSingleWithOffsetAndLockModeReturnValue(IntegrationTestContext context) {
        // When
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(3L)
                        .lock(LockModeType.PESSIMISTIC_READ).single()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
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
        LockableEntity result = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).gt(2L)
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).first()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
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
                                .lock(LockModeType.PESSIMISTIC_READ).single()
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
                        .lock(LockModeType.PESSIMISTIC_READ).window(1, 10)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities.getFirst().getId()).isEqualTo(2L);
    }

}

