package farcic.dev.erp_gestao.produto.dto.respose;

import farcic.dev.erp_gestao.produto.entity.UnidadeComercial;
import lombok.Builder;

@Builder
public record ProdutoResponse(
        Long id,
        String codigoInterno,
        String descricao,
        String gtin,
        UnidadeComercial unidade,
        String ncm,
        String cest,
        Boolean ativo,
        Long lojaId
) {
}
