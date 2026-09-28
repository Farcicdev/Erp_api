package farcic.dev.erp_gestao.produto.dto.request;

import farcic.dev.erp_gestao.produto.entity.UnidadeComercial;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlterarProdutoRequest(

        @Size(max = 50)
        String codigoInterno,

        @Size(max = 255)
        String descricao,
        @Pattern(regexp = "(?:[0-9]{8}|[0-9]{12}|[0-9]{13}|[0-9]{14})",
                message = "GTIN deve possuir 8, 12, 13 ou 14 números")
        String gtin,

        UnidadeComercial unidade,

        @Pattern(regexp = "[0-9]{8}", message = "NCM deve possuir exatamente 8 números")
        String ncm,
        @Pattern(regexp = "[0-9]{7}", message = "CEST deve possuir exatamente 7 números")
        String cest

) {
}
