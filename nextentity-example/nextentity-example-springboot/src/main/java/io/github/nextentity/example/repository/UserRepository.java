package io.github.nextentity.example.repository;

import io.github.nextentity.example.entity.User;
import io.github.nextentity.spring.AbstractJpaRepository;
import io.github.nextentity.spring.JpaRepositoryFactoryConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends AbstractJpaRepository<User, Long> {

    private final $number<Long> id = $(User::getId);
    private final $string username = $(User::getUsername);

    protected UserRepository(JpaRepositoryFactoryConfiguration configuration) {
        super(configuration);
    }

    public User getById(Long userId) {
        return repository().where(id).eq(userId).getSingle();
    }

    public User findByUsername(String name) {
        @SuppressWarnings("unused") String first = repository.select(username).getFirst();
        return repository.where(username).eq(name).getSingle();
    }

}
