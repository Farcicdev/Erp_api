package farcic.dev.erp_gestao.domain;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EstruturaPersistenciaTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void persisteHierarquiaComSequencesERecuperaRelacionamentos() {
        Loja loja = criarHierarquia();
        Long id = loja.getId();
        entityManager.clear();

        Loja recuperada = entityManager.find(Loja.class, id);
        Empresa empresa = recuperada.getEmpresa();
        assertThat(empresa.getLojas()).containsExactly(recuperada);
        assertThat(empresa.getRevenda().getEmpresas()).containsExactly(empresa);
        assertThat(recuperada.getAtivo()).isTrue();
    }

    @Test
    void bancoImpedeEmpresaSemRevenda() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO empresa (nome) VALUES ('Sem revenda')"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeLojaSemEmpresa() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario) "
                + "VALUES ('Sem empresa', 'Loja', 'Loja Ltda', '12345678000100', '123456789', 'SIMPLES_NACIONAL')"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeReferenciaInexistente() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO empresa (nome, revenda_id) VALUES ('Inválida', -1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeLojaComEmpresaInexistente() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, empresa_id) "
                + "VALUES ('Inválida', 'Loja', 'Loja Ltda', '12345678000100', '123456789', 'SIMPLES_NACIONAL', -1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void excluirRevendaNaoApagaEmpresasEmCascata() {
        Loja loja = criarHierarquia();
        Long revendaId = loja.getEmpresa().getRevenda().getId();
        entityManager.clear();
        entityManager.remove(entityManager.find(Revenda.class, revendaId));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("fk_empresa_revenda");
    }

    @Test
    void excluirEmpresaNaoApagaLojasEmCascata() {
        Loja loja = criarHierarquia();
        Long empresaId = loja.getEmpresa().getId();
        entityManager.clear();
        entityManager.remove(entityManager.find(Empresa.class, empresaId));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("fk_loja_empresa");
    }

    private Loja criarHierarquia() {
        Revenda revenda = Revenda.builder().nome("Revenda de teste")
                .emailContato("estrutura@example.test").cnpj("12345678000100").build();
        Empresa empresa = Empresa.builder().nome("Empresa de teste").revenda(revenda).build();
        Loja loja = Loja.builder().nome("Loja de teste").nomeFantasia("Loja de teste")
                .razaoSocial("Loja de teste Ltda").cnpj("12345678000100")
                .inscricaoEstadual("123456789").regimeTributario(RegimeTributario.SIMPLES_NACIONAL)
                .empresa(empresa).build();
        entityManager.persist(revenda);
        entityManager.persist(empresa);
        entityManager.persist(loja);
        entityManager.flush();
        return loja;
    }
}
