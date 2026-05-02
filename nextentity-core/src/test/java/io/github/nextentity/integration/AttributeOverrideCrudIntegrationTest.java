package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Address;
import io.github.nextentity.integration.entity.ContactInfo;
import io.github.nextentity.integration.entity.PersonWithNestedOverriddenContact;
import io.github.nextentity.integration.entity.PersonWithOverriddenAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("@AttributeOverride CRUD Integration Tests")
public class AttributeOverrideCrudIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: person with @AttributeOverridden address columns")
    void shouldCreatePersonWithOverriddenAddress(IntegrationTestContext context) {
        Address address = new Address("1st Ave", "Springfield", "12345");
        PersonWithOverriddenAddress person = new PersonWithOverriddenAddress(100L, "John", address);

        context.getUpdateExecutor().insert(person, context.getEntityContext(PersonWithOverriddenAddress.class));

        PersonWithOverriddenAddress found = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).eq(100L)
                .first();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
        assertThat(found.getAddress()).isNotNull();
        assertThat(found.getAddress().getStreet()).isEqualTo("1st Ave");
        assertThat(found.getAddress().getCity()).isEqualTo("Springfield");
        assertThat(found.getAddress().getZipCode()).isEqualTo("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: person with nested @AttributeOverridden contact columns")
    void shouldCreatePersonWithNestedOverriddenContact(IntegrationTestContext context) {
        Address addr = new Address("5th Ave", "Metropolis", "67890");
        ContactInfo contact = new ContactInfo("john@test.com", "555-0100", addr);
        PersonWithNestedOverriddenContact person = new PersonWithNestedOverriddenContact(200L, "Jane", contact);

        context.getUpdateExecutor().insert(person, context.getEntityContext(PersonWithNestedOverriddenContact.class));

        PersonWithNestedOverriddenContact found = context.queryPersonWithNestedOverriddenContacts()
                .where(PersonWithNestedOverriddenContact::getId).eq(200L)
                .first();
        assertThat(found).isNotNull();
        assertThat(found.getContactInfo().getEmail()).isEqualTo("john@test.com");
        assertThat(found.getContactInfo().getPhone()).isEqualTo("555-0100");
        assertThat(found.getContactInfo().getAddress().getStreet()).isEqualTo("5th Ave");
        assertThat(found.getContactInfo().getAddress().getCity()).isEqualTo("Metropolis");
        assertThat(found.getContactInfo().getAddress().getZipCode()).isEqualTo("67890");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Batch insert with @AttributeOverridden address")
    void shouldBatchCreateWithOverriddenAddress(IntegrationTestContext context) {
        PersonWithOverriddenAddress p1 = new PersonWithOverriddenAddress(110L, "Alice",
                new Address("Elm St", "Gotham", "11111"));
        PersonWithOverriddenAddress p2 = new PersonWithOverriddenAddress(111L, "Bob",
                new Address("Oak Ave", "Star City", "22222"));

        context.getUpdateExecutor().insertAll(List.of(p1, p2),
                context.getEntityContext(PersonWithOverriddenAddress.class));

        List<PersonWithOverriddenAddress> all = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).in(110L, 111L)
                .orderBy(PersonWithOverriddenAddress::getId).asc()
                .list();
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getAddress().getStreet()).isEqualTo("Elm St");
        assertThat(all.get(1).getAddress().getStreet()).isEqualTo("Oak Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Filter by overridden field via path chain")
    void shouldFilterByOverriddenFieldViaPathChain(IntegrationTestContext context) {
        PersonWithOverriddenAddress p1 = new PersonWithOverriddenAddress(120L, "U1",
                new Address("Broadway", "NYC", "001"));
        PersonWithOverriddenAddress p2 = new PersonWithOverriddenAddress(121L, "U2",
                new Address("5th Ave", "NYC", "002"));

        context.getUpdateExecutor().insertAll(List.of(p1, p2),
                context.getEntityContext(PersonWithOverriddenAddress.class));

        List<PersonWithOverriddenAddress> result = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getAddress).get(Address::getStreet).eq("Broadway")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAddress().getStreet()).isEqualTo("Broadway");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Filter by nested overridden field (ContactInfo.Address.street)")
    void shouldFilterByNestedOverriddenField(IntegrationTestContext context) {
        PersonWithNestedOverriddenContact p1 = new PersonWithNestedOverriddenContact(130L, "NU1",
                new ContactInfo("a@x.com", "111", new Address("Pine St", "Gotham", "000")));
        PersonWithNestedOverriddenContact p2 = new PersonWithNestedOverriddenContact(131L, "NU2",
                new ContactInfo("b@x.com", "222", new Address("Cedar Ave", "Star City", "000")));

        context.getUpdateExecutor().insertAll(List.of(p1, p2),
                context.getEntityContext(PersonWithNestedOverriddenContact.class));

        List<PersonWithNestedOverriddenContact> result = context.queryPersonWithNestedOverriddenContacts()
                .where(PersonWithNestedOverriddenContact::getContactInfo).get(ContactInfo::getAddress).get(Address::getCity).eq("Gotham")
                .list();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getContactInfo().getAddress().getCity()).isEqualTo("Gotham");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update overridden embedded fields")
    void shouldUpdateOverriddenEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithOverriddenAddress(140L, "Batman",
                        new Address("Old St", "OldCity", "00000")),
                context.getEntityContext(PersonWithOverriddenAddress.class));

        PersonWithOverriddenAddress toUpdate = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).eq(140L).first();
        toUpdate.getAddress().setStreet("New St");
        toUpdate.getAddress().setCity("NewCity");
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithOverriddenAddress.class));

        PersonWithOverriddenAddress updated = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).eq(140L).first();
        assertThat(updated.getAddress().getStreet()).isEqualTo("New St");
        assertThat(updated.getAddress().getCity()).isEqualTo("NewCity");
        assertThat(updated.getAddress().getZipCode()).isEqualTo("00000");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update nested overridden fields (ContactInfo.email + Address.street)")
    void shouldUpdateNestedOverriddenFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithNestedOverriddenContact(150L, "Nested Update",
                        new ContactInfo("old@test.com", "555-0000",
                                new Address("Old Rd", "OldCity", "77777"))),
                context.getEntityContext(PersonWithNestedOverriddenContact.class));

        PersonWithNestedOverriddenContact toUpdate = context.queryPersonWithNestedOverriddenContacts()
                .where(PersonWithNestedOverriddenContact::getId).eq(150L).first();
        toUpdate.getContactInfo().setEmail("new@test.com");
        toUpdate.getContactInfo().getAddress().setStreet("New Rd");
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithNestedOverriddenContact.class));

        PersonWithNestedOverriddenContact updated = context.queryPersonWithNestedOverriddenContacts()
                .where(PersonWithNestedOverriddenContact::getId).eq(150L).first();
        assertThat(updated.getContactInfo().getEmail()).isEqualTo("new@test.com");
        assertThat(updated.getContactInfo().getAddress().getStreet()).isEqualTo("New Rd");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete person with @AttributeOverridden address")
    void shouldDeleteWithOverriddenAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithOverriddenAddress(160L, "Delete Me",
                        new Address("Cedar Ln", "Star City", "33333")),
                context.getEntityContext(PersonWithOverriddenAddress.class));

        PersonWithOverriddenAddress toDelete = context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).eq(160L).first();
        assertThat(toDelete).isNotNull();

        context.getUpdateExecutor().delete(toDelete, context.getEntityContext(PersonWithOverriddenAddress.class));

        assertThat(context.queryPersonWithOverriddenAddresses()
                .where(PersonWithOverriddenAddress::getId).eq(160L).first()).isNull();
    }
}
