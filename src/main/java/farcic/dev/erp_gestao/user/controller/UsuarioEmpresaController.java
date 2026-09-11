package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioEmpresaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioEmpresaResponse;
import farcic.dev.erp_gestao.user.service.UsuarioEmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas/{revendaId}/empresas")
@PreAuthorize("hasRole('ADMIN_REVENDA')")
@RequiredArgsConstructor
public class UsuarioEmpresaController {

    private final UsuarioEmpresaService usuarioEmpresaService;

    @PostMapping("/{empresaId/usuarios}")
    @ResponseStatus(HttpStatus.OK)
    public UsuarioEmpresaResponse vincularEmpresa(@AuthenticationPrincipal String keycloakSubAuthenticado, @PathVariable Long revendaId, @PathVariable Long empresaId, @Valid @RequestBody VincularUsuarioEmpresaRequest vincularUsuarioEmpresaRequest){
        return usuarioEmpresaService.vincularEmpresa(
                keycloakSubAuthenticado,
                revendaId,
                empresaId,
                vincularUsuarioEmpresaRequest);
    }

}
