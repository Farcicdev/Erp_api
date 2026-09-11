package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.response.AcessoRevendaResponse;
import farcic.dev.erp_gestao.user.service.AcessoRevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class AcessoAtualRevenda {

    private final AcessoRevendaService acessoRevendaService;

    @GetMapping("/revendas")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN_REVENDA')")
    public List<AcessoRevendaResponse> buscarRevendas(@AuthenticationPrincipal Jwt jwt){
        return acessoRevendaService.buscarRevendas(jwt.getSubject());
    }
}
