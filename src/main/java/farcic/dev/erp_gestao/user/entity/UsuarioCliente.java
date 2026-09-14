package farcic.dev.erp_gestao.user.entity;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "usuario_cliente",
        indexes = @Index(name = "idx_usuario_cliente_cliente_id", columnList = "cliente_id"),
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_usuario_cliente",
                        columnNames = {"usuario_id", "cliente_id"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UsuarioCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_cliente_seq")
    @SequenceGenerator(name = "usuario_cliente_seq", sequenceName = "usuario_cliente_seq",allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_cliente_usuario"))
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_cliente_cliente"))
    private Cliente cliente;
    @Column(nullable = false)
    private Boolean ativo = true;

    public UsuarioCliente(Usuario usuario, Cliente cliente) {
        this.usuario = usuario;
        this.cliente = cliente;
        this.ativo = true;
    }

}
