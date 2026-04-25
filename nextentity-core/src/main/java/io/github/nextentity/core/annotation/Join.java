package io.github.nextentity.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 声明投影字段使用显式 JOIN 关系。
///
/// 适用于投影字段需要连接到一个不在实体关联元模型中的目标类型时。
/// 目标类型可以是实体或另一个投影类型。
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Join {

    Class<?> target();

    String sourceAttribute();

    String targetAttribute();

}
