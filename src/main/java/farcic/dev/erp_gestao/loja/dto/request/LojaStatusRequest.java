package farcic.dev.erp_gestao.loja.dto.request;

import jakarta.validation.constraints.NotNull;

public record LojaStatusRequest(@NotNull Boolean ativo) {
}
