package farcic.dev.erp_gestao.revenda.dto.response;

import lombok.Builder;

@Builder
public record CriarRevendaResponse(

    Long id,
    String nome,
    String emailContato,
    String cnpj,
    Boolean ativo

) {
}
