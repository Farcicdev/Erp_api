package farcic.dev.erp_gestao.loja.entity.configuracaoFiscal;

import farcic.dev.erp_gestao.loja.entity.Loja;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracao_fiscal")
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracao_fiscal_seq")
    @SequenceGenerator(name = "configuracao_fiscal_seq", sequenceName = "configuracao_fiscal_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(
            name = "loja_id",
            nullable = false,
            unique = true
    )
    private Loja loja;
}
