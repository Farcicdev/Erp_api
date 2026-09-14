package farcic.dev.erp_gestao.shared.exeception;

public class AcessoClienteNegadoException extends RuntimeException {
    public AcessoClienteNegadoException(String message) {
        super(message);
    }
}
