package farcic.dev.erp_gestao.loja.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import farcic.dev.erp_gestao.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loja",
        indexes = @Index(name = "idx_loja_cliente_id", columnList = "cliente_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_loja_cnpj", columnNames = "cnpj"))
@Getter
@Setter
@NoArgsConstructor
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loja_seq")
    @SequenceGenerator(name = "loja_seq", sequenceName = "loja_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;
    @Column(name = "nome_fantasia", nullable = false, length = 255)
    private String nomeFantasia;
    @Column(name = "razao_social", nullable = false, length = 255)
    private String razaoSocial;
    @Column(name = "cnpj", nullable = false, length = 14)
    private String cnpj;
    @Column(name = "inscricao_estadual", nullable = false, length = 15)
    private String inscricaoEstadual;
    @Column(name = "regime_tributario", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario;
    @Column(nullable = false)
    private Boolean ativo = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_loja_cliente"))
    @Setter(AccessLevel.NONE)
    private Cliente cliente;


    // O builder usa o construtor para preservar defaults e sincronizar a associação.
    @Builder
    public Loja(Long id, String nome, String nomeFantasia, String razaoSocial, String cnpj,
                String inscricaoEstadual, RegimeTributario regimeTributario,
                Boolean ativo, Cliente cliente) {
        this.id = id;
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.regimeTributario = regimeTributario;
        this.ativo = ativo == null ? true : ativo;
        setCliente(cliente);
    }

    public void setCliente(Cliente cliente) {
        if (this.cliente == cliente) {
            return;
        }
        Cliente anterior = this.cliente;
        this.cliente = cliente;
        if (anterior != null) {
            anterior.removerLoja(this);
        }
        if (cliente != null) {
            cliente.adicionarLoja(this);
        }
    }
}
