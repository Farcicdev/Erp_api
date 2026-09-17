package farcic.dev.erp_gestao.produto.mapper;

import farcic.dev.erp_gestao.produto.dto.request.ProdutosRequest;
import farcic.dev.erp_gestao.produto.dto.respose.ProdutosResponse;
import farcic.dev.erp_gestao.produto.entity.Produtos;
import farcic.dev.erp_gestao.loja.entity.Loja;
import org.springframework.stereotype.Component;

@Component
public class ProdutosMapper {

    public Produtos toEntity (ProdutosRequest request, Loja loja){
        return Produtos.builder()
                .codigoInterno(request.codigoInterno())
                .descricao(request.descricao())
                .gtin(request.gtin())
                .unidade(request.unidade())
                .ncm(request.ncm())
                .cest(request.cest())
                .loja(loja)
                .build();
    }

    public ProdutosResponse toResponse(Produtos entity){
        return ProdutosResponse.builder()
                .id(entity.getId())
                .codigoInterno(entity.getCodigoInterno())
                .descricao(entity.getDescricao())
                .gtin(entity.getGtin())
                .unidade(entity.getUnidade())
                .ncm(entity.getNcm())
                .cest(entity.getCest())
                .ativo(entity.getAtivo())
                .lojaId(entity.getLoja().getId())
                .build();
    }

}
