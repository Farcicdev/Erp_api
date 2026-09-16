package farcic.dev.erp_gestao.produto.repository;

import farcic.dev.erp_gestao.produto.entity.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produtos, Long> {
    boolean existsByLoja_IdAndCodigoInterno(Long lojaId, String codigoInterno);

    boolean existsByLoja_IdAndGtin(Long lojaId, String gtin);
}
