package farcic.dev.erp_gestao.shared.config;

import farcic.dev.erp_gestao.shared.exeception.ResponseError;
import farcic.dev.erp_gestao.shared.exeception.RevendaNotFoundException;
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

}
