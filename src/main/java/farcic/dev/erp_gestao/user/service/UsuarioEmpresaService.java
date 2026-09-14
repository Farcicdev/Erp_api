package farcic.dev.erp_gestao.user.service;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.empresa.repository.EmpresaRepository;
import farcic.dev.erp_gestao.shared.exeception.EmpresaNaoEncontradaException;
import farcic.dev.erp_gestao.shared.exeception.EmpresaInativaException;
import farcic.dev.erp_gestao.shared.exeception.UsuarioJaVinculadoException;
import farcic.dev.erp_gestao.user.dto.request.VincularUsuarioEmpresaRequest;
import farcic.dev.erp_gestao.user.dto.response.UsuarioEmpresaResponse;
import farcic.dev.erp_gestao.user.entity.Usuario;
import farcic.dev.erp_gestao.user.entity.UsuarioEmpresa;
import farcic.dev.erp_gestao.user.repository.UsuarioEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioEmpresaService {

    private final UsuarioEmpresaRepository usuarioEmpresaRepository;
    private final AcessoRevendaService acessoRevendaService;
    private final UsuarioCadastroService usuarioCadastroService;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public UsuarioEmpresaResponse vincularEmpresa(String keycloakSubAuthenticado,Long revendaId, Long empresaId, VincularUsuarioEmpresaRequest vincularUsuarioEmpresaRequest) {
        acessoRevendaService.validarAcesso(keycloakSubAuthenticado, revendaId);

        Empresa empresa = empresaRepository.findByIdAndRevendaId(empresaId, revendaId).orElseThrow(
                () -> new EmpresaNaoEncontradaException("Empresa não encontrada nesta revenda")
        );

        if (Boolean.FALSE.equals(empresa.getAtivo())) {
            throw new EmpresaInativaException("Não é possível vincular usuário a empresa inativa"
            );
        }
        Usuario usuario = usuarioCadastroService.buscarOuCriar(vincularUsuarioEmpresaRequest.keycloakSub());
        boolean jaExiste = usuarioEmpresaRepository.existsByUsuarioIdAndEmpresaId(
                usuario.getId(),
                empresa.getId()
        );

        if(jaExiste){
            throw new UsuarioJaVinculadoException("Usuário já está vinculado a essa empresa"
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

}
