package farcic.dev.erp_gestao.produto.mapper;

import farcic.dev.erp_gestao.produto.dto.request.ProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.entity.Produto;
import farcic.dev.erp_gestao.loja.entity.Loja;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public Produto toEntity (ProdutoRequest request, Loja loja){
        return Produto.builder()
                .codigoInterno(request.codigoInterno())
                .descricao(request.descricao())
                .gtin(request.gtin())
                .unidade(request.unidade())
                .ncm(request.ncm())
                .cest(request.cest())
                .loja(loja)
                .build();
    }

    public ProdutoResponse toResponse(Produto entity){
        return ProdutoResponse.builder()
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
