package farcic.dev.erp_gestao.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VincularUsuarioEmpresaRequest(

        @NotBlank
        @Size(max = 255)
        String keycloakSub

) {
}
