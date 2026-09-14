package farcic.dev.erp_gestao.cliente.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank
        @Size(max = 255)
        String nome,
        @Email
        @Size(max = 255)
        String emailContato,
        @Size(max = 30)
        String telefone
){
}
