package farcic.dev.erp_gestao.cliente.repository;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Page<Cliente> findAllByRevendaIdAndAtivoTrue(Long revendaId, Pageable pageable);

    Optional<Cliente> findByIdAndRevendaId(Long clienteId, Long revendaId);

}
