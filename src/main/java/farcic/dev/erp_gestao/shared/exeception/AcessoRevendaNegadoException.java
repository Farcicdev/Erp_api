package farcic.dev.erp_gestao.shared.exeception;

public class AcessoRevendaNegadoException extends RuntimeException {
    public AcessoRevendaNegadoException(String message) {
        super(message);
    }
}
