package farcic.dev.erp_gestao.loja.dto.request;

import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LojaRequest(
        @NotBlank
        @Size(max = 255)
        String nome,
        @NotBlank
        @Size(max = 255)
        String nomeFantasia,
        @NotBlank
        @Size(max = 255)
        String razaoSocial,
        @NotBlank
        @Pattern(regexp = "\\d{14}", message = "CNPJ deve possuir exatamente 14 numeros")
        String cnpj,
        @NotBlank
        @Size(max = 15)
        String inscricaoEstadual,
        @NotNull
        RegimeTributario regimeTributario

) {
}
