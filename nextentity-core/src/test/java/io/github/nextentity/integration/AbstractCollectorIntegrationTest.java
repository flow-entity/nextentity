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

    @Nested
    @DisplayName("map() Integration Tests")
    class MapTests {

        /**
         * Test objective: Verify that map transforms results correctly.
         * Covers: AbstractCollector.map() and MappedCollector.getList()
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should map results with transformation")
        void shouldMapResults_WithTransformation(IntegrationTestContext context) {
            // Given
            Function<Employee, String> nameMapper = Employee::getName;

            // When
            List<String> names = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .map(nameMapper)
                    .getList(0, 5);

            // Then
            assertThat(names).hasSize(5);
            assertThat(names).allSatisfy(name -> assertThat(name).isNotNull());
        }

        /**
         * Test objective: Verify that map count delegates to original collector.
         * Covers: MappedCollector.count()
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delegate count to original collector")
        void shouldDelegateCount_ToOriginalCollector(IntegrationTestContext context) {
            // Given
            long expectedCount = context.queryEmployees().count();

            // When
            long mappedCount = context.queryEmployees()
                    .map(Employee::getName)
                    .count();

            // Then
            assertThat(mappedCount).isEqualTo(expectedCount);
        }

        /**
         * Test objective: Verify that map exist delegates to original collector.
         * Covers: MappedCollector.exist()
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should delegate exist to original collector")
        void shouldDelegateExist_ToOriginalCollector(IntegrationTestContext context) {
            // When
            boolean exists = context.queryEmployees()
                    .map(Employee::getName)
                    .exist(0);

            // Then
            assertThat(exists).isTrue();
        }

        /**
         * Test objective: Verify that nested map chains mappers correctly.
         * Covers: MappedCollector.map(Function) line 206-207
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should chain mappers with nested map calls")
        void shouldChainMappers_WithNestedMapCalls(IntegrationTestContext context) {
            // Given - map Employee -> name (String) -> name length (Integer)
            Function<Employee, String> nameMapper = Employee::getName;
            Function<String, Integer> lengthMapper = String::length;

            // When
            List<Integer> nameLengths = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .map(nameMapper)
                    .map(lengthMapper)
                    .getList(0, 5);

            // Then
            assertThat(nameLengths).hasSize(5);
            assertThat(nameLengths).allSatisfy(length -> assertThat(length).isPositive());
        }

        /**
         * Test objective: Verify that map works with slice.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should map results with slice")
        void shouldMapResults_WithSlice(IntegrationTestContext context) {
            // When
            Slice<String> slice = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .map(Employee::getName)
                    .slice(0, 3);

            // Then
            assertThat(slice.data()).hasSize(3);
            assertThat(slice.data()).allSatisfy(name -> assertThat(name).isNotNull());
        }

        /**
         * Test objective: Verify that map works with getPage.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should map results with getPage")
        void shouldMapResults_WithGetPage(IntegrationTestContext context) {
            // Given
            Pageable pageable = Pages.pageable(1, 5);

            // When
            Page<String> page = context.queryEmployees()
                    .orderBy(Employee::getId).asc()
                    .map(Employee::getName)
                    .getPage(pageable);

            // Then
            assertThat(page.getItems()).hasSize(5);
            assertThat(page.getItems()).allSatisfy(name -> assertThat(name).isNotNull());
        }

        /**
         * Test objective: Verify that map works with where condition.
         */
        @ParameterizedTest
        @ArgumentsSource(IntegrationTestProvider.class)
        @DisplayName("Should map results with where condition")
        void shouldMapResults_WithWhereCondition(IntegrationTestContext context) {
            // When
            List<String> names = context.queryEmployees()
                    .where(Employee::getActive).eq(true)
                    .orderBy(Employee::getId).asc()
                    .map(Employee::getName)
                    .getList(0, 5);

            // Then
            assertThat(names).isNotEmpty();
            assertThat(names).allSatisfy(name -> assertThat(name).isNotNull());
        }
    }
}