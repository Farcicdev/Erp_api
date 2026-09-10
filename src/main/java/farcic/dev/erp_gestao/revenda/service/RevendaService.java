package farcic.dev.erp_gestao.revenda.service;

import farcic.dev.erp_gestao.revenda.dto.request.CriarRevendaRequest;
import farcic.dev.erp_gestao.revenda.dto.response.CriarRevendaResponse;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.mapper.RevendaMapper;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RevendaService {

    private final RevendaRepository revendaRepository;
    private final RevendaMapper criarRevendaMapper;

    public CriarRevendaResponse criarRevenda(CriarRevendaRequest request) {
        Revenda entity = criarRevendaMapper.toEntity(request);
        Revenda savedEntity = revendaRepository.save(entity);
        return criarRevendaMapper.toResponse(savedEntity);
    }

    public Page<CriarRevendaResponse> listarRevendas(Pageable pageable) {
        return revendaRepository.findAll(pageable).map(criarRevendaMapper::toResponse);
    }

    public CriarRevendaResponse revendaPorId(Long id) {
        Revenda revenda = buscarRevendaPorId(id);
        return criarRevendaMapper.toResponse(revenda);
    }

    @Transactional
    public void inativarRevenda(Long id) {
        Revenda revenda = buscarRevendaPorId(id);
        revenda.setAtivo(false);
    }

    private Revenda buscarRevendaPorId(Long id) {
        return revendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Revenda não encontrada com o ID: " + id));
    }
}
