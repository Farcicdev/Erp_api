package farcic.dev.erp_gestao.shared.config;

import farcic.dev.erp_gestao.shared.exeception.AcessoRevendaNegadoException;
import farcic.dev.erp_gestao.shared.exeception.ResponseError;
import farcic.dev.erp_gestao.shared.exeception.EmpresaNaoEncontradaException;
import farcic.dev.erp_gestao.shared.exeception.EmpresaInativaException;
import farcic.dev.erp_gestao.shared.exeception.RevendaInativaException;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
import farcic.dev.erp_gestao.shared.exeception.UsuarioJaVinculadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler({RevendaNotFoundException.class, EmpresaNaoEncontradaException.class})
    public ResponseEntity<ResponseError> handleNaoEncontrado(RuntimeException e) {
        return resposta(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({RevendaInativaException.class, EmpresaInativaException.class,
            UsuarioJaVinculadoException.class})
    public ResponseEntity<ResponseError> handleConflito(RuntimeException e) {
        return resposta(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(AcessoRevendaNegadoException.class)
    public ResponseEntity<ResponseError> handleAcessoRevendaNegado(AcessoRevendaNegadoException e) {
        return resposta(HttpStatus.FORBIDDEN, e);
    }

    private ResponseEntity<ResponseError> resposta(HttpStatus status, RuntimeException e) {
        return ResponseEntity.status(status).body(new ResponseError(e.getMessage(), LocalDateTime.now()));
    }
}
