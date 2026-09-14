package farcic.dev.erp_gestao.user.dto.response;

public record UsuarioClienteResponse(

        Long id,
        Long usuarioId,
        String keycloakSub,
        Long clienteId,
        Boolean ativo


) {
}
