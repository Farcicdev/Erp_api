package farcic.dev.erp_gestao.revenda.repository;

import farcic.dev.erp_gestao.revenda.entity.Revenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevendaRepository extends JpaRepository<Revenda, Long> {
}
