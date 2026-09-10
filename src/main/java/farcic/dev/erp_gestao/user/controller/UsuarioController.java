package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioRevendaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioRevendaResponse;
import farcic.dev.erp_gestao.user.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/{revendaId}/revendas")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRevendaResponse criarUsuario(@Valid @PathVariable Long revendaId,@RequestBody VincularUsuarioRevendaRequest request) {
        return usuarioService.vincular(revendaId, request);
    }

}
