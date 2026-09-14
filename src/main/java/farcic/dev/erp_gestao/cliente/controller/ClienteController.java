package farcic.dev.erp_gestao.cliente.controller;

import farcic.dev.erp_gestao.cliente.dto.request.ClienteRequest;
import farcic.dev.erp_gestao.cliente.dto.request.ClienteStatusRequest;
import farcic.dev.erp_gestao.cliente.dto.response.ClienteResponse;
import farcic.dev.erp_gestao.cliente.service.ClienteService;
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
@RequestMapping("/revendas/{revendaId}/clientes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_REVENDA')")
public class ClienteController {
    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criarCliente(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                                        @Valid @RequestBody ClienteRequest request) {
        return clienteService.criarCliente(jwt.getSubject(), revendaId, request);
    }

    @GetMapping
    public Page<ClienteResponse> listarClientes(@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId,
                                                @RequestParam(defaultValue = "0") @Min(0) int page,
                                                @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return clienteService.listar(jwt.getSubject(), revendaId, PageRequest.of(page, size));
    }

    @GetMapping("/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN_REVENDA', 'ADMIN_CLIENTE')")
    public ClienteResponse buscarPorId(@PathVariable Long revendaId, @PathVariable Long clienteId,
                                       @AuthenticationPrincipal Jwt jwt) {
        return clienteService.buscarPorId(revendaId, jwt.getSubject(), clienteId);
    }

    @PatchMapping("/{clienteId}/status")
    public ClienteResponse mudarStatus(@PathVariable Long revendaId, @PathVariable Long clienteId,
                                       @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ClienteStatusRequest request) {
        return clienteService.mudarStatus(revendaId, jwt.getSubject(), clienteId, request.ativo());
    }
}
