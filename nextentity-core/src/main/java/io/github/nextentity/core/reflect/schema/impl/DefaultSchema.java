package io.github.nextentity.core.reflect.schema.impl;

import io.github.nextentity.core.reflect.schema.Attribute;
import io.github.nextentity.core.reflect.schema.Schema;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSchema extends AbstractSchema<AttributeSet<Attribute>, Attribute> implements Schema {

    /// Schema 缓存（全局共享）
    private static final ConcurrentHashMap<Class<?>, DefaultSchema> CACHE = new ConcurrentHashMap<>();

    /// 无参 public 构造函数，如果没有则为 null
    private final @Nullable Constructor<?> constructor;

    protected DefaultSchema(Class<?> type) {
        super(type);
        this.constructor = findNoArgsConstructor(type);
    }

    /// 查找无参 public 构造函数
    ///
    /// @param type 类型
    /// @return 无参 public 构造函数，如果没有则返回 null
    private static @Nullable Constructor<?> findNoArgsConstructor(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            if (constructor.canAccess(null)) {
                return constructor;
            }
        } catch (NoSuchMethodException _) {
            // 无参 public 构造函数不存在
        }
        return null;
    }

    public static DefaultSchema of(Class<?> type) {
        return CACHE.computeIfAbsent(type, DefaultSchema::new);
    }

    /// 获取无参 public 构造函数
    ///
    /// @return 无参 public 构造函数，如果没有则返回 null
    public @Nullable Constructor<?> getConstructor() {
        return constructor;
    }

    /// 判断是否存在无参 public 构造函数
    ///
    /// @return 如果存在则返回 true
    public boolean hasConstructor() {
        return constructor != null;
    }

    protected AttributeSet<Attribute> createAttributes() {
        List<DefaultAccessor> accessors = DefaultAccessor.of(type);

        // 创建属性列表
        ArrayList<Attribute> attributes = new ArrayList<>();
        int ordinal = 0;
        for (DefaultAccessor accessor : accessors) {
            List<DefaultAccessor> nestedAccessors = DefaultAccessor.of(accessor.type());
            if (nestedAccessors.isEmpty()) {
                DefaultAttribute attribute = new DefaultAttribute(this, accessor, ordinal++);
                attributes.add(attribute);
            } else {
                attributes.add(new DefaultSchemaAttribute(accessor, this, ordinal++));
            }
        }

        return new AttributeSet<>(attributes);
    }

}
