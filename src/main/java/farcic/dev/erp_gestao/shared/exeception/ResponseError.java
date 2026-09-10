package farcic.dev.erp_gestao.shared.exeception;

import java.time.LocalDateTime;

public record ResponseError (

        String message,
        LocalDateTime dateError

) {
}
