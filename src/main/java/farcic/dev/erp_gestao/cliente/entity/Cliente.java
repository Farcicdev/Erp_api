package farcic.dev.erp_gestao.cliente.entity;

import farcic.dev.erp_gestao.estabelecimento.entity.Estabelecimento;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "cliente_seq",sequenceName = "cliente_seq", allocationSize = 1)
    private Long id;
    @Column()
    private String nome;

    private String emailContato;

    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenda_id", nullable = false)
    private Revenda revenda;

    @OneToMany(mappedBy = "cliente")
    private List<Estabelecimento> estabelecimento;
}
