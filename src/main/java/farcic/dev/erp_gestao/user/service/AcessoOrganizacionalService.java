package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.shared.exeception.AcessoClienteNegadoException;
import farcic.dev.erp_gestao.shared.exeception.AcessoRevendaNegadoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AcessoOrganizacionalService {
    private final AcessoRevendaService acessoRevendaService;
    private final AcessoClienteService acessoClienteService;

    public void validarLeitura(String keycloakSub, Long revendaId, Long clienteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !keycloakSub.equals(auth.getName())) {
            throw new AcessoClienteNegadoException("Acesso negado");
        }
        boolean adminRevenda = possuiRole(auth, "ROLE_ADMIN_REVENDA");
        boolean adminCliente = possuiRole(auth, "ROLE_ADMIN_CLIENTE");
        if (adminRevenda) {
            try {
                acessoRevendaService.validarAcesso(keycloakSub, revendaId);
                return;
            } catch (AcessoRevendaNegadoException e) {
                if (!adminCliente) {
                    throw e;
                }
            }
        }
        if (adminCliente) {
            acessoClienteService.validarAcesso(keycloakSub, revendaId, clienteId);
            return;
        }
        throw new AcessoClienteNegadoException("Acesso negado");
    }

    private boolean possuiRole(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
