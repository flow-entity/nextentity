package io.github.nextentity.test.projection;

public record UserRecord(int id,
                         int randomNumber,
                         String username,
                         Integer pid,
                         UserRecord parentUser) {
}
