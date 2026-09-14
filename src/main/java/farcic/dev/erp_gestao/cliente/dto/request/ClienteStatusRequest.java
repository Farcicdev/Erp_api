package farcic.dev.erp_gestao.cliente.dto.request;

import jakarta.validation.constraints.NotNull;

public record ClienteStatusRequest (

        @NotNull
        Boolean ativo

){
}
