package farcic.dev.erp_gestao.produto.controller;

import farcic.dev.erp_gestao.produto.dto.request.AlterarProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.request.ProdutoRequest;
import farcic.dev.erp_gestao.produto.dto.request.ProdutoStatusRequest;
import farcic.dev.erp_gestao.produto.dto.response.ProdutoResponse;
import farcic.dev.erp_gestao.produto.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lojas/{lojaId}/produtos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_CLIENTE')")
public class ProdutoController {
    private final CriarProdutoService criarProdutoService;
    private final ListarProdutoService listarProdutoService;
    private final BuscarProdutoPorIdService buscarProdutoPorIdService;
    private final BuscarProdutoService buscarProdutoService;
    private final MudarStatusService mudarStatusService;
    private final AlterarProdutoService alterarProdutoService;

    @GetMapping
    public Page<ProdutoResponse> listar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(defaultValue = "0") @Min(0) int page,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
                                        @RequestParam(defaultValue = "true") boolean ativo) {
        return listarProdutoService.listarProdutos(PageRequest.of(page, size), lojaId, jwt.getSubject(), ativo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponse criar(@PathVariable Long lojaId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProdutoRequest request) {
        return criarProdutoService.cadastrarProdutos(request, lojaId, jwt.getSubject());
    }

    @GetMapping("/{produtoId}")
    @ResponseStatus(HttpStatus.OK)
    public ProdutoResponse buscarPorId(@PathVariable Long lojaId, @PathVariable Long produtoId, @AuthenticationPrincipal Jwt jwt){
        return buscarProdutoPorIdService.buscarProdutosPorId(lojaId, produtoId,jwt.getSubject());
    }

    @GetMapping("/buscar")
    @ResponseStatus(HttpStatus.OK)
    public Page<ProdutoResponse> buscarProdutos(@PathVariable Long lojaId,
                                                @RequestParam(required = false) String busca,
                                                Pageable pageable,
                                                @AuthenticationPrincipal Jwt jwt){
        return buscarProdutoService.listarProdutoLojaComBusca(lojaId, busca, pageable,jwt.getSubject());
    }

    @PatchMapping("/{produtoId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void mudarStatus(@PathVariable Long lojaId,@PathVariable Long produtoId,@AuthenticationPrincipal Jwt jwt,
                           @Valid @RequestBody ProdutoStatusRequest request){
        mudarStatusService.ativarOuInativar(lojaId, produtoId, jwt.getSubject(), request.ativo());
    }

    @PatchMapping("/{produtoId}/alterar")
    public ProdutoResponse alterarProduto(@PathVariable Long lojaId,@PathVariable Long produtoId,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody AlterarProdutoRequest produtoRequest){
        return alterarProdutoService.alterarProduto(lojaId, produtoId, jwt.getSubject(), produtoRequest);
    }

}
