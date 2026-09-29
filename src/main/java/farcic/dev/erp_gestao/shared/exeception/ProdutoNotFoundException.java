package farcic.dev.erp_gestao.shared.exeception;

public class ProdutoNotFoundException extends RuntimeException {
    public ProdutoNotFoundException(String mensagem) {
        super(mensagem);
    }
}
