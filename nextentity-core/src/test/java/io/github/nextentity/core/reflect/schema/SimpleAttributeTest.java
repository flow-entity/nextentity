// package io.github.nextentity.core.reflect.schema;
//
// import io.github.nextentity.core.util.ImmutableList;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
//
// import java.lang.reflect.Field;
// import java.lang.reflect.Method;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// /// 测试目标：验证 SimpleAttribute 提供正确的属性元数据
// /// <p>
// /// 测试场景：
// /// 1. Getter 和 setter 方法正常工作
// /// 2. 构建器模式设置器返回 this
// /// 3. 嵌套属性的路径计算正常工作
// /// <p>
// /// 预期结果：可以正确创建和访问属性元数据
// class SimpleAttributeTest {
//
//     @Nested
//     class BasicProperties {
//
//         /// 测试目标：验证所有属性都可以设置和检索
//         /// 测试场景：创建包含所有属性的属性对象
//         /// 预期结果：所有属性都可访问
//         @Test
//         void allProperties_ShouldBeAccessible() throws Exception {
//             // given
//             Class<?> type = String.class;
//             String name = "testName";
//             Method getter = TestEntity.class.getMethod("getName");
//             Method setter = TestEntity.class.getMethod("setName", String.class);
//             Field field = TestEntity.class.getDeclaredField("name");
//             Schema declareBy = new SimpleSchema().type(TestEntity.class);
//             int ordinal = 5;
//
//             // when
//             SimpleAttribute attr = new SimpleAttribute(type, name, getter, setter, field, declareBy, ordinal);
//
//             // then
//             assertThat(attr.type()).isEqualTo(type);
//             assertThat(attr.name()).isEqualTo(name);
//             assertThat(attr.getter()).isEqualTo(getter);
//             assertThat(attr.setter()).isEqualTo(setter);
//             assertThat(attr.field()).isEqualTo(field);
//             assertThat(attr.declareBy()).isEqualTo(declareBy);
//             assertThat(attr.ordinal()).isEqualTo(ordinal);
//         }
//
//         /// 测试目标：验证默认构造函数创建空属性
//         /// 测试场景：使用默认构造函数创建
//         /// 预期结果：所有属性为 null/默认值
//         @Test
//         void defaultConstructor_ShouldCreateEmptyAttribute() {
//             // when
//             SimpleAttribute attr = new SimpleAttribute();
//
//             // then
//             assertThat(attr.type()).isNull();
//             assertThat(attr.name()).isNull();
//             assertThat(attr.getter()).isNull();
//             assertThat(attr.setter()).isNull();
//             assertThat(attr.field()).isNull();
//             assertThat(attr.declareBy()).isNull();
//             assertThat(attr.ordinal()).isZero();
//         }
//     }
//
//     @Nested
//     class BuilderPattern {
//
//         /// 测试目标：验证设置器方法返回 this 以支持链式调用
//         /// 测试场景：链接多个设置器
//         /// 预期结果：返回相同的实例
//         @Test
//         void setters_ShouldReturnThis() {
//             // given
//             SimpleAttribute attr = new SimpleAttribute();
//
//             // when
//             SimpleAttribute result = attr
//                     .type(String.class)
//                     .name("test")
//                     .ordinal(1);
//
//             // then
//             assertThat(result).isSameAs(attr);
//             assertThat(attr.type()).isEqualTo(String.class);
//             assertThat(attr.name()).isEqualTo("test");
//             assertThat(attr.ordinal()).isEqualTo(1);
//         }
//     }
//
//     @Nested
//     class PathCalculation {
//
//         /// 测试目标：验证没有父级时路径返回单个元素
//         /// 测试场景：获取没有父级的属性的路径
//         /// 预期结果：路径仅包含属性名称
//         @Test
//         void path_WithoutParent_ShouldReturnSingleElement() {
//             // given
//             SimpleAttribute attr = new SimpleAttribute()
//                     .name("fieldName");
//
//             // when
//             ImmutableList<String> path = attr.path();
//
//             // then
//             assertThat(path).containsExactly("fieldName");
//         }
//     }
//
//     @Nested
//     class SetAttribute {
//
//         /// 测试目标：验证 setAttribute 复制所有属性
//         /// 测试场景：将一个属性的属性复制到另一个属性
//         /// 预期结果：所有属性都被复制
//         @Test
//         void setAttribute_ShouldCopyAllProperties() throws Exception {
//             // given
//             Method getter = TestEntity.class.getMethod("getName");
//             Method setter = TestEntity.class.getMethod("setName", String.class);
//             Field field = TestEntity.class.getDeclaredField("name");
//             Schema schema = new SimpleSchema().type(TestEntity.class);
//
//             SimpleAttribute source = new SimpleAttribute(String.class, "name", getter, setter, field, schema, 3);
//             SimpleAttribute target = new SimpleAttribute();
//
//             // when
//             target.setAttribute(source);
//
//             // then
//             assertThat(target.type()).isEqualTo(String.class);
//             assertThat(target.name()).isEqualTo("name");
//             assertThat(target.getter()).isEqualTo(getter);
//             assertThat(target.setter()).isEqualTo(setter);
//             assertThat(target.field()).isEqualTo(field);
//             assertThat(target.declareBy()).isEqualTo(schema);
//             assertThat(target.ordinal()).isEqualTo(3);
//         }
//     }
//
//     // 测试实体类
//     static class TestEntity {
//         private String name;
//
//         public String getName() { return name; }
//         public void setName(String name) { this.name = name; }
//     }
// }