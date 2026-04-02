package io.github.nextentity.integration;

import io.github.nextentity.api.model.Page;
import io.github.nextentity.api.model.Pageable;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.core.Pages;
import io.github.nextentity.api.model.Sliceable;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AbstractCollector interface default methods.
 * <p>
 * Covers:
 * - slice(Sliceable) method
 * - map() with nested mapping
 *
 * @author HuangChengwei
 */
@DisplayName("AbstractCollector Integration Tests")
public class AbstractCollectorIntegrationTest {

    @Nested
    @DisplayName("slice(Sliceable) Integration Tests")
    class SliceWithSliceableTests {

        /**
         * Test objective: Verify that slice(Sliceable) returns correct results when count > offset.
         * Covers: AbstractCollector.slice(Sliceable) line 155-162
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should slice with Sliceable when count greater than offset")
        void shouldSliceWithSliceable_WhenCountGreaterThanOffset(IntegrationTestContext context) {
            // Given
            Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(0, 5);

            // When
            Slice<Employee> slice = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .slice(sliceable);

            // Then
            assertThat(slice.data()).hasSize(5);
            assertThat(slice.offset()).isEqualTo(0);
            assertThat(slice.limit()).isEqualTo(5);
        }

        /**
         * Test objective: Verify that slice(Sliceable) returns empty slice when count <= offset.
         * Covers: AbstractCollector.slice(Sliceable) line 155-158 (empty path)
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should return empty slice when offset exceeds count")
        void shouldReturnEmptySlice_WhenOffsetExceedsCount(IntegrationTestContext context) {
            // Given
            Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(1000, 5);

            // When
            Slice<Employee> slice = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .slice(sliceable);

            // Then
            assertThat(slice.data()).isEmpty();
        }

        /**
         * Test objective: Verify that slice(Sliceable) works with custom collector.
         * Covers: AbstractCollector.slice(Sliceable) with custom result type
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should slice with custom Sliceable collector")
        void shouldSliceWithCustomSliceableCollector(IntegrationTestContext context) {
            // Given
            Sliceable<Employee, Long> sliceable = new Sliceable<>() {
                @Override
                public int offset() {
                    return 0;
                }

                @Override
                public int limit() {
                    return 3;
                }

                @Override
                public Long collect(List<Employee> data, long total) {
                    return total;
                }
            };

            // When
            Long total = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .slice(sliceable);

            // Then
            assertThat(total).isGreaterThan(0);
        }

        /**
         * Test objective: Verify that slice(Sliceable) works with where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should slice with where condition using Sliceable")
        void shouldSliceWithWhereCondition(IntegrationTestContext context) {
            // Given
            Sliceable<Employee, Slice<Employee>> sliceable = createSliceable(0, 3);

            // When
            Slice<Employee> slice = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .slice(sliceable);

            // Then
            assertThat(slice.data()).hasSize(3);
            assertThat(slice.data()).allMatch(Employee::getActive);
        }

        private <T> Sliceable<T, Slice<T>> createSliceable(int offset, int limit) {
            return new Sliceable<>() {
                @Override
                public int offset() {
                    return offset;
                }

                @Override
                public int limit() {
                    return limit;
                }

                @Override
                public Slice<T> collect(List<T> data, long total) {
                    return new Slice<>() {
                        @Override
                        public List<T> data() {
                            return data;
                        }

                        @Override
                        public long total() {
                            return total;
                        }

                        @Override
                        public int offset() {
                            return offset;
                        }

                        @Override
                        public int limit() {
                            return limit;
                        }
                    };
                }
            };
        }
    }
}