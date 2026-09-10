package farcic.dev.erp_gestao.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_sequence", allocationSize = 1)
    private Long  id;
    @Column(name = "keycloak_sub",nullable = false, unique = true)
    private String keycloakSub;

    @Column(name = "ativo",nullable = false)
    private Boolean ativo = true;
}
