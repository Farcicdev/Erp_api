package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.produto.dto.request.ProdutosRequest;
import farcic.dev.erp_gestao.produto.dto.respose.ProdutosResponse;
import farcic.dev.erp_gestao.produto.mapper.ProdutosMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutosRepository;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.produto.entity.Produtos;
import farcic.dev.erp_gestao.shared.exeception.ProdutoJaCadastradoException;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarProdutosService {

    private final ProdutosRepository produtosRepository;
    private final AcessoLojaService acessoLojaService;
    private final ProdutosMapper produtosMapper;

    @Transactional
    public ProdutosResponse cadastrarProdutos(ProdutosRequest produtosRequest, Long lojaId, String keycloakSub) {
        Loja loja = acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        if (produtosRepository.existsByLoja_IdAndCodigoInterno(loja.getId(), produtosRequest.codigoInterno())) {
            throw new ProdutoJaCadastradoException("Código interno já cadastrado nesta loja");
        }
        if (produtosRequest.gtin() != null
                && produtosRepository.existsByLoja_IdAndGtin(loja.getId(), produtosRequest.gtin())) {
            throw new ProdutoJaCadastradoException("GTIN já cadastrado nesta loja");
        }

        Produtos produto = produtosMapper.toEntity(produtosRequest, loja);
        return produtosMapper.toResponse(produtosRepository.save(produto));
    }

}
