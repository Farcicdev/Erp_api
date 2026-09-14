package farcic.dev.erp_gestao.cliente.dto.response;

import lombok.Builder;

@Builder
public record ClienteResponse(

        Long id,
        String nome,
        String emailContato,
        String telefone,
        Boolean ativo,
        Long revendaId
) {
}
