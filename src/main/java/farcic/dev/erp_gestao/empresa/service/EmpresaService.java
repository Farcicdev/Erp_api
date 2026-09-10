package farcic.dev.erp_gestao.empresa.service;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.mapper.EmpresaMapper;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final RevendaRepository revendaRepository;
    private final UsuarioRevendaRepository usuarioRevendaRepository;
    private final EmpresaMapper empresaMapper;

    //criar
    @Transactional
    public EmpresaResponse criarEmpresa(String keycloak, Long revendaId, EmpresaRequest request){
        boolean possuiAcesso = usuarioRevendaRepository.existsByUsuario_KeycloakSubAndRevenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(keycloak, revendaId);

        if(!possuiAcesso){
            throw new RuntimeException("Usuario nao possui acesso a esta revenda");
        }
        Revenda revenda = revendaRepository.findById(revendaId).orElseThrow(
                ()-> new RevendaNotFoundException("Revenda nao encontrada")
        );

        Empresa entity = empresaMapper.toEntity(request, revenda);

        Empresa save = empresaRepository.save(entity);

        return empresaMapper.toResponse(save);
    }

}
