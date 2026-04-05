package io.github.nextentity.api.model;

/// 分页参数接口，定义了分页的基本参数。
///
/// @author HuangChengwei
/// @since 1.0.0
/// @deprecated 已废弃，请直接使用 offset/limit 参数进行分页查询。
///             示例：`repository.query().list(offset, limit)`
@Deprecated
public interface Pageable {

    /// 获取当前页码。
    ///
    /// @return 当前页码
    int page();

    /// 获取每页大小。
    ///
    /// @return 每页大小
    int size();

    /// 计算分页偏移量。
    ///
    /// @return 分页偏移量
    default int offset() {
        return (page() - 1) * size();
    }

}
