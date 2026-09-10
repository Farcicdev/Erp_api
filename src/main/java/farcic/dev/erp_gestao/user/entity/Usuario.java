package farcic.dev.erp_gestao.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_sequence", allocationSize = 1)
    private Long  id;
    @Column(name = "keycloak_sub",nullable = false, unique = true, length = 100)
    private String keycloakSub;

    @Column(name = "ativo",nullable = false)
    private Boolean ativo = true;

    public Usuario(String keycloakSub) {
        this.keycloakSub = keycloakSub;
        this.ativo = true;
    }
}
