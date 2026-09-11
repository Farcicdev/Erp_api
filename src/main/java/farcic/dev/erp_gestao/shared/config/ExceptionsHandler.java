package farcic.dev.erp_gestao.shared.config;

import farcic.dev.erp_gestao.shared.exeception.AcessoRevendaNegadoException;
import farcic.dev.erp_gestao.shared.exeception.ResponseError;
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

    @ExceptionHandler(RevendaNotFoundException.class)
    public ResponseEntity<ResponseError> handleRevendaNotFound(RevendaNotFoundException e) {
        ResponseError error = new ResponseError(
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RevendaInativaException.class)
    public ResponseEntity<ResponseError> handleRevendaInativa(RevendaInativaException e) {
        ResponseError error = new ResponseError(
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsuarioJaVinculadoException.class)
    public ResponseEntity<ResponseError> handleUsuarioJaVinculado(UsuarioJaVinculadoException e) {
        ResponseError error = new ResponseError(
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AcessoRevendaNegadoException.class)
    public ResponseEntity<ResponseError> handleAcessoRevendaNegado(AcessoRevendaNegadoException e) {
        ResponseError error = new ResponseError(
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
