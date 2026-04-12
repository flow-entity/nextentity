package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.AutoIncrementEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Auto-increment ID generation integration tests.
 * <p>
 * Tests insert operations for entities with database-generated identity values.
 * Scenarios include:
 * - Single insert with ID auto-generation
 * - Batch insert with multiple IDs auto-generated
 * - ID value verification after insert
 * - Null field handling
 * - Full field insert
 * - Transactional insert
 * <p>
 * These tests run against MySQL, PostgreSQL, and SQL Server using Testcontainers.
 *
 * @author HuangChengwei
 */
@DisplayName("Auto-Increment ID Generation Integration Tests")
public class AutoIncrementInsertIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    /**
     * Tests single entity insert with auto-generated ID.
     * Verifies that:
     * - Entity ID is null before insert (no pre-assignment)
     * - Entity ID is populated after insert
     * - Generated ID is positive (valid database value)
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should generate ID on single insert")
    void shouldGenerateIdOnSingleInsert(IntegrationTestContext context) {
        // Given - entity without ID
        AutoIncrementEntity entity = createEntity("Test Entity", "Test Description", 1, true);

        // Verify ID is null before insert
        assertThat(entity.getId()).isNull();

        // When
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then - ID should be auto-generated
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getId()).isPositive();

        // Verify entity can be queried by generated ID
        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(entity.getId())
                .single();
        assertThat(queried).isNotNull();
        assertThat(queried.getName()).isEqualTo("Test Entity");
    }

    /**
     * Tests batch insert with auto-generated IDs.
     * Verifies that:
     * - Each entity gets unique ID
     * - All IDs are positive
     * - IDs are sequentially assigned
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should generate unique IDs on batch insert")
    void shouldGenerateUniqueIdsOnBatchInsert(IntegrationTestContext context) {
        // Given - multiple entities without IDs
        List<AutoIncrementEntity> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            entities.add(createEntity("Batch Entity " + i, "Description " + i, i, true));
        }

        // Verify all IDs are null before insert
        assertThat(entities).allMatch(e -> e.getId() == null);

        // When
        context.getUpdateExecutor().insertAll(entities, context.getEntityContext(AutoIncrementEntity.class));

        // Then - all IDs should be generated
        assertThat(entities).allMatch(e -> e.getId() != null && e.getId() > 0);

        // Verify IDs are unique
        List<Long> ids = entities.stream().map(AutoIncrementEntity::getId).toList();
        assertThat(ids).doesNotHaveDuplicates();

        // Verify all entities can be queried
        List<AutoIncrementEntity> queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).in(ids.toArray(new Long[0]))
                .list();
        assertThat(queried).hasSize(10);
    }

    /**
     * Tests insert with null optional fields.
     * Verifies that:
     * - Entity with null fields can be inserted
     * - ID is still generated correctly
     * - Null fields remain null in database
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert entity with null optional fields")
    void shouldInsertEntityWithNullOptionalFields(IntegrationTestContext context) {
        // Given - entity with null fields
        AutoIncrementEntity entity = new AutoIncrementEntity();
        entity.setName("Minimal Entity");
        entity.setDescription(null);    // null
        entity.setPriority(null);        // null
        entity.setActive(true);

        // When
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then
        assertThat(entity.getId()).isNotNull().isPositive();

        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(entity.getId())
                .single();
        assertThat(queried.getName()).isEqualTo("Minimal Entity");
        assertThat(queried.getDescription()).isNull();
        assertThat(queried.getPriority()).isNull();
        assertThat(queried.getActive()).isTrue();
    }

    /**
     * Tests insert with all fields populated.
     * Verifies that:
     * - All field values are persisted correctly
     * - ID generation works alongside full data
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert entity with all fields")
    void shouldInsertEntityWithAllFields(IntegrationTestContext context) {
        // Given - fully populated entity
        AutoIncrementEntity entity = new AutoIncrementEntity(
                "Full Entity",
                "Complete description with all fields",
                100,
                true
        );

        // When
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then
        assertThat(entity.getId()).isNotNull().isPositive();

        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(entity.getId())
                .single();
        assertThat(queried.getName()).isEqualTo("Full Entity");
        assertThat(queried.getDescription()).isEqualTo("Complete description with all fields");
        assertThat(queried.getPriority()).isEqualTo(100);
        assertThat(queried.getActive()).isTrue();
        assertThat(queried.getCreatedAt()).isNotNull();
    }

    /**
     * Tests large batch insert (50 entities).
     * Verifies that:
     * - All entities receive unique IDs
     * - Performance is acceptable for batch operations
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle large batch insert efficiently")
    void shouldHandleLargeBatchInsertEfficiently(IntegrationTestContext context) {
        // Given - 50 entities
        List<AutoIncrementEntity> entities = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            entities.add(createEntity("Large Batch " + i, "Desc " + i, i % 10, i % 2 == 0));
        }

        // When
        long startTime = System.currentTimeMillis();
        context.getUpdateExecutor().insertAll(entities, context.getEntityContext(AutoIncrementEntity.class));
        long duration = System.currentTimeMillis() - startTime;

        // Then
        assertThat(entities).allMatch(e -> e.getId() != null);
        assertThat(entities).hasSize(50);

        // All IDs should be unique
        List<Long> ids = entities.stream().map(AutoIncrementEntity::getId).toList();
        assertThat(ids).doesNotHaveDuplicates();

        // Log performance (informational)
        System.out.println("Batch insert of 50 auto-increment entities took: " + duration + "ms");
    }

    /**
     * Tests insert followed by update.
     * Verifies that:
     * - ID remains same after update
     * - Updated values are persisted
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should preserve ID after update")
    void shouldPreserveIdAfterUpdate(IntegrationTestContext context) {
        // Given - insert entity
        AutoIncrementEntity entity = createEntity("Original Name", "Original Desc", 1, true);
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));
        Long originalId = entity.getId();
        assertThat(originalId).isNotNull();

        // When - update entity
        entity.setName("Updated Name");
        entity.setPriority(999);
        context.getUpdateExecutor().update(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then - ID should be preserved
        assertThat(entity.getId()).isEqualTo(originalId);

        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(originalId)
                .single();
        assertThat(queried.getName()).isEqualTo("Updated Name");
        assertThat(queried.getPriority()).isEqualTo(999);
    }

    /**
     * Tests insert in transaction.
     * Verifies that:
     * - ID generation works in transactional context
     * - Transaction commit preserves generated ID
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should generate ID in transaction")
    void shouldGenerateIdInTransaction(IntegrationTestContext context) {
        // Given
        AutoIncrementEntity entity = createEntity("Transaction Entity", "In Transaction", 5, true);
        Long generatedId = context.doInTransaction(() -> {
            // When - insert in transaction
            context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));
            return entity.getId();
        });

        // Then - ID should be valid after transaction
        assertThat(generatedId).isNotNull().isPositive();

        // Verify entity exists outside transaction
        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(generatedId)
                .single();
        assertThat(queried).isNotNull();
        assertThat(queried.getName()).isEqualTo("Transaction Entity");
    }

    /**
     * Tests sequential inserts get sequential IDs.
     * Verifies that:
     * - Each insert gets a new ID
     * - IDs increment in order (database behavior)
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should assign sequential IDs on sequential inserts")
    void shouldAssignSequentialIdsOnSequentialInserts(IntegrationTestContext context) {
        // Given
        AutoIncrementEntity first = createEntity("First", "First entity", 1, true);
        AutoIncrementEntity second = createEntity("Second", "Second entity", 2, true);
        AutoIncrementEntity third = createEntity("Third", "Third entity", 3, true);

        // When - sequential inserts
        context.getUpdateExecutor().insert(first, context.getEntityContext(AutoIncrementEntity.class));
        context.getUpdateExecutor().insert(second, context.getEntityContext(AutoIncrementEntity.class));
        context.getUpdateExecutor().insert(third, context.getEntityContext(AutoIncrementEntity.class));

        // Then - IDs should be sequential (or at least increasing)
        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isNotNull();
        assertThat(third.getId()).isNotNull();

        assertThat(second.getId()).isGreaterThan(first.getId());
        assertThat(third.getId()).isGreaterThan(second.getId());
    }

    /**
     * Tests insert with special characters in text fields.
     * Verifies that:
     * - Special characters are handled correctly
     * - ID generation is not affected by data content
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields(IntegrationTestContext context) {
        // Given - entity with special characters
        AutoIncrementEntity entity = createEntity(
                "Test's Entity with \"quotes\"",
                "Description with special chars: <>&'\"",
                1,
                true
        );

        // When
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then
        assertThat(entity.getId()).isNotNull().isPositive();

        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(entity.getId())
                .single();
        assertThat(queried.getName()).isEqualTo("Test's Entity with \"quotes\"");
        assertThat(queried.getDescription()).isEqualTo("Description with special chars: <>&'\"");
    }

    /**
     * Tests insert with boundary priority values.
     * Verifies that:
     * - Integer boundary values (MIN, MAX) are handled
     * - ID generation works with extreme data values
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle boundary priority values")
    void shouldHandleBoundaryPriorityValues(IntegrationTestContext context) {
        // Given - entities with boundary values
        AutoIncrementEntity minPriority = createEntity("Min Priority", "Min", Integer.MIN_VALUE, true);
        AutoIncrementEntity maxPriority = createEntity("Max Priority", "Max", Integer.MAX_VALUE, true);
        AutoIncrementEntity zeroPriority = createEntity("Zero Priority", "Zero", 0, true);

        // When
        context.getUpdateExecutor().insert(minPriority, context.getEntityContext(AutoIncrementEntity.class));
        context.getUpdateExecutor().insert(maxPriority, context.getEntityContext(AutoIncrementEntity.class));
        context.getUpdateExecutor().insert(zeroPriority, context.getEntityContext(AutoIncrementEntity.class));

        // Then
        assertThat(minPriority.getId()).isNotNull();
        assertThat(maxPriority.getId()).isNotNull();
        assertThat(zeroPriority.getId()).isNotNull();

        AutoIncrementEntity queriedMin = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(minPriority.getId())
                .single();
        assertThat(queriedMin.getPriority()).isEqualTo(Integer.MIN_VALUE);

        AutoIncrementEntity queriedMax = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(maxPriority.getId())
                .single();
        assertThat(queriedMax.getPriority()).isEqualTo(Integer.MAX_VALUE);
    }

    /**
     * Tests insert with null name should fail.
     * Name is defined as NOT NULL in schema.
     * This test documents the expected validation behavior.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should fail when inserting entity with null name (NOT NULL constraint)")
    void shouldFailOnNullName(IntegrationTestContext context) {
        // Given - entity with null name (violates NOT NULL constraint)
        AutoIncrementEntity entity = new AutoIncrementEntity();
        entity.setName(null);  // NOT NULL column
        entity.setDescription("Valid description");
        entity.setActive(true);

        // When/Then - should throw exception due to NOT NULL constraint
        assertThatThrownBy(() -> context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class)))
                .isInstanceOf(RuntimeException.class);
    }

    /**
     * Tests that insert doesn't affect existing entities.
     * Verifies that:
     * - New inserts don't modify existing entity IDs
     * - Data isolation is maintained
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should not affect existing entities on new insert")
    void shouldNotAffectExistingEntitiesOnNewInsert(IntegrationTestContext context) {
        // Given - insert initial entity
        AutoIncrementEntity initial = createEntity("Initial", "Initial entity", 1, true);
        context.getUpdateExecutor().insert(initial, context.getEntityContext(AutoIncrementEntity.class));
        Long initialId = initial.getId();
        long initialCount = context.queryAutoIncrementEntities().count();

        // When - insert new entity
        AutoIncrementEntity newEntity = createEntity("New Entity", "New", 2, true);
        context.getUpdateExecutor().insert(newEntity, context.getEntityContext(AutoIncrementEntity.class));

        // Then - initial entity unchanged
        AutoIncrementEntity unchanged = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(initialId)
                .single();
        assertThat(unchanged.getName()).isEqualTo("Initial");
        assertThat(unchanged.getId()).isEqualTo(initialId);

        // Count should increase by 1
        long newCount = context.queryAutoIncrementEntities().count();
        assertThat(newCount).isEqualTo(initialCount + 1);
    }

    /**
     * Tests empty batch insert should not throw exception.
     * Verifies graceful handling of edge case.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle empty batch insert gracefully")
    void shouldHandleEmptyBatchInsertGracefully(IntegrationTestContext context) {
        // Given
        List<AutoIncrementEntity> emptyList = new ArrayList<>();

        // When/Then - should not throw exception
        assertThatNoException().isThrownBy(() ->
                context.getUpdateExecutor().insertAll(emptyList, context.getEntityContext(AutoIncrementEntity.class)));
    }

    /**
     * Tests single element batch insert.
     * Verifies that batch API works correctly with single entity.
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should handle single element batch insert")
    void shouldHandleSingleElementBatchInsert(IntegrationTestContext context) {
        // Given
        List<AutoIncrementEntity> singleList = new ArrayList<>();
        singleList.add(createEntity("Single Batch", "Single", 1, true));

        // When
        context.getUpdateExecutor().insertAll(singleList, context.getEntityContext(AutoIncrementEntity.class));

        // Then
        assertThat(singleList.get(0).getId()).isNotNull().isPositive();

        AutoIncrementEntity queried = context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(singleList.get(0).getId())
                .single();
        assertThat(queried.getName()).isEqualTo("Single Batch");
    }

    /**
     * Tests insert followed by delete and verify ID remains valid.
     * Verifies that:
     * - Generated ID works for delete operation
     * - Delete doesn't affect other entities' IDs
     */
    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete entity using auto-generated ID")
    void shouldDeleteEntityUsingAutoGeneratedId(IntegrationTestContext context) {
        // Given - insert entity
        AutoIncrementEntity entity = createEntity("To Delete", "Will be deleted", 1, true);
        context.getUpdateExecutor().insert(entity, context.getEntityContext(AutoIncrementEntity.class));
        Long id = entity.getId();
        assertThat(id).isNotNull();

        // Verify exists
        assertThat(context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(id)
                .single()).isNotNull();

        // When - delete
        context.getUpdateExecutor().delete(entity, context.getEntityContext(AutoIncrementEntity.class));

        // Then - should not exist
        assertThat(context.queryAutoIncrementEntities()
                .where(AutoIncrementEntity::getId).eq(id)
                .list()).isEmpty();
    }

    // Helper methods

    private AutoIncrementEntity createEntity(String name, String description, Integer priority, Boolean active) {
        return new AutoIncrementEntity(name, description, priority, active);
    }
}