package io.github.nextentity.spring.integration.fixbug;

import io.github.nextentity.api.Path;
import io.github.nextentity.spring.integration.db.DB;
import io.github.nextentity.spring.integration.db.UserQueryProvider;
import io.github.nextentity.spring.integration.db.UserRepository;
import io.github.nextentity.spring.integration.entity.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeepFetchTest {

    @DB(value = "h2", type = "jdbc")
    @ParameterizedTest
    @ArgumentsSource(UserQueryProvider.class)
    void fetch(UserRepository userQuery) {
        User ib = null;
        for (User user : userQuery.users()) {
            User cur = user.getParentUser();
            if (cur != null && cur.getParentUser() != null) {
                ib = user;
            }
        }
        assertNotNull(ib, "测试数据构造错误");

        User ia = userQuery.fetch(Path.of(User::getParentUser).get(User::getParentUser))
                .where(User::getId).eq(ib.getId())
                .single();

        User b = ib.getParentUser();
        User a = ia.getParentUser();
        assertNotNull(a);
        if (b.getParentUser() != null) {
            assertEquals(b.getParentUser(), a.getParentUser());
        }
    }
}
