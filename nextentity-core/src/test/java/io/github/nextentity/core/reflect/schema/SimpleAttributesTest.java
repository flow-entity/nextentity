// package io.github.nextentity.core.reflect.schema;
//
// import io.github.nextentity.core.util.ImmutableArray;
// import io.github.nextentity.core.util.ImmutableList;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
//
// import java.lang.reflect.Field;
// import java.util.ArrayList;
// import java.util.List;
//
// import static org.assertj.core.api.Assertions.assertThat;
//
// /// 单元测试 SimpleAttributes.
// class SimpleAttributesTest {
//
//     private SimpleAttributes attributes;
//     private TestAttribute idAttr;
//     private TestAttribute nameAttr;
//     private TestAttribute nestedAttr;
//
//     @BeforeEach
//     void setUp() {
//         idAttr = new TestAttribute();
//         idAttr.name("id").ordinal(0).declareBy(null).field(null).getter(null).setter(null);
//
//         nameAttr = new TestAttribute();
//         nameAttr.name("name").ordinal(1).declareBy(null).field(null).getter(null).setter(null);
//
//         nestedAttr = new TestAttribute();
//         nestedAttr.name("nested").ordinal(2).declareBy(null).field(null).getter(null).setter(null);
//
//         List<Attribute> attrList = new ArrayList<>();
//         attrList.add(idAttr);
//         attrList.add(nameAttr);
//         attrList.add(nestedAttr);
//
//         attributes = new SimpleAttributes(attrList);
//     }
//
//     @Nested
//     class GetByName {
//
//         /// 测试目标：验证 get(String) 按名称返回属性。
//         /// 测试场景：使用现有属性名称调用 get()。
//         /// 预期结果：返回正确的属性。
//         @Test
//         void get_WithExistingName_ShouldReturnAttribute() {
//             // when
//             Attribute result = attributes.get("id");
//
//             // then
//             assertThat(result).isSameAs(idAttr);
//         }
//
//         /// 测试目标：验证 get(String) 对于不存在的名称返回 null。
//         /// 测试场景：使用不存在的属性名称调用 get()。
//         /// 预期结果：返回 null。
//         @Test
//         void get_WithNonExistentName_ShouldReturnNull() {
//             // when
//             Attribute result = attributes.get("unknown");
//
//             // then
//             assertThat(result).isNull();
//         }
//     }
//
//     @Nested
//     class GetPrimitives {
//
//         /// 测试目标：验证 getPrimitives() 只返回基本属性。
//         /// 测试场景：对混合类型属性调用 getPrimitives()。
//         /// 预期结果：只返回 isPrimitive() 返回 true 的属性。
//         @Test
//         void getPrimitives_ShouldReturnOnlyPrimitives() {
//             // when
//             ImmutableArray<Attribute> result = attributes.getPrimitives();
//
//             // then
//             assertThat(result).hasSize(2);
//             assertThat(result).containsExactlyInAnyOrder(idAttr, nameAttr);
//             assertThat(result).doesNotContain(nestedAttr);
//         }
//     }
//
//     @Nested
//     class Inheritance {
//
//         /// 测试目标：验证 SimpleAttributes 扩展 ImmutableList。
//         /// 测试场景：检查继承。
//         /// 预期结果：是 ImmutableList 的实例。
//         @Test
//         void shouldExtendImmutableList() {
//             assertThat(attributes).isInstanceOf(ImmutableList.class);
//         }
//
//         /// 测试目标：验证 SimpleAttributes 实现 Attributes。
//         /// 测试场景：检查接口实现。
//         /// 预期结果：是 Attributes 的实例。
//         @Test
//         void shouldImplementAttributes() {
//             assertThat(attributes).isInstanceOf(Attributes.class);
//         }
//     }
//
//     /// 测试 Attribute 的实现.
//     static class TestAttribute extends SimpleAttribute {
//         @Override
//         public boolean isPrimitive() {
//             // 对 id 和 name 返回 true，对 nested 返回 false
//             return !name().equals("nested");
//         }
//     }
// }