package farcic.dev.erp_gestao.revenda.entity;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "revenda")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Revenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "revenda_seq", sequenceName = "revenda_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    @Column(name = "email_contato", nullable = false, unique = true, length = 150)
    private String emailContato;
    @Column(name = "cnpj", nullable = false, unique = true, length = 14)
    private String cnpj;
    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "revenda")
    private List<Cliente> clientes = new ArrayList<>();

}
