package farcic.dev.erp_gestao.organizacao;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MigrationClienteTests {
    @Autowired Environment environment;

    @Test
    void migraV3ComDadosPreservandoIdsSequencesConstraintsEIndices() throws Exception {
        String url = environment.getRequiredProperty("spring.datasource.url");
        String user = environment.getRequiredProperty("spring.datasource.username");
        String password = environment.getRequiredProperty("spring.datasource.password");
        String schema = "migration_test_" + UUID.randomUUID().toString().replace("-", "");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement sql = connection.createStatement()) {
            try {
                Flyway.configure().dataSource(url, user, password).schemas(schema).defaultSchema(schema)
                        .target("3").load().migrate();
                sql.execute("SET search_path TO " + schema);
                sql.execute("INSERT INTO revenda (nome, email_contato, cnpj) VALUES ('Revenda', 'a@a.test', '123')");
                sql.execute("INSERT INTO empresa (nome, revenda_id) VALUES ('Cliente preservado', 1)");
                sql.execute("INSERT INTO usuarios (keycloak_sub) VALUES ('sub-preservado')");
                sql.execute("INSERT INTO usuario_empresa (usuario_id, empresa_id) VALUES (1, 1)");
                sql.execute("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, empresa_id) "
                        + "VALUES ('Loja', 'Loja', 'Loja', '12345678000100', '123', 'SIMPLES_NACIONAL', 1)");
                // As sequences não devem voltar para 1 após a renomeação.
                sql.execute("SELECT setval('empresa_seq', 50)");
                sql.execute("SELECT setval('usuario_empresa_seq', 70)");
                Flyway flyway = Flyway.configure().dataSource(url, user, password)
                        .schemas(schema).defaultSchema(schema).load();
                assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
                flyway.validate();
                assertThat(value(sql, "SELECT nome FROM cliente WHERE id = 1")).isEqualTo("Cliente preservado");
                assertThat(value(sql, "SELECT cliente_id FROM loja WHERE id = 1")).isEqualTo("1");
                assertThat(value(sql, "SELECT cliente_id FROM usuario_cliente WHERE id = 1")).isEqualTo("1");
                assertThat(value(sql, "INSERT INTO cliente (nome, revenda_id) VALUES ('Novo', 1) RETURNING id")).isEqualTo("51");
                assertThat(value(sql, "INSERT INTO usuario_cliente (usuario_id, cliente_id) VALUES (1, 51) RETURNING id")).isEqualTo("71");
                assertConstraint(sql, "INSERT INTO usuario_cliente (usuario_id, cliente_id) VALUES (1, 1)", "23505");
                assertConstraint(sql, "INSERT INTO cliente (nome, revenda_id) VALUES ('Inválido', -1)", "23503");
                assertConstraint(sql, "UPDATE loja SET cliente_id = -1 WHERE id = 1", "23503");
                assertConstraint(sql, "INSERT INTO usuario_cliente (usuario_id, cliente_id) VALUES (-1, 1)", "23503");
                assertConstraint(sql, "INSERT INTO usuario_cliente (usuario_id, cliente_id) VALUES (1, -1)", "23503");
                assertConstraint(sql, "INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, cliente_id) "
                        + "SELECT nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, cliente_id FROM loja WHERE id = 1", "23505");
                for (String index : new String[]{"idx_cliente_revenda_id", "idx_loja_cliente_id", "idx_usuario_cliente_cliente_id", "uk_usuario_cliente"}) {
                    assertThat(value(sql, "SELECT count(*) FROM pg_indexes WHERE schemaname = '" + schema + "' AND indexname = '" + index + "'"))
                            .isEqualTo("1");
                }
                assertThat(value(sql, "SELECT count(*) FROM information_schema.tables WHERE table_schema = '" + schema
                        + "' AND table_name IN ('empresa', 'usuario_empresa')")).isEqualTo("0");
            } finally {
                sql.execute("SET search_path TO public");
                sql.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
            }
        }
    }

    private String value(Statement sql, String query) throws SQLException {
        try (ResultSet result = sql.executeQuery(query)) {
            assertThat(result.next()).isTrue();
            return result.getString(1);
        }
    }

    private void assertConstraint(Statement sql, String query, String sqlState) {
        assertThatThrownBy(() -> sql.execute(query)).isInstanceOf(SQLException.class)
                .satisfies(error -> assertThat(((SQLException) error).getSQLState()).isEqualTo(sqlState));
    }
}
