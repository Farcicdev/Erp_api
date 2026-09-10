package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.user.dto.response.AcessoRevendaResponse;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessoRevendaService {

    private final UsuarioRevendaRepository usuarioRevendaRepository;

    @Transactional()
    public List<AcessoRevendaResponse> buscarRevendas(String keycloakSub) {
        return usuarioRevendaRepository.findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(keycloakSub)
                .stream()
                .map(acesso -> new AcessoRevendaResponse(
                        acesso.getRevenda().getId(),
                        acesso.getRevenda().getNome()
                ))
                .toList();
    }

}
