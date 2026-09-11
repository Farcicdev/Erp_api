package farcic.dev.erp_gestao.user.dto.response;

public record UsuarioEmpresaResponse(

        Long id,
        Long usuarioId,
        String keycloakSub,
        Long empresaId,
        Boolean ativo


) {
}
