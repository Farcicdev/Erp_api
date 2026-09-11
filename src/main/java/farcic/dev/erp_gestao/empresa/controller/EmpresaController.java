package farcic.dev.erp_gestao.empresa.controller;

import farcic.dev.erp_gestao.empresa.dto.request.EmpresaRequest;
import farcic.dev.erp_gestao.empresa.dto.request.EmpresaStatusRequest;
import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.service.EmpresaService;
import jakarta.validation.Valid;
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
@RequestMapping("/revendas/{revendaId}/empresas")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN_REVENDA')")
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpresaResponse criarEmpresa (@AuthenticationPrincipal Jwt jwt, @PathVariable Long revendaId, @Valid @RequestBody EmpresaRequest empresaRequest){
        String keycloakSub = jwt.getSubject();

        return empresaService.criarEmpresa(keycloakSub, revendaId, empresaRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<EmpresaResponse> listarEmpresa(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable Long revendaId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        return empresaService.listar(
                jwt.getSubject(),
                revendaId,
                pageable
        );
    }

        @GetMapping("/{empresaId}")
        @ResponseStatus(HttpStatus.OK)
        public EmpresaResponse buscarPorId(@PathVariable Long revendaId, @PathVariable Long empresaId,@AuthenticationPrincipal Jwt jwt ){
            return empresaService.buscarPorId(
                    revendaId,
                    jwt.getSubject(),
                    empresaId
            );
        }

        @PatchMapping("/{empresaId}/inativacao")
        @ResponseStatus(HttpStatus.ACCEPTED)
        public EmpresaResponse inativarEmpresa (@PathVariable Long revendaId, @PathVariable Long empresaId, @AuthenticationPrincipal Jwt jwt, @RequestBody EmpresaStatusRequest request){
            return empresaService.mudarStatus(
                    revendaId,
                    jwt.getSubject(),
                    empresaId,
                    request.ativo()
            );
        }

}
