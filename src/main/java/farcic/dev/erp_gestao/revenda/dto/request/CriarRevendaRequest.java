package farcic.dev.erp_gestao.revenda.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarRevendaRequest(

        @NotBlank
        @Size(max = 100)
        String nome,

        @NotBlank
        @Email
        @Size(max = 150)
        String emailContato,

        @NotBlank
        @Pattern(regexp = "\\d{14}")
        String cnpj

) {
}
