package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.empresa.dto.response.EmpresaResponse;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.shared.exeception.AcessoRevendaNegadoException;
import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioEmpresaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioEmpresaResponse;
import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.entity.UsuarioEmpresa;
import farcic.dev.erp_gestao.user.repository.UsuarioEmpresaRepository;
import farcic.dev.erp_gestao.user.repository.UsuarioRepository;
import farcic.dev.erp_gestao.user.repository.UsuarioRevendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioEmpresaService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final UsuarioRevendaRepository usuarioRevendaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public UsuarioEmpresaResponse vincularEmpresa(String keycloakSubAuthenticado,Long revendaId, Long empresaId, VincularUsuarioEmpresaRequest vincularUsuarioEmpresaRequest) {
        validarAcesso(keycloakSubAuthenticado, revendaId);

        Empresa empresa = empresaRepository.findByIdAndRevendaId(empresaId, revendaId).orElseThrow(
                () -> new RuntimeException("empresa nao encontrada nesta revenda")
        );

        if (Boolean.FALSE.equals(empresa.getAtivo())) {
            throw new RuntimeException("Nao e possivel vincular usuario a empresa inativa"
            );
        }
        Usuario usuario = usuarioRepository.findByKeycloakSub(vincularUsuarioEmpresaRequest.keycloakSub())
                .orElseGet(() -> usuarioRepository.save(new Usuario(vincularUsuarioEmpresaRequest.keycloakSub()))
                );
        boolean jaExiste = usuarioEmpresaRepository.existsByUsuarioIdAndEmpresaId(
                usuario.getId(),
                empresa.getId()
        );

        if(!jaExiste){
            throw new RuntimeException("Nao e possivel vincular usuario a uma empresa inativa"
            );
        }

        UsuarioEmpresa vinculo = new UsuarioEmpresa(usuario, empresa);
        UsuarioEmpresa vinculoSalvo = usuarioEmpresaRepository.save(vinculo);

        return new UsuarioEmpresaResponse(
                vinculoSalvo.getId(),
                usuario.getId(),
                usuario.getKeycloakSub(),
                empresa.getId(),
                vinculoSalvo.getAtivo()
        );
    }

    private void validarAcesso(String keycloakSub, Long revendaId){
        boolean possuiAcesso = usuarioRevendaRepository.existsByUsuario_KeycloakSubAndRevenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(
                keycloakSub,
                revendaId
        );

        if (!possuiAcesso){
            throw new AcessoRevendaNegadoException("Usuario não possui acesso a esta revenda");
        }
    }

}
