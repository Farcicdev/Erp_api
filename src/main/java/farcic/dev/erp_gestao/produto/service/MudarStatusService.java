package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.entity.Produto;
import farcic.dev.erp_gestao.shared.exeception.ProdutoNotFoundException;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MudarStatusService {

    private final ProdutoRepository produtoRepository;
    private final AcessoLojaService acessoLojaService;

    @Transactional
    public void ativarOuInativar(Long lojaId,Long produtoId,String keycloakSub, boolean ativo){
        acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        Produto produto = produtoRepository.findByIdAndLojaId(produtoId, lojaId).orElseThrow(
                () -> new ProdutoNotFoundException("Produto não encontrado")
        );

        produto.setAtivo(ativo);

        produtoRepository.save(produto);
    }
}
