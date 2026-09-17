package farcic.dev.erp_gestao.produto.repository;

import farcic.dev.erp_gestao.produto.entity.Produtos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutosRepository extends JpaRepository<Produtos, Long> {
    boolean existsByLoja_IdAndCodigoInterno(Long lojaId, String codigoInterno);

    boolean existsByLoja_IdAndGtin(Long lojaId, String gtin);

    Page<Produtos> findAllByLoja_Id(Long lojaId, Pageable pageable);
}
