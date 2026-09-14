package farcic.dev.erp_gestao.shared.exeception;

public class EmpresaNaoEncontradaException extends RuntimeException {
    public EmpresaNaoEncontradaException(String message) {
        super(message);
    }
}
