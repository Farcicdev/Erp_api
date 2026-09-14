package farcic.dev.erp_gestao.cliente.service;

import farcic.dev.erp_gestao.cliente.dto.request.ClienteRequest;
import farcic.dev.erp_gestao.cliente.dto.response.ClienteResponse;
import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.cliente.mapper.ClienteMapper;
import farcic.dev.erp_gestao.cliente.repository.ClienteRepository;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import farcic.dev.erp_gestao.user.service.AcessoOrganizacionalService;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RevendaRepository revendaRepository;
    private final ClienteMapper clienteMapper;
    private final AcessoRevendaService acessoRevendaService;
    private final ConsultaClienteService consultaClienteService;
    private final AcessoOrganizacionalService acessoOrganizacionalService;

    //criar
    @Transactional
    public ClienteResponse criarCliente(String keycloakSub, Long revendaId, ClienteRequest request){

        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        Revenda revenda = revendaRepository.findById(revendaId).orElseThrow(
                ()-> new RevendaNotFoundException("Revenda nao encontrada")
        );

        Cliente entity = clienteMapper.toEntity(request, revenda);

        Cliente save = clienteRepository.save(entity);

        return clienteMapper.toResponse(save);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(String keycloakSub, Long revendaId, Pageable pageable){

        acessoRevendaService.validarAcesso(keycloakSub, revendaId);

        return clienteRepository.findAllByRevendaIdAndAtivoTrue(revendaId, pageable)
                .map(clienteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long revendaId, String keycloakSub,Long clienteId){
        acessoOrganizacionalService.validarLeitura(keycloakSub, revendaId, clienteId);

        Cliente cliente = consultaClienteService.buscarEntidadeAtivaNaRevenda(clienteId, revendaId);

        return clienteMapper.toResponse(cliente);
    }
    @Transactional
    public ClienteResponse mudarStatus(Long revendaId, String keycloackSub, Long clienteId, Boolean ativo){
        acessoRevendaService.validarAcesso(keycloackSub, revendaId);

        Cliente cliente = consultaClienteService.buscarEntidadeNaRevenda(clienteId, revendaId);

        cliente.setAtivo(ativo);

        return clienteMapper.toResponse(cliente);
    }

}
