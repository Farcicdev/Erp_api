package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.user.dto.response.AcessoClienteResponse;
import farcic.dev.erp_gestao.user.repository.UsuarioClienteRepository;
import lombok.RequiredArgsConstructor;
import farcic.dev.erp_gestao.shared.exeception.AcessoClienteNegadoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessoClienteService {

    private final UsuarioClienteRepository usuarioClienteRepository;

    @Transactional(readOnly = true)
    public List<AcessoClienteResponse> acessoCliente(String keycloakSub){
        return usuarioClienteRepository.findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndCliente_AtivoTrueAndCliente_Revenda_AtivoTrue(
                keycloakSub
        )
                .stream()
                .map(vinculo -> new AcessoClienteResponse(
                        vinculo.getCliente().getId(),
                        vinculo.getCliente().getNome()
                )).toList();
    }


    @Transactional(readOnly = true)
    public void validarAcesso(String keycloakSub, Long revendaId, Long clienteId) {
        if (!usuarioClienteRepository.existsByUsuario_KeycloakSubAndCliente_IdAndCliente_Revenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndCliente_AtivoTrueAndCliente_Revenda_AtivoTrue(
                keycloakSub, clienteId, revendaId)) {
            throw new AcessoClienteNegadoException("Usuário não possui acesso a este cliente");
        }
    }
}
