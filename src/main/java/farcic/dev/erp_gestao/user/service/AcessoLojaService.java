package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.repository.LojaRepository;
import farcic.dev.erp_gestao.shared.exeception.LojaInativaException;
import farcic.dev.erp_gestao.shared.exeception.LojaNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcessoLojaService {

    private final LojaRepository lojaRepository;
    private final AcessoClienteService acessoClienteService;

    @Transactional(readOnly = true)
    public Loja buscarLojaAutorizada(String keycloakSub, Long lojaId) {
        Loja loja = lojaRepository.findById(lojaId)
                .orElseThrow(() ->
                        new LojaNotFoundException("Loja não encontrada"));

        Cliente cliente = loja.getCliente();

        acessoClienteService.validarAcesso(
                keycloakSub,
                cliente.getRevenda().getId(),
                cliente.getId()
        );

        if (!Boolean.TRUE.equals(loja.getAtivo())) {
            throw new LojaInativaException("Loja inativa");
        }

        return loja;
    }

}
