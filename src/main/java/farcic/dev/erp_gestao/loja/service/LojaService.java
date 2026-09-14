package farcic.dev.erp_gestao.loja.service;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.cliente.service.ConsultaClienteService;
import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.mapper.LojaMapper;
import farcic.dev.erp_gestao.loja.repository.LojaRepository;
import farcic.dev.erp_gestao.shared.exeception.LojaNotFoundException;
import farcic.dev.erp_gestao.shared.exeception.LojaInativaException;
import farcic.dev.erp_gestao.shared.exeception.LojaCnpjJaCadastradoException;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import farcic.dev.erp_gestao.user.service.AcessoOrganizacionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LojaService {
    private final LojaRepository lojaRepository;
    private final ConsultaClienteService consultaClienteService;
    private final LojaMapper lojaMapper;
    private final AcessoRevendaService acessoRevendaService;
    private final AcessoOrganizacionalService acessoOrganizacionalService;

    @Transactional
    public LojaResponse criar(String keycloakSub, Long revendaId, Long clienteId, LojaRequest request) {
        acessoRevendaService.validarAcesso(keycloakSub, revendaId);
        Cliente cliente = consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);
        if (lojaRepository.existsByCnpj(request.cnpj())) {
            throw new LojaCnpjJaCadastradoException("CNPJ já cadastrado em outra loja");
        }
        return lojaMapper.toResponse(lojaRepository.save(lojaMapper.toEntity(request, cliente)));
    }

    @Transactional(readOnly = true)
    public Page<LojaResponse> listar(String sub, Long revendaId, Long clienteId, Pageable pageable) {
        acessoOrganizacionalService.validarLeitura(sub, revendaId, clienteId);
        consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);
        return lojaRepository.findAllByCliente_IdAndCliente_Revenda_IdAndAtivoTrue(clienteId, revendaId, pageable)
                .map(lojaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LojaResponse buscarPorId(String sub, Long revendaId, Long clienteId, Long lojaId) {
        acessoOrganizacionalService.validarLeitura(sub, revendaId, clienteId);
        consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);
        Loja loja = buscarNaHierarquia(lojaId, clienteId, revendaId);
        if (!Boolean.TRUE.equals(loja.getAtivo())) {
            throw new LojaInativaException("Loja inativa");
        }
        return lojaMapper.toResponse(loja);
    }

    @Transactional
    public LojaResponse mudarStatus(String sub, Long revendaId, Long clienteId, Long lojaId, Boolean ativo) {
        acessoRevendaService.validarAcesso(sub, revendaId);
        consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);
        // O próprio registro pode estar inativo para permitir sua reativação.
        Loja loja = buscarNaHierarquia(lojaId, clienteId, revendaId);
        loja.setAtivo(ativo);
        return lojaMapper.toResponse(loja);
    }

    @Transactional(readOnly = true)
    public List<LojaResponse> buscarLojasAcessiveis(String sub) {
        return lojaRepository.buscarLojasAcessiveis(sub).stream().map(lojaMapper::toResponse).toList();
    }

    private Loja buscarNaHierarquia(Long lojaId, Long clienteId, Long revendaId) {
        return lojaRepository.findByIdAndCliente_IdAndCliente_Revenda_Id(lojaId, clienteId, revendaId)
                .orElseThrow(() -> new LojaNotFoundException("Loja não encontrada neste cliente e revenda"));
    }
}
