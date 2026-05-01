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

    // ── 非 id 字段条件查询 ──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by name condition")
    void shouldQueryByName(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                TestDataFactory.createPersonWithAddress(600L, "Alice", new Address("1st St", "Springfield", "11111")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getName).eq("Alice")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(600L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by name like")
    void shouldQueryByNameLike(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(601L, "Alice", new Address("Oak St", "Portland", "97201")),
                TestDataFactory.createPersonWithAddress(602L, "Albert", new Address("Pine St", "Seattle", "98101")),
                TestDataFactory.createPersonWithAddress(603L, "Bob", new Address("Elm St", "Denver", "80201"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getName).like("Al%")
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Alice");
        assertThat(result.get(1).getName()).isEqualTo("Albert");
    }

    // ── 嵌入字段条件查询 ──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by embedded city field")
    void shouldQueryByEmbeddedCity(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(610L, "U1", new Address("A St", "Springfield", "111")),
                TestDataFactory.createPersonWithAddress(611L, "U2", new Address("B St", "Metropolis", "222"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("Springfield")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(610L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by embedded zipCode field")
    void shouldQueryByEmbeddedZipCode(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(612L, "U1", new Address("1st Ave", "NYC", "10001")),
                TestDataFactory.createPersonWithAddress(613L, "U2", new Address("2nd Ave", "NYC", "10002"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getZipCode).eq("10001")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(612L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by embedded street field with like")
    void shouldQueryByEmbeddedStreetLike(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(614L, "U1", new Address("Market Street", "SF", "94101")),
                TestDataFactory.createPersonWithAddress(615L, "U2", new Address("Market Road", "LA", "90001")),
                TestDataFactory.createPersonWithAddress(616L, "U3", new Address("Broadway", "NYC", "10001"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getStreet).like("Market%")
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(result).hasSize(2);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when querying non-existent embedded city")
    void shouldQueryByEmbeddedCityNonExistent(IntegrationTestContext context) {
        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("NONEXISTENT")
                .list();
        assertThat(result).isEmpty();
    }

    // ── 多条件组合查询（AND）──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by name and embedded city combined")
    void shouldQueryByNameAndEmbeddedCity(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(620L, "Alice", new Address("1st St", "Springfield", "111")),
                TestDataFactory.createPersonWithAddress(621L, "Alice", new Address("2nd St", "Metropolis", "222")),
                TestDataFactory.createPersonWithAddress(622L, "Bob", new Address("3rd St", "Springfield", "333"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getName).eq("Alice")
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("Springfield")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(620L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by embedded city and zipCode combined")
    void shouldQueryByEmbeddedCityAndZipCode(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithAddress(623L, "U1", new Address("A St", "SF", "94101")),
                TestDataFactory.createPersonWithAddress(624L, "U2", new Address("B St", "SF", "94102")),
                TestDataFactory.createPersonWithAddress(625L, "U3", new Address("C St", "LA", "94101"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("SF")
                .where(PersonWithAddress::getAddress).get(Address::getZipCode).eq("94102")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(624L);
    }

    // ── 多层嵌套嵌入字段条件查询 ──

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by nested embedded zip code (address.zip.code)")
    void shouldQueryByNestedEmbeddedZipCode(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithCrossLayerEmbedded(500L, "U1", "SF", "Market", "94105", "10001"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(501L, "U2", "NY", "5th Ave", "10001", "90210")
        ), context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getAddress).get(AddressWithZip::getZip).get(ZipCode::getCode).eq("94105")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(500L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by secondary zip code with attribute override")
    void shouldQueryBySecondaryZipCode(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithCrossLayerEmbedded(502L, "U1", "SF", "Market", "94105", "10001"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(503L, "U2", "NY", "5th Ave", "10001", "90210")
        ), context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getSecondaryZip).get(ZipCode::getCode).eq("90210")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(503L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should query by nested city and zip code combined")
    void shouldQueryByNestedCityAndZipCode(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                TestDataFactory.createPersonWithCrossLayerEmbedded(504L, "U1", "SF", "Market", "94105", "10001"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(505L, "U2", "SF", "Market", "94102", "10002"),
                TestDataFactory.createPersonWithCrossLayerEmbedded(506L, "U3", "LA", "Broadway", "94105", "10003")
        ), context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getAddress).get(AddressWithZip::getCity).eq("SF")
                .where(PersonWithCrossLayerEmbedded::getAddress).get(AddressWithZip::getZip).get(ZipCode::getCode).eq("94102")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(505L);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty when querying non-existent nested zip code")
    void shouldQueryByNestedEmbeddedZipCodeNonExistent(IntegrationTestContext context) {
        List<PersonWithCrossLayerEmbedded> result = context.queryPersonWithCrossLayerEmbedded()
                .where(PersonWithCrossLayerEmbedded::getAddress).get(AddressWithZip::getZip).get(ZipCode::getCode).eq("NONEXIST")
                .list();
        assertThat(result).isEmpty();
    }
}
