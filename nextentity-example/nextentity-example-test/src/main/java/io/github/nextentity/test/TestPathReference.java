package io.github.nextentity.test;

import io.github.nextentity.core.PathReference;
import io.github.nextentity.test.entity.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestPathReference {

    public static void main(String[] args) {
        PathReference reference = PathReference.of(User::getId);
        log.info("{}", reference);
    }

}
