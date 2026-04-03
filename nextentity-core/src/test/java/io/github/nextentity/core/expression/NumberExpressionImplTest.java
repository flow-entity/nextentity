package io.github.nextentity.core.expression;

import io.github.nextentity.api.Expression;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证NumberExpressionImpl正确实现数值运算
/// <p>
/// 测试场景：
/// 1. 算术运算（加法、减法、乘法、除法、模）
/// 2. 聚合函数（总和、平均值、最大值、最小值）
class NumberExpressionImplTest {

    @Nested
    class ArithmeticOperations {

        /// 测试目标：验证add()创建ADD操作符
        /// 测试场景：在表达式上调用add()
        /// 预期结果：具有ADD操作符的表达式
        @Test
        void add_CreatesAddOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expression.of(10);

            // when
            var result = expr.add(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.ADD);
        }

        /// 测试目标：验证subtract()创建SUBTRACT操作符
        /// 测试场景：在表达式上调用subtract()
        /// 预期结果：具有SUBTRACT操作符的表达式
        @Test
        void subtract_CreatesSubtractOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expression.of(5);

            // when
            var result = expr.subtract(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUBTRACT);
        }

        /// 测试目标：验证multiply()创建MULTIPLY操作符
        /// 测试场景：在表达式上调用multiply()
        /// 预期结果：具有MULTIPLY操作符的表达式
        @Test
        void multiply_CreatesMultiplyOperator() {
            // given
            PathNode pathNode = new PathNode("price");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expression.of(2);

            // when
            var result = expr.multiply(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MULTIPLY);
        }

        /// 测试目标：验证divide()创建DIVIDE操作符
        /// 测试场景：在表达式上调用divide()
        /// 预期结果：具有DIVIDE操作符的表达式
        @Test
        void divide_CreatesDivideOperator() {
            // given
            PathNode pathNode = new PathNode("total");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expression.of(10);

            // when
            var result = expr.divide(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.DIVIDE);
        }

        /// 测试目标：验证mod()创建MOD操作符
        /// 测试场景：在表达式上调用mod()
        /// 预期结果：具有MOD操作符的表达式
        @Test
        void mod_CreatesModOperator() {
            // given
            PathNode pathNode = new PathNode("value");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);
            var other = Expression.of(3);

            // when
            var result = expr.mod(other);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MOD);
        }
    }

    @Nested
    class AggregateFunctions {

        /// 测试目标：验证sum()创建SUM操作符
        /// 测试场景：在表达式上调用sum()
        /// 预期结果：具有SUM操作符的表达式
        @Test
        void sum_CreatesSumOperator() {
            // given
            PathNode pathNode = new PathNode("amount");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.sum();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUM);
        }

        /// 测试目标：验证avg()创建AVG操作符
        /// 测试场景：在表达式上调用avg()
        /// 预期结果：具有AVG操作符的表达式
        @Test
        void avg_CreatesAvgOperator() {
            // given
            PathNode pathNode = new PathNode("price");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.avg();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.AVG);
        }

        /// 测试目标：验证max()创建MAX操作符
        /// 测试场景：在表达式上调用max()
        /// 预期结果：具有MAX操作符的表达式
        @Test
        void max_CreatesMaxOperator() {
            // given
            PathNode pathNode = new PathNode("score");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.max();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MAX);
        }

        /// 测试目标：验证min()创建MIN操作符
        /// 测试场景：在表达式上调用min()
        /// 预期结果：具有MIN操作符的表达式
        @Test
        void min_CreatesMinOperator() {
            // given
            PathNode pathNode = new PathNode("score");
            NumberExpressionImpl<Object, Integer> expr = new NumberExpressionImpl<>(pathNode);

            // when
            var result = expr.min();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.MIN);
        }
    }
}
