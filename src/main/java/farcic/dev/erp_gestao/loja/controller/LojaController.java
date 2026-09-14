package farcic.dev.erp_gestao.loja.controller;

import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import jakarta.validation.Valid;
import farcic.dev.erp_gestao.loja.service.CriarLojaService;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas/{revendaId}/empresas/{empresaId}/lojas")
@PreAuthorize("hasRole('ADMIN_REVENDA')")
@RequiredArgsConstructor
public class LojaController {

    private final CriarLojaService criarLojaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LojaResponse criarLoja(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId, @PathVariable Long empresaId, @Valid @RequestBody LojaRequest lojaRequest){
        return criarLojaService.criar(jwt.getSubject(), revendaId, empresaId, lojaRequest);
    }

}
