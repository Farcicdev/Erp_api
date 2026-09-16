package farcic.dev.erp_gestao.produto.controller;

import lombok.RequiredArgsConstructor;
import farcic.dev.erp_gestao.produto.dto.request.ProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.respose.ProdutoResponse;
import farcic.dev.erp_gestao.produto.service.CriarProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lojas/{lojaId}/produtos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_CLIENTE')")
public class ProdutosController {
    private final CriarProdutoService criarProdutoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProdutoRequest request) {
        return criarProdutoService.cadastrarProdutos(request, lojaId, jwt.getSubject());
    }
}
