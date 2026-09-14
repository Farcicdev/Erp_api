package farcic.dev.erp_gestao.empresa.service;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.mapper.EmpresaMapper;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import farcic.dev.erp_gestao.shared.exeception.EmpresaNaoEncontradaException;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
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
    private final EmpresaMapper empresaMapper;
    private final AcessoRevendaService acessoRevendaService;

    //criar
    @Transactional
    public EmpresaResponse criarEmpresa(String keycloakSub, Long revendaId, EmpresaRequest request){

        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        Revenda revenda = revendaRepository.findById(revendaId).orElseThrow(
                ()-> new RevendaNotFoundException("Revenda nao encontrada")
        );

        Empresa entity = empresaMapper.toEntity(request, revenda);

        Empresa save = empresaRepository.save(entity);

        return empresaMapper.toResponse(save);
    }

    @Transactional(readOnly = true)
    public Page<EmpresaResponse> listar(String keycloakSub, Long revendaId, Pageable pageable){

        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        return empresaRepository.findAllByRevendaId(revendaId, pageable)
                .map(empresaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse buscarPorId(Long revendaId, String keycloakSub,Long empresaId){
        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        Empresa empresa = buscarEmpresaDaRevenda(empresaId, revendaId);

        return empresaMapper.toResponse(empresa);
    }
    @Transactional
    public EmpresaResponse mudarStatus(Long revendaId, String keycloackSub, Long empresaId, Boolean ativo){
        acessoRevendaService.validarAcesso(keycloackSub, revendaId);

        Empresa empresa = buscarEmpresaDaRevenda(empresaId, revendaId);

        empresa.setAtivo(ativo);

        return empresaMapper.toResponse(empresa);
    }

    private Empresa buscarEmpresaDaRevenda(Long empresaId, Long revendaId) {
        return empresaRepository.findByIdAndRevendaId(empresaId, revendaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada nesta revenda"));
    }
}
