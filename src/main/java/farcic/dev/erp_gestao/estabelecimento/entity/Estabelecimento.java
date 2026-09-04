package farcic.dev.erp_gestao.estabelecimento.entity;

import farcic.dev.erp_gestao.cliente.entity.Cliente;
import jakarta.persistence.*;

@Entity
@Table
public class Estabelecimento {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

}
