package io.github.nextentity.core.util;

import io.github.nextentity.api.*;
import io.github.nextentity.api.PathRef.BooleanRef;
import io.github.nextentity.api.PathRef.NumberRef;
import io.github.nextentity.api.PathRef.StringRef;

/// 路径工厂工具类，提供创建各种路径表达式的静态方法。
///
/// **已废弃**：请使用 {@link Path} 及其子类的静态 {@code of} 方法替代。
///
/// ## 替代方案对照表
///
/// | 废弃方法 | 替代方法 |
/// |---------|---------|
/// | `Paths.root()` | `EntityRoot.of()` |
/// | `Paths.get(PathRef)` | `Path.of(PathRef)` |
/// | `Paths.get(BooleanRef)` | `Path.of(BooleanRef)` 或 `BooleanPath.of(BooleanRef)` |
/// | `Paths.get(StringRef)` | `Path.of(StringRef)` 或 `StringPath.of(StringRef)` |
/// | `Paths.get(NumberRef)` | `Path.of(NumberRef)` 或 `NumberPath.of(NumberRef)` |
/// | `Paths.get(EnumRef)` | `Path.of(EnumRef)` 或 `EnumPath.of(EnumRef)` |
/// | `Paths.path(PathRef)` | `Path.of(PathRef)` |
/// | `Paths.entity(PathRef)` | `EntityPath.of(EntityPathRef)` 或 `Path.of(EntityPathRef)` |
/// | `Paths.string(PathRef)` | `StringPath.of(StringRef)` |
/// | `Paths.number(PathRef)` | `NumberPath.of(NumberRef)` |
/// | `Paths.bool(BooleanRef)` | `BooleanPath.of(BooleanRef)` |
/// | `Paths.path(String)` | `Path.of(String)` (类型不安全) |
/// | `Paths.entityPath(String)` | `EntityPath.of(String)` (类型不安全) |
/// | `Paths.stringPath(String)` | 无直接替代，使用 `Path.of(String)` |
/// | `Paths.numberPath(String)` | 无直接替代，使用 `Path.of(String)` |
/// | `Paths.booleanPath(String)` | 无直接替代，使用 `Path.of(String)` |
///
/// ## 示例：迁移到新 API
///
/// ```java
/// // 旧写法 (已废弃)
/// Path<User, String> namePath = Paths.path(User::getName);
/// BooleanPath<User> activePath = Paths.bool(User::isActive);
///
/// // 新写法
/// Path<User, String> namePath = Path.of(User::getName);
/// BooleanPath<User> activePath = BooleanPath.of(User::isActive);
/// // 或者使用 Path.of，它会自动选择正确的子类型
/// Path<User, String> namePath = Path.of(User::getName);
/// Path<User, Boolean> activePath = Path.of(User::isActive);
/// ```
///
/// @author HuangChengwei
/// @since 1.0.0
/// @deprecated 自 2.0 版本起废弃，请使用 {@link Path#of(PathRef)} 及相关子类的静态方法
@Deprecated
public interface Paths {

    /// 创建实体根路径。
    ///
    /// @deprecated 请使用 {@link EntityRoot#of()} 替代
    @Deprecated
    static <T> EntityRoot<T> root() {
        return DefaultEntityRoot.of();
    }

