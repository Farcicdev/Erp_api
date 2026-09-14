package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.cliente.service.ConsultaClienteService;
import farcic.dev.erp_gestao.shared.exeception.UsuarioJaVinculadoException;
import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioClienteRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioClienteResponse;
import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.entity.UsuarioCliente;
import farcic.dev.erp_gestao.user.repository.UsuarioClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioClienteService {

    private final UsuarioClienteRepository usuarioClienteRepository;
    private final AcessoRevendaService acessoRevendaService;
    private final UsuarioCadastroService usuarioCadastroService;
    private final ConsultaClienteService consultaClienteService;

    @Transactional
    public UsuarioClienteResponse vincularCliente(String keycloakSubAuthenticado,Long revendaId, Long clienteId, VincularUsuarioClienteRequest vincularUsuarioClienteRequest) {
        acessoRevendaService.validarAcesso(keycloakSubAuthenticado, revendaId);

        Cliente cliente = consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);

        Usuario usuario = usuarioCadastroService.buscarOuCriar(vincularUsuarioClienteRequest.keycloakSub());
        boolean jaExiste = usuarioClienteRepository.existsByUsuarioIdAndClienteId(
                usuario.getId(),
                cliente.getId()
        );

        if(jaExiste){
            throw new UsuarioJaVinculadoException("Usuário já está vinculado a essa cliente"
            );
        }

        UsuarioCliente vinculo = new UsuarioCliente(usuario, cliente);
        UsuarioCliente vinculoSalvo = usuarioClienteRepository.save(vinculo);

        return new UsuarioClienteResponse(
                vinculoSalvo.getId(),
                usuario.getId(),
                usuario.getKeycloakSub(),
                cliente.getId(),
                vinculoSalvo.getAtivo()
        );
    }

}
