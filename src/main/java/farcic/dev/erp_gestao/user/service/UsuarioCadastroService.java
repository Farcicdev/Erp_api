package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioCadastroService {
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario buscarOuCriar(String keycloakSub) {
        return usuarioRepository.findByKeycloakSub(keycloakSub)
                .orElseGet(() -> usuarioRepository.save(new Usuario(keycloakSub)));
    }
}
