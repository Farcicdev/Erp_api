package farcic.dev.erp_gestao.empresa.service;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.request.EmpresaStatusRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.mapper.EmpresaMapper;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import farcic.dev.erp_gestao.shared.exeception.AcessoRevendaNegadoException;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final RevendaRepository revendaRepository;
    private final UsuarioRevendaRepository usuarioRevendaRepository;
    private final EmpresaMapper empresaMapper;
    private final AcessoRevendaService acessoRevendaService;

    //criar
    @Transactional
    public EmpresaResponse criarEmpresa(String keycloakSub, Long revendaId, EmpresaRequest request){

        validarAcesso(keycloakSub, revendaId);

        Revenda revenda = revendaRepository.findById(revendaId).orElseThrow(
                ()-> new RevendaNotFoundException("Revenda nao encontrada")
        );

        Empresa entity = empresaMapper.toEntity(request, revenda);

        Empresa save = empresaRepository.save(entity);

        return empresaMapper.toResponse(save);
    }

    @Transactional(readOnly = true)
    public Page<EmpresaResponse> listar(String keycloakSub, Long revendaId, Pageable pageable){

        validarAcesso(keycloakSub, revendaId);

        return empresaRepository.findAllByRevendaId(revendaId, pageable)
                .map(empresaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long revendaId, String keycloakSub,Long empresaId){
        validarAcesso(keycloakSub, revendaId);

        Empresa empresa = empresaRepository.findByIdAndRevendaId(empresaId, revendaId).orElseThrow(
                ()-> new RuntimeException("empresa nao encontrada")
        );

        return empresaMapper.toResponse(empresa);
    }
    @Transactional
    public EmpresaResponse mudarStatus(Long revendaId, String keycloackSub, Long empresaId, Boolean ativo){
        validarAcesso(keycloackSub, revendaId);

        Empresa empresa = empresaRepository.findByIdAndRevendaId(empresaId, revendaId).orElseThrow(
                ()-> new RuntimeException("empresa nao encontrada")
        );

        empresa.setAtivo(ativo);

        return empresaMapper.toResponse(empresa);
    }

    private void validarAcesso(String keycloakSub, Long revendaId){
        boolean possuiAcesso = usuarioRevendaRepository.existsByUsuario_KeycloakSubAndRevenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(
                keycloakSub,
                revendaId
        );

        if (!possuiAcesso){
            throw new AcessoRevendaNegadoException("Usuario não possui acesso a esta revenda");
        }
    }

}
