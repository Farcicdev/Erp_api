package farcic.dev.erp_gestao.empresa.dto.request;

import jakarta.validation.constraints.NotNull;

public record EmpresaStatusRequest (

        @NotNull
        Boolean ativo

){
}
