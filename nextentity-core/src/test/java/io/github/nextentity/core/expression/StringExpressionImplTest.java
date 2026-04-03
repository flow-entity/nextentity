package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证StringExpressionImpl正确实现字符串操作
/// <p>
/// 测试场景：
/// 1. lower()操作
/// 2. upper()操作
/// 3. substring()操作
/// 4. trim()操作
/// 5. length()操作
class StringExpressionImplTest {

    @Nested
    class StringOperations {

        /// 测试目标：验证lower()创建LOWER操作符
        /// 测试场景：在表达式上调用lower()
        /// 预期结果：具有LOWER操作符的表达式
        @Test
        void lower_CreatesLowerOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.lower();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.LOWER);
        }

        /// 测试目标：验证upper()创建UPPER操作符
        /// 测试场景：在表达式上调用upper()
        /// 预期结果：具有UPPER操作符的表达式
        @Test
        void upper_CreatesUpperOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.upper();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.UPPER);
        }

        /// 测试目标：验证trim()创建TRIM操作符
        /// 测试场景：在表达式上调用trim()
        /// 预期结果：具有TRIM操作符的表达式
        @Test
        void trim_CreatesTrimOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.trim();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.TRIM);
        }

        /// 测试目标：验证substring()创建SUBSTRING操作符
        /// 测试场景：在表达式上调用substring()
        /// 预期结果：具有SUBSTRING操作符的表达式
        @Test
        void substring_CreatesSubstringOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.substring(0, 10);

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.SUBSTRING);
        }

        /// 测试目标：验证length()创建LENGTH操作符
        /// 测试场景：在表达式上调用length()
        /// 预期结果：具有LENGTH操作符的表达式
        @Test
        void length_CreatesLengthOperator() {
            // given
            PathNode pathNode = new PathNode("name");
            StringExpressionImpl<Object> expr = new StringExpressionImpl<>(pathNode);

            // when
            var result = expr.length();

            // then
            assertThat(result).isNotNull();
            ExpressionTree tree = (ExpressionTree) result;
            ExpressionNode root = tree.getRoot();
            assertThat(root).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) root;
            assertThat(opNode.operator()).isEqualTo(Operator.LENGTH);
        }
    }
}
