package farcic.dev.erp_gestao.produto.entity;

import farcic.dev.erp_gestao.loja.entity.Loja;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "produtos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_produto_loja_codigo", columnNames = {"loja_id", "codigo_interno"}),
        @UniqueConstraint(name = "uk_produtos_loja_gtin", columnNames = {"loja_id", "gtin"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
    @SequenceGenerator(name = "produto_seq", sequenceName = "produto_seq", allocationSize = 1)
    private Long id;
    @Column(name = "codigo_interno", nullable = false, length = 50)
    private String codigoInterno;
    @Column(nullable = false, length = 255)
    private String descricao;
    @Column(length = 14)
    private String gtin;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeComercial unidade;
    @Column(nullable = false, length = 8)
    private String ncm;
    @Column(length = 7)
    private String cest;
    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loja_id",nullable = false)
    private Loja loja;

    @PrePersist
    @PreUpdate
    private void normalizarGtin() {
        if (gtin != null) {
            gtin = gtin.strip();
            if (gtin.isEmpty()) {
                gtin = null;
            }
        }
    }

}
