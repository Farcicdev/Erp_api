package farcic.dev.erp_gestao.loja.mapper;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import org.springframework.stereotype.Component;

@Component
public class LojaMapper {
    public Loja toEntity(LojaRequest request, Cliente cliente) {
        return Loja.builder()
                .nome(request.nome())
                .nomeFantasia(request.nomeFantasia())
                .razaoSocial(request.razaoSocial())
                .cnpj(request.cnpj())
                .inscricaoEstadual(request.inscricaoEstadual())
                .regimeTributario(request.regimeTributario())
                .cliente(cliente)
                .build();
    }

    public LojaResponse toResponse(Loja loja) {
        return LojaResponse.builder()
                .id(loja.getId())
                .nome(loja.getNome())
                .nomeFantasia(loja.getNomeFantasia())
                .razaoSocial(loja.getRazaoSocial())
                .cnpj(loja.getCnpj())
                .inscricaoEstadual(loja.getInscricaoEstadual())
                .regimeTributario(loja.getRegimeTributario())
                .ativo(loja.getAtivo())
                .clienteId(loja.getCliente().getId())
                .build();
    }
}
