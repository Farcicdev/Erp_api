package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioRevendaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioRevendaResponse;
import farcic.dev.erp_gestao.user.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/{revendaId}/usuarios")
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRevendaResponse criarUsuario(@PathVariable Long revendaId,@Valid @RequestBody VincularUsuarioRevendaRequest request) {
        return usuarioService.vincular(revendaId, request);
    }

}
