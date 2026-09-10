package farcic.dev.erp_gestao.user.repository;

import farcic.dev.erp_gestao.user.entity.UsuarioRevenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRevendaRepository extends JpaRepository<UsuarioRevenda, Long> {

    boolean existsByUsuarioIdAndRevendaId(Long usuarioId, Long revendaId);

    Optional<UsuarioRevenda> findByUsuarioIdAndRevendaId(Long usuarioId, Long revendaId);
}
