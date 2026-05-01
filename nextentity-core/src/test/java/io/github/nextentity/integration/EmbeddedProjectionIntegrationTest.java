package io.github.nextentity.integration;

import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.dto.*;
import io.github.nextentity.integration.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Embedded Projection Integration Tests")
public class EmbeddedProjectionIntegrationTest {

    @AfterEach
    void tearDown() {
        var context = IntegrationTestProvider.getEntityManagerContext();
        if (context != null) {
            context.reset();
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project flattened embedded fields to JavaBean with @EntityPath")
    void shouldProjectEmbeddedToJavaBeanWithEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80000L, "Alice", new Address("123 Main St", "Springfield", "12345")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getId).eq(80000L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressBasic dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80000L);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getStreet()).isEqualTo("123 Main St");
        assertThat(dto.getCity()).isEqualTo("Springfield");
        assertThat(dto.getZipCode()).isEqualTo("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project flattened embedded fields to Record with @EntityPath")
    void shouldProjectEmbeddedToRecordWithEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80001L, "Bob", new Address("456 Oak Ave", "Metropolis", "67890")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressRecord> results = context.queryPersonWithAddresses()
                .select(PersonAddressRecord.class)
                .where(PersonWithAddress::getId).eq(80001L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressRecord record = results.getFirst();
        assertThat(record.id()).isEqualTo(80001L);
        assertThat(record.name()).isEqualTo("Bob");
        assertThat(record.street()).isEqualTo("456 Oak Ave");
        assertThat(record.city()).isEqualTo("Metropolis");
        assertThat(record.zipCode()).isEqualTo("67890");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project flattened embedded fields to Interface with @EntityPath")
    void shouldProjectEmbeddedToInterfaceWithEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80002L, "Carol", new Address("789 Pine Rd", "Gotham", "11111")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressInfo> results = context.queryPersonWithAddresses()
                .select(PersonAddressInfo.class)
                .where(PersonWithAddress::getId).eq(80002L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressInfo info = results.getFirst();
        assertThat(info.getId()).isEqualTo(80002L);
        assertThat(info.getName()).isEqualTo("Carol");
        assertThat(info.getStreet()).isEqualTo("789 Pine Rd");
        assertThat(info.getCity()).isEqualTo("Gotham");
        assertThat(info.getZipCode()).isEqualTo("11111");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should filter by embedded field and project with @EntityPath")
    void shouldFilterByEmbeddedFieldWithProjection(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(80010L, "Dave", new Address("Elm St", "Springfield", "111")),
                new PersonWithAddress(80011L, "Eve", new Address("Oak Ave", "Metropolis", "222"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getAddress).get(Address::getCity).eq("Springfield")
                .orderBy(PersonWithAddress::getId).asc()
                .list();

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo("Dave");
        assertThat(results.getFirst().getCity()).isEqualTo("Springfield");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("[DEFECT] Nested DTO with AddressDto silently drops embedded address")
    void shouldFailToMapNestedEmbeddedAddress(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80030L, "Grace", new Address("Main St", "Boston", "02101")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonWithNestedAddressEmbedded> results = context.queryPersonWithAddresses()
                .select(PersonWithNestedAddressEmbedded.class)
                .where(PersonWithAddress::getId).eq(80030L)
                .list();

        assertThat(results).hasSize(1);
        PersonWithNestedAddressEmbedded dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80030L);
        assertThat(dto.getName()).isEqualTo("Grace");

        assertThat(dto.getAddress()).as("DEFECT: address should be non-null AddressDto - EmbeddedAttribute not handled in projection switch").isNotNull();
        assertThat(dto.getAddress().getStreet()).as("DEFECT: address.street should be 'Main St' - nested embedded projection broken").isEqualTo("Main St");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project flattened nested embedded fields with @EntityPath")
    void shouldProjectNestedEmbeddedWithEntityPath(IntegrationTestContext context) {
        Address addr = new Address("999 Deep Rd", "DeepCity", "77777");
        ContactInfo contact = new ContactInfo("grace@test.com", "555-9999", addr);
        context.getUpdateExecutor().insert(
                new PersonWithNestedAddress(80040L, "Grace", contact),
                context.getEntityContext(PersonWithNestedAddress.class));

        List<PersonContactBasic> results = context.queryPersonWithNestedAddresses()
                .select(PersonContactBasic.class)
                .where(PersonWithNestedAddress::getId).eq(80040L)
                .list();

        assertThat(results).hasSize(1);
        PersonContactBasic dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80040L);
        assertThat(dto.getName()).isEqualTo("Grace");
        assertThat(dto.getEmail()).isEqualTo("grace@test.com");
        assertThat(dto.getPhone()).isEqualTo("555-9999");
        assertThat(dto.getStreet()).isEqualTo("999 Deep Rd");
        assertThat(dto.getCity()).isEqualTo("DeepCity");
        assertThat(dto.getZipCode()).isEqualTo("77777");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project @AttributeOverride renamed columns with @EntityPath")
    void shouldProjectOverriddenEmbeddedWithEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithOverriddenAddress(80050L, "Henry",
                        new Address("Override St", "OverrideCity", "99999")),
                context.getEntityContext(PersonWithOverriddenAddress.class));

        List<PersonAddressOverridden> results = context.queryPersonWithOverriddenAddresses()
                .select(PersonAddressOverridden.class)
                .where(PersonWithOverriddenAddress::getId).eq(80050L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressOverridden dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80050L);
        assertThat(dto.getName()).isEqualTo("Henry");
        assertThat(dto.getStreet()).isEqualTo("Override St");
        assertThat(dto.getCity()).isEqualTo("OverrideCity");
        assertThat(dto.getZipCode()).isEqualTo("99999");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project cross-layer embedded fields with @EntityPath")
    void shouldProjectCrossLayerEmbeddedWithEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithCrossLayerEmbedded(80060L, "Isaac",
                        new AddressWithZip("CrossCity", "Cross St", new ZipCode("94105")),
                        new ZipCode("12345")),
                context.getEntityContext(PersonWithCrossLayerEmbedded.class));

        List<PersonCrossLayerBasic> results = context.queryPersonWithCrossLayerEmbedded()
                .select(PersonCrossLayerBasic.class)
                .where(PersonWithCrossLayerEmbedded::getId).eq(80060L)
                .list();

        assertThat(results).hasSize(1);
        PersonCrossLayerBasic dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80060L);
        assertThat(dto.getName()).isEqualTo("Isaac");
        assertThat(dto.getCity()).isEqualTo("CrossCity");
        assertThat(dto.getStreet()).isEqualTo("Cross St");
        assertThat(dto.getZipCode()).isEqualTo("94105");
        assertThat(dto.getSecondaryCode()).isEqualTo("12345");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should project null embedded to DTO with null fields")
    void shouldProjectNullEmbeddedToDtoWithNullFields(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80090L, "NullAddr", null),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getId).eq(80090L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressBasic dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80090L);
        assertThat(dto.getName()).isEqualTo("NullAddr");
        assertThat(dto.getStreet()).isNull();
        assertThat(dto.getCity()).isNull();
        assertThat(dto.getZipCode()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should support embedded projection with orderBy on root field")
    void shouldSupportEmbeddedProjectionWithOrderBy(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(80100L, "Charlie", new Address("1st St", "Alpha", "001")),
                new PersonWithAddress(80101L, "Alice", new Address("2nd Ave", "Beta", "002")),
                new PersonWithAddress(80102L, "Bob", new Address("3rd Blvd", "Gamma", "003"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getId).in(80100L, 80101L, 80102L)
                .orderBy(PersonWithAddress::getName).asc()
                .list();

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getName()).isEqualTo("Alice");
        assertThat(results.get(0).getCity()).isEqualTo("Beta");
        assertThat(results.get(1).getName()).isEqualTo("Bob");
        assertThat(results.get(1).getCity()).isEqualTo("Gamma");
        assertThat(results.get(2).getName()).isEqualTo("Charlie");
        assertThat(results.get(2).getCity()).isEqualTo("Alpha");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("[DEFECT] @EntityPath type mismatch silently skips attribute")
    void shouldFailOnTypeMismatchInEntityPath(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80110L, "TypeFail", new Address("Broadway", "NYC", "10001")),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressWrongType> results = context.queryPersonWithAddresses()
                .select(PersonAddressWrongType.class)
                .where(PersonWithAddress::getId).eq(80110L)
                .list();

        assertThat(results).hasSize(1);
        PersonAddressWrongType dto = results.getFirst();
        assertThat(dto.getId()).isEqualTo(80110L);
        assertThat(dto.getName()).isEqualTo("TypeFail");
        assertThat(dto.getCity()).isEqualTo("NYC");

        // Long street mismatches entity String -> matchProjectionBasicAttribute returns false -> silently skipped.
        // Correct behavior should NOT silently drop the attribute.
        assertThat(dto.getStreet()).isNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("[DEFECT] DTO with only embedded fields returns null row when embedded is null")
    void shouldReturnNullRowWhenAllEmbeddedFieldsNull(IntegrationTestContext context) {
        context.getUpdateExecutor().insert(
                new PersonWithAddress(80120L, "AllNull", null),
                context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressOnlyEmbedded> results = context.queryPersonWithAddresses()
                .select(PersonAddressOnlyEmbedded.class)
                .where(PersonWithAddress::getId).eq(80120L)
                .list();

        assertThat(results).hasSize(1);
        // When ALL properties have null values, ObjectConstructor returns null.
        // This means the list contains [null], causing NPE on results.get(0).getStreet().
        PersonAddressOnlyEmbedded dto = results.getFirst();
        assertThat(dto).as("DEFECT: DTO with all-null embedded fields returns null from ObjectConstructor, expected non-null DTO with null fields").isNotNull();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should support pagination with embedded projection")
    void shouldProjectEmbeddedWithPagination(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(80070L, "Jack", new Address("1st St", "CityA", "001")),
                new PersonWithAddress(80071L, "Kate", new Address("2nd Ave", "CityB", "002")),
                new PersonWithAddress(80072L, "Leo", new Address("3rd Blvd", "CityC", "003"))
        ), context.getEntityContext(PersonWithAddress.class));

        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getId).in(80070L, 80071L, 80072L)
                .orderBy(PersonWithAddress::getId).asc()
                .list(2);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Jack");
        assertThat(results.get(1).getName()).isEqualTo("Kate");
        assertThat(results.get(0).getCity()).isEqualTo("CityA");
        assertThat(results.get(1).getCity()).isEqualTo("CityB");
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should return empty list when no match")
    void shouldReturnEmptyForNonExistent(IntegrationTestContext context) {
        List<PersonAddressBasic> results = context.queryPersonWithAddresses()
                .select(PersonAddressBasic.class)
                .where(PersonWithAddress::getId).eq(99999L)
                .list();

        assertThat(results).isEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should support selectDistinct with embedded projection")
    void shouldSelectDistinctEmbeddedProjection(IntegrationTestContext context) {
        context.getUpdateExecutor().insertAll(List.of(
                new PersonWithAddress(80080L, "Mike", new Address("Same St", "SameCity", "001")),
                new PersonWithAddress(80081L, "Nina", new Address("Same St", "SameCity", "001")),
                new PersonWithAddress(80082L, "Oscar", new Address("Other St", "OtherCity", "002"))
        ), context.getEntityContext(PersonWithAddress.class));

        // Use ProjectEmbeddedOnly (projects only address.city) so DISTINCT actually deduplicates:
        // 2 of 3 records share city="SameCity" -> only 2 distinct values returned
        List<ProjectEmbeddedOnly> results = context.queryPersonWithAddresses()
                .selectDistinct(ProjectEmbeddedOnly.class)
                .where(PersonWithAddress::getId).in(80080L, 80081L, 80082L)
                .list();

        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProjectEmbeddedOnly::getCity)
                .containsExactlyInAnyOrder("OtherCity", "SameCity");
    }
}
