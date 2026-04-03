package io.github.nextentity.core.expression;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

///
/// 测试目标：验证FromEntity正确表示实体源
/// <p>
/// 测试场景：
/// 1. 使用实体类创建
/// 2. 访问类型属性
///
class FromEntityTest {

    @Nested
    class Creation {

        @Test
        void fromEntity_CreatesWithType() {
            // given
            Class<?> entityClass = String.class;

            // when
            FromEntity fromEntity = new FromEntity(entityClass);

            // then
            assertThat(fromEntity.type()).isEqualTo(String.class);
        }

        @Test
        void fromEntity_ImplementsFrom() {
            // given
            FromEntity fromEntity = new FromEntity(String.class);

            // then
            assertThat(fromEntity).isInstanceOf(From.class);
        }
    }
}
