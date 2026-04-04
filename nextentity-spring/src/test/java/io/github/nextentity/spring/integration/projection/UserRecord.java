package io.github.nextentity.spring.integration.projection;

public record UserRecord(Integer id,
                         int randomNumber,
                         String username,
                         Integer pid,
                         UserRecord parentUser) {
}
