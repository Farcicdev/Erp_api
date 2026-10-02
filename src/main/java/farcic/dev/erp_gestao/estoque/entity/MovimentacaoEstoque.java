package farcic.dev.erp_gestao.estoque.entity;

import farcic.dev.erp_gestao.user.entity.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque", indexes =
        @Index(name = "idx_movimentacao_estoque_data", columnList = "estoque_id, criado_em"))
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movimentacao_estoque_seq")
    @SequenceGenerator(name = "movimentacao_estoque_seq", sequenceName = "movimentacao_estoque_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estoque_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_movimentacao_estoque_estoque"))
    private Estoque estoque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_movimentacao_estoque_usuario"))
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20, updatable = false)
    private TipoMovimentacaoEstoque tipo;

    @DecimalMin(value = "0.000", inclusive = false)
    @Digits(integer = 12, fraction = 3)
    @Column(nullable = false, precision = 15, scale = 3, updatable = false)
    private BigDecimal quantidade;

    @DecimalMin("0.000")
    @Digits(integer = 12, fraction = 3)
    @Column(name = "saldo_anterior", nullable = false, precision = 15, scale = 3, updatable = false)
    private BigDecimal saldoAnterior;

    @DecimalMin("0.000")
    @Digits(integer = 12, fraction = 3)
    @Column(name = "saldo_posterior", nullable = false, precision = 15, scale = 3, updatable = false)
    private BigDecimal saldoPosterior;

    @Column(name = "motivo", length = 500, updatable = false)
    private String motivo;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void registrarDataCriacao() {
        criadoEm = LocalDateTime.now();
    }

}
