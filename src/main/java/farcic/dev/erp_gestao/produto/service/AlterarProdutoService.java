package farcic.dev.erp_gestao.produto.service;

import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.produto.dto.request.AlterarProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.entity.Produto;
import farcic.dev.erp_gestao.produto.mapper.ProdutoMapper;
import farcic.dev.erp_gestao.produto.repository.ProdutoRepository;
import farcic.dev.erp_gestao.shared.exeception.ProdutoInativoException;
import farcic.dev.erp_gestao.shared.exeception.ProdutoJaCadastradoException;
import farcic.dev.erp_gestao.user.service.AcessoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlterarProdutoService {

    private final ProdutoRepository produtoRepository;
    private final AcessoLojaService acessoLojaService;
    private final ProdutoMapper produtoMapper;

    @Transactional
    public ProdutoResponse alterarProduto(Long lojaId, Long produtoId, String keycloakSub, AlterarProdutoRequest produtoRequest){
        Loja loja = acessoLojaService.buscarLojaAutorizada(keycloakSub, lojaId);

        Produto produto = produtoRepository.findByIdAndLojaIdAndAtivoTrue(produtoId, lojaId).orElseThrow(
                () -> new ProdutoInativoException("Produto nao encontrado!")
        );

        validarAlteracoesUnicas(produto, produtoRequest, loja.getId());

        produtoMapper.atualizar(produto, produtoRequest);

        return produtoMapper.toResponse(produto);
    }


    private void validarAlteracoesUnicas(
            Produto produto,
            AlterarProdutoRequest request,
            Long lojaId
    ) {
        if (request.codigoInterno() != null
                && !request.codigoInterno().equals(produto.getCodigoInterno())
                && produtoRepository.existsByLoja_IdAndCodigoInterno(
                lojaId, request.codigoInterno())) {
            throw new ProdutoJaCadastradoException(
                    "Código interno já cadastrado nesta loja"
            );
        }

        if (request.gtin() != null
                && !request.gtin().equals(produto.getGtin())
                && produtoRepository.existsByLoja_IdAndGtin(
                lojaId, request.gtin())) {
            throw new ProdutoJaCadastradoException(
                    "GTIN já cadastrado nesta loja"
            );
        }
    }
}
