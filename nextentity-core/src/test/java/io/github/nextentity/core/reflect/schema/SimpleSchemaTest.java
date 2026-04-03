package io.github.nextentity.core.reflect.schema;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/// 测试目标：验证 SimpleSchema 提供正确的模式元数据
/// <p>
/// 测试场景：
/// 1. 类型和属性可以设置和检索
/// 2. 构建器模式正常工作
/// <p>
/// 预期结果：可以正确创建和访问模式元数据
class SimpleSchemaTest {

    @Nested
    class BasicProperties {

        /// 测试目标：验证类型可以设置和检索
        /// 测试场景：设置类型并获取回来
        /// 预期结果：返回正确的类型
        @Test
        void type_ShouldBeSettable() {
            // given
            SimpleSchema schema = new SimpleSchema();

            // when
            schema.type(String.class);

            // then
            assertThat(schema.type()).isEqualTo(String.class);
        }

        /// 测试目标：验证属性可以设置和检索
        /// 测试场景：设置属性并获取回来
        /// 预期结果：返回正确的属性
        @Test
        void attributes_ShouldBeSettable() {
            // given
            SimpleSchema schema = new SimpleSchema();
            Attributes attrs = new SimpleAttributes(Collections.emptyList());

            // when
            schema.attributes(attrs);

            // then
            assertThat(schema.attributes()).isEqualTo(attrs);
        }
    }

    @Nested
    class BuilderPattern {

        /// 测试目标：验证设置器方法返回 this 以支持链式调用
        /// 测试场景：链接设置器
        /// 预期结果：返回相同的实例
        @Test
        void setters_ShouldReturnThis() {
            // given
            SimpleSchema schema = new SimpleSchema();
            Attributes attrs = new SimpleAttributes(Collections.emptyList());

            // when
            SimpleSchema result = schema
                    .type(String.class)
                    .attributes(attrs);

            // then
            assertThat(result).isSameAs(schema);
        }
    }
}