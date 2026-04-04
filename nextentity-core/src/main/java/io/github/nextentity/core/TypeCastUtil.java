package io.github.nextentity.core;

import io.github.nextentity.api.EntityRoot;
import io.github.nextentity.api.Expression;
import io.github.nextentity.core.util.ImmutableArray;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// 类型转换操作的工具类。
///
/// 该类提供绕过编译时类型检查的不安全类型转换方法。
/// 请谨慎使用 - 这些方法只应在调用代码逻辑保证类型关系的情况下使用。
///
/// **警告：** 不当使用这些方法可能导致运行时出现 `ClassCastException` 异常。
/// 请始终确保源类型和目标类型是兼容的。
public final class TypeCastUtil {

    private TypeCastUtil() {
        // 工具类，防止实例化
    }

    ///
    /// 将未知类型的 List 转换为目标类型的 List。
    ///
    /// 当已知列表元素为类型 T 时，此方法是安全的
    /// （例如，当列表是用 T 元素创建但通过泛型 API 传递时）。
    ///
    /// @param expression 要转换的列表
    /// @param <T> 目标元素类型
    /// @return 转换后的列表
    ///
    public static <T> List<T> cast(List<?> expression) {
        return unsafeCast(expression);
    }

    ///
    /// 将未知类型的 ImmutableArray 转换为目标类型的 ImmutableArray。
    ///
    /// @param expression 要转换的数组
    /// @param <T> 目标元素类型
    /// @return 转换后的数组
    ///
    public static <T> ImmutableArray<T> cast(ImmutableArray<?> expression) {
        return unsafeCast(expression);
    }

    ///
    /// 将未知类型的 Class 转换为目标类型的 Class。
    ///
    /// @param resolve 要转换的类
    /// @param <T> 目标类型
    /// @return 转换后的类
    ///
    public static <T> Class<T> cast(Class<?> resolve) {
        return unsafeCast(resolve);
    }

    ///
    /// 将未知类型的 EntityRoot 转换为目标类型的 EntityRoot。
    ///
    /// @param builder 要转换的实体根
    /// @param <T> 目标实体类型
    /// @return 转换后的实体根
    ///
    public static <T> EntityRoot<T> cast(EntityRoot<?> builder) {
        return unsafeCast(builder);
    }

    ///
    /// 对对象执行未检查的强制转换到目标类型。
    ///
    /// **警告：** 此方法绕过了编译时类型检查。
    /// 仅在调用代码的逻辑保证类型关系时使用。
    ///
    /// 安全使用的示例：
    /// ```
    /// // 安全：我们知道列表包含 String 元素
    /// List<String> strings = TypeCastUtil.unsafeCast(Arrays.asList("a", "b", "c"));
    /// ```
    ///
    /// 不安全使用的示例（将在运行时导致 ClassCastException）：
    /// ```
    /// // 不安全：列表包含 Integer，而不是 String
    /// List<String> strings = TypeCastUtil.unsafeCast(Arrays.asList(1, 2, 3));
    /// String s = strings.get(0); // 这里出现 ClassCastException
    /// ```
    ///
    /// @param object 要转换的对象
    /// @param <T> 目标类型
    /// @return 转换后的对象
    ///
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(@Nullable Object object) {
        return (T) object;
    }

}
