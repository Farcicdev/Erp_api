package farcic.dev.erp_gestao.loja;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.loja.mapper.LojaMapper;
import farcic.dev.erp_gestao.loja.repository.LojaRepository;
import farcic.dev.erp_gestao.loja.service.CriarLojaService;
import farcic.dev.erp_gestao.shared.exeception.*;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriarLojaServiceTests {
    private final LojaRepository lojas = mock(LojaRepository.class);
    private final EmpresaRepository empresas = mock(EmpresaRepository.class);
    private final UsuarioRevendaRepository acessos = mock(UsuarioRevendaRepository.class);
    private final CriarLojaService service = new CriarLojaService(
            lojas, empresas, new LojaMapper(), new AcessoRevendaService(acessos));
    private final LojaRequest request = new LojaRequest("Loja", "Fantasia", "Razão social",
            "12345678000100", "123", RegimeTributario.SIMPLES_NACIONAL);

    @Test
    void acessoNegadoNaoConsultaEmpresaNemGravaLoja() {
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(AcessoRevendaNegadoException.class);
        verifyNoInteractions(empresas, lojas);
    }

    @Test
    void empresaForaDaRevendaNaoPermiteCriacao() {
        permitirAcesso();
        when(empresas.findByIdAndRevendaId(2L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(EmpresaNaoEncontradaException.class);
        verifyNoInteractions(lojas);
    }

    @Test
    void empresaInativaNaoPermiteCriacao() {
        permitirAcesso();
        when(empresas.findByIdAndRevendaId(2L, 1L))
                .thenReturn(Optional.of(Empresa.builder().id(2L).ativo(false).build()));
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(EmpresaInativaException.class);
        verifyNoInteractions(lojas);
    }

    @Test
    void criaLojaAtivaComDadosEVinculoDaEmpresa() {
        permitirAcesso();
        Empresa empresa = Empresa.builder().id(2L).build();
        when(empresas.findByIdAndRevendaId(2L, 1L)).thenReturn(Optional.of(empresa));
        when(lojas.save(any(Loja.class))).thenAnswer(invocation -> {
            Loja loja = invocation.getArgument(0);
            assertThat(loja.getEmpresa()).isSameAs(empresa);
            assertThat(empresa.getLojas()).contains(loja);
            loja.setId(3L);
            return loja;
        });
        var response = service.criar("usuario", 1L, 2L, request);
        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.empresaId()).isEqualTo(2L);
        assertThat(response.ativo()).isTrue();
        assertThat(response).usingRecursiveComparison().comparingOnlyFields(
                "nome", "nomeFantasia", "razaoSocial", "cnpj", "inscricaoEstadual", "regimeTributario")
                .isEqualTo(request);
    }

    private void permitirAcesso() {
        when(acessos.existsByUsuario_KeycloakSubAndRevenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(
                "usuario", 1L)).thenReturn(true);
    }
}
