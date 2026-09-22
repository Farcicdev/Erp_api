package farcic.dev.erp_gestao.produto.controller;

import farcic.dev.erp_gestao.produto.service.BuscarProdutosPorIdService;
import lombok.RequiredArgsConstructor;
import farcic.dev.erp_gestao.produto.dto.request.ProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.service.CriarProdutoService;
import farcic.dev.erp_gestao.produto.service.ListarProdutoService;
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
public class ProdutoController {
    private final CriarProdutoService criarProdutoService;
    private final ListarProdutoService listarProdutoService;
    private final BuscarProdutosPorIdService buscarProdutosPorIdService;

    @GetMapping
    public Page<ProdutoResponse> listar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return listarProdutoService.listarProdutos(PageRequest.of(page, size), lojaId, jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProdutoRequest request) {
        return criarProdutoService.cadastrarProdutos(request, lojaId, jwt.getSubject());
    }

    @GetMapping("/{produtoId}")
    @ResponseStatus(HttpStatus.OK)
    public ProdutoResponse buscarPorId(@PathVariable Long lojaId, @PathVariable Long produtoId, @AuthenticationPrincipal Jwt jwt){
        return buscarProdutosPorIdService.buscarProdutosPorId(lojaId, produtoId,jwt.getSubject());
    }
}
