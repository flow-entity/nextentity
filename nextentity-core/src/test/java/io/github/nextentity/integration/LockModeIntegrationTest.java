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
import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Lock mode integration tests.
 * <p>
 * Tests pessimistic and optimistic locking mechanisms using
 * the getList method with LockModeType parameter.
 * <p>
 * Note: Lock operations require an active transaction.
 * All lock tests are wrapped in doInTransaction.
 * <p>
 * Uses LockableEntity which has a @Version field to support
 * optimistic locking (OPTIMISTIC, OPTIMISTIC_FORCE_INCREMENT).
 * <p>
 * These tests run against MySQL and PostgreSQL using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Lock Mode Integration Tests")
public class LockModeIntegrationTest {

    // ========================================
    // 1. PESSIMISTIC_READ Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic read lock")
    void shouldQueryWithPessimisticRead(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.PESSIMISTIC_READ).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with pessimistic read lock")
    void shouldGetSingleWithPessimisticRead(IntegrationTestContext context) {
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

    // ========================================
    // 2. PESSIMISTIC_WRITE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic write lock")
    void shouldQueryWithPessimisticWrite(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.PESSIMISTIC_WRITE).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get single with pessimistic write lock")
    void shouldGetSingleWithPessimisticWrite(IntegrationTestContext context) {
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

    // ========================================
    // 3. OPTIMISTIC Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic lock")
    void shouldQueryWithOptimistic(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.OPTIMISTIC).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with optimistic force increment lock")
    void shouldQueryWithOptimisticForceIncrement(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    // ========================================
    // 4. READ/WRITE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with read lock")
    void shouldQueryWithReadLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.READ).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with write lock")
    void shouldQueryWithWriteLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.WRITE).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    // ========================================
    // 5. NONE Lock Tests
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with no lock")
    void shouldQueryWithNoLock(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .lock(LockModeType.NONE).limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    // ========================================
    // 6. Lock with Conditions
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with where condition")
    void shouldLockWithWhereCondition(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getName).like("Lockable%")
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).limit(10)
        );

        // Then
        assertThat(entities).isNotEmpty();
        assertThat(entities).allMatch(e -> e.getName().startsWith("Lockable"));
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with multiple conditions")
    void shouldLockWithMultipleConditions(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getName).like("Lockable%")
                        .where(LockableEntity::getId).lt(4L)
                        .lock(LockModeType.PESSIMISTIC_WRITE).limit(10)
        );

        // Then
        assertThat(entities).isNotEmpty();
        for (LockableEntity entity : entities) {
            assertThat(entity.getName()).startsWith("Lockable");
            assertThat(entity.getId()).isLessThan(4L);
        }
    }

    // ========================================
    // 7. Lock with Pagination
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with pagination")
    void shouldLockWithPagination(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).limit(3)
        );

        // Then
        assertThat(entities).hasSize(3);
    }

    // ========================================
    // 8. Lock with Offset
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with offset")
    void shouldLockWithOffset(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).window(2, 3)
        );

        // Then
        assertThat(entities).hasSize(3);
        assertThat(entities.get(0).getId()).isEqualTo(3L);
    }

    // ========================================
    // 9. Lock with First
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get first with lock")
    void shouldGetFirstWithLock(IntegrationTestContext context) {
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

    // ========================================
    // 10. PESSIMISTIC_FORCE_INCREMENT Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with pessimistic force increment lock")
    void shouldQueryWithPessimisticForceIncrement(IntegrationTestContext context) {
        // When & Then
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().doInTransaction(() -> {
                    context.queryLockableEntities()
                            .where(LockableEntity::getId).eq(1L)
                            .lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT).limit(10);
                    return null;
                })
        );
    }

    // ========================================
    // 11. Lock with In Condition
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should lock with in condition")
    void shouldLockWithInCondition(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).in(1L, 2L, 3L)
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).limit(10)
        );

        // Then
        assertThat(entities).hasSize(3);
    }

    // ========================================
    // 12. Null Lock Mode
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query with null lock mode")
    void shouldQueryWithNullLockMode(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .where(LockableEntity::getId).eq(1L)
                        .limit(10)
        );

        // Then
        assertThat(entities).hasSize(1);
        assertThat(entities.get(0).getName()).isNotNull();
    }

    // ========================================
    // 13. Limit with Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should limit with lock mode")
    void shouldLimitWithLockMode(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .orderBy(LockableEntity::getId).asc()
                        .lock(LockModeType.PESSIMISTIC_READ).limit(3)
        );

        // Then
        assertThat(entities).hasSize(3);
    }

    // ========================================
    // 14. GetList with Lock
    // ========================================

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should get list with lock mode")
    void shouldGetListWithLockMode(IntegrationTestContext context) {
        // When
        List<LockableEntity> entities = context.getUpdateExecutor().doInTransaction(() ->
                context.queryLockableEntities()
                        .lock(LockModeType.PESSIMISTIC_READ).list()
        );

        // Then
        assertThat(entities).hasSize(5);
    }
}

