package farcic.dev.erp_gestao.shared.exeception;

public class ProdutoInativoException extends RuntimeException {
    public ProdutoInativoException(String message) {
        super(message);
    }
}
