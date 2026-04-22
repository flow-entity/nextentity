package io.github.nextentity.core.expression;

import io.github.nextentity.api.PathRef;
import io.github.nextentity.integration.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 单元测试 PathOperatorImpl.
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

        ///
        /// 测试目标: 验证y get() with generic path returns PathOperator.
        /// 测试场景: Call get() with a path to a nested property.
        /// 预期结果: Returns PathOperatorImpl with appended path.
        @Test
        void get_WithGenericPath_ShouldReturnPathOperator() {
            // when
            var result = operator.get(Employee::getDepartment);

            // then
            assertThat(result).isInstanceOf(PathOperatorImpl.class);
        }

        ///
        /// 测试目标: 验证y get() appends path 正确.
        /// 测试场景: Call get() with nested path then eq.
        /// 预期结果: The path is appended and eq operator is created.
        @Test
        void get_ShouldAppendPath() {
            // when
            var result = operator.get((PathRef.StringRef<Employee>) Employee::getName);
            result.eq("test");

            // then
            assertThat(capturedNode).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) capturedNode;
            assertThat(opNode.operator()).isEqualTo(Operator.EQ);
        }
    }

    @Nested
    class GetWithStringRef {

        ///
        /// 测试目标: 验证y get() with StringRef returns StringOperator.
        /// 测试场景: Call get() with a string path.
        /// 预期结果: Returns StringOperatorImpl.
        @Test
        void get_WithStringRef_ShouldReturnStringOperator() {
            // when
            var result = operator.get((PathRef.StringRef<Employee>) Employee::getName);

            // then
            assertThat(result).isInstanceOf(StringOperatorImpl.class);
        }
    }

    @Nested
    class GetWithNumberRef {

        ///
        /// 测试目标: 验证y get() with NumberRef returns NumberOperator.
        /// 测试场景: Call get() with a number path.
        /// 预期结果: Returns NumberOperatorImpl.
        @Test
        void get_WithNumberRef_ShouldReturnNumberOperator() {
            // when
            var result = operator.get((PathRef.NumberRef<Employee, Long>) Employee::getId);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }

        ///
        /// 测试目标: 验证y get() with Double path returns NumberOperator.
        /// 测试场景: Call get() with a double path.
        /// 预期结果: Returns NumberOperatorImpl.
        @Test
        void get_WithDoubleRef_ShouldReturnNumberOperator() {
            // when
            var result = operator.get((PathRef.NumberRef<Employee, Double>) Employee::getSalary);

            // then
            assertThat(result).isInstanceOf(NumberOperatorImpl.class);
        }
    }
}
