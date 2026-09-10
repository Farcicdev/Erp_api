package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.revenda.repository.RevendaRepository;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioRevendaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioRevendaResponse;
import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.entity.UsuarioRevenda;
import farcic.dev.erp_gestao.user.repository.UsuarioRepository;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRevendaRepository usuarioRevendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RevendaRepository revendaRepository;

    //vincular
    @Transactional
    public UsuarioRevendaResponse vincular(Long revendaId, VincularUsuarioRevendaRequest request) {
        Revenda revenda = revendaRepository.findById(revendaId).orElseThrow(
                () -> new RevendaNotFoundException("Revenda não encontrada")
        );

        if(!revenda.getAtivo()) {
            throw new RuntimeException("Revenda não está ativa");
        }

        Usuario usuario = usuarioRepository.findByKeycloakSub(request.keycloakSub()).orElseGet(
                () -> usuarioRepository.save(
                        new Usuario(request.keycloakSub())
                )
        );

        boolean jaExiste = usuarioRevendaRepository.existsByUsuarioIdAndRevendaId(
                usuario.getId(),
                revenda.getId()
        );

        if(jaExiste) {
            throw new RuntimeException("Usuário já está vinculado a essa revenda");
        }

        UsuarioRevenda usuarioRevenda = new UsuarioRevenda(usuario, revenda);
        UsuarioRevenda vinculoSalvo = usuarioRevendaRepository.save(usuarioRevenda);

        return new UsuarioRevendaResponse(
                vinculoSalvo.getId(),
                usuario.getId(),
                usuario.getKeycloakSub(),
                revenda.getId(),
                vinculoSalvo.getAtivo()
        );
    }

}
