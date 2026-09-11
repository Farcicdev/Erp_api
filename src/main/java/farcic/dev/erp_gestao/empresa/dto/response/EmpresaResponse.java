package farcic.dev.erp_gestao.empresa.dto.response;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
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

/*    public static EmpresaResponse from(Empresa empresa){
        return new EmpresaResponse(
                empresa.getId(),
                empresa.getNome(),
                empresa.getEmailContato(),
                empresa.getTelefone(),
                empresa.getAtivo(),
                empresa.getRevenda().getId()
        );
    }*/
}
