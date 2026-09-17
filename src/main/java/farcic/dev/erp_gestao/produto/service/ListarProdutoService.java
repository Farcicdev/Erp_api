package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.mapper.ProdutoMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListarProdutoService {

    private final ProdutoRepository produtoRepository;
    private final AcessoLojaService acessoLojaService;
    private final ProdutoMapper produtoMapper;

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarProdutos(Pageable pageable, Long lojaId, String keycloakSub){
        acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        return produtoRepository.findAllByLoja_IdAndAtivoTrue(lojaId, pageable)
                .map(produtoMapper::toResponse);
    }

}
