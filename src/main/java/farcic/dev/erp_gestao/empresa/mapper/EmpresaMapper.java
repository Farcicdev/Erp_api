package farcic.dev.erp_gestao.empresa.mapper;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(EmpresaRequest request, Revenda revenda){
        return Empresa.builder()
                .nome(request.nome())
                .emailContato(request.emailContato())
                .telefone(request.telefone())
                .revenda(revenda)
                .build();
    }

    public EmpresaResponse toResponse(Empresa empresa){
        return EmpresaResponse.builder()
                .id(empresa.getId())
                .nome(empresa.getNome())
                .emailContato(empresa.getEmailContato())
                .telefone(empresa.getTelefone())
                .ativo(empresa.getAtivo())
                .revendaId(empresa.getRevenda().getId())
                .build();
    }

}
