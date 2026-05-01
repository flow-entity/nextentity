package io.github.nextentity.jdbc;

import io.github.nextentity.core.meta.EntityType;
import io.github.nextentity.core.meta.impl.DefaultMetamodel;
import io.github.nextentity.core.meta.impl.TestEntities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 移植自 feature/embedded-support-d：SQL 构建器对 {@code @Embedded} 属性的 INSERT/UPDATE SQL 生成测试。
 */
@DisplayName("SQL构建器 - 嵌入属性持久化")
class EmbeddedPersistSqlTest {

    private DefaultMetamodel metamodel;
    private DefaultSqlBuilder sqlBuilder;

    @BeforeEach
    void setUp() {
        metamodel = DefaultMetamodel.of();
        sqlBuilder = new DefaultSqlBuilder(new MySqlDialect(), JdbcConfig.DEFAULT);
    }

    @Nested
    @DisplayName("INSERT SQL")
    class InsertSqlTests {

        @Test
        @DisplayName("INSERT 包含嵌入属性的子列")
        void shouldInsertIncludeEmbeddedSubColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            TestEntities.EntityWithEmbedded entity = new TestEntities.EntityWithEmbedded();
            entity.setId(1L);
            entity.setName("John");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            entity.setAddress(addr);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            assertThat(sql).containsIgnoringCase("city");
            assertThat(sql).containsIgnoringCase("street");
            assertThat(sql).containsIgnoringCase("zip_code");
        }

