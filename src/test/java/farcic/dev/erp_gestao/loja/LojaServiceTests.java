package farcic.dev.erp_gestao.loja;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.cliente.repository.ClienteRepository;
import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import farcic.dev.erp_gestao.loja.mapper.LojaMapper;
import farcic.dev.erp_gestao.loja.repository.LojaRepository;
import farcic.dev.erp_gestao.loja.service.LojaService;
import farcic.dev.erp_gestao.cliente.service.ConsultaClienteService;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.user.service.AcessoOrganizacionalService;
import farcic.dev.erp_gestao.shared.exeception.*;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LojaServiceTests {
    private final LojaRepository lojas = mock(LojaRepository.class);
    private final ClienteRepository clientes = mock(ClienteRepository.class);
    private final UsuarioRevendaRepository acessos = mock(UsuarioRevendaRepository.class);
    private final LojaService service = new LojaService(
            lojas, new ConsultaClienteService(clientes), new LojaMapper(), new AcessoRevendaService(acessos),
            mock(AcessoOrganizacionalService.class));
    private final LojaRequest request = new LojaRequest("Loja", "Fantasia", "Razão social",
            "12345678000100", "123", RegimeTributario.SIMPLES_NACIONAL);

    @Test
    void acessoNegadoNaoConsultaClienteNemGravaLoja() {
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(AcessoRevendaNegadoException.class);
        verifyNoInteractions(clientes, lojas);
    }

    @Test
    void clienteForaDaRevendaNaoPermiteCriacao() {
        permitirAcesso();
        when(clientes.findByIdAndRevendaId(2L, 1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(ClienteNotFoundException.class);
        verifyNoInteractions(lojas);
    }

    @Test
    void clienteInativoNaoPermiteCriacao() {
        permitirAcesso();
        when(clientes.findByIdAndRevendaId(2L, 1L))
                .thenReturn(Optional.of(Cliente.builder().revenda(Revenda.builder().id(1L).build()).id(2L).ativo(false).build()));
        assertThatThrownBy(() -> service.criar("usuario", 1L, 2L, request))
                .isInstanceOf(ClienteInativoException.class);
        verifyNoInteractions(lojas);
    }

    @Test
    void criaLojaAtivaComDadosEVinculoDoCliente() {
        permitirAcesso();
        Cliente cliente = Cliente.builder().revenda(Revenda.builder().id(1L).build()).id(2L).build();
        when(clientes.findByIdAndRevendaId(2L, 1L)).thenReturn(Optional.of(cliente));
        when(lojas.save(any(Loja.class))).thenAnswer(invocation -> {
            Loja loja = invocation.getArgument(0);
            assertThat(loja.getCliente()).isSameAs(cliente);
            assertThat(cliente.getLojas()).contains(loja);
            loja.setId(3L);
            return loja;
        });
        var response = service.criar("usuario", 1L, 2L, request);
        assertThat(response.id()).isEqualTo(3L);
        assertThat(response.clienteId()).isEqualTo(2L);
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
