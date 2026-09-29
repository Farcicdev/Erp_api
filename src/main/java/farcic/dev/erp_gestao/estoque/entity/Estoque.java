package farcic.dev.erp_gestao.estoque.entity;

import farcic.dev.erp_gestao.loja.entity.Loja;
import farcic.dev.erp_gestao.produto.entity.Produto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estoque_seq")
    private Long Id;
    @OneToOne
    private Loja loja;
    @OneToMany
    private Produto produto;

    private BigDecimal estoqueAtual;

}
