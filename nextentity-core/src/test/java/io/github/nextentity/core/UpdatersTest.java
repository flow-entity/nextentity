package io.github.nextentity.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Updaters utility class.
 */
@ExtendWith(MockitoExtension.class)
class UpdatersTest {

    @Mock
    private UpdateExecutor updateExecutor;

    private Updaters.UpdateImpl<TestEntity> updater;

    @BeforeEach
    void setUp() {
        updater = (Updaters.UpdateImpl<TestEntity>) Updaters.create(updateExecutor, TestEntity.class);
    }

    /**
     * Test objective: Verify that create method creates an Update instance.
     * Test scenario: Call Updaters.create with executor and type.
     * Expected result: Returns a non-null Update instance.
     */
    @Test
    void create_ShouldReturnUpdateInstance() {
        // when
        var result = Updaters.create(updateExecutor, TestEntity.class);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Updaters.UpdateImpl.class);
    }

    /**
     * Test objective: Verify that insert delegates to updateExecutor.
     * Test scenario: Insert a single entity.
     * Expected result: updateExecutor.insert is called with correct parameters.
     */
    @Test
    void insert_SingleEntity_ShouldDelegateToUpdateExecutor() {
        // given
        TestEntity entity = new TestEntity(1L, "test");

        // when
        updater.insert(entity);

        // then
        verify(updateExecutor).insert(entity, TestEntity.class);
    }

    /**
     * Test objective: Verify that insert with iterable delegates to insertAll.
     * Test scenario: Insert multiple entities.
     * Expected result: updateExecutor.insertAll is called with correct parameters.
     */
    @Test
    void insert_MultipleEntities_ShouldDelegateToInsertAll() {
        // given
        List<TestEntity> entities = Arrays.asList(
                new TestEntity(1L, "test1"),
                new TestEntity(2L, "test2")
        );

        // when
        updater.insert(entities);

        // then
        verify(updateExecutor).insertAll(entities, TestEntity.class);
    }

    /**
     * Test objective: Verify that update delegates to updateExecutor.
     * Test scenario: Update a single entity.
     * Expected result: updateExecutor.update is called and returns updated entity.
     */
    @Test
    void update_SingleEntity_ShouldDelegateToUpdateExecutor() {
        // given
        TestEntity entity = new TestEntity(1L, "test");
        when(updateExecutor.update(entity, TestEntity.class)).thenReturn(entity);

        // when
        TestEntity result = updater.update(entity);

        // then
        assertThat(result).isEqualTo(entity);
        verify(updateExecutor).update(entity, TestEntity.class);
    }

    /**
     * Test objective: Verify that update with iterable delegates to updateAll.
     * Test scenario: Update multiple entities.
     * Expected result: updateExecutor.updateAll is called and returns updated entities.
     */
    @Test
    void update_MultipleEntities_ShouldDelegateToUpdateAll() {
        // given
        List<TestEntity> entities = Arrays.asList(
                new TestEntity(1L, "test1"),
                new TestEntity(2L, "test2")
        );
        when(updateExecutor.updateAll(entities, TestEntity.class)).thenReturn(entities);

        // when
        List<TestEntity> result = updater.update(entities);

        // then
        assertThat(result).isEqualTo(entities);
        verify(updateExecutor).updateAll(entities, TestEntity.class);
    }

    /**
     * Test objective: Verify that delete delegates to updateExecutor.
     * Test scenario: Delete a single entity.
     * Expected result: updateExecutor.delete is called with correct parameters.
     */
    @Test
    void delete_SingleEntity_ShouldDelegateToUpdateExecutor() {
        // given
        TestEntity entity = new TestEntity(1L, "test");

        // when
        updater.delete(entity);

        // then
        verify(updateExecutor).delete(entity, TestEntity.class);
    }

    /**
     * Test objective: Verify that delete with iterable delegates to deleteAll.
     * Test scenario: Delete multiple entities.
     * Expected result: updateExecutor.deleteAll is called with correct parameters.
     */
    @Test
    void delete_MultipleEntities_ShouldDelegateToDeleteAll() {
        // given
        List<TestEntity> entities = Arrays.asList(
                new TestEntity(1L, "test1"),
                new TestEntity(2L, "test2")
        );

        // when
        updater.delete(entities);

        // then
        verify(updateExecutor).deleteAll(entities, TestEntity.class);
    }

    /**
     * Test objective: Verify that toString returns correct format.
     * Test scenario: Call toString on updater.
     * Expected result: Returns string with entity type name.
     */
    @Test
    void toString_ShouldReturnFormattedString() {
        // when
        String result = updater.toString();

        // then
        assertThat(result).isEqualTo("Updater(TestEntity)");
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
}
