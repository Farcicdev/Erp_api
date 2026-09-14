package farcic.dev.erp_gestao.domain;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstruturaOrganizacionalTests {

    @Test
    void buildersPreservamDefaultsEVinculos() {
        Revenda revenda = Revenda.builder().build();
        Cliente cliente = Cliente.builder().revenda(revenda).build();
        Loja loja = Loja.builder().cliente(cliente).build();

        assertThat(revenda.getAtivo()).isTrue();
        assertThat(cliente.getAtivo()).isTrue();
        assertThat(loja.getAtivo()).isTrue();
        assertThat(revenda.getClientes()).containsExactly(cliente);
        assertThat(cliente.getLojas()).containsExactly(loja);
        assertThat(Revenda.builder().ativo(null).build().getAtivo()).isTrue();
        assertThat(Cliente.builder().ativo(false).build().getAtivo()).isFalse();
        assertThat(new Revenda().getClientes()).isEmpty();
        assertThat(new Cliente().getLojas()).isEmpty();
    }

    @Test
    void trocaDeVinculoAtualizaOsDoisLadosSemDuplicar() {
        Revenda anterior = new Revenda();
        Revenda atual = new Revenda();
        Cliente cliente = Cliente.builder().revenda(anterior).build();
        atual.adicionarCliente(cliente);
        atual.adicionarCliente(cliente);

        assertThat(anterior.getClientes()).isEmpty();
        assertThat(atual.getClientes()).containsExactly(cliente);
        assertThat(cliente.getRevenda()).isSameAs(atual);

        Cliente outra = new Cliente();
        Loja loja = Loja.builder().cliente(cliente).build();
        loja.setCliente(outra);
        outra.adicionarLoja(loja);

        assertThat(cliente.getLojas()).isEmpty();
        assertThat(outra.getLojas()).containsExactly(loja);
        assertThat(loja.getCliente()).isSameAs(outra);
        outra.removerLoja(loja);
        assertThat(outra.getLojas()).isEmpty();
        assertThat(loja.getCliente()).isNull();
        atual.removerCliente(cliente);
        assertThat(atual.getClientes()).isEmpty();
        assertThat(cliente.getRevenda()).isNull();
    }

    @Test
    void colecoesNaoPermitemAlterarVinculosPorForaDosMetodos() {
        assertThatThrownBy(() -> new Revenda().getClientes().add(new Cliente()))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new Cliente().getLojas().add(new Loja()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void serializacaoNaoPercorreRelacionamentos() {
        Revenda revenda = Revenda.builder().nome("Revenda").build();
        Cliente cliente = Cliente.builder().nome("Cliente").revenda(revenda).build();
        Loja loja = Loja.builder().nome("Loja").cliente(cliente).build();
        JsonMapper mapper = JsonMapper.builder().build();

        assertThat(mapper.readTree(mapper.writeValueAsString(revenda)).has("clientes")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(cliente)).has("revenda")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(cliente)).has("lojas")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(loja)).has("cliente")).isFalse();
    }
}
