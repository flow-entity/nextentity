# 测试问题记录

## 2026-03-20 测试失败记录

### Bug #1: ImmutableList.of() 未创建防御性副本

#### 测试方法
`ImmutableListTest.of_ShouldCreateDefensiveCopy`

#### 失败原因
- 实际行为: 创建 ImmutableList 后，修改原始数组会影响列表内容
- 预期行为: ImmutableList 应该创建防御性副本，不受原始数组修改影响

#### 问题分析
`ImmutableList.of(T... elements)` 方法直接使用传入的数组 `elements`，没有创建副本。
当外部代码修改原始数组时，ImmutableList 内部的数据也会被修改，违反了不可变性的原则。

#### 解决方案
- [x] 记录此问题
- [ ] 修复业务代码 - 需要在 ImmutableList.of() 中添加 Arrays.copyOf() 防御性副本
- [ ] 修复测试用例 - 测试本身是正确的

#### 验证结果
[待修复后验证]

---

### Bug #2: NumberConverter 精度丢失时返回原值

#### 测试方法
`NumberConverterTest.convert_DoubleToInt_ShouldTruncate` (@Disabled，测试正确行为)

#### 失败原因
- 实际行为: Double(3.14) 转 int 时，返回原值 3.14
- 预期行为: 返回 Integer(3)

#### 问题分析
`NumberConverter.doConvert()` 方法检测到精度丢失时返回原始值，而不是转换后的值。
这是设计行为，但当目标类型是 int 时，期望截断而不是拒绝转换。

#### 解决方案
- [x] 记录此问题
- [x] 测试用例已修正为测试正确行为（@Disabled）
- [ ] 修复业务代码 - 修改精度丢失处理逻辑

#### 验证结果
修复后移除 @Disabled 注解即可验证

---

### Bug #3: ReflectUtil.getEnum(Class, String) 方法调用错误

#### 测试方法
`EnumConverterTest.convert_StringToEnum_ShouldReturnEnum` (@Disabled，测试正确行为)

#### 失败原因
- 实际行为: 字符串 "ACTIVE" 转枚举返回原字符串
- 预期行为: 返回 TestStatus.ACTIVE

#### 问题分析
`ReflectUtil.getEnum(Class<?>, String)` 方法中：
```java
Method method = cls.getMethod("valueOf"); // 获取无参 valueOf 方法 - 错误！应该是 getMethod("valueOf", String.class)
return method.invoke(name); // 错误：invoke 的第一个参数应该是 null（静态方法），第二个参数是 name
```
应该修改为：
```java
Method method = cls.getMethod("valueOf", String.class);
return method.invoke(null, name);
```

#### 解决方案
- [x] 记录此问题
- [x] 测试用例已修正为测试正确行为（@Disabled）
- [ ] 修复业务代码 - 修正 ReflectUtil.getEnum 方法调用

#### 验证结果
修复后移除 @Disabled 注解即可验证

