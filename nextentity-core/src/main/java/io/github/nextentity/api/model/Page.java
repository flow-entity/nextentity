package io.github.nextentity.api.model;

import java.util.List;

/// 分页结果接口，包含分页数据和总记录数。
///
/// @param <T> 数据类型
/// @author HuangChengwei
/// @since 1.0.0
/// @deprecated 已废弃，请使用 {@link Slice} 接口代替。
///             Slice 接口提供了更完整的分页信息，包括偏移量和限制数。
@Deprecated
public interface Page<T> {

    /// 获取当前页的数据列表。
    ///
    /// @return 数据列表
    List<T> getItems();

    /// 获取总记录数。
    ///
    /// @return 总记录数
    long getTotal();
}
