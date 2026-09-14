package farcic.dev.erp_gestao.loja.service;

import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import farcic.dev.erp_gestao.loja.repository.LojaRepository;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.loja.mapper.LojaMapper;
import farcic.dev.erp_gestao.shared.exeception.EmpresaNaoEncontradaException;
import farcic.dev.erp_gestao.shared.exeception.EmpresaInativaException;
import org.springframework.transaction.annotation.Transactional;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarLojaService {

    private final LojaRepository lojaRepository;
    private final EmpresaRepository empresaRepository;
    private final LojaMapper lojaMapper;
    private final AcessoRevendaService acessoRevendaService;

    @Transactional
    public LojaResponse criar(String keycloakSub, Long revendaId, Long empresaId, LojaRequest lojaRequest){
        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        Empresa empresa = empresaRepository.findByIdAndRevendaId(empresaId, revendaId)
                .orElseThrow(() -> new EmpresaNaoEncontradaException("Empresa não encontrada nesta revenda"));

        if (Boolean.FALSE.equals(empresa.getAtivo())) {
            throw new EmpresaInativaException("Não é possível criar loja em empresa inativa");
        }

        return lojaMapper.toResponse(lojaRepository.save(lojaMapper.toEntity(lojaRequest, empresa)));
    }

}
