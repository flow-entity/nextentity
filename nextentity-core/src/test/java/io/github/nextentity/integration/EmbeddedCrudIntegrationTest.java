package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.config.fixtures.TestDataFactory;
import io.github.nextentity.integration.entity.Address;
import io.github.nextentity.integration.entity.AddressWithZip;
import io.github.nextentity.integration.entity.PersonWithAddress;
import io.github.nextentity.integration.entity.PersonWithCrossLayerEmbedded;
import io.github.nextentity.integration.entity.ZipCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Embedded CRUD Integration Tests")
public class EmbeddedCrudIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    // ── 单层嵌入 CRUD ──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert entity with embedded address")
    void shouldInsertEntityWithEmbeddedAddress(IntegrationTestContext context) {
        PersonWithAddress emp = TestDataFactory.createPersonWithAddress(
                100L, "Test User", new Address("Market St", "San Francisco", "94105"));

        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(100L)
                .list();
        assertThat(result).hasSize(1);
        PersonWithAddress inserted = result.get(0);
        assertThat(inserted.getName()).isEqualTo("Test User");
        assertThat(inserted.getAddress()).isNotNull();
        assertThat(inserted.getAddress().getStreet()).isEqualTo("Market St");
        assertThat(inserted.getAddress().getCity()).isEqualTo("San Francisco");
        assertThat(inserted.getAddress().getZipCode()).isEqualTo("94105");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update embedded address fields")
    void shouldUpdateEmbeddedAddress(IntegrationTestContext context) {
        PersonWithAddress emp = TestDataFactory.createPersonWithAddress(
                200L, "Update User", new Address("5th Ave", "New York", "10001"));
        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> list = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(200L)
                .list();
        assertThat(list).hasSize(1);
        PersonWithAddress loaded = list.get(0);
        assertThat(loaded.getAddress().getCity()).isEqualTo("New York");

        loaded.getAddress().setCity("Boston");
        loaded.getAddress().setZipCode("02101");
        context.getUpdateExecutor().update(loaded, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> updatedList = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(200L)
                .list();
        assertThat(updatedList).hasSize(1);
        PersonWithAddress updated = updatedList.get(0);
        assertThat(updated.getAddress().getCity()).isEqualTo("Boston");
        assertThat(updated.getAddress().getZipCode()).isEqualTo("02101");
        assertThat(updated.getAddress().getStreet()).isEqualTo("5th Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should delete entity with embedded address")
    void shouldDeleteEntityWithEmbeddedAddress(IntegrationTestContext context) {
        PersonWithAddress emp = TestDataFactory.createPersonWithAddress(
                300L, "Delete User", new Address("Main St", "Chicago", "60601"));
        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> exists = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(300L)
                .list();
        assertThat(exists).hasSize(1);

        context.getUpdateExecutor().delete(exists.get(0), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(300L)
                .list();
        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert and query entity with null embedded address")
    void shouldInsertAndQueryNullEmbeddedAddress(IntegrationTestContext context) {
        PersonWithAddress emp = new PersonWithAddress(400L, "No Address", null);

        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(400L)
                .list();
        assertThat(result).hasSize(1);
        PersonWithAddress loaded = result.get(0);
        assertThat(loaded.getName()).isEqualTo("No Address");
        assertThat(loaded.getAddress()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should batch insert entities with embedded addresses")
    void shouldBatchInsertEmbeddedAddressEntities(IntegrationTestContext context) {
        List<PersonWithAddress> newEmployees = List.of(
                TestDataFactory.createPersonWithAddress(500L, "User A", new Address("Pike St", "Seattle", "98101")),
                TestDataFactory.createPersonWithAddress(501L, "User B", new Address("Ocean Dr", "Miami", "33101")),
                TestDataFactory.createPersonWithAddress(502L, "User C", new Address("Broadway", "Denver", "80201"))
        );

        context.getUpdateExecutor().insertAll(newEmployees, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(500L, 501L, 502L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getAddress().getCity()).isEqualTo("Seattle");
        assertThat(result.get(1).getAddress().getCity()).isEqualTo("Miami");
        assertThat(result.get(2).getAddress().getCity()).isEqualTo("Denver");
    }

    // ── 错层（cross-layer）嵌入 CRUD ──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should insert cross-layer embedded entity")
    void shouldInsertCrossLayerEmbeddedEntity(IntegrationTestContext context) {
        PersonWithCrossLayerEmbedded emp = TestDataFactory.createPersonWithCrossLayerEmbedded(
                100L, "Test User", "San Francisco", "Market St", "94105", "12345");

        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getId).eq(100L)
                .list();
        assertThat(result).hasSize(1);
        PersonWithCrossLayerEmbedded inserted = result.get(0);
        assertThat(inserted.getName()).isEqualTo("Test User");
        assertThat(inserted.getAddress()).isNotNull();
        assertThat(inserted.getAddress().getCity()).isEqualTo("San Francisco");
        assertThat(inserted.getAddress().getZip()).isNotNull();
        assertThat(inserted.getAddress().getZip().getCode()).isEqualTo("94105");
        assertThat(inserted.getSecondaryZip()).isNotNull();
        assertThat(inserted.getSecondaryZip().getCode()).isEqualTo("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update cross-layer embedded fields")
    void shouldUpdateCrossLayerEmbeddedFields(IntegrationTestContext context) {
        PersonWithCrossLayerEmbedded emp = TestDataFactory.createPersonWithCrossLayerEmbedded(
                200L, "Update Test", "New York", "5th Ave", "10001", "90210");
        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> list = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getId).eq(200L)
                .list();
        assertThat(list).hasSize(1);
        PersonWithCrossLayerEmbedded loaded = list.get(0);

        loaded.getAddress().setCity("Boston");
        loaded.getAddress().getZip().setCode("02101");
        loaded.getSecondaryZip().setCode("77777");
        context.getUpdateExecutor().update(loaded, context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> updatedList = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getId).eq(200L)
                .list();
        assertThat(updatedList).hasSize(1);
        PersonWithCrossLayerEmbedded updated = updatedList.get(0);
        assertThat(updated.getAddress().getCity()).isEqualTo("Boston");
        assertThat(updated.getAddress().getZip().getCode()).isEqualTo("02101");
        assertThat(updated.getSecondaryZip().getCode()).isEqualTo("77777");
        assertThat(updated.getAddress().getStreet()).isEqualTo("5th Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should batch insert cross-layer embedded entities")
    void shouldBatchInsertCrossLayerEmbedded(IntegrationTestContext context) {
        List<PersonWithCrossLayerEmbedded> newEmployees = List.of(
                TestDataFactory.createPersonWithCrossLayerEmbedded(300L, "User A", "Seattle", "Pike St", "98101", "10001"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(301L, "User B", "Miami", "Ocean Dr", "33101", "10002"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(302L, "User C", "Denver", "Broadway", "80201", "10003")
        );

        context.getUpdateExecutor().insertAll(newEmployees, context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getId).in(300L, 301L, 302L)
                .orderBy(PersonWithCrossLayerEmbedded::getId).asc()
                .list();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getAddress().getZip().getCode()).isEqualTo("98101");
        assertThat(result.get(1).getSecondaryZip().getCode()).isEqualTo("10002");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should update cross-layer embedded via conditional update API")
    void shouldConditionalUpdateCrossLayerEmbedded(IntegrationTestContext context) {
        PersonWithCrossLayerEmbedded emp = TestDataFactory.createPersonWithCrossLayerEmbedded(
                400L, "Original Name", "New York", "5th Ave", "10001", "90210");
        context.getUpdateExecutor().insert(emp, context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        context.doInTransaction(() -> {
            int affected = context.update(PersonWithCrossLayerEmbedded.class)
                    .set(PersonWithCrossLayerEmbedded::getName, "Updated Name")
                    .where(PersonWithCrossLayerEmbedded::getId).eq(400L)
                    .execute();
            assertThat(affected).isEqualTo(1);
        });

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getId).eq(400L)
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Updated Name");
        assertThat(result.get(0).getAddress().getCity()).isEqualTo("New York");
    }
}
