package io.github.nextentity.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// 子查询注解，用于标记子查询相关的类型。
@Target(TYPE)
@Retention(RUNTIME)
public @interface SubSelect {
    /// 子查询的内容。
    ///
    /// @return 子查询字符串
    String value();
}
