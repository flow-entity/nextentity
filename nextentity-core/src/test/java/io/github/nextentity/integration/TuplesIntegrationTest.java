package io.github.nextentity.integration;

import io.github.nextentity.api.model.Tuple2;
import io.github.nextentity.api.model.Tuple3;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// Integration tests for Tuples class with database queries.
///
/// @author HuangChengwei
@DisplayName("Tuples Integration Tests")
public class TuplesIntegrationTest {

    @Nested
    @DisplayName("Tuple Query Integration Tests")
    class TupleQueryIntegrationTest {

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Tuple2 from query projection")
        void shouldCreateTuple2FromQueryProjection(IntegrationTestContext context) {
            // When
            List<Tuple2<String, Double>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary)
                    .where(Employee::getId).eq(1L)
                    .list();

            // Then
            assertThat(results).isNotEmpty();
            Tuple2<String, Double> tuple = results.get(0);
            assertThat(tuple.get0()).isNotNull();
            assertThat(tuple.get1()).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should create Tuple3 from query projection")
        void shouldCreateTuple3FromQueryProjection(IntegrationTestContext context) {
            // When
            List<Tuple3<String, Double, Boolean>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary, Employee::getActive)
                    .where(Employee::getId).eq(1L)
                    .list();

            // Then
            assertThat(results).isNotEmpty();
            Tuple3<String, Double, Boolean> tuple = results.get(0);
            assertThat(tuple.get0()).isNotNull();
            assertThat(tuple.get1()).isNotNull();
            assertThat(tuple.get2()).isNotNull();
        }

        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should handle multiple tuple results")
        void shouldHandleMultipleTupleResults(IntegrationTestContext context) {
            // When
            List<Tuple2<String, Double>> results = context.queryEmployees()
                    .select(Employee::getName, Employee::getSalary)
                    .orderBy(Employee::getId).asc()
                    .list(5);

            // Then
            assertThat(results).isNotEmpty();
            assertThat(results.size()).isLessThanOrEqualTo(5);
            for (Tuple2<String, Double> tuple : results) {
                assertThat(tuple.get0()).isNotNull();
            }
        }
    }
}

