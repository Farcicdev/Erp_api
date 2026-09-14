package farcic.dev.erp_gestao.loja.repository;

import farcic.dev.erp_gestao.loja.entity.Loja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
}
