package io.github.nextentity.test.entity;

import io.github.nextentity.api.BooleanPath;
import io.github.nextentity.api.NumberPath;
import io.github.nextentity.api.SimpleExpression;
import io.github.nextentity.api.StringPath;
import io.github.nextentity.core.EntityMeta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class UserMeta<T> extends EntityMeta<User, T> {

    public final NumberPath<T, Integer> id = of(User::getId);

    public final NumberPath<T, Integer> randomNumber = of(User::getRandomNumber);

    public final StringPath<T> username = of(User::getUsername);

    public final SimpleExpression<T, Date> time = of(User::getTime);

    public final NumberPath<T, Integer> pid = of(User::getPid);

    public final NumberPath<T, Double> timestamp = of(User::getTimestamp);

    public final UserMeta<T> parentUser = this.of(User::getParentUser, UserMeta::new);

    public final BooleanPath<T> valid = of(User::isValid);

    public final SimpleExpression<T, Gender> gender = of(User::getGender);

    public final SimpleExpression<T, Date> instant = of(User::getInstant);

    public final NumberPath<T, Long> testLong = of(User::getTestLong);

    public final NumberPath<T, Integer> testInteger = of(User::getTestInteger);

    public final SimpleExpression<T, LocalDate> testLocalDate = of(User::getTestLocalDate);

    public final SimpleExpression<T, LocalDateTime> testLocalDateTime = of(User::getTestLocalDateTime);

    public final TestMeta<T> test = this.of(User::getTest, TestMeta::new);

    public final UserMeta<T> randomUser = this.of(User::getRandomUser, UserMeta::new);

    public final UserMeta<T> testUser = this.of(User::getTestUser, UserMeta::new);

}
