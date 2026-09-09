package farcic.dev.erp_gestao.revenda.controller;

import farcic.dev.erp_gestao.revenda.dto.request.CriarRevendaRequest;
import farcic.dev.erp_gestao.revenda.dto.response.CriarRevendaResponse;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.service.RevendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revendas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class RevendaController {

    private final RevendaService revendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CriarRevendaResponse criarRevenda(@Valid @RequestBody CriarRevendaRequest request) {
        return revendaService.criarRevenda(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CriarRevendaResponse> listarRevendas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return revendaService.listarRevendas(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CriarRevendaResponse buscarRevendaPorId(@PathVariable Long id) {
        return revendaService.revendaPorId(id);
    }

    @PatchMapping("/{id}/inativacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void inativar(@PathVariable Long id) {
        revendaService.inativarRevenda(id);
    }

}
