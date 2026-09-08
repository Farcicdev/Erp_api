package farcic.dev.erp_gestao.loja.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import farcic.dev.erp_gestao.empresa.entity.Empresa;
import farcic.dev.erp_gestao.loja.entity.configuracaoFiscal.ConfiguracaoFiscal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loja")
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
    @Column(name = "cnpj", nullable = false, length = 14, unique = true)
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
    @JoinColumn(name = "empresa_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_loja_empresa"))
    @Setter(AccessLevel.NONE)
    private Empresa empresa;

    @JsonIgnore
    @OneToOne(mappedBy = "loja", fetch = FetchType.LAZY)
    private ConfiguracaoFiscal configuracaoFiscal;

    // O builder usa o construtor para preservar defaults e sincronizar a associação.
    @Builder
    public Loja(Long id, String nome, String nomeFantasia, String razaoSocial, String cnpj,
                String inscricaoEstadual, RegimeTributario regimeTributario,
                Boolean ativo, Empresa empresa) {
        this.id = id;
        this.nome = nome;
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.inscricaoEstadual = inscricaoEstadual;
        this.regimeTributario = regimeTributario;
        this.ativo = ativo == null ? true : ativo;
        setEmpresa(empresa);
    }

    public void setEmpresa(Empresa empresa) {
        if (this.empresa == empresa) {
            return;
        }
        Empresa anterior = this.empresa;
        this.empresa = empresa;
        if (anterior != null) {
            anterior.removerLoja(this);
        }
        if (empresa != null) {
            empresa.adicionarLoja(this);
        }
    }
}
