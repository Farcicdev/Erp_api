package farcic.dev.erp_gestao.produto.dto.request;

import farcic.dev.erp_gestao.produto.entity.UnidadeComercial;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProdutosRequest(

        @NotBlank
        @Size(max = 50)
        String codigoInterno,
        @NotBlank
        @Size(max = 255)
        String descricao,
        @Pattern(regexp = "(?:[0-9]{8}|[0-9]{12}|[0-9]{13}|[0-9]{14})",
                message = "GTIN deve possuir 8, 12, 13 ou 14 números")
        String gtin,
        @NotNull
        UnidadeComercial unidade,
        @NotBlank
        @Pattern(regexp = "[0-9]{8}", message = "NCM deve possuir exatamente 8 números")
        String ncm,
        @Pattern(regexp = "[0-9]{7}", message = "CEST deve possuir exatamente 7 números")
        String cest

) {
    public ProdutosRequest {
        codigoInterno = normalizar(codigoInterno);
        descricao = normalizar(descricao);
        gtin = normalizar(gtin);
        ncm = normalizar(ncm);
        cest = normalizar(cest);
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return null;
        }
        String normalizado = valor.strip();
        return normalizado.isEmpty() ? null : normalizado;
    }
}
