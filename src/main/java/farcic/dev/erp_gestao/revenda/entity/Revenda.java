package farcic.dev.erp_gestao.revenda.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "revenda")
@Getter
@Setter
@NoArgsConstructor
public class Revenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revenda_seq")
    @SequenceGenerator(name = "revenda_seq", sequenceName = "revenda_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email_contato", nullable = false, length = 150, unique = true)
    private String emailContato;

    @Column(name = "cnpj", nullable = false, length = 14, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private Boolean ativo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "revenda")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Empresa> empresas = new ArrayList<>();

    // O builder usa o construtor para preservar defaults e sincronizar a associação.
    @Builder
    public Revenda(Long id, String nome, String emailContato, String cnpj, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.emailContato = emailContato;
        this.cnpj = cnpj;
        this.ativo = ativo == null ? true : ativo;
    }

    @JsonIgnore
    public List<Empresa> getEmpresas() {
        return Collections.unmodifiableList(empresas);
    }

    public void adicionarEmpresa(Empresa empresa) {
        Objects.requireNonNull(empresa, "Empresa não pode ser nula");
        if (!empresas.contains(empresa)) {
            empresas.add(empresa);
        }
        if (empresa.getRevenda() != this) {
            empresa.setRevenda(this);
        }
    }

    public void removerEmpresa(Empresa empresa) {
        if (empresas.remove(empresa) && empresa.getRevenda() == this) {
            empresa.setRevenda(null);
        }
    }
}
