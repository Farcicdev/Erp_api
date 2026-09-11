package farcic.dev.erp_gestao.empresa.controller;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping("/{id}/empresas")
    @PreAuthorize("hasRole('REVENDA_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse criarEmpresa (@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId, @Valid @RequestBody EmpresaRequest empresaRequest){
        String keycloakSub = jwt.getSubject();

        return empresaService.criarEmpresa(keycloakSub, revendaId, empresaRequest);
    }

}
