package farcic.dev.erp_gestao.domain;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
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
        Empresa empresa = Empresa.builder().revenda(revenda).build();
        Loja loja = Loja.builder().empresa(empresa).build();

        assertThat(revenda.getAtivo()).isTrue();
        assertThat(empresa.getAtivo()).isTrue();
        assertThat(loja.getAtivo()).isTrue();
        assertThat(revenda.getEmpresas()).containsExactly(empresa);
        assertThat(empresa.getLojas()).containsExactly(loja);
        assertThat(Revenda.builder().ativo(null).build().getAtivo()).isTrue();
        assertThat(Empresa.builder().ativo(false).build().getAtivo()).isFalse();
        assertThat(new Revenda().getEmpresas()).isEmpty();
        assertThat(new Empresa().getLojas()).isEmpty();
    }

    @Test
    void trocaDeVinculoAtualizaOsDoisLadosSemDuplicar() {
        Revenda anterior = new Revenda();
        Revenda atual = new Revenda();
        Empresa empresa = Empresa.builder().revenda(anterior).build();
        atual.adicionarEmpresa(empresa);
        atual.adicionarEmpresa(empresa);

        assertThat(anterior.getEmpresas()).isEmpty();
        assertThat(atual.getEmpresas()).containsExactly(empresa);
        assertThat(empresa.getRevenda()).isSameAs(atual);

        Empresa outra = new Empresa();
        Loja loja = Loja.builder().empresa(empresa).build();
        loja.setEmpresa(outra);
        outra.adicionarLoja(loja);

        assertThat(empresa.getLojas()).isEmpty();
        assertThat(outra.getLojas()).containsExactly(loja);
        assertThat(loja.getEmpresa()).isSameAs(outra);
        outra.removerLoja(loja);
        assertThat(outra.getLojas()).isEmpty();
        assertThat(loja.getEmpresa()).isNull();
        atual.removerEmpresa(empresa);
        assertThat(atual.getEmpresas()).isEmpty();
        assertThat(empresa.getRevenda()).isNull();
    }

    @Test
    void colecoesNaoPermitemAlterarVinculosPorForaDosMetodos() {
        assertThatThrownBy(() -> new Revenda().getEmpresas().add(new Empresa()))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new Empresa().getLojas().add(new Loja()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void serializacaoNaoPercorreRelacionamentos() {
        Revenda revenda = Revenda.builder().nome("Revenda").build();
        Empresa empresa = Empresa.builder().nome("Empresa").revenda(revenda).build();
        Loja loja = Loja.builder().nome("Loja").empresa(empresa).build();
        JsonMapper mapper = JsonMapper.builder().build();

        assertThat(mapper.readTree(mapper.writeValueAsString(revenda)).has("empresas")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(empresa)).has("revenda")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(empresa)).has("lojas")).isFalse();
        assertThat(mapper.readTree(mapper.writeValueAsString(loja)).has("empresa")).isFalse();
    }
}
