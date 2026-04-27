package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标：验证LiteralNode正确表示字面值
/// <p>
/// 测试场景：
/// 1. 值存储和检索
/// 2. 静态TRUE/FALSE常量
/// 3. 对布尔字面值的NOT操作
/// 4. AND操作优化
/// 5. OR操作优化
///
class LiteralNodeTest {

    @Nested
    class ValueStorage {

        ///
        /// 测试目标：验证值被正确存储
        /// 测试场景：使用各种值创建LiteralNode
        /// 预期结果：值可以被检索
        ///
        @Test
        void value_IsStoredCorrectly() {
            // given
            String stringValue = "test";

            // when
            LiteralNode node = new LiteralNode(stringValue);

            // then
            assertThat(node.value()).isEqualTo(stringValue);
        }

        ///
        /// 测试目标：验证允许空值
        /// 测试场景：使用null创建LiteralNode
        /// 预期结果：Null被存储
        ///
        @Test
        void value_CanBeNull() {
            // when
            LiteralNode node = new LiteralNode(null);

            // then
            assertThat(node.value()).isNull();
        }
    }

    @Nested
    class StaticConstants {

        ///
        /// 测试目标：验证TRUE常量
        /// 测试场景：访问TRUE常量
        /// 预期结果：包含true值
        ///
        @Test
        void trueConstant_ContainsTrue() {
            // when
            LiteralNode trueNode = LiteralNode.TRUE;

            // then
            assertThat(trueNode.value()).isEqualTo(Boolean.TRUE);
        }

        ///
        /// 测试目标：验证FALSE常量
        /// 测试场景：访问FALSE常量
        /// 预期结果：包含false值
        ///
        @Test
        void falseConstant_ContainsFalse() {
            // when
            LiteralNode falseNode = LiteralNode.FALSE;

            // then
            assertThat(falseNode.value()).isEqualTo(Boolean.FALSE);
        }
    }

    @Nested
    class NotOperation {

        ///
        /// 测试目标：验证对true的NOT操作返回false
        /// 测试场景：对TRUE应用NOT
        /// 预期结果：返回FALSE
        ///
        @Test
        void operate_NotOnTrue_ReturnsFalse() {
            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.NOT);

            // then
            assertThat(result).isSameAs(LiteralNode.FALSE);
        }

        ///
        /// 测试目标：验证对false的NOT操作返回true
        /// 测试场景：对FALSE应用NOT
        /// 预期结果：返回TRUE
        ///
        @Test
        void operate_NotOnFalse_ReturnsTrue() {
            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.NOT);

            // then
            assertThat(result).isSameAs(LiteralNode.TRUE);
        }

        ///
        /// 测试目标：验证对非布尔值的NOT操作使用默认行为
        /// 测试场景：对字符串字面值应用NOT
        /// 预期结果：返回OperatorNode
        ///
        @Test
        void operate_NotOnNonBoolean_ReturnsOperatorNode() {
            // given
            LiteralNode stringNode = new LiteralNode("test");

            // when
            ExpressionNode result = stringNode.operate(Operator.NOT);

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
        }
    }

    @Nested
    class AndOperation {

        ///
        /// 测试目标：验证与false的AND操作返回false
        /// 测试场景：对FALSE应用AND
        /// 预期结果：返回FALSE
        ///
        @Test
        void operate_AndOnFalse_ReturnsFalse() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.AND, List.of(otherNode));

            // then
            assertThat(result).isSameAs(LiteralNode.FALSE);
        }

        ///
        /// 测试目标：验证与true的AND操作继续处理其他节点
        /// 测试场景：对TRUE与其他节点应用AND
        /// 预期结果：返回包含其他节点的OperatorNode
        ///
        @Test
        void operate_AndOnTrue_ReturnsOperatorNodeWithOtherNodes() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.AND, List.of(otherNode));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result;
            assertThat(opNode.operator()).isEqualTo(Operator.AND);
        }
    }

    @Nested
    class OrOperation {

        ///
        /// 测试目标：验证与true的OR操作返回true
        /// 测试场景：对TRUE应用OR
        /// 预期结果：返回TRUE
        ///
        @Test
        void operate_OrOnTrue_ReturnsTrue() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.TRUE.operate(Operator.OR, List.of(otherNode));

            // then
            assertThat(result).isSameAs(LiteralNode.TRUE);
        }

        ///
        /// 测试目标：验证与false的OR操作继续处理其他节点
        /// 测试场景：对FALSE与其他节点应用OR
        /// 预期结果：返回包含其他节点的OperatorNode
        ///
        @Test
        void operate_OrOnFalse_ReturnsOperatorNodeWithOtherNodes() {
            // given
            LiteralNode otherNode = new LiteralNode("test");

            // when
            ExpressionNode result = LiteralNode.FALSE.operate(Operator.OR, List.of(otherNode));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode opNode = (OperatorNode) result;
            assertThat(opNode.operator()).isEqualTo(Operator.OR);
        }
    }

}
