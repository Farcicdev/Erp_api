package farcic.dev.erp_gestao.user.repository;

import farcic.dev.erp_gestao.user.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByKeycloakSub(String keycloakSub);

}
