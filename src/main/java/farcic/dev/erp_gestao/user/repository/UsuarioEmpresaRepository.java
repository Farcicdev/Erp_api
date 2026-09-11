package farcic.dev.erp_gestao.user.repository;

import farcic.dev.erp_gestao.user.entity.UsuarioEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioEmpresaRepository extends JpaRepository<UsuarioEmpresa, Long> {

    boolean existsByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    List<UsuarioEmpresa> findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndEmpresa_AtivoTrue(String keycloakSub);

}
