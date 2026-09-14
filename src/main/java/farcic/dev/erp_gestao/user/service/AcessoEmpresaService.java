package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.user.dto.response.AcessoEmpresaResponse;
import farcic.dev.erp_gestao.user.repository.UsuarioEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AcessoEmpresaService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;

    @Transactional(readOnly = true)
    public List<AcessoEmpresaResponse> acessoEmpresa(String keycloakSub){
        return usuarioEmpresaRepository.findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndEmpresa_AtivoTrue(
                keycloakSub
        )
                .stream()
                .map(vinculo -> new AcessoEmpresaResponse(
                        vinculo.getId(),
                        vinculo.getEmpresa().getNome()
                )).toList();
    }


}
