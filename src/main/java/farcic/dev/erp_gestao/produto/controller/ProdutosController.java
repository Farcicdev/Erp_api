package farcic.dev.erp_gestao.produto.controller;

import lombok.RequiredArgsConstructor;
import farcic.dev.erp_gestao.produto.dto.request.ProdutosRequest;
import farcic.dev.erp_gestao.produto.dto.respose.ProdutosResponse;
import farcic.dev.erp_gestao.produto.service.CriarProdutosService;
import farcic.dev.erp_gestao.produto.service.ListarProdutosService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lojas/{lojaId}/produtos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_CLIENTE')")
public class ProdutosController {
    private final CriarProdutosService criarProdutosService;
    private final ListarProdutosService listarProdutosService;

    @GetMapping
    public Page<ProdutosResponse> listar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return listarProdutosService.listarProdutos(PageRequest.of(page, size), lojaId, jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutosResponse criar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProdutosRequest request) {
        return criarProdutosService.cadastrarProdutos(request, lojaId, jwt.getSubject());
    }
}
