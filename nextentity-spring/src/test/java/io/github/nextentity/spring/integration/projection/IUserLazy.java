package io.github.nextentity.spring.integration.projection;

import io.github.nextentity.core.annotation.EntityPath;
import io.github.nextentity.core.annotation.Fetch;

import static jakarta.persistence.FetchType.LAZY;

/**
 * 带懒加载属性的投影接口。
 *
 * 用于测试投影对象 LAZY 属性的批量加载功能。
 * parentUser 属性标记为 LAZY，首次访问时触发批量 WHERE IN 查询。
 */
public interface IUserLazy {

    Integer getId();

    int getRandomNumber();

    String getUsername();

    Integer getPid();

    /**
     * 懒加载的父用户信息。
     *
     * 使用 @Fetch(LAZY) 标记为延迟加载，
     * 首次访问时通过批量 WHERE IN 查询加载所有关联对象。
     */
    @EntityPath("parentUser")
    @Fetch(LAZY)
    IUserLazyParent getParentUser();

    /**
     * 父用户的嵌套投影接口。
     */
    interface IUserLazyParent {

        Integer getId();

        String getUsername();

        int getRandomNumber();
    }
}