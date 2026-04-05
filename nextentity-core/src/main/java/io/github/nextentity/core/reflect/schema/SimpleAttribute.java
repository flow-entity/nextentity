package io.github.nextentity.core.reflect.schema;

import io.github.nextentity.core.util.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/// {@link Attribute} 的简单实现。
///
/// 该类为属性元数据提供了具体实现
/// 包括名称、类型、getter/setter 方法、字段引用和
/// 嵌套属性的路径计算。
///
/// @author HuangChengwei
/// @since 1.0.0
public class SimpleAttribute implements Attribute {

    private Class<?> type;

    private String name;

    private Method getter;

    private Method setter;

    private Field field;

    private Schema declareBy;

    private int ordinal;

    private volatile ImmutableList<String> path;

    /// 创建一个空的SimpleAttribute实例。
    public SimpleAttribute() {
    }

    /// 使用所有属性创建一个新的SimpleAttribute实例。
    ///
    /// @param type 属性类型
    /// @param name 属性名称
    /// @param getter getter方法
    /// @param setter setter方法
    /// @param field 字段
    /// @param declareBy 声明模式
    /// @param ordinal 序号位置
    public SimpleAttribute(Class<?> type, String name, Method getter, Method setter, Field field, Schema declareBy, int ordinal) {
        this.type = type;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.field = field;
        this.declareBy = declareBy;
        this.ordinal = ordinal;
    }

    /// 从另一个属性复制属性。
    ///
    /// @param attribute 源属性
    public void setAttribute(Attribute attribute) {
        this.type = attribute.type();
        this.name = attribute.name();
        this.getter = attribute.getter();
        this.setter = attribute.setter();
        this.field = attribute.field();
        this.declareBy = attribute.declareBy();
        this.ordinal = attribute.ordinal();
    }

    public Class<?> type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public Method getter() {
        return this.getter;
    }

    public Method setter() {
        return this.setter;
    }

    public Field field() {
        return this.field;
    }

    public Schema declareBy() {
        return this.declareBy;
    }

    /// 计算此属性的路径。
    ///
    /// 对于嵌套属性，路径包含父属性名称。
    /// 使用双重检查锁定进行线程安全的延迟初始化。
    ///
    /// @return 属性路径作为不可变名称列表
    @Override
    public ImmutableList<String> path() {
        if(path == null) {
            synchronized (this) {
                if(path == null) {
                    Schema schema = declareBy();
                    if (schema instanceof Attribute p) {
                        ImmutableList<String> pp = p.path();
                        String[] strings = new String[pp.size() + 1];
                        for (int i = 0; i < pp.size(); i++) {
                            strings[i] = pp.get(i);
                        }
                        strings[pp.size()] = name();
                        path = ImmutableList.of(strings);
                    } else {
                        path = ImmutableList.of(name());
                    }
                }
            }
        }
        return path;
    }

    public int ordinal() {
        return this.ordinal;
    }

    /// 设置属性类型。
    ///
    /// @param type 类型
    /// @return 此实例用于链式调用
    public SimpleAttribute type(Class<?> type) {
        this.type = type;
        return this;
    }

    /// 设置属性名称。
    ///
    /// @param name 名称
    /// @return 此实例用于链式调用
    public SimpleAttribute name(String name) {
        this.name = name;
        return this;
    }

    /// 设置getter方法。
    ///
    /// @param getter getter方法
    /// @return 此实例用于链式调用
    public SimpleAttribute getter(Method getter) {
        this.getter = getter;
        return this;
    }

    /// 设置setter方法。
    ///
    /// @param setter setter方法
    /// @return 此实例用于链式调用
    public SimpleAttribute setter(Method setter) {
        this.setter = setter;
        return this;
    }

    /// 设置字段。
    ///
    /// @param field 字段
    /// @return 此实例用于链式调用
    public SimpleAttribute field(Field field) {
        this.field = field;
        return this;
    }

    /// 设置声明模式。
    ///
    /// @param declareBy 声明模式
    /// @return 此实例用于链式调用
    public SimpleAttribute declareBy(Schema declareBy) {
        this.declareBy = declareBy;
        return this;
    }

    /// 设置序号位置。
    ///
    /// @param ordinal 序号
    /// @return 此实例用于链式调用
    public SimpleAttribute ordinal(int ordinal) {
        this.ordinal = ordinal;
        return this;
    }
}
