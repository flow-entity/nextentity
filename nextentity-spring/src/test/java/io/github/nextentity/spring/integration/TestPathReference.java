package io.github.nextentity.spring.integration;

import io.github.nextentity.core.PathReference;
import io.github.nextentity.spring.integration.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPathReference {

    private static final Logger log = LoggerFactory.getLogger(TestPathReference.class);

    public static void main(String[] args) {
        PathReference reference = PathReference.of(User::getId);
        log.info("{}", reference);
    }

}
