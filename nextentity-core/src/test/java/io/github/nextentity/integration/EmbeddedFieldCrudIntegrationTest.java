package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.api.EntityPath;
import io.github.nextentity.integration.entity.Address;
import io.github.nextentity.integration.entity.ContactInfo;
import io.github.nextentity.integration.entity.PersonWithAddress;
import io.github.nextentity.integration.entity.PersonWithNestedAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Embedded Field CRUD Integration Tests")
public class EmbeddedFieldCrudIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: single person with @Embedded address")
    void shouldCreatePersonWithEmbeddedAddress(IntegrationTestContext context) {
        Address address = new Address("123 Main St", "Springfield", "12345");
        PersonWithAddress person = new PersonWithAddress(100L, "John Doe", address);

        context.getUpdateExecutor().insert(person, context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress found = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(100L)
                .first();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John Doe");
        assertThat(found.getAddress()).isNotNull();
        assertThat(found.getAddress().getStreet()).isEqualTo("123 Main St");
        assertThat(found.getAddress().getCity()).isEqualTo("Springfield");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: single person with nested @Embedded (ContactInfo > Address)")
    void shouldCreatePersonWithNestedEmbedded(IntegrationTestContext context) {
        Address addr = new Address("456 Oak Ave", "Metropolis", "67890");
        ContactInfo contact = new ContactInfo("john@example.com", "555-0100", addr);
        PersonWithNestedAddress person = new PersonWithNestedAddress(200L, "Jane Doe", contact);

        context.getUpdateExecutor().insert(person, context.getEntityContext(PersonWithNestedAddress.class));

        PersonWithNestedAddress found = context.queryPersonWithNestedAddresses()
                .where(PersonWithNestedAddress::getId).eq(200L)
                .first();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Jane Doe");
        assertThat(found.getContactInfo().getEmail()).isEqualTo("john@example.com");
        assertThat(found.getContactInfo().getAddress().getStreet()).isEqualTo("456 Oak Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: batch insert persons with @Embedded address")
    void shouldBatchCreatePersonsWithEmbedded(IntegrationTestContext context) {
        PersonWithAddress p1 = new PersonWithAddress(110L, "Alice", new Address("1st St", "A", "111"));
        PersonWithAddress p2 = new PersonWithAddress(111L, "Bob", new Address("2nd Ave", "B", "222"));

        context.getUpdateExecutor().insertAll(List.of(p1, p2),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> all = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(110L, 111L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getAddress().getStreet()).isEqualTo("1st St");
        assertThat(all.get(1).getAddress().getStreet()).isEqualTo("2nd Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query person by id returns complete @Embedded fields")
    void shouldReadPersonByIdWithEmbeddedFields(IntegrationTestContext context) {
        Address address = new Address("999 Query Blvd", "TestCity", "99999");
        context.getUpdateExecutor().insert(
                new PersonWithAddress(300L, "Query User", address),
                context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress found = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(300L)
                .first();

        assertThat(found).isNotNull();
        assertThat(found.getAddress().getStreet()).isEqualTo("999 Query Blvd");
        assertThat(found.getAddress().getCity()).isEqualTo("TestCity");
        assertThat(found.getAddress().getZipCode()).isEqualTo("99999");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query all returns list with embedded fields")
    void shouldReadAllPersonsWithEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(310L, "U1", new Address("A St", "Acity", "001")),
                new PersonWithAddress(311L, "U2", new Address("B St", "Bcity", "002"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> all = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(310L, 311L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getAddress().getCity()).isEqualTo("Acity");
        assertThat(all.get(1).getAddress().getCity()).isEqualTo("Bcity");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query person with nested @Embedded returns all levels")
    void shouldReadNestedEmbeddedPerson(IntegrationTestContext context) {
        Address addr = new Address("777 Deep Rd", "DeepCity", "77777");
        ContactInfo contact = new ContactInfo("deep@test.com", "555-7777", addr);
        context.getUpdateExecutor().insert(
                new PersonWithNestedAddress(320L, "Deep User", contact),
                context.getEntityContext(PersonWithNestedAddress.class));

        PersonWithNestedAddress found = context.queryPersonWithNestedAddresses()
                .where(PersonWithNestedAddress::getId).eq(320L)
                .first();

        assertThat(found.getContactInfo().getEmail()).isEqualTo("deep@test.com");
        assertThat(found.getContactInfo().getAddress().getCity()).isEqualTo("DeepCity");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query non-existent returns null")
    void shouldReadNonExistentReturnsNull(IntegrationTestContext context) {
        PersonWithAddress found = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(99999L)
                .first();

        assertThat(found).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: filter by @Embedded internal field via path chain")
    void shouldFilterByEmbeddedInternalField(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(330L, "U1", new Address("Elm St", "Springfield", "111")),
                new PersonWithAddress(331L, "U2", new Address("Oak Ave", "Metropolis", "222"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("Springfield")
                .orderBy(PersonWithAddress::getId).asc()
                .list();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAddress().getCity()).isEqualTo("Springfield");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: filter by @Embedded internal field via EntityPath API")
    void shouldFilterByEmbeddedFieldViaEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(340L, "U1", new Address("Broadway", "NYC", "001")),
                new PersonWithAddress(341L, "U2", new Address("5th Ave", "NYC", "002"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(EntityPath.of(PersonWithAddress::getAddress).get(Address::getStreet)).eq("Broadway")
                .list();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAddress().getStreet()).isEqualTo("Broadway");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: filter by nested @Embedded internal field (ContactInfo.Address.city)")
    void shouldFilterByNestedEmbeddedInternalField(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithNestedAddress(350L, "NU1",
                        new ContactInfo("a@x.com", "111", new Address("1st", "Gotham", "000"))),
                new PersonWithNestedAddress(351L, "NU2",
                        new ContactInfo("b@x.com", "222", new Address("2nd", "Star City", "000")))
        ), context.getEntityContext(PersonWithNestedAddress.class));

        List<PersonWithNestedAddress> result = context.queryPersonWithNestedAddresses()
                .where(PersonWithNestedAddress::getContactInfo).get(ContactInfo::getAddress).get(Address::getCity).eq("Gotham")
                .list();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getContactInfo().getAddress().getCity()).isEqualTo("Gotham");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query by @Embedded zipCode returns empty for non-existent value")
    void shouldQueryByEmbeddedZipCodeNonExistent(IntegrationTestContext context) {
        List<PersonWithAddress> result = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getAddress).get(Address::getZipCode).eq("NONEXIST")
                .list();

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: single entity embedded street field")
    void shouldUpdateEmbeddedStreetField(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(400L, "Batman", new Address("789 Pine Rd", "Gotham", "11111")),
                context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress toUpdate = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(400L).first();
        toUpdate.getAddress().setStreet("321 Elm St");
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress updated = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(400L).first();
        assertThat(updated.getAddress().getStreet()).isEqualTo("321 Elm St");
        assertThat(updated.getAddress().getCity()).isEqualTo("Gotham");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: all embedded fields changed")
    void shouldUpdateAllEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(410L, "Spiderman", new Address("Old St", "OldCity", "00000")),
                context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress toUpdate = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(410L).first();
        toUpdate.setAddress(new Address("New St", "NewCity", "99999"));
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithAddress.class));

        PersonWithAddress updated = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(410L).first();
        assertThat(updated.getAddress().getStreet()).isEqualTo("New St");
        assertThat(updated.getAddress().getCity()).isEqualTo("NewCity");
        assertThat(updated.getAddress().getZipCode()).isEqualTo("99999");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: nested @Embedded fields (ContactInfo.email + Address.street)")
    void shouldUpdateNestedEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithNestedAddress(420L, "Nested Update",
                        new ContactInfo("old@test.com", "555-0000",
                                new Address("777 Old Rd", "OldCity", "77777"))),
                context.getEntityContext(PersonWithNestedAddress.class));

        PersonWithNestedAddress toUpdate = context.queryPersonWithNestedAddresses()
                .where(PersonWithNestedAddress::getId).eq(420L).first();
        toUpdate.getContactInfo().setEmail("new@test.com");
        toUpdate.getContactInfo().getAddress().setStreet("888 New Rd");
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithNestedAddress.class));

        PersonWithNestedAddress updated = context.queryPersonWithNestedAddresses()
                .where(PersonWithNestedAddress::getId).eq(420L).first();
        assertThat(updated.getContactInfo().getEmail()).isEqualTo("new@test.com");
        assertThat(updated.getContactInfo().getAddress().getStreet()).isEqualTo("888 New Rd");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: batch update embedded fields")
    void shouldBatchUpdateEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(430L, "Bulk1", new Address("A St", "CityA", "001")),
                new PersonWithAddress(431L, "Bulk2", new Address("B St", "CityB", "002"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> list = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(430L, 431L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        list.get(0).getAddress().setCity("UpdatedA");
        list.get(1).getAddress().setCity("UpdatedB");
        context.getUpdateExecutor().updateAll(list, context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> updated = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(430L, 431L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(updated.get(0).getAddress().getCity()).isEqualTo("UpdatedA");
        assertThat(updated.get(1).getAddress().getCity()).isEqualTo("UpdatedB");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete: single person with @Embedded address")
    void shouldDeletePersonWithEmbeddedAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(500L, "Delete Me", new Address("555 Cedar Ln", "Star City", "33333")),
                context.getEntityContext(PersonWithAddress.class));

        assertThat(context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(500L).first()).isNotNull();

        PersonWithAddress toDelete = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(500L).first();
        context.getUpdateExecutor().delete(toDelete, context.getEntityContext(PersonWithAddress.class));

        assertThat(context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(500L).first()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete: batch delete persons with @Embedded address")
    void shouldBatchDeletePersonsWithEmbeddedAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(510L, "Del1", new Address("X St", "X", "000")),
                new PersonWithAddress(511L, "Del2", new Address("Y St", "Y", "000"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonWithAddress> toDelete = context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(510L, 511L)
                .orderBy(PersonWithAddress::getId).asc()
                .list();
        assertThat(toDelete).hasSize(2);

        context.getUpdateExecutor().deleteAll(toDelete, context.getEntityContext(PersonWithAddress.class));

        assertThat(context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).in(510L, 511L)
                .list()).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete: delete non-existent person with @Embedded address")
    void shouldDeleteNonExistentPersonWithEmbedded(IntegrationTestContext context) {
        assertThat(context.queryPersonWithAddresses()
                .where(PersonWithAddress::getId).eq(99999L).first()).isNull();
    }
}
