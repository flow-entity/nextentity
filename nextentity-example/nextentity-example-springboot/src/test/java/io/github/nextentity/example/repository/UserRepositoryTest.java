package io.github.nextentity.example.repository;

import io.github.nextentity.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void getById() {
        assertNotNull(userRepository.getById(1L));
        User test = userRepository.findByUsername("test");
        System.out.println(test);
    }

}