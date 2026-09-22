package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.entity.Produto;
import farcic.dev.erp_gestao.produto.mapper.ProdutoMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.shared.exeception.ProdutoInativoException;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarProdutosPorIdService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final AcessoLojaService acessoLojaService;

    @Transactional(readOnly = true)
    public ProdutoResponse buscarProdutosPorId(Long lojaId, Long produtoId, String keycloakSub){

        acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        Produto produtoNaoExiste = produtoRepository.findByIdAndLojaIdAndAtivoTrue(produtoId, lojaId).orElseThrow(
                () -> new ProdutoInativoException("Produto nao encontrado!")
        );

        return produtoMapper.toResponse(produtoNaoExiste);
    }

}
