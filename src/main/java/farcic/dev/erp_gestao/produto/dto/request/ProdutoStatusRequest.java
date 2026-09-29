package farcic.dev.erp_gestao.produto.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProdutoStatusRequest(@NotNull Boolean ativo) {
}
