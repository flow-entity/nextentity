package io.github.nextentity.spring.integration.entity;

import io.github.nextentity.core.annotation.SubSelect;
import jakarta.persistence.Id;

@SubSelect("select u.username as username, max(u.random_number) as max_random_number, count( u.id ) as count from `user` u group by u.username")
public class UserSummaryMysql {
    @Id
    private String username;
    private Integer maxRandomNumber;
    private Long count;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getMaxRandomNumber() {
        return maxRandomNumber;
    }

    public void setMaxRandomNumber(Integer maxRandomNumber) {
        this.maxRandomNumber = maxRandomNumber;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}