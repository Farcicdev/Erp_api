package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.dto.request.ProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.mapper.ProdutoMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.produto.entity.Produto;
import farcic.dev.erp_gestao.shared.exeception.ProdutoJaCadastradoException;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarProdutoService {

    private final ProdutoRepository produtoRepository;
    private final AcessoLojaService acessoLojaService;
    private final ProdutoMapper produtoMapper;

    @Transactional
    public ProdutoResponse cadastrarProdutos(ProdutoRequest produtoRequest, Long lojaId, String keycloakSub) {
        Loja loja = acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        if (produtoRepository.existsByLoja_IdAndCodigoInterno(loja.getId(), produtoRequest.codigoInterno())) {
            throw new ProdutoJaCadastradoException("Código interno já cadastrado nesta loja");
        }
        if (produtoRequest.gtin() != null
                && produtoRepository.existsByLoja_IdAndGtin(loja.getId(), produtoRequest.gtin())) {
            throw new ProdutoJaCadastradoException("GTIN já cadastrado nesta loja");
        }

        Produto produto = produtoMapper.toEntity(produtoRequest, loja);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

}
