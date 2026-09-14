package farcic.dev.erp_gestao.cliente.mapper;

import farcic.dev.erp_gestao.cliente.dto.request.ClienteRequest;
import farcic.dev.erp_gestao.cliente.dto.response.ClienteResponse;
import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request, Revenda revenda){
        return Cliente.builder()
                .nome(request.nome())
                .emailContato(request.emailContato())
                .telefone(request.telefone())
                .revenda(revenda)
                .build();
    }

    public ClienteResponse toResponse(Cliente cliente){
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .emailContato(cliente.getEmailContato())
                .telefone(cliente.getTelefone())
                .ativo(cliente.getAtivo())
                .revendaId(cliente.getRevenda().getId())
                .build();
    }

}
