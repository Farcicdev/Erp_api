package farcic.dev.erp_gestao.cliente.service;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.cliente.repository.ClienteRepository;
import farcic.dev.erp_gestao.shared.exeception.ClienteNotFoundException;
import farcic.dev.erp_gestao.shared.exeception.ClienteInativoException;
import farcic.dev.erp_gestao.shared.exeception.RevendaInativaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultaClienteService {
    private final ClienteRepository clienteRepository;

    public Cliente buscarEntidadeAtivaNaRevenda(Long clienteId, Long revendaId) {
        Cliente cliente = buscarEntidadeNaRevenda(clienteId, revendaId);
        if (!Boolean.TRUE.equals(cliente.getAtivo())) {
            throw new ClienteInativoException("Cliente inativo");
        }
        return cliente;
    }

    // Permite localizar um cliente inativo exclusivamente para administrar seu status.
    public Cliente buscarEntidadeNaRevenda(Long clienteId, Long revendaId) {
        Cliente cliente = clienteRepository.findByIdAndRevendaId(clienteId, revendaId)
                .orElseThrow(() -> new ClienteNotFoundException("Cliente não encontrado nesta revenda"));
        if (!Boolean.TRUE.equals(cliente.getRevenda().getAtivo())) {
            throw new RevendaInativaException("Revenda inativa");
        }
        return cliente;
    }
}
