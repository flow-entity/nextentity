package io.github.nextentity.spring.integration.entity;

import io.github.nextentity.core.annotation.SubSelect;
import jakarta.persistence.Id;


@SubSelect("SELECT u.username AS username, max(u.random_number) AS max_random_number, count( u.id ) AS count from [user] u GROUP BY u.username")
public class UserSummarySqlServer {
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