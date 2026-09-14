package farcic.dev.erp_gestao.loja.repository;

import farcic.dev.erp_gestao.loja.entity.Loja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
    boolean existsByCnpj(String cnpj);

    Page<Loja> findAllByCliente_IdAndCliente_Revenda_IdAndAtivoTrue(Long clienteId, Long revendaId, Pageable pageable);

    Optional<Loja> findByIdAndCliente_IdAndCliente_Revenda_Id(Long lojaId, Long clienteId, Long revendaId);

    @Query("""
            select l from Loja l
            where l.ativo = true and l.cliente.ativo = true and l.cliente.revenda.ativo = true
              and exists (select v.id from UsuarioCliente v
                          where v.cliente = l.cliente and v.ativo = true
                            and v.usuario.ativo = true and v.usuario.keycloakSub = :sub)
            order by l.id
            """)
    List<Loja> buscarLojasAcessiveis(@Param("sub") String keycloakSub);
}
