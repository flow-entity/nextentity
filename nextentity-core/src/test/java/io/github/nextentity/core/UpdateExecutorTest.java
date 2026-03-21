package io.github.nextentity.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for UpdateExecutor interface default methods.
 */
class UpdateExecutorTest {

    private TestUpdateExecutor updateExecutor;

    @BeforeEach
    void setUp() {
        updateExecutor = new TestUpdateExecutor();
    }

    /**
     * Test objective: Verify that insert default method delegates to insertAll with single element list.
     * Test scenario: Call insert with a single entity.
     * Expected result: insertAll is called with a list containing the single entity.
     */
    @Test
    void insert_SingleEntity_ShouldDelegateToInsertAll() {
        // given
        TestEntity entity = new TestEntity(1L, "test");

        // when
        updateExecutor.insert(entity, TestEntity.class);

        // then
        assertThat(updateExecutor.getInsertedEntities()).hasSize(1);
        assertThat(updateExecutor.getInsertedEntities()).contains(entity);
    }

    /**
     * Test objective: Verify that update default method delegates to updateAll and returns first element.
     * Test scenario: Call update with a single entity.
     * Expected result: updateAll is called and the first element of the result is returned.
     */
    @Test
    void update_SingleEntity_ShouldDelegateToUpdateAll() {
        // given
        TestEntity entity = new TestEntity(1L, "test");
        updateExecutor.setUpdateResult(Collections.singletonList(entity));

        // when
        TestEntity result = updateExecutor.update(entity, TestEntity.class);

        // then
        assertThat(result).isEqualTo(entity);
        assertThat(updateExecutor.getUpdatedEntities()).hasSize(1);
        assertThat(updateExecutor.getUpdatedEntities()).contains(entity);
    }

    /**
     * Test objective: Verify that delete default method delegates to deleteAll with single element list.
     * Test scenario: Call delete with a single entity.
     * Expected result: deleteAll is called with a list containing the single entity.
     */
    @Test
    void delete_SingleEntity_ShouldDelegateToDeleteAll() {
        // given
        TestEntity entity = new TestEntity(1L, "test");

        // when
        updateExecutor.delete(entity, TestEntity.class);

        // then
        assertThat(updateExecutor.getDeletedEntities()).hasSize(1);
        assertThat(updateExecutor.getDeletedEntities()).contains(entity);
    }

    /**
     * Test entity class for testing.
     */
    static class TestEntity {
        private final Long id;
        private final String name;

        TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Test implementation of UpdateExecutor.
     */
    @SuppressWarnings("unchecked")
    static class TestUpdateExecutor implements UpdateExecutor {
        private final List<Object> insertedEntities = new ArrayList<>();
        private final List<Object> updatedEntities = new ArrayList<>();
        private final List<Object> deletedEntities = new ArrayList<>();
        private List<Object> updateResult = Collections.emptyList();

        public List<Object> getInsertedEntities() {
            return insertedEntities;
        }

        public List<Object> getUpdatedEntities() {
            return updatedEntities;
        }

        public List<Object> getDeletedEntities() {
            return deletedEntities;
        }

        public void setUpdateResult(List<Object> updateResult) {
            this.updateResult = updateResult;
        }

        @Override
        public <T> void insertAll(Iterable<T> entities, Class<T> entityType) {
            entities.forEach(e -> insertedEntities.add(e));
        }

        @Override
        public <T> List<T> updateAll(Iterable<T> entities, Class<T> entityType) {
            entities.forEach(e -> updatedEntities.add(e));
            return (List<T>) updateResult;
        }

        @Override
        public <T> void deleteAll(Iterable<T> entities, Class<T> entityType) {
            entities.forEach(e -> deletedEntities.add(e));
        }

        @Override
        public <T> T patch(T entity, Class<T> entityType) {
            return entity;
        }
    }
}
