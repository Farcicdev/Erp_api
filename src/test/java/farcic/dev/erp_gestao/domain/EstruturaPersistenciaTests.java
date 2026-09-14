package farcic.dev.erp_gestao.domain;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
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

@org.springframework.test.context.ActiveProfiles("test")
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
        Cliente cliente = recuperada.getCliente();
        assertThat(cliente.getLojas()).containsExactly(recuperada);
        assertThat(cliente.getRevenda().getClientes()).containsExactly(cliente);
        assertThat(recuperada.getAtivo()).isTrue();
    }

    @Test
    void bancoImpedeClienteSemRevenda() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO cliente (nome) VALUES ('Sem revenda')"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeLojaSemCliente() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario) "
                + "VALUES ('Sem cliente', 'Loja', 'Loja Ltda', '12345678000100', '123456789', 'SIMPLES_NACIONAL')"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeReferenciaInexistente() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO cliente (nome, revenda_id) VALUES ('Inválida', -1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void bancoImpedeLojaComClienteInexistente() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO loja (nome, nome_fantasia, razao_social, cnpj, inscricao_estadual, regime_tributario, cliente_id) "
                + "VALUES ('Inválida', 'Loja', 'Loja Ltda', '12345678000100', '123456789', 'SIMPLES_NACIONAL', -1)"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void excluirRevendaNaoApagaClientesEmCascata() {
        Loja loja = criarHierarquia();
        Long revendaId = loja.getCliente().getRevenda().getId();
        entityManager.clear();
        entityManager.remove(entityManager.find(Revenda.class, revendaId));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("fk_cliente_revenda");
    }

    @Test
    void excluirClienteNaoApagaLojasEmCascata() {
        Loja loja = criarHierarquia();
        Long clienteId = loja.getCliente().getId();
        entityManager.clear();
        entityManager.remove(entityManager.find(Cliente.class, clienteId));
        assertThatThrownBy(() -> entityManager.flush()).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("fk_loja_cliente");
    }

    private Loja criarHierarquia() {
        Revenda revenda = Revenda.builder().nome("Revenda de teste")
                .emailContato("estrutura@example.test").cnpj("12345678000100").build();
        Cliente cliente = Cliente.builder().nome("Cliente de teste").revenda(revenda).build();
        Loja loja = Loja.builder().nome("Loja de teste").nomeFantasia("Loja de teste")
                .razaoSocial("Loja de teste Ltda").cnpj("12345678000100")
                .inscricaoEstadual("123456789").regimeTributario(RegimeTributario.SIMPLES_NACIONAL)
                .cliente(cliente).build();
        entityManager.persist(revenda);
        entityManager.persist(cliente);
        entityManager.persist(loja);
        entityManager.flush();
        return loja;
    }
}
