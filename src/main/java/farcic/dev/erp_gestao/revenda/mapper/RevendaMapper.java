package farcic.dev.erp_gestao.revenda.mapper;

import farcic.dev.erp_gestao.revenda.dto.request.CriarRevendaRequest;
import farcic.dev.erp_gestao.revenda.dto.response.CriarRevendaResponse;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import org.springframework.stereotype.Component;

@Component
public class RevendaMapper {

    public Revenda toEntity(CriarRevendaRequest revendaDTO) {
        return Revenda.builder()
                .nome(revendaDTO.nome())
                .emailContato(revendaDTO.emailContato())
                .cnpj(revendaDTO.cnpj())
                .build();
    }

    public CriarRevendaResponse toResponse(Revenda entity) {
        return CriarRevendaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .emailContato(entity.getEmailContato())
                .cnpj(entity.getCnpj())
                .ativo(entity.getAtivo())
                .build();
    }

}
