package io.github.nextentity.core.reflect;

import java.util.NoSuchElementException;

/// 延时加载观察器注册中心。
///
/// 使用 {@link ScopedValue} 确保观察器只在绑定范围内生效，
/// 自动管理生命周期，防止值泄漏。
///
/// @author HuangChengwei
/// @since 2.1.0
public final class LoadObserverRegistry {

    /// 观察器 ScopedValue（全局唯一）。
    public static final ScopedValue<LoadObserver> OBSERVER = ScopedValue.newInstance();

    private LoadObserverRegistry() {
    }

    /// 获取当前绑定的观察器。
    ///
    /// @return 当前绑定的观察器
    /// @throws NoSuchElementException 如果未绑定
    public static LoadObserver get() {
        return OBSERVER.get();
    }

    /// 检查是否绑定了观察器。
    ///
    /// @return 如果绑定了观察器返回 true
    public static boolean isBound() {
        return OBSERVER.isBound();
    }

    /// 在观察器绑定范围内执行操作。
    ///
    /// @param observer 观察器
    /// @param action   要执行的操作
    public static void withObserver(LoadObserver observer, Runnable action) {
        ScopedValue.where(OBSERVER, observer).run(action);
    }
}