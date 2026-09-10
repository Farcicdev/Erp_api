package farcic.dev.erp_gestao.user.entity;

import farcic.dev.erp_gestao.revenda.entity.Revenda;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario_revenda",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_usuario_revenda",
               columnNames = {"usuario_id", "revenda_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioRevenda {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_revenda_seq")
    @SequenceGenerator(name = "usuario_revenda_seq", sequenceName = "usuario_revenda_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "usuario_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_usuario_revenda_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "revenda_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_usuario_revenda_revenda"))
    private Revenda revenda;

    @Column(nullable = false)
    private Boolean ativo = true;

}
