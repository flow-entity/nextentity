package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.AddressRecord;
import io.github.nextentity.integration.entity.PersonWithRecordAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Embedded Record CRUD Integration Tests")
public class EmbeddedRecordCrudIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: insert person with @Embedded record address")
    void shouldCreatePersonWithRecordEmbeddedAddress(IntegrationTestContext context) {
        PersonWithRecordAddress person = new PersonWithRecordAddress(
                600L, "Alice", new AddressRecord("100 Main St", "Springfield", "62701"));

        context.getUpdateExecutor().insert(person, context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress found = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(600L)
                .first();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Alice");
        assertThat(found.getAddress()).isNotNull();
        assertThat(found.getAddress().street()).isEqualTo("100 Main St");
        assertThat(found.getAddress().city()).isEqualTo("Springfield");
        assertThat(found.getAddress().zipCode()).isEqualTo("62701");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Create: batch insert persons with @Embedded record address")
    void shouldBatchCreatePersonsWithRecordEmbedded(IntegrationTestContext context) {
        PersonWithRecordAddress p1 = new PersonWithRecordAddress(610L, "Bob", new AddressRecord("1st St", "A", "111"));
        PersonWithRecordAddress p2 = new PersonWithRecordAddress(611L, "Carol", new AddressRecord("2nd Ave", "B", "222"));

        context.getUpdateExecutor().insertAll(List.of(p1, p2),
                context.getEntityContext(PersonWithRecordAddress.class));

        List<PersonWithRecordAddress> all = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).in(610L, 611L)
                .orderBy(PersonWithRecordAddress::getId).asc()
                .list();
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getAddress().street()).isEqualTo("1st St");
        assertThat(all.get(1).getAddress().street()).isEqualTo("2nd Ave");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: query person by id returns complete @Embedded record fields")
    void shouldReadPersonByIdWithRecordEmbeddedFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithRecordAddress(620L, "Dave", new AddressRecord("999 Query Blvd", "TestCity", "99999")),
                context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress found = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(620L)
                .first();

        assertThat(found).isNotNull();
        assertThat(found.getAddress().street()).isEqualTo("999 Query Blvd");
        assertThat(found.getAddress().city()).isEqualTo("TestCity");
        assertThat(found.getAddress().zipCode()).isEqualTo("99999");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: filter by @Embedded record internal field via path chain")
    void shouldFilterByRecordEmbeddedInternalField(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithRecordAddress(630L, "Eve", new AddressRecord("Elm St", "Springfield", "111")),
                new PersonWithRecordAddress(631L, "Frank", new AddressRecord("Oak Ave", "Metropolis", "222"))
        ), context.getEntityContext(PersonWithRecordAddress.class));

        List<PersonWithRecordAddress> result = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getAddress).get(AddressRecord::city).eq("Springfield")
                .orderBy(PersonWithRecordAddress::getId).asc()
                .list();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAddress().city()).isEqualTo("Springfield");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Read: filter by @Embedded record street field")
    void shouldFilterByRecordEmbeddedStreet(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithRecordAddress(640L, "Grace", new AddressRecord("Broadway", "NYC", "001")),
                new PersonWithRecordAddress(641L, "Heidi", new AddressRecord("5th Ave", "NYC", "002"))
        ), context.getEntityContext(PersonWithRecordAddress.class));

        List<PersonWithRecordAddress> result = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getAddress).get(AddressRecord::street).eq("Broadway")
                .list();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAddress().street()).isEqualTo("Broadway");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: replace entire @Embedded record address")
    void shouldUpdateRecordEmbeddedByReplacingWholeRecord(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithRecordAddress(650L, "Ivan", new AddressRecord("789 Pine Rd", "Gotham", "11111")),
                context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress toUpdate = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(650L).first();
        toUpdate.setAddress(new AddressRecord("321 Elm St", "NewCity", "22222"));
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress updated = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(650L).first();
        assertThat(updated.getAddress().street()).isEqualTo("321 Elm St");
        assertThat(updated.getAddress().city()).isEqualTo("NewCity");
        assertThat(updated.getAddress().zipCode()).isEqualTo("22222");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Update: set @Embedded record to null")
    void shouldUpdateRecordEmbeddedToNull(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithRecordAddress(660L, "Judy", new AddressRecord("Some St", "SomeCity", "33333")),
                context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress toUpdate = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(660L).first();
        toUpdate.setAddress(null);
        context.getUpdateExecutor().update(toUpdate, context.getEntityContext(PersonWithRecordAddress.class));

        PersonWithRecordAddress updated = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(660L).first();
        assertThat(updated.getName()).isEqualTo("Judy");
        assertThat(updated.getAddress()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete: single person with @Embedded record address")
    void shouldDeletePersonWithRecordEmbeddedAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithRecordAddress(670L, "Karl", new AddressRecord("555 Cedar Ln", "Star City", "44444")),
                context.getEntityContext(PersonWithRecordAddress.class));

        assertThat(context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(670L).first()).isNotNull();

        PersonWithRecordAddress toDelete = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(670L).first();
        context.getUpdateExecutor().delete(toDelete, context.getEntityContext(PersonWithRecordAddress.class));

        assertThat(context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).eq(670L).first()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Delete: batch delete persons with @Embedded record address")
    void shouldBatchDeletePersonsWithRecordEmbeddedAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithRecordAddress(680L, "Leo", new AddressRecord("X St", "X", "000")),
                new PersonWithRecordAddress(681L, "Mallory", new AddressRecord("Y St", "Y", "000"))
        ), context.getEntityContext(PersonWithRecordAddress.class));

        List<PersonWithRecordAddress> toDelete = context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).in(680L, 681L)
                .orderBy(PersonWithRecordAddress::getId).asc()
                .list();
        assertThat(toDelete).hasSize(2);

        context.getUpdateExecutor().deleteAll(toDelete, context.getEntityContext(PersonWithRecordAddress.class));

        assertThat(context.queryPersonWithRecordAddresses()
                .where(PersonWithRecordAddress::getId).in(680L, 681L)
                .list()).isEmpty();
    }
}
