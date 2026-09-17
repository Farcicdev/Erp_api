package farcic.dev.erp_gestao.produto.repository;

import farcic.dev.erp_gestao.produto.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByLoja_IdAndCodigoInterno(Long lojaId, String codigoInterno);

    boolean existsByLoja_IdAndGtin(Long lojaId, String gtin);

    Page<Produto> findAllByLoja_IdAndAtivoTrue(Long lojaId, Pageable pageable);
}
