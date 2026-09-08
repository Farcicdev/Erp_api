package farcic.dev.erp_gestao.empresa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import farcic.dev.erp_gestao.revenda.entity.Revenda;
import farcic.dev.erp_gestao.loja.entity.Loja;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_seq")
    @SequenceGenerator(name = "empresa_seq", sequenceName = "empresa_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email_contato", nullable = true, length = 255)
    private String emailContato;

    @Column(name = "telefone", nullable = true, length = 255)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo = true;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "revenda_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_empresa_revenda"))
    @Setter(AccessLevel.NONE)
    private Revenda revenda;

    @JsonIgnore
    @OneToMany(mappedBy = "empresa")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Loja> lojas = new ArrayList<>();

    // O builder usa o construtor para preservar defaults e sincronizar a associação.
    @Builder
    public Empresa(Long id, String nome, String emailContato, String telefone, Boolean ativo, Revenda revenda) {
        this.id = id;
        this.nome = nome;
        this.emailContato = emailContato;
        this.telefone = telefone;
        this.ativo = ativo == null ? true : ativo;
        setRevenda(revenda);
    }

    public void setRevenda(Revenda revenda) {
        if (this.revenda == revenda) {
            return;
        }
        Revenda anterior = this.revenda;
        this.revenda = revenda;
        if (anterior != null) {
            anterior.removerEmpresa(this);
        }
        if (revenda != null) {
            revenda.adicionarEmpresa(this);
        }
    }

    @JsonIgnore
    public List<Loja> getLojas() {
        return Collections.unmodifiableList(lojas);
    }

    public void adicionarLoja(Loja loja) {
        Objects.requireNonNull(loja, "Loja não pode ser nula");
        if (!lojas.contains(loja)) {
            lojas.add(loja);
        }
        if (loja.getEmpresa() != this) {
            loja.setEmpresa(this);
        }
    }

    public void removerLoja(Loja loja) {
        if (lojas.remove(loja) && loja.getEmpresa() == this) {
            loja.setEmpresa(null);
        }
    }
}
