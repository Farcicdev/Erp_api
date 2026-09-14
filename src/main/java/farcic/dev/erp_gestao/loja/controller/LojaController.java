package farcic.dev.erp_gestao.loja.controller;

import farcic.dev.erp_gestao.loja.dto.request.LojaRequest;
import farcic.dev.erp_gestao.loja.dto.request.LojaStatusRequest;
import farcic.dev.erp_gestao.loja.dto.response.LojaResponse;
import farcic.dev.erp_gestao.loja.service.LojaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas/{revendaId}/clientes/{clienteId}/lojas")
@PreAuthorize("hasRole('ADMIN_REVENDA')")
@RequiredArgsConstructor
public class LojaController {
    private final LojaService lojaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LojaResponse criar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                              @PathVariable Long clienteId, @Valid @RequestBody LojaRequest request) {
        return lojaService.criar(jwt.getSubject(), revendaId, clienteId, request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_REVENDA', 'ADMIN_CLIENTE')")
    public Page<LojaResponse> listar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                                     @PathVariable Long clienteId,
                                     @RequestParam(defaultValue = "0") @Min(0) int page,
                                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return lojaService.listar(jwt.getSubject(), revendaId, clienteId, PageRequest.of(page, size));
    }

    @GetMapping("/{lojaId}")
    @PreAuthorize("hasAnyRole('ADMIN_REVENDA', 'ADMIN_CLIENTE')")
    public LojaResponse buscarPorId(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                                    @PathVariable Long clienteId, @PathVariable Long lojaId) {
        return lojaService.buscarPorId(jwt.getSubject(), revendaId, clienteId, lojaId);
    }

    @PatchMapping("/{lojaId}/status")
    public LojaResponse mudarStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                                    @PathVariable Long clienteId, @PathVariable Long lojaId,
                                    @Valid @RequestBody LojaStatusRequest request) {
        return lojaService.mudarStatus(jwt.getSubject(), revendaId, clienteId, lojaId, request.ativo());
    }
}
