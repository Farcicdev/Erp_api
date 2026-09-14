package farcic.dev.erp_gestao.loja.dto.response;

import farcic.dev.erp_gestao.loja.entity.RegimeTributario;
import lombok.Builder;

@Builder
public record LojaResponse(
        Long id,
        String nome,
        String nomeFantasia,
        String razaoSocial,
        String cnpj,
        String inscricaoEstadual,
        RegimeTributario regimeTributario,
        Boolean ativo,
        Long clienteId

) {
}
