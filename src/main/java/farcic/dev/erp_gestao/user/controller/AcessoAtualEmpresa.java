package farcic.dev.erp_gestao.user.controller;

import farcic.dev.erp_gestao.user.dto.response.AcessoEmpresaResponse;
import farcic.dev.erp_gestao.user.service.AcessoEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class AcessoAtualEmpresa {

    private final AcessoEmpresaService acessoEmpresaService;

    @GetMapping("/empresas")
    @PreAuthorize("hasRole('ADMIN_EMPRESA')")
    public List<AcessoEmpresaResponse> acessoEmpresa(@AuthenticationPrincipal Jwt jwt){
        return acessoEmpresaService.acessoEmpresa(jwt.getSubject());
    }

}
