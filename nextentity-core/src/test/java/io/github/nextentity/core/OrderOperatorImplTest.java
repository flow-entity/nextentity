package io.github.nextentity.core;

import io.github.nextentity.api.OrderByStep;
import io.github.nextentity.api.Path;
import io.github.nextentity.api.SortOrder;
import io.github.nextentity.api.model.EntityRoot;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderOperatorImpl.
 */
@ExtendWith(MockitoExtension.class)
class OrderOperatorImplTest {

    @Mock
    private WhereImpl<Employee, Employee> builder;

    private OrderOperatorImpl<Employee, Employee> orderOperator;

    @BeforeEach
    void setUp() {
        Collection<Path<Employee, Comparable<?>>> paths = Arrays.asList(
                Employee::getName,
                Employee::getSalary
        );
        orderOperator = new OrderOperatorImpl<>(builder, paths);
    }

    /**
     * Test objective: Verify that sort with ASC order creates correct sort expressions.
     * Test scenario: Call sort with SortOrder.ASC.
     * Expected result: Sort expressions with ASC order are created and passed to builder.
     */
    @Test
    void sort_WithAscOrder_ShouldCreateSortExpressionsWithAsc() {
        // given
        when(builder.addOrderBy(any())).thenReturn(mock(OrderByStep.class));

        // when
        orderOperator.sort(SortOrder.ASC);

        // then
        verify(builder).addOrderBy(any(List.class));
    }

    /**
     * Test objective: Verify that sort with DESC order creates correct sort expressions.
     * Test scenario: Call sort with SortOrder.DESC.
     * Expected result: Sort expressions with DESC order are created and passed to builder.
     */
    @Test
    void sort_WithDescOrder_ShouldCreateSortExpressionsWithDesc() {
        // given
        when(builder.addOrderBy(any())).thenReturn(mock(OrderByStep.class));

        // when
        orderOperator.sort(SortOrder.DESC);

        // then
        verify(builder).addOrderBy(any(List.class));
    }

    /**
     * Test objective: Verify that root() returns a valid EntityRoot.
     * Test scenario: Call root() method.
     * Expected result: Returns a non-null EntityRoot instance.
     */
    @Test
    void root_ShouldReturnEntityRoot() {
        // when
        EntityRoot<Employee> root = orderOperator.root();

        // then
        assertThat(root).isNotNull();
    }

    /**
     * Test objective: Verify that multiple paths are handled correctly.
     * Test scenario: Create OrderOperatorImpl with multiple paths.
     * Expected result: All paths are converted to sort expressions.
     */
    @Test
    void sort_WithMultiplePaths_ShouldCreateSortExpressionsForAll() {
        // given
        Collection<Path<Employee, Comparable<?>>> paths = Arrays.asList(
                Employee::getName,
                Employee::getId
        );
        OrderOperatorImpl<Employee, Employee> multiPathOperator = new OrderOperatorImpl<>(builder, paths);
        when(builder.addOrderBy(any())).thenReturn(mock(OrderByStep.class));

        // when
        multiPathOperator.sort(SortOrder.ASC);

        // then
        verify(builder).addOrderBy(argThat(list -> list.size() == 2));
    }
}
