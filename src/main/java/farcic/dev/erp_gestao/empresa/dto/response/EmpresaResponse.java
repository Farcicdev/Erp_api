package farcic.dev.erp_gestao.empresa.dto.response;

import lombok.Builder;

@Builder
public record EmpresaResponse(

        Long id,
        String nome,
        String emailContato,
        String telefone,
        Boolean ativo,
        Long revendaId

) {
}
