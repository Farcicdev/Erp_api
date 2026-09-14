package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import farcic.dev.erp_gestao.loja.service.LojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AcessoAtualLoja {
    private final LojaService lojaService;

    @GetMapping("/me/lojas")
    @PreAuthorize("hasRole('ADMIN_CLIENTE')")
    public List<LojaResponse> buscarLojas(@AuthenticationPrincipal Jwt jwt) {
        return lojaService.buscarLojasAcessiveis(jwt.getSubject());
    }
}
