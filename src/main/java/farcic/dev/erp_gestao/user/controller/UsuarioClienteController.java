package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioClienteRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioClienteResponse;
import farcic.dev.erp_gestao.user.service.UsuarioClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas/{revendaId}/clientes")
@PreAuthorize("hasRole('ADMIN_REVENDA')")
@RequiredArgsConstructor
public class UsuarioClienteController {

    private final UsuarioClienteService usuarioClienteService;

    @PostMapping("/{clienteId}/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioClienteResponse vincularCliente(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId, @PathVariable Long clienteId, @Valid @RequestBody VincularUsuarioClienteRequest vincularUsuarioClienteRequest){
        return usuarioClienteService.vincularCliente(
                jwt.getSubject(),
                revendaId,
                clienteId,
                vincularUsuarioClienteRequest);
    }

}
