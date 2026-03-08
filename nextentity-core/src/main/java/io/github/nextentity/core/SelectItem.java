package io.github.nextentity.core;

import io.github.nextentity.core.expression.ExpressionNode;
import io.github.nextentity.core.expression.LiteralNode;
import io.github.nextentity.core.expression.OperatorNode;
import io.github.nextentity.core.meta.EntityAttribute;

public sealed interface SelectItem permits LiteralNode, OperatorNode, EntityAttribute {

     ExpressionNode expression();

}