    /// 从路径引用创建路径表达式。
    ///
    /// @deprecated 请使用 {@link Path#of(PathRef)} 替代
    @Deprecated
    static <T, U> EntityPath<T, U> get(PathRef<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    /// 从布尔引用创建布尔路径。
    ///
    /// @deprecated 请使用 {@link Path#of(BooleanRef)} 或 {@link BooleanPath#of(BooleanRef)} 替代
    @Deprecated
    static <T> BooleanPath<T> get(BooleanRef<T> path) {
        return Paths.<T>root().get(path);
    }

    /// 从字符串引用创建字符串路径。
    ///
    /// @deprecated 请使用 {@link Path#of(StringRef)} 或 {@link StringPath#of(StringRef)} 替代
    @Deprecated
    static <T> StringPath<T> get(StringRef<T> path) {
        return Paths.<T>root().get(path);
    }

    /// 从数值引用创建数值路径。
    ///
    /// @deprecated 请使用 {@link Path#of(NumberRef)} 或 {@link NumberPath#of(NumberRef)} 替代
    @Deprecated
    static <T, U extends Number> NumberPath<T, U> get(NumberRef<T, U> path) {
        return Paths.<T>root().get(path);
    }

    /// 从路径引用创建路径表达式。
    ///
    /// @deprecated 请使用 {@link Path#of(PathRef)} 替代
    @Deprecated
    static <T, U> Path<T, U> path(PathRef<T, U> path) {
        return Paths.<T>root().get(path);
    }

    /// 从实体路径引用创建实体路径。
    ///
    /// @deprecated 请使用 {@link EntityPath#of(PathRef)} 或 {@link Path#of(PathRef.EntityPathRef)} 替代
    @Deprecated
    static <T, U> EntityPath<T, U> entity(PathRef<T, U> path) {
        return Paths.<T>root().entity(path);
    }

    /// 从字符串引用创建字符串路径。
    ///
    /// @deprecated 请使用 {@link StringPath#of(StringRef)} 替代
    @Deprecated
    static <T> StringPath<T> string(PathRef<T, String> path) {
        return Paths.<T>root().string(path);
    }

    /// 从数值引用创建数值路径。
    ///
    /// @deprecated 请使用 {@link NumberPath#of(NumberRef)} 替代
    @Deprecated
    static <T, U extends Number> NumberPath<T, U> number(PathRef<T, U> path) {
        return Paths.<T>root().number(path);
    }

    /// 从布尔引用创建布尔路径。
    ///
    /// @deprecated 请使用 {@link BooleanPath#of(BooleanRef)} 替代
    @Deprecated
    static <T> BooleanPath<T> bool(PathRef<T, Boolean> path) {
        return Paths.<T>root().bool(path);
    }

    // type-unsafe

    /// 从字段名创建路径表达式（类型不安全）。
    ///
    /// 此方法无法在编译时验证字段名的正确性和类型匹配，
    /// 仅建议在动态查询场景中使用。
    ///
    /// @deprecated 请使用 {@link Path#of(String)} 替代
    @Deprecated
    static <T, U> Path<T, U> path(String fieldName) {
        return Paths.<T>root().path(fieldName);
    }

    /// 从字段名创建实体路径（类型不安全）。
    ///
    /// 此方法无法在编译时验证字段名的正确性和类型匹配，
    /// 仅建议在动态查询场景中使用。
    ///
    /// @deprecated 请使用 {@link EntityPath#of(String)} 替代
    @Deprecated
    static <T, U> EntityPath<T, U> entityPath(String fieldName) {
        return Paths.<T>root().entityPath(fieldName);
    }

    /// 从字段名创建字符串路径（类型不安全）。
    ///
    /// 此方法无法在编译时验证字段名的正确性和类型匹配，
    /// 仅建议在动态查询场景中使用。
    ///
    /// @deprecated 无直接替代，请使用 {@link Path#of(String)} 替代
    @Deprecated
    static <T> StringPath<T> stringPath(String fieldName) {
        return Paths.<T>root().stringPath(fieldName);
    }

    /// 从字段名创建数值路径（类型不安全）。
    ///
    /// 此方法无法在编译时验证字段名的正确性和类型匹配，
    /// 仅建议在动态查询场景中使用。
    ///
    /// @deprecated 无直接替代，请使用 {@link Path#of(String)} 替代
    @Deprecated
    static <T, U extends Number> NumberPath<T, U> numberPath(String fieldName) {
        return Paths.<T>root().numberPath(fieldName);
    }

    /// 从字段名创建布尔路径（类型不安全）。
    ///
    /// 此方法无法在编译时验证字段名的正确性和类型匹配，
    /// 仅建议在动态查询场景中使用。
    ///
    /// @deprecated 无直接替代，请使用 {@link Path#of(String)} 替代
    @Deprecated
    static <T> BooleanPath<T> booleanPath(String fieldName) {
        return Paths.<T>root().booleanPath(fieldName);
    }

}
