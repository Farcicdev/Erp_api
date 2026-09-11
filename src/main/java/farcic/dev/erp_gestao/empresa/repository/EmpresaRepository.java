package farcic.dev.erp_gestao.empresa.repository;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Page<Empresa> findAllByRevendaId(Long revendaId, Pageable pageable);

    Optional<Empresa> findByIdAndRevendaId(Long empresaId, Long revendaId);

}