        @Test
        @DisplayName("@AttributeOverride 的列名在 INSERT 中正确")
        void shouldInsertRespectAttributeOverrideColumnName() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);
            TestEntities.EntityWithAttributeOverride entity = new TestEntities.EntityWithAttributeOverride();
            entity.setId(1L);
            TestEntities.FullName name = new TestEntities.FullName();
            name.setFirstName("John");
            name.setLastName("Doe");
            entity.setFullName(name);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            assertThat(sql).containsIgnoringCase("first_name_ov");
        }

        @Test
        @DisplayName("INSERT 参数包含嵌入子属性值")
        void shouldInsertParametersIncludeEmbeddedValues() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            TestEntities.EntityWithEmbedded entity = new TestEntities.EntityWithEmbedded();
            entity.setId(1L);
            entity.setName("John");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            entity.setAddress(addr);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            InsertSqlStatement stmt = statements.get(0);
            ArrayList<Object> firstRow = new ArrayList<>();
            stmt.parameters().iterator().next().forEach(firstRow::add);
            // id, name, street, city, zip_code
            assertThat(firstRow).containsExactly(1L, "John", "5th Ave", "NYC", "10001");
        }
    }

    @Nested
    @DisplayName("UPDATE SQL")
    class UpdateSqlTests {

        @Test
        @DisplayName("UPDATE 包含嵌入属性的子列")
        void shouldUpdateIncludeEmbeddedSubColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithEmbedded.class);
            TestEntities.EntityWithEmbedded entity = new TestEntities.EntityWithEmbedded();
            entity.setId(1L);
            entity.setName("John");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            entity.setAddress(addr);

            BatchSqlStatement stmt = sqlBuilder.buildUpdateStatement(
                    Collections.singletonList(entity), entityType);

            String sql = stmt.sql();
            assertThat(sql).containsIgnoringCase("street");
            assertThat(sql).containsIgnoringCase("city");
            assertThat(sql).containsIgnoringCase("zip_code");
        }

        @Test
        @DisplayName("@AttributeOverride 的列名在 UPDATE 中正确")
        void shouldUpdateRespectAttributeOverrideColumnName() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithAttributeOverride.class);
            TestEntities.EntityWithAttributeOverride entity = new TestEntities.EntityWithAttributeOverride();
            entity.setId(1L);
            entity.setName("John");
            TestEntities.FullName name = new TestEntities.FullName();
            name.setFirstName("John");
            name.setLastName("Doe");
            entity.setFullName(name);

            BatchSqlStatement stmt = sqlBuilder.buildUpdateStatement(
                    Collections.singletonList(entity), entityType);

            String sql = stmt.sql();
            assertThat(sql).containsIgnoringCase("first_name_ov");
        }
    }

    @Nested
    @DisplayName("嵌套嵌入持久化")
    class NestedEmbeddedPersistTests {

        @Test
        @DisplayName("INSERT 包含嵌套嵌入的所有子列")
        void shouldInsertIncludeNestedEmbeddedColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);
            TestEntities.EntityWithNestedEmbedded entity = new TestEntities.EntityWithNestedEmbedded();
            entity.setId(1L);
            TestEntities.ContactInfo contact = new TestEntities.ContactInfo();
            contact.setEmail("a@b.com");
            contact.setPhone("123456");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            contact.setAddress(addr);
            entity.setContactInfo(contact);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            assertThat(sql).containsIgnoringCase("email");
            assertThat(sql).containsIgnoringCase("phone");
            assertThat(sql).containsIgnoringCase("street");
            assertThat(sql).containsIgnoringCase("city");
            assertThat(sql).containsIgnoringCase("zip_code");
        }

        @Test
        @DisplayName("嵌套 @AttributeOverride 的列名在 INSERT 中正确")
        void shouldInsertRespectNestedAttributeOverride() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedAttributeOverride.class);
            TestEntities.EntityWithNestedAttributeOverride entity = new TestEntities.EntityWithNestedAttributeOverride();
            entity.setId(1L);
            TestEntities.ContactInfo contact = new TestEntities.ContactInfo();
            contact.setEmail("a@b.com");
            contact.setPhone("123456");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            contact.setAddress(addr);
            entity.setContactInfo(contact);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            // email 被 @AttributeOverride 为 contact_email
            assertThat(sql).containsIgnoringCase("contact_email");
            // address.street 被 @AttributeOverride 为 deep_street
            assertThat(sql).containsIgnoringCase("deep_street");
        }

        @Test
        @DisplayName("UPDATE 包含嵌套嵌入的所有子列")
        void shouldUpdateIncludeNestedEmbeddedColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithNestedEmbedded.class);
            TestEntities.EntityWithNestedEmbedded entity = new TestEntities.EntityWithNestedEmbedded();
            entity.setId(1L);
            TestEntities.ContactInfo contact = new TestEntities.ContactInfo();
            contact.setEmail("a@b.com");
            contact.setPhone("123456");
            TestEntities.Address addr = new TestEntities.Address();
            addr.setStreet("5th Ave");
            addr.setCity("NYC");
            addr.setZipCode("10001");
            contact.setAddress(addr);
            entity.setContactInfo(contact);

            BatchSqlStatement stmt = sqlBuilder.buildUpdateStatement(
                    Collections.singletonList(entity), entityType);

            String sql = stmt.sql();
            assertThat(sql).containsIgnoringCase("email");
            assertThat(sql).containsIgnoringCase("phone");
            assertThat(sql).containsIgnoringCase("street");
            assertThat(sql).containsIgnoringCase("city");
            assertThat(sql).containsIgnoringCase("zip_code");
        }
    }

    @Nested
    @DisplayName("错层嵌套持久化")
    class CrossLayerPersistTests {

        @Test
        @DisplayName("INSERT 包含错层嵌套的所有子列")
        void shouldInsertIncludeCrossLayerColumns() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerEmbedded.class);
            TestEntities.EntityWithCrossLayerEmbedded entity = new TestEntities.EntityWithCrossLayerEmbedded();
            entity.setId(1L);
            TestEntities.AddressWithZip addr = new TestEntities.AddressWithZip();
            addr.setCity("NYC");
            TestEntities.ZipCode zip = new TestEntities.ZipCode();
            zip.setCode("10001");
            addr.setZip(zip);
            entity.setAddress(addr);
            TestEntities.ZipCode secZip = new TestEntities.ZipCode();
            secZip.setCode("90210");
            entity.setSecondaryZip(secZip);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            assertThat(sql).containsIgnoringCase("city");
            // code 列会出现两次（address.zip.code 和 secondaryZip.code）
            long codeCount = countOccurrences(sql, "code");
            assertThat(codeCount).isEqualTo(2);
        }

        @Test
        @DisplayName("错层 @AttributeOverride 的列名在 INSERT 中正确且独立")
        void shouldInsertRespectCrossLayerOverride() {
            EntityType entityType = metamodel.getEntity(TestEntities.EntityWithCrossLayerOverride.class);
            TestEntities.EntityWithCrossLayerOverride entity = new TestEntities.EntityWithCrossLayerOverride();
            entity.setId(1L);
            TestEntities.AddressWithZip addr = new TestEntities.AddressWithZip();
            addr.setCity("NYC");
            TestEntities.ZipCode zip = new TestEntities.ZipCode();
            zip.setCode("10001");
            addr.setZip(zip);
            entity.setAddress(addr);
            TestEntities.ZipCode secZip = new TestEntities.ZipCode();
            secZip.setCode("90210");
            entity.setSecondaryZip(secZip);

            List<InsertSqlStatement> statements = sqlBuilder.buildInsertStatement(
                    Collections.singletonList(entity), entityType);

            assertThat(statements).hasSize(1);
            String sql = statements.get(0).sql();
            assertThat(sql).containsIgnoringCase("addr_city");
            assertThat(sql).containsIgnoringCase("addr_zip_code");
            assertThat(sql).containsIgnoringCase("sec_zip_code");
        }

        private long countOccurrences(String sql, String columnName) {
            String quoted = detectQuote(sql) + columnName + detectQuote(sql);
            return Pattern.compile(Pattern.quote(quoted), Pattern.CASE_INSENSITIVE)
                    .matcher(sql).results().count();
        }

        private String detectQuote(String sql) {
            if (sql.contains("`")) return "`";
            if (sql.contains("\"")) return "\"";
            if (sql.contains("[")) return "[";
            return "";
        }
    }
}
