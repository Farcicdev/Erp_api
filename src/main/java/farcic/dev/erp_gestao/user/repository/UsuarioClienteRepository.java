package farcic.dev.erp_gestao.user.repository;

import farcic.dev.erp_gestao.user.entity.UsuarioCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioClienteRepository extends JpaRepository<UsuarioCliente, Long> {

    boolean existsByUsuarioIdAndClienteId(Long usuarioId, Long clienteId);

    List<UsuarioCliente> findAllByUsuario_KeycloakSubAndUsuario_AtivoTrueAndAtivoTrueAndCliente_AtivoTrueAndCliente_Revenda_AtivoTrue(String keycloakSub);

    boolean existsByUsuario_KeycloakSubAndCliente_IdAndCliente_Revenda_IdAndUsuario_AtivoTrueAndAtivoTrueAndCliente_AtivoTrueAndCliente_Revenda_AtivoTrue(
            String keycloakSub, Long clienteId, Long revendaId);
}

