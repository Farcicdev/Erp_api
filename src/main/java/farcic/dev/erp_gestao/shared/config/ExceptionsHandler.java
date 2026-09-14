package farcic.dev.erp_gestao.shared.config;

import farcic.dev.erp_gestao.shared.exeception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler({RevendaNotFoundException.class, ClienteNotFoundException.class, LojaNotFoundException.class})
    public ResponseEntity<ResponseError> handleNaoEncontrado(RuntimeException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({RevendaInativaException.class, ClienteInativoException.class, LojaInativaException.class,
            UsuarioJaVinculadoException.class, LojaCnpjJaCadastradoException.class})
    public ResponseEntity<ResponseError> handleConflito(RuntimeException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler({AcessoRevendaNegadoException.class, AcessoClienteNegadoException.class, AccessDeniedException.class})
    public ResponseEntity<ResponseError> handleAcessoNegado(RuntimeException e) {
        return resposta(HttpStatus.FORBIDDEN, "Acesso negado");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseError> handleIntegridade(DataIntegrityViolationException e) {
        return resposta(HttpStatus.CONFLICT, "Operação conflita com os dados cadastrados");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .distinct().sorted().collect(java.util.stream.Collectors.joining("; "));
        return resposta(HttpStatus.BAD_REQUEST, mensagem.isEmpty() ? "Dados inválidos" : mensagem);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            HandlerMethodValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<ResponseError> handleRequestInvalido(Exception e) {
        return resposta(HttpStatus.BAD_REQUEST, "Requisição inválida");
    }

    private ResponseEntity<ResponseError> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ResponseError(mensagem, LocalDateTime.now()));
    }
}
