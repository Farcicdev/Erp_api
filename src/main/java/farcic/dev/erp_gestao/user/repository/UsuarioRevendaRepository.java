package farcic.dev.erp_gestao.user.repository;

import farcic.dev.erp_gestao.user.entity.UsuarioRevenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRevendaRepository extends JpaRepository<UsuarioRevenda, Long> {

    boolean existsByUsuarioIdAndRevendaId(Long usuarioId, Long revendaId);

    boolean existsByUsuario_KeycloakSubAndRevenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(String keycloakSub, Long revendaId);

    Optional<UsuarioRevenda> findByUsuarioIdAndRevendaId(Long usuarioId, Long revendaId);

    List<UsuarioRevenda> findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndRevenda_AtivoTrue(String keycloakSub);
}
