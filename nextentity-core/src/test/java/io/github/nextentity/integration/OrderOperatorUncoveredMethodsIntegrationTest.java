package io.github.nextentity.integration;

import io.github.nextentity.api.OrderOperator;
import io.github.nextentity.api.PathRef;
import io.github.nextentity.api.model.Order;
import io.github.nextentity.api.model.Slice;
import io.github.nextentity.integration.config.IntegrationTestContext;
import io.github.nextentity.integration.config.IntegrationTestProvider;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderOperator Uncovered Methods Integration Tests")
public class OrderOperatorUncoveredMethodsIntegrationTest {

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection without explicit sort order")
    void shouldOrderByCollectionWithoutExplicitSortOrder(IntegrationTestContext context) {
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getId);

        List<Employee> employees = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(paths)
                .limit(5);

        assertThat(employees).isNotEmpty();
        for (int i = 1; i < employees.size(); i++) {
            assertThat(employees.get(i).getId()).isGreaterThanOrEqualTo(employees.get(i - 1).getId());
        }
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection with multiple paths")
    void shouldOrderByCollectionWithMultiplePaths(IntegrationTestContext context) {
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getDepartmentId);
        paths.add(Employee::getName);

        List<Employee> employees = context.queryEmployees()
                .orderBy(paths)
                .limit(10);

        assertThat(employees).isNotEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy Collection return OrderOperator for chaining")
    void shouldOrderByCollectionReturnOrderOperatorForChaining(IntegrationTestContext context) {
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getId);

        OrderOperator<Employee, Employee> orderOperator = context.queryEmployees().orderBy(paths);
        List<Employee> employees = orderOperator.limit(5);

        assertThat(employees).isNotEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with offset and limit without explicit sort order")
    void shouldSliceWithOffsetAndLimitWithoutExplicitSortOrder(IntegrationTestContext context) {
        Slice<Employee> slice = context.queryEmployees()
                .orderBy(Employee::getId)
                .slice(0, 5);

        assertThat(slice.data()).hasSize(5);
        assertThat(slice.total()).isPositive();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should slice with where clause")
    void shouldSliceWithWhereClause(IntegrationTestContext context) {
        Slice<Employee> slice = context.queryEmployees()
                .where(Employee::getSalary).gt(50000.0)
                .orderBy(Employee::getId)
                .slice(0, 5);

        assertThat(slice.data()).isNotEmpty();
        assertThat(slice.data()).allMatch(e -> e.getSalary() > 50000.0);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should orderBy empty List of Orders work")
    void shouldOrderByEmptyListOfOrdersWork(IntegrationTestContext context) {
        List<Order<Employee>> orders = new ArrayList<>();

        List<Employee> employees = context.queryEmployees()
                .orderBy(Employee::getId)
                .orderBy(orders)
                .limit(5);

        assertThat(employees).isNotEmpty();
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should chain orderBy Collection with limit")
    void shouldChainOrderByCollectionWithLimit(IntegrationTestContext context) {
        Collection<PathRef<Employee, ? extends Comparable<?>>> paths = new ArrayList<>();
        paths.add(Employee::getSalary);

        List<Employee> employees = context.queryEmployees()
                .orderBy(paths)
                .limit(3);

        assertThat(employees).hasSize(3);
    }

    @ParameterizedTest
    @ArgumentsSource(IntegrationTestProvider.class)
    @DisplayName("Should OrderOperator methods delegate to asc correctly")
    void shouldOrderOperatorMethodsDelegateToAscCorrectly(IntegrationTestContext context) {
        long countDirect = context.queryEmployees()
                .orderBy(Employee::getId)
                .count();

        long countExplicit = context.queryEmployees()
                .orderBy(Employee::getId).asc()
                .count();

        assertThat(countDirect).isEqualTo(countExplicit);
    }
}
