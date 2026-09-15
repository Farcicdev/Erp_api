package farcic.dev.erp_gestao.produto;

import farcic.dev.erp_gestao.produto.entity.Produtos;
import farcic.dev.erp_gestao.produto.entity.UnidadeComercial;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.loja.entity.Loja;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProdutoPersistenciaTests {
    @Autowired ProdutoRepository repository;
    @Autowired EntityManager entityManager;
    @Autowired JdbcTemplate jdbc;

    @Test
    void persistePeloRepositorioComDefaultEPermiteMultiplosGtinsAusentes() {
        Long lojaId = criarLoja("11111111000111");
        Produtos produto = produto(lojaId, "A", "  ");
        repository.saveAndFlush(produto);
        repository.saveAndFlush(produto(lojaId, "B", null));
        entityManager.clear();
        Produtos salvo = repository.findById(produto.getId()).orElseThrow();
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getGtin()).isNull();
        assertThat(salvo.getLoja().getId()).isEqualTo(lojaId);
        assertThat(salvo.getUnidade()).isEqualTo(UnidadeComercial.UN);
        salvo.setGtin(" 12345678 ");
        repository.flush();
        entityManager.clear();
        assertThat(repository.findById(produto.getId()).orElseThrow().getGtin()).isEqualTo("12345678");
    }

    @Test
    void permiteMesmoCodigoEGtinEmLojasDiferentes() {
        repository.saveAndFlush(produto(criarLoja("11111111000111"), "A", "12345678"));
        repository.saveAndFlush(produto(criarLoja("22222222000122"), "A", "12345678"));
    }

    @Test
    void impedeCodigoDuplicadoNaMesmaLoja() {
        Long lojaId = criarLoja("11111111000111");
        repository.saveAndFlush(produto(lojaId, "A", null));
        assertThatThrownBy(() -> repository.saveAndFlush(produto(lojaId, "A", null)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void impedeGtinDuplicadoNaMesmaLoja() {
        Long lojaId = criarLoja("11111111000111");
        repository.saveAndFlush(produto(lojaId, "A", "12345678"));
        assertThatThrownBy(() -> repository.saveAndFlush(produto(lojaId, "B", "12345678")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoRejeitaUnidadeInvalida() {
        Long lojaId = criarLoja("11111111000111");
        assertThatThrownBy(() -> inserirViaSql(lojaId, "INVALIDA"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("ck_produtos_unidade");
    }

    @Test
    void bancoRejeitaLojaInexistente() {
        assertThatThrownBy(() -> inserirViaSql(-1L, "UN"))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("fk_produtos_loja");
    }

    @Test
    void bancoAceitaTodasUnidadesDoEnumEAplicaDefaultAtivo() {
        Long lojaId = criarLoja("11111111000111");
        for (UnidadeComercial unidade : UnidadeComercial.values()) {
            inserirViaSql(lojaId, unidade.name());
        }
        assertThat(jdbc.queryForObject("SELECT count(*) FROM produtos WHERE loja_id = ? AND ativo = TRUE",
                Long.class, lojaId)).isEqualTo((long) UnidadeComercial.values().length);
    }

    private void inserirViaSql(Long lojaId, String unidade) {
        jdbc.update("INSERT INTO produtos (codigo_interno, descricao, unidade, ncm, loja_id) VALUES (?, 'Produto', ?, '12345678', ?)",
                unidade, unidade, lojaId);
    }

    private Produtos produto(Long lojaId, String codigo, String gtin) {
        return Produtos.builder().codigoInterno(codigo).descricao("Produto")
                .gtin(gtin).unidade(UnidadeComercial.UN).ncm("12345678")
                .loja(entityManager.getReference(Loja.class, lojaId)).build();
    }

    private Long criarLoja(String cnpj) {
        Long revendaId = jdbc.queryForObject("INSERT INTO revenda (nome, email_contato, cnpj) VALUES ('Revenda', ?, ?) RETURNING id",
                Long.class, cnpj + "@teste.local", cnpj);
        Long clienteId = jdbc.queryForObject("INSERT INTO cliente (nome, revenda_id) VALUES ('Cliente', ?) RETURNING id",
                Long.class, revendaId);
        return jdbc.queryForObject("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, cliente_id) "
                + "VALUES ('Loja', 'Loja', 'Loja', ?, '123', 'SIMPLES_NACIONAL', ?) RETURNING id", Long.class, cnpj, clienteId);
    }
}
