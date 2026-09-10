package farcic.dev.erp_gestao.user.dto.response;

public record UsuarioRevendaResponse(

        Long id,
        Long usuarioId,
        String keycloakSub,
        Long revendaId,
        Boolean ativo

) {
}
