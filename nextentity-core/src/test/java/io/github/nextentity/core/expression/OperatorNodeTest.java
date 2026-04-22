package io.github.nextentity.core.expression;

import io.github.nextentity.core.util.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标: 验证y OperatorNode 正确 represents 操作s
/// <p>
/// 测试场景s:
/// 1. Creation with operands and operator
/// 2. NOT 操作 optimization (double negation)
/// 3. Multivalued operator optimization
/// 4. Operand access 方法
class OperatorNodeTest {

    @Nested
    class Creation {

        ///
        /// 测试目标: 验证y operands and operator are stored
        /// 测试场景: Create OperatorNode with operands and operator
        /// 预期结果: All values are retrievable
        @Test
        void constructor_StoresOperandsAndOperator() {
            // given
            ImmutableList<ExpressionNode> operands = ImmutableList.of(
                    new LiteralNode("a"),
                    new LiteralNode("b")
            );

            // when
            OperatorNode node = new OperatorNode(operands, Operator.EQ);

            // then
            assertThat(node.operands()).isEqualTo(operands);
            assertThat(node.operator()).isEqualTo(Operator.EQ);
        }
    }

    @Nested
    class NotOperationOptimization {

        ///
        /// 测试目标: 验证y double NOT cancels out
        /// 测试场景: Apply NOT to NOT operator node
        /// 预期结果: Returns inner operand
        @Test
        void operate_NotOnNot_ReturnsInnerOperand() {
            // given
            LiteralNode inner = new LiteralNode("test");
            OperatorNode notNode = new OperatorNode(ImmutableList.of(inner), Operator.NOT);

            // when
            ExpressionNode result = notNode.operate(Operator.NOT);

            // then
            assertThat(result).isEqualTo(inner);
        }
    }

    @Nested
    class MultivaluedOperatorOptimization {

        ///
        /// 测试目标: 验证y AND on AND merges operands
        /// 测试场景: Apply AND to AND operator node
        /// 预期结果: Combined operands
        @Test
        void operate_AndOnAnd_MergesOperands() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode andNode = new OperatorNode(ImmutableList.of(a, b), Operator.AND);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = andNode.operate(Operator.AND, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            assertThat(resultNode.operands()).hasSize(3);
            assertThat(resultNode.operator()).isEqualTo(Operator.AND);
        }

        ///
        /// 测试目标: 验证y OR on OR merges operands
        /// 测试场景: Apply OR to OR operator node
        /// 预期结果: Combined operands
        @Test
        void operate_OrOnOr_MergesOperands() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode orNode = new OperatorNode(ImmutableList.of(a, b), Operator.OR);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = orNode.operate(Operator.OR, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            assertThat(resultNode.operands()).hasSize(3);
            assertThat(resultNode.operator()).isEqualTo(Operator.OR);
        }

        ///
        /// 测试目标: 验证y non-multivalued operator does not merge
        /// 测试场景: Apply EQ to EQ operator node
        /// 预期结果: Nested operator node
        @Test
        void operate_EqOnEq_CreatesNestedNode() {
            // given
            LiteralNode a = new LiteralNode("a");
            LiteralNode b = new LiteralNode("b");
            OperatorNode eqNode = new OperatorNode(ImmutableList.of(a, b), Operator.EQ);
            LiteralNode c = new LiteralNode("c");

            // when
            ExpressionNode result = eqNode.operate(Operator.EQ, List.of(c));

            // then
            assertThat(result).isInstanceOf(OperatorNode.class);
            OperatorNode resultNode = (OperatorNode) result;
            // Should create new node with original as first operand
            assertThat(resultNode.operands()).hasSize(2);
        }
    }

    @Nested
    class OperandAccess {

        ///
        /// 测试目标: 验证y firstOperand() returns first operand
        /// 测试场景: Call firstOperand() on node with multiple operands
        /// 预期结果: First operand
        @Test
        void firstOperand_ReturnsFirstOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.firstOperand();

            // then
            assertThat(result).isEqualTo(first);
        }

        ///
        /// 测试目标: 验证y secondOperand() returns second operand
        /// 测试场景: Call secondOperand() on node with two operands
        /// 预期结果: Second operand
        @Test
        void secondOperand_ReturnsSecondOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.secondOperand();

            // then
            assertThat(result).isEqualTo(second);
        }

        ///
        /// 测试目标: 验证y secondOperand() returns null for single operand
        /// 测试场景: Call secondOperand() on node with one operand
        /// 预期结果: null
        @Test
        void secondOperand_SingleOperand_ReturnsNull() {
            // given
            LiteralNode only = new LiteralNode("only");
            OperatorNode node = new OperatorNode(ImmutableList.of(only), Operator.NOT);

            // when
            ExpressionNode result = node.secondOperand();

            // then
            assertThat(result).isNull();
        }

        ///
        /// 测试目标: 验证y thirdOperand() returns third operand
        /// 测试场景: Call thirdOperand() on node with three operands
        /// 预期结果: Third operand
        @Test
        void thirdOperand_ReturnsThirdOperand() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            LiteralNode third = new LiteralNode("third");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second, third), Operator.BETWEEN);

            // when
            ExpressionNode result = node.thirdOperand();

            // then
            assertThat(result).isEqualTo(third);
        }

        ///
        /// 测试目标: 验证y thirdOperand() returns null for two operands
        /// 测试场景: Call thirdOperand() on node with two operands
        /// 预期结果: null
        @Test
        void thirdOperand_TwoOperands_ReturnsNull() {
            // given
            LiteralNode first = new LiteralNode("first");
            LiteralNode second = new LiteralNode("second");
            OperatorNode node = new OperatorNode(ImmutableList.of(first, second), Operator.EQ);

            // when
            ExpressionNode result = node.thirdOperand();

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    class ExpressionMethod {

        ///
        /// 测试目标: 验证y expression() returns self
        /// 测试场景: Call expression() on OperatorNode
        /// 预期结果: Returns same instance
        @Test
        void expression_ReturnsSelf() {
            // given
            OperatorNode node = new OperatorNode(
                    ImmutableList.of(new LiteralNode("a")),
                    Operator.NOT
            );

            // when
            ExpressionNode result = node.expression();

            // then
            assertThat(result).isSameAs(node);
        }
    }
}
