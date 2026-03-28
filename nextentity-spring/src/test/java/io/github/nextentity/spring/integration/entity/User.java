package io.github.nextentity.spring.integration.entity;

import io.github.nextentity.core.util.Exceptions;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

@Entity
@Table(name = "users")
public class User extends EnableOptimisticLock implements Cloneable {

    @Id
    private int id;

    private int randomNumber;

    private String username;

    private Date time;

    private Integer pid;

    private Double timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", insertable = false, updatable = false)
    private User parentUser;

    private boolean valid;

    private Gender gender;

    private Date instant;

    private Long testLong;

    private Integer testInteger;

    private LocalDate testLocalDate;

    private LocalDateTime testLocalDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "randomNumber", updatable = false, insertable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User randomUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testInteger", updatable = false, insertable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User testUser;

    private transient Test test;

    public User() {
    }

    public boolean isNew() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() == user.getId()
               && getRandomNumber() == user.getRandomNumber()
               && isValid() == user.isValid()
               && Objects.equals(getUsername(), user.getUsername())
               && Objects.equals(getTime(), user.getTime())
               && Objects.equals(getPid(), user.getPid())
               && Objects.equals(getTimestamp(), user.getTimestamp())
               && getGender() == user.getGender()
               && Objects.equals(getInstant(), user.getInstant())
               && Objects.equals(getTestLong(), user.getTestLong())
               && Objects.equals(getTestInteger(), user.getTestInteger())
               && Objects.equals(getTestLocalDate(), user.getTestLocalDate())
               && Objects.equals(getTestLocalDateTime(), user.getTestLocalDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRandomNumber(), getUsername(), getTime(), getPid(),
                getTimestamp(), isValid(), getGender(), getInstant(), getTestLong(),
                getTestInteger(), getTestLocalDate(), getTestLocalDateTime());
    }

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            throw Exceptions.sneakyThrow(e);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }

    public User getParentUser() {
        return parentUser;
    }

    public void setParentUser(User parentUser) {
        this.parentUser = parentUser;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getInstant() {
        return instant;
    }

    public void setInstant(Date instant) {
        this.instant = instant;
    }

    public Long getTestLong() {
        return testLong;
    }

    public void setTestLong(Long testLong) {
        this.testLong = testLong;
    }

    public Integer getTestInteger() {
        return testInteger;
    }

    public void setTestInteger(Integer testInteger) {
        this.testInteger = testInteger;
    }

    public LocalDate getTestLocalDate() {
        return testLocalDate;
    }

    public void setTestLocalDate(LocalDate testLocalDate) {
        this.testLocalDate = testLocalDate;
    }

    public LocalDateTime getTestLocalDateTime() {
        return testLocalDateTime;
    }

    public void setTestLocalDateTime(LocalDateTime testLocalDateTime) {
        this.testLocalDateTime = testLocalDateTime;
    }

    public User getRandomUser() {
        return randomUser;
    }

    public void setRandomUser(User randomUser) {
        this.randomUser = randomUser;
    }

    public User getTestUser() {
        return testUser;
    }

    public void setTestUser(User testUser) {
        this.testUser = testUser;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               '}';
    }
}
