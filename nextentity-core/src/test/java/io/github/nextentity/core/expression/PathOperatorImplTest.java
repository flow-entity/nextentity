package io.github.nextentity.core.expression;

import io.github.nextentity.api.Path;
import io.github.nextentity.test.entity.Department;
import io.github.nextentity.test.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PathOperatorImpl.
 */
class PathOperatorImplTest {

    private PathOperatorImpl<Employee, Employee, String> operator;
    private ExpressionNode capturedNode;

    @BeforeEach
    void setUp() {
        PathNode pathNode = new PathNode("employee");
        capturedNode = null;
        Function<ExpressionNode, String> callback = node -> {
            capturedNode = node;
            return "result";
        };
        operator = new PathOperatorImpl<>(pathNode, callback);
    }

    @Nested
    class GetWithPath {

        /**
         * Test objective: Verify get() with generic path returns PathOperator.
         * Test scenario: Call get() with a path to a nested property.
         * Expected result: Returns PathOperatorImpl with appended path.
         */
        @Test
        void get_WithGenericPath_ShouldReturnPathOperator() {
            // when
            var result = operator.get(Employee::getDepartment);

            // then
            assertThat(result).isInstanceOf(PathOperatorImpl.class);
        }

        /**
         * Test objective: Verify get() appends path correctly.
         * Test scenario: Call get() with nested path then eq.
         * Expected result: The path is appended and eq operator is created.
         */
        @Test
        void get_ShouldAppendPath() {
            // when
            var result = operator.get((Path.StringRef<Employee>) Employee::getName);
            result.eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }
    }

    @Nested
    class GetWithStringRef {

        /**
         * Test objective: Verify get() with StringRef returns StringOperator.
         * Test scenario: Call get() with a string path.
         * Expected result: Returns StringOperatorImpl.
         */
        @Test
        void get_WithStringRef_ShouldReturnStringOperator() {
            // when
            var result = operator.get((Path.StringRef<Employee>) Employee::getName);

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }
    }

    @Nested
    class GetWithNumberRef {

        /**
         * Test objective: Verify get() with NumberRef returns NumberOperator.
         * Test scenario: Call get() with a number path.
         * Expected result: Returns NumberOperatorImpl.
         */
        @Test
        void get_WithNumberRef_ShouldReturnNumberOperator() {
            // when
            var result = operator.get((Path.NumberRef<Employee, Long>) Employee::getId);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        /**
         * Test objective: Verify get() with Double path returns NumberOperator.
         * Test scenario: Call get() with a double path.
         * Expected result: Returns NumberOperatorImpl.
         */
        @Test
        void get_WithDoubleRef_ShouldReturnNumberOperator() {
            // when
            var result = operator.get((Path.NumberRef<Employee, Double>) Employee::getSalary);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }
    }
}
