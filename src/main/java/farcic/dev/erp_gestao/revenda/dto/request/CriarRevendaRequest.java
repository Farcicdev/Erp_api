package farcic.dev.erp_gestao.revenda.dto.request;

public record CriarRevendaRequest(

    String nome,
    String emailContato,
    String cnpj,
    Boolean ativo

) {
}
