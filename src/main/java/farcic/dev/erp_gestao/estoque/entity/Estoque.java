package farcic.dev.erp_gestao.estoque.entity;

import farcic.dev.erp_gestao.produto.entity.Produto;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "estoque", uniqueConstraints =
        @UniqueConstraint(name = "uk_estoque_produto", columnNames = "produto_id"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estoque_seq")
    @SequenceGenerator(name = "estoque_seq", sequenceName = "estoque_seq", allocationSize = 1)
    private Long id;

    // O produto já determina a loja à qual este saldo pertence.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estoque_produto"))
    private Produto produto;

    @DecimalMin("0.000")
    @Digits(integer = 12, fraction = 3)
    @Column(name = "estoque_atual", nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueAtual = BigDecimal.ZERO;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

}
