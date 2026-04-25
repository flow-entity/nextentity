package io.github.nextentity.core.constructor;

import io.github.nextentity.jdbc.Arguments;

import java.util.List;

/// 值构造器接口
///
/// 负责从 Arguments 构造值，核心职责是"构造值"而非"设置属性"。
/// 实现类型包括：单值（{@link SingleValueConstructor}）、对象（{@link ObjectConstructor}）、
/// Record（{@link RecordConstructor}）、代理（{@link ProxyConstructor}）、
/// 数组（{@link ArrayConstructor}）和懒加载（{@link LazyValueConstructor}）。
///
/// @author HuangChengwei
/// @since 2.2.2
public interface ValueConstructor {

    /// 获取所有列（按 SQL select 顺序）
    ///
    /// @return 列列表
    List<Column> columns();

    /// 构造值（按列序消费 Arguments）
    ///
    /// @param arguments 参数供应器
    /// @return 构造的值
    Object construct(Arguments arguments);
}