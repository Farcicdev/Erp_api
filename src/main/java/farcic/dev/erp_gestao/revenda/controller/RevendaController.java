package farcic.dev.erp_gestao.revenda.controller;

import farcic.dev.erp_gestao.revenda.dto.request.CriarRevendaRequest;
import farcic.dev.erp_gestao.revenda.dto.response.CriarRevendaResponse;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.service.RevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/revenda")
@RequiredArgsConstructor
public class RevendaController {

    private final RevendaService revendaService;

    @PostMapping("/criar")
    public CriarRevendaResponse criarRevenda(CriarRevendaRequest request) {
        return revendaService.criarRevenda(request);
    }

    @GetMapping
    public Page<Revenda> listarRevendas(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return revendaService.listarRevendas(pageable);
    }

    @GetMapping("/{id}")
    public Revenda buscarRevendaPorId(@PathVariable Long id) {
        return revendaService.buscarRevendaPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletarRevenda(@PathVariable Long id) {
        revendaService.deletarRevenda(id);
    }

}
