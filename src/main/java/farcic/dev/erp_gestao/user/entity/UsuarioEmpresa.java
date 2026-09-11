package farcic.dev.erp_gestao.user.entity;

import farcic.dev.erp_gestao.empresa.entity.Empresa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "usuario_empresa",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_empresa",
                        columnNames = {"usuario_id", "empresa_id"}
                )
        }
)
@NoArgsConstructor
@Getter
public class UsuarioEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_empresa_seq")
    @SequenceGenerator(name = "usuario_empresa_seq", sequenceName = "usuario_empresa_seq",allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    @Column(nullable = false)
    private Boolean ativo = true;

}
